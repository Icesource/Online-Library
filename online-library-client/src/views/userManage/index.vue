<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input @keyup.enter.native="handleFilter" style="width: 200px;" class="filter-item" :placeholder="'输入内容按回车查找'"
                v-model="listQuery.username">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary"
                 icon="el-icon-edit">添加
      </el-button>
      <el-button class="filter-item" :loading="downloadLoading" style="margin-left: 20px;" type="info" icon="document"
                 @click="handleDownload">Export Excel
      </el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit
              highlight-current-row
              style="width: 100%">
      <el-table-column align="center" :label="'编号'" width="200px">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column width="250px" align="center" :label="'用户名'">
        <template slot-scope="scope">
          <span>{{scope.row.username}}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="150px" :label="'权限'">
        <template slot-scope="scope">
          <el-tag v-for="item in scope.row.roles" :key="item.id">{{item}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="'操作'" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page="listQuery.currentPage" :page-sizes="[10,20,30, 50]" :page-size="listQuery.pageSize"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="70px"
               style='width: 400px; margin-left:50px;'>
        <el-form-item v-if="dialogStatus==='update'" :label="'编号'" prop="id">
          <el-input v-model="temp.id" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item :label="'用户名'" prop="name">
          <el-input v-if="dialogStatus==='update'" v-model="temp.username" :disabled="true"></el-input>
          <el-input v-if="dialogStatus==='create'" v-model="temp.username"></el-input>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="'密码'" prop="name">
          <el-input v-model="temp.password" placeholder="请输入需要更改的密码"></el-input>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="'角色'" prop="email">
          <el-checkbox-group v-model="checkedRoles">
            <el-checkbox v-for="role in allRoles" :key="role.id" :label="role">{{role.name}}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="dialogStatus=='create'" :label="'密码'" prop="password">
          <el-input type="password" v-model="temp.password"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确认</el-button>
        <el-button v-else type="primary" @click="updateData">确认</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import { fetchList, deleteUser, createUser, updateUser } from '@/api/users'
  import { rolesList } from '@/api/roles'
  import { Message, MessageBox } from 'element-ui'
  import waves from '@/directive/waves' // 水波纹指令
  import { parseTime } from '@/utils'

  export default {
    name: 'complexTable',
    directives: {
      waves
    },
    data() {
      return {
        tableKey: 0,
        list: null,
        allRoles: null,
        checkedRoles: [],
        total: null,
        listLoading: true,
        listQuery: {
          currentPage: 0,
          pageSize: 20,
          username: undefined
        },
        temp: {
          id: undefined,
          username: '',
          password: ''
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑用户信息',
          create: '创建用户'
        },
        dialogPvVisible: false,
        downloadLoading: false,
        filename: '',
        autoWidth: true,
        bookType: 'xlsx'
      }
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          published: 'success',
          draft: 'info',
          deleted: 'danger'
        }
        return statusMap[status]
      }
    },
    created() {
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        // this.listQuery.role = 'user'
        fetchList(this.listQuery).then(response => {
          this.list = response.data.content
          this.total = response.data.totalElements
          this.listLoading = false
        })
        rolesList().then(response => {
          this.allRoles = response.data.content
        })
      },
      handleFilter() {
        this.listQuery.currentPage = 0
        this.getList()
      },
      handleSizeChange(val) {
        this.listQuery.pageSize = val
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.currentPage = val
        this.getList()
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          username: '',
          password: ''
        }
        this.checkedRoles = []
      },
      handleCreate() {
        this.resetTemp()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            var formData = new FormData()
            formData.append('username', this.temp.username)
            formData.append('password', this.temp.password)
            createUser(formData).then((data) => {
              Message({
                message: '创建用户成功',
                type: 'success',
                duration: 3 * 1000
              })
              this.dialogFormVisible = false
              this.getList()
            })
          }
        })
      },
      handleUpdate(row) {
        this.resetTemp()
        this.temp = Object.assign({}, row) // copy obj
        this.temp.timestamp = new Date(this.temp.timestamp)
        this.temp.password = null
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            var formData = new FormData()
            formData.append('id', this.temp.id)
            if (this.temp.password != null) {
              formData.append('password', this.temp.password)
            }
            const roles = []
            this.checkedRoles.forEach((item) => {
              roles.push(item.id)
            })
            formData.append('roleIds', roles)
            updateUser(formData).then(() => {
              for (const v of this.list) {
                if (v.id === this.temp.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.temp)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify({
                title: '成功',
                message: '更新成功',
                type: 'success',
                duration: 2000
              })
              this.getList()
            })
          }
        })
      },
      handleDelete(row) {
        var query = {
          'id': row.id
        }
        deleteUser(query).then(res => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
        })
      },
      formatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => {
          if (j === 'timestamp') {
            return parseTime(v[j])
          } else {
            return v[j]
          }
        }))
      },
      changeAvatar() {
        this.temp.avatarFile = this.$refs['avatarFile'].files[0]
      },
      handleDownload() {
        this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['编号', '用户名', '角色']
          const filterVal = ['id', 'username', 'roles']
          const list = this.list.filter(v => {
            return { 'id': v.id, 'username': v.username, 'roles': v.roles.join(',') }
          })
          const data = this.formatJson(filterVal, list)
          excel.export_json_to_excel({
            header: tHeader,
            data,
            filename: this.filename,
            autoWidth: this.autoWidth,
            bookType: this.bookType
          })
          this.downloadLoading = false
        })
      }
    }
  }
</script>
