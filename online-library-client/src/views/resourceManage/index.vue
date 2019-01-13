<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input @keyup.enter.native="handleFilter" style="width: 200px;" class="filter-item" :placeholder="'输入内容按回车查找'"
                v-model="listQuery.search">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary"
                 icon="el-icon-edit">添加
      </el-button>
      <el-button class="filter-item" :loading="downloadLoading" style="margin-left: 20px;" type="info" icon="document"
                 @click="downloadExcel">Export Excel
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
      <el-table-column width="250px" align="center" :label="'名称'">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column width="350px" align="center" :label="'文件'">
        <template slot-scope="scope">
          <iframe width="100%" v-if="scope.row.format==='doc'||
          scope.row.format==='docx'||
          scope.row.format==='pdf'||
          scope.row.format==='xls'||
          scope.row.format==='txt'||
          scope.row.format==='pptx'"
                  :src="'http://www.xdocin.com/xdoc?_func=to&_format=html&_cache=true&_source=true&_xdoc='+scope.row.link"
                  frameborder="0"></iframe>
          <div style="width: 100%;height: 200px" v-if="scope.row.format==='png'||
          scope.row.format==='jpg'||
          scope.row.format==='jpeg'||
          scope.row.format==='bmp'||
          scope.row.format==='git'"><img style="width: 100%;height: 100%" :src="scope.row.link" frameborder="0"></div>
          <video v-if="scope.row.format==='mp4'"
                 id="my-video" class="video-js" controls preload="auto" width="100%" height="200"
                 data-setup="{}">
            <source :src="scope.row.link" type='video/mp4'>
          </video>
          <audio controls v-if="scope.row.format==='mp3'">
            <source :src="scope.row.link" type="audio/mpeg">
          </audio>
        </template>
      </el-table-column>
      <el-table-column width="80px" :label="'文件格式'">
        <template slot-scope="scope">
          <span>{{scope.row.format}}</span>
        </template>
      </el-table-column>
      <el-table-column width="100px" :label="'访问量'">
        <template slot-scope="scope">
          <span>{{scope.row.clickCount}}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="150px" :label="'上传者'">
        <template slot-scope="scope">
          <span>{{scope.row.uploaderName}}</span>
        </template>
      </el-table-column>

      <el-table-column min-width="150px" :label="'上传时间'">
        <template slot-scope="scope">
          <span>{{scope.row.createTime}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="'操作'" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleDownload(scope.row)">
            下载
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
        <el-form-item :label="'名称'" prop="name">
          <el-input placeholder="不填写则默认为原始文件名" v-model="temp.name"></el-input>
        </el-form-item>
        <el-form-item :label="'文件'" prop="file">
          <input ref="file" type="file" v-on:change="changeFile()">
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确认</el-button>
        <!--<el-button v-else type="primary" @click="updateData">确认</el-button>-->
      </div>
    </el-dialog>


  </div>
</template>

<script>
  import { resourceListByType, resourceListAll, resourceAdd, resourceDelete, resourceDownload } from '@/api/resource'
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
          pageSize: 50,
          search: undefined
        },
        temp: {
          id: undefined,
          name: ''
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '更新资源',
          create: '上传资源'
        },
        dialogPvVisible: false,
        downloadLoading: false
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

    /**
     * 组件复用问题，当访问该组件的typeId发生变化时，同组件内不跳转（不刷新）
     * 该生命周期钩子在当前路由改变，但是该组件被复用时调用
     * @param to
     * @param from
     * @param next
     */
    watch: {
      '$route'(to, from) {
        this.getList()
      }
    },
    methods: {
      getList() {
        this.listLoading = true
        // this.listQuery.role = 'user'
        const typeId = this.$route.params.id
        resourceListByType(this.listQuery, typeId).then(response => {
          this.list = response.data.content
          this.total = response.data.totalElements
          this.listLoading = false
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
          name: ''
        }
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
            if (!this.temp.file) {
              Message({
                message: '请选择文件',
                type: 'error',
                duration: 5 * 1000
              })
              return
            }
            var formData = new FormData()
            if (this.temp.name !== '' && this.temp.name !== null) {
              formData.append('name', this.temp.name)
            }
            formData.append('file', this.temp.file)
            formData.append('typeId', this.$route.params.id)
            const uploadMessage = Message({
              message: '上传资源中，请稍等',
              type: 'info',
              duration: 0
            })
            resourceAdd(formData).then((data) => {
              uploadMessage.close()
              Message({
                message: '创建资源成功',
                type: 'success',
                duration: 3 * 1000
              })
              this.dialogFormVisible = false
              this.getList()
            })
          }
        })
      },
      // handleUpdate(row) {
      //   this.temp = Object.assign({}, row) // copy obj
      //   this.temp.timestamp = new Date(this.temp.timestamp)
      //   this.temp.password = null
      //   this.dialogStatus = 'update'
      //   this.dialogFormVisible = true
      //   this.$nextTick(() => {
      //     this.$refs['dataForm'].clearValidate()
      //   })
      // },
      handleDelete(row) {
        console.log(row)
        const query = { 'id': row.id }
        resourceDelete(query).then(res => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
        })
      },
      handleDownload(row) {
        var temp = document.createElement('form')
        temp.action = '/api/resource/download/' + this.$route.params.id + '?id=' + row.id
        temp.method = 'POST'
        temp.style.display = 'none'
        document.body.appendChild(temp)
        temp.submit()
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
      changeFile() {
        this.temp.file = this.$refs['file'].files[0]
      },
      downloadExcel() {
        this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['编号', '文件名', '文件格式', '访问量', '上传者', '上传者id', '上传时间', 'URL', '类型id']
          const filterVal = ['id', 'name', 'format', 'clickCount', 'uploaderName', 'uploaderid', 'createTime', 'link', 'typeid']
          const list = this.list.filter(v => {
            console.log(v)
            return {
              'id': v.id,
              'name': v.name,
              'format': v.format,
              'clickCount': v.clickCount,
              'uploaderName': v.uploaderName,
              'uploaderid': v.uploaderid,
              'createTime': v.createTime,
              'link': v.link,
              'typeid': v.typeid
            }
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
