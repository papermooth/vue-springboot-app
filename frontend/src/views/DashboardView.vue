<template>
  <div class="dashboard-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">用户总数</p>
              <p class="stat-value">{{ userCount }}</p>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon product-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">产品总数</p>
              <p class="stat-value">{{ productCount }}</p>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">订单总数</p>
              <p class="stat-value">{{ orderCount }}</p>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon revenue-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">今日收入</p>
              <p class="stat-value">¥{{ todayRevenue.toFixed(2) }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 最新订单 -->
      <el-card class="order-card" shadow="hover" style="margin-top: 20px;">
        <template #header>
          <div class="chart-header">
            <span>最新订单</span>
            <el-button type="text" @click="viewAllOrders">查看全部</el-button>
          </div>
        </template>
        <el-table :data="recentOrders" style="width: 100%">
          <el-table-column prop="orderNo" label="订单号" min-width="180" />
          <el-table-column prop="username" label="用户" width="120" />
          <el-table-column prop="totalAmount" label="金额" width="100">
            <template #default="scope">¥{{ scope.row.totalAmount.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button type="primary" link size="small" @click="viewOrderDetail(scope.row.id)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Goods, Document, Money } from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'
import { productApi } from '@/api/product.js'
import { orderApi } from '@/api/order.js'

export default {
  name: 'DashboardView',
  components: {
    User,
    Goods,
    Document,
    Money
  },
  setup() {
    
    // 统计数据
    const userCount = ref(0)
    const productCount = ref(0)
    const orderCount = ref(0)
    const todayRevenue = ref(0)
    const recentOrders = ref([])
    const loading = ref(false)
    
    // 获取统计数据
    const fetchDashboardStats = async () => {
      loading.value = true
      
      try {
        // 使用真实API获取数据
        const [users, products, orders] = await Promise.all([
          userApi.getAllUsers(),
          productApi.getAllProducts(),
          orderApi.getAllOrders()
        ])
        
        // 更新统计数据
        userCount.value = users?.length || 0
        productCount.value = products?.length || 0
        orderCount.value = orders?.length || 0
        
        // 计算今日收入（如果有时间字段可筛选）
        todayRevenue.value = 0
        
        // 获取最新订单
        recentOrders.value = orders ? orders.slice(0, 3) : []
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '获取统计数据失败'
        ElMessage.error(errorMessage)
        console.error('获取仪表盘数据失败:', error)
        
        // 设置默认值
        userCount.value = 0
        productCount.value = 0
        orderCount.value = 0
        todayRevenue.value = 0
        recentOrders.value = []
      } finally {
        loading.value = false
      }
    }
    
    // 获取订单状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'PAID':
          return 'success'
        case 'SHIPPED':
          return 'warning'
        case 'COMPLETED':
          return 'primary'
        case 'CANCELLED':
          return 'danger'
        default:
          return 'info'
      }
    }
    
    // 获取订单状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'PAID':
          return '已支付'
        case 'SHIPPED':
          return '已发货'
        case 'COMPLETED':
          return '已完成'
        case 'CANCELLED':
          return '已取消'
        default:
          return status
      }
    }
    
    // 查看订单详情（显示提示信息，因为订单详情页面不存在）
    const viewOrderDetail = (orderId) => {
      ElMessage.info('订单详情页面尚未实现')
    }
    
    // 查看所有订单（显示提示信息，因为订单列表页面不存在）
    const viewAllOrders = () => {
      ElMessage.info('订单列表页面尚未实现')
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchDashboardStats()
    })
    
    return {
      userCount,
      productCount,
      orderCount,
      todayRevenue,
      recentOrders,
      loading,
      getStatusTagType,
      getStatusText,
      viewOrderDetail,
      viewAllOrders
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-title {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #909399;
}

.stat-value {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>