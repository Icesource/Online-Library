import { asyncRouterMap, constantRouterMap } from '@/router'
import { typeList } from '../../api/type'
import Layout from '@/views/layout/Layout'

function hasPermission(roles, route) {
  if (route.meta && route.meta.role) {
    return roles.some(role => route.meta.role.indexOf(role) >= 0)
  } else {
    return true
  }
}

function packType(root) {
  var temp = {
    path: '/resource',
    name: root.id,
    component: () => import('@/views/resourceManage/index'),
    meta: { title: root.name },
    children: []
  }
  if (root.children !== undefined && root.children !== null && root.children.length > 0) {
    root.children.forEach((item) => {
      temp.children.push(packType(item))
    })
  } else {
    temp.path = root.id
  }
  return temp
}

function generateType({ commit }) {
  typeList().then(response => {
    const res = response.data
    const nestedRouter = [{
      path: '/resource',
      component: Layout,
      meta: {
        title: '所有资源',
        icon: 'form'
      },
      children: []
    }]
    res.children.forEach((child) => {
      var temp = packType(child)
      nestedRouter[0].children.push(temp)
    })
    // return nestedRouter
    commit('ADD_ROUTERS', nestedRouter)
  })
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    },
    ADD_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = state.routers.concat(routers)
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        // 这里是
        const { roles } = data
        const accessedRouters = asyncRouterMap.filter(v => {
          if (roles.indexOf('系统管理员') >= 0) return true
          if (hasPermission(roles, v)) {
            if (v.children && v.children.length > 0) {
              v.children = v.children.filter(child => {
                if (hasPermission(roles, child)) {
                  return child
                }
                return false
              })
              return v
            } else {
              return v
            }
          }
          return false
        })
        // console.log(accessedRouters)
        commit('SET_ROUTERS', accessedRouters)
        console.log(accessedRouters)
        generateType({ commit })
        resolve()
      })
    }
  }
}

export default permission

