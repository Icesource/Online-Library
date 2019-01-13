import router from './router'
import store from './store'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css'// Progress 进度条样式
import { Message } from 'element-ui'
import { getToken } from '@/utils/auth'

const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  NProgress.start()
  console.log(getToken('username'))
  if (getToken('username') !== 'undefined' && getToken('username') !== '') {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
    } else {
      if (store.getters.roles.length === 0) {
        setTimeout(() => {
          store.dispatch('GetInfo').then(res => {
            const roles = res.data.roles
            store.dispatch('GenerateRoutes', { roles }).then(() => { // 生成可访问的路由表
              router.addRoutes(store.getters.addRouters)
              next()
              // 动态添加可访问路由表
              // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
            })
          }).catch((err) => {
            console.log(err)
            store.dispatch('FedLogOut').then(() => {
              Message.error('用户认证失败，请重新登录')
              next({ path: '/login' })
            })
          })
        }, 2000)
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      store.dispatch('FedLogOut').then(() => {
        Message.error('用户认证失败，请重新登录')
        next({ path: '/login' })
      })
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})
