import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue'),
          meta: { title: '仪表盘', icon: 'el-icon-data-line' }
        },
        // 用户管理路由
        {
          path: 'users',
          name: 'users',
          meta: { title: '用户管理', icon: 'el-icon-user' },
          children: [
            {
              path: 'list',
              name: 'userList',
              component: () => import('../views/users/UserList.vue'),
              meta: { title: '用户列表' }
            },
            {
              path: 'add',
              name: 'addUser',
              component: () => import('../views/users/AddUser.vue'),
              meta: { title: '添加用户' }
            },
            {
              path: 'edit/:id',
              name: 'editUser',
              component: () => import('../views/users/UserEdit.vue'),
              meta: { title: '编辑用户' },
              props: true
            }
          ]
        },
        // 产品管理路由
        {
          path: 'products',
          name: 'products',
          meta: { title: '产品管理', icon: 'el-icon-goods' },
          children: [
            {
              path: 'list',
              name: 'productList',
              component: () => import('../views/products/ProductList.vue'),
              meta: { title: '产品列表' }
            },
            {
              path: 'add',
              name: 'addProduct',
              component: () => import('../views/products/ProductAdd.vue'),
              meta: { title: '添加产品' }
            },
            {
              path: 'edit/:id',
              name: 'editProduct',
              component: () => import('../views/products/ProductEdit.vue'),
              meta: { title: '编辑产品' },
              props: true
            }
          ]
        }
        // 订单管理路由暂时移除，因为相关文件不存在
        // 登录页面路由暂时移除，因为相关文件不存在
        // 404页面路由暂时移除，因为相关文件不存在
      ]
    }
  ]
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 电商管理系统`
  } else {
    document.title = '电商管理系统'
  }
  
  // 暂时跳过登录检查，因为登录页面不存在
  next()
})

export default router