<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-left">
        <el-button type="primary" @click="toggleSidebar" plain>
          <el-icon><Menu /></el-icon>
        </el-button>
        <span class="app-title">Vue-SpringBoot 应用</span>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar size="small" :src="avatar || ''" :icon="User" />
            <span>{{ username || '未登录' }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主体内容 -->
    <div class="app-main">
      <!-- 侧边栏 -->
      <aside class="app-sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
        <nav class="sidebar-nav">
          <el-menu
            :default-active="activePath"
            class="sidebar-menu"
            router
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">
              <el-icon><Home /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/dashboard">
              <el-icon><DataLine /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-sub-menu index="/users">
              <template #title>
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </template>
              <el-menu-item index="/users/list">用户列表</el-menu-item>
              <el-menu-item index="/users/add">添加用户</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="/products">
              <template #title>
                <el-icon><Goods /></el-icon>
                <span>产品管理</span>
              </template>
              <el-menu-item index="/products/list">产品列表</el-menu-item>
              <el-menu-item index="/products/add">添加产品</el-menu-item>
              <el-menu-item index="/products/categories">分类管理</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="/orders">
              <template #title>
                <el-icon><ShoppingCart /></el-icon>
                <span>订单管理</span>
              </template>
              <el-menu-item index="/orders/list">订单列表</el-menu-item>
              <el-menu-item index="/orders/detail">订单详情</el-menu-item>
              <el-menu-item index="/orders/statistics">订单统计</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </nav>
      </aside>

      <!-- 内容区域 -->
      <main class="content-wrapper" :class="{ 'content-expanded': sidebarCollapsed }">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive :include="keepAliveComponents">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Menu,
  User,
  ArrowDown,
  HomeFilled,
  DataLine,
  Goods,
  ShoppingCart
} from '@element-plus/icons-vue'

export default {
  name: 'Layout',
  components: {
    Menu,
    User,
    ArrowDown,
    Home: HomeFilled,
    DataLine,
    Goods,
    ShoppingCart
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const sidebarCollapsed = ref(false)
    const username = ref('')
    const avatar = ref('')
    
    // 需要缓存的组件
    const keepAliveComponents = ['UserList', 'ProductList', 'OrderList']
    
    // 计算当前激活的路径
    const activePath = computed(() => {
      const path = route.path
      // 处理子路由的匹配
      if (path.startsWith('/users/')) {
        return '/users'
      } else if (path.startsWith('/products/')) {
        return '/products'
      } else if (path.startsWith('/orders/')) {
        return '/orders'
      }
      return path
    })
    
    // 切换侧边栏
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }
    
    // 处理菜单选择
    const handleMenuSelect = (key) => {
      router.push(key)
    }
    
    // 跳转到个人中心
    const goToProfile = () => {
      router.push('/profile')
    }
    
    // 退出登录
    const logout = () => {
      // 清除token
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      username.value = ''
      avatar.value = ''
      // 跳转到登录页
      router.push('/login')
    }
    
    // 初始化用户信息
    onMounted(() => {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        const info = JSON.parse(userInfo)
        username.value = info.username || '管理员'
        avatar.value = info.avatar || ''
      }
    })
    
    return {
      sidebarCollapsed,
      username,
      avatar,
      activePath,
      keepAliveComponents,
      toggleSidebar,
      handleMenuSelect,
      goToProfile,
      logout
    }
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 顶部导航栏 */
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.app-title {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

/* 主体内容 */
.app-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 侧边栏 */
.app-sidebar {
  width: 240px;
  background-color: #001529;
  transition: width 0.3s;
  overflow: hidden;
  height: 100%;
}

.sidebar-collapsed {
  width: 64px;
}

.sidebar-nav {
  height: 100%;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.65);
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-collapsed .el-menu-item__content,
.sidebar-collapsed .el-sub-menu__title .el-sub-menu__title-inner {
  display: none;
}

.sidebar-collapsed .el-menu-item,
.sidebar-collapsed .el-sub-menu__title {
  padding: 0 20px;
}

/* 内容区域 */
.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
  transition: all 0.3s;
}

.content-expanded {
  padding-left: 40px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>