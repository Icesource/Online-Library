<template>
  <div class="app-container">
    <tree-table v-loading="listLoading" :data="data" :eval-func="func" :eval-args="args" :expand-all="expandAll" border>
      <el-table-column width="300px" label="编号">
        <template slot-scope="scope">
          <span style="color:sandybrown">{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="名称">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="'操作'" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleCreate(scope.row)">
            新建
          </el-button>
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </tree-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="70px"
               style='width: 400px; margin-left:50px;'>
        <el-form-item v-if="dialogStatus==='update'" :label="'编号'" prop="id">
          <el-input v-model="temp.id" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item :label="'名称'" prop="name">
          <el-input v-model="temp.name"></el-input>
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
  import treeTable from '@/components/TreeTable'
  import treeToArray from './customEval'
  import { Message, MessageBox } from 'element-ui'
  import { typeAdd, typeDelete, typeList, typeUpdate } from '../../api/type'

  export default {
    name: 'CustomTreeTableDemo',
    components: { treeTable },
    data() {
      return {
        func: treeToArray,
        expandAll: false,
        data: {},
        args: [null, null, 'timeLine'],
        listLoading: true,
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '更新资源类型信息',
          create: '新建资源类型'
        },
        temp: {
          id: undefined,
          name: '',
          superiorid: ''
        }
      }
    },
    created() {
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        // this.listQuery.role = 'user'
        typeList().then(response => {
          this.data = response.data
          this.listLoading = false
        })
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          name: ''
        }
      },
      handleUpdate(row) {
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
            formData.append('name', this.temp.name)
            typeUpdate(formData).then(() => {
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
      handleCreate(row) {
        this.resetTemp()
        this.temp.superiorid = row.id
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        console.log(1)
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            var formData = new FormData()
            formData.append('name', this.temp.name)
            formData.append('superiorid', this.temp.superiorid)
            typeAdd(formData).then((data) => {
              Message({
                message: '创建资源类型成功',
                type: 'success',
                duration: 3 * 1000
              })
              this.dialogFormVisible = false
              this.getList()
            })
          }
        })
      },
      handleDelete(row) {
        var formData = new FormData()
        formData.append('id', row.id)
        typeDelete(formData).then(res => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
        })
      },
    }
  }
</script>
