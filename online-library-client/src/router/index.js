import Vue from 'vue'
import Router from 'vue-router'
// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

export const constantRouterMap = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/404', component: () => import('@/views/404'), hidden: true },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: 'Dashboard',
    hidden: true,
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index')
      }
    ]
  }
]

const typeRouterMap = [{
  path: '/resource/:id',
  nama: 'resource',
  component: Layout,
  children: [
    {
      path: '',
      name: 'resourceChild',
      component: () => import('@/views/resourceManage/index')
    }
  ]
}]

var myRouter = new Router({
  routes: constantRouterMap
})

myRouter.addRoutes(typeRouterMap)

export default myRouter

export const asyncRouterMap = [
  {
    path: '/user',
    component: Layout,
    meta: { title: '用户管理', icon: 'form', role: ['公司领导'] },
    children: [
      {
        path: 'index',
        name: 'user',
        component: () => import('@/views/userManage/index'),
        meta: { title: '用户管理', icon: 'form', role: ['公司领导'] }
      }
    ]
  },
  {
    path: '/role',
    component: Layout,
    meta: { title: '角色管理', icon: 'form', role:[''] },
    children: [
      {
        path: 'index',
        name: 'role',
        component: () => import('@/views/RoleManage/index'),
        meta: { title: '角色管理', icon: 'form', role:[''] }
      }
    ]
  },
  {
    path: '/function',
    component: Layout,
    meta: { title: '功能管理', icon: 'form', role:[''] },
    children: [
      {
        path: 'index',
        name: 'function',
        component: () => import('@/views/functionManage/index'),
        meta: { title: '功能管理', icon: 'form', role:[''] }
      }
    ]
  },
  {
    path: '/resourceType',
    component: Layout,
    meta: { title: '资源类型管理', icon: 'form', role: ['资源管理员', '公司领导'] },
    children: [
      {
        path: 'index',
        name: 'resourceType',
        component: () => import('@/views/resourceTypeManage/index'),
        meta: { title: '资源类型管理', icon: 'form', role: ['资源管理员', '公司领导'] }
      }
    ]
  }
]

