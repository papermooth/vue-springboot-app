<template>
  <div class="user-list-container">
    <div class="page-header">
      <h1>用户管理</h1>
      <el-button type="primary" @click="goToAddUser">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button>
    </div>

    <!-- 搜索功能已移除，后端API不支持搜索 -->

    <!-- 用户列表表格 -->
    <el-table
      v-loading="loading"
      :data="usersData"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="fullName" label="姓名" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewUser(scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="editUser(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="30%"
      @close="resetDeleteDialog"
    >
      <span>确定要删除用户 <strong>{{ selectedUser?.username }}</strong> 吗？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'

export default {
  name: 'UserList',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const users = ref([])
    const selectedRows = ref([])
    const deleteDialogVisible = ref(false)
    const selectedUser = ref(null)
    
    // 分页相关
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    // 计算当前页的数据
    const usersData = computed(() => {
      return users.value
    })
    
    // 获取用户列表
    const getUserList = async () => {
      loading.value = true
      try {
        const response = await userApi.getAllUsers()
        users.value = response || []
        total.value = users.value.length
      } catch (error) {
        console.error('获取用户列表失败详情:', error)
        console.error('错误响应:', error.response)
        ElMessage.error(`获取用户列表失败: ${error.message || '未知错误'}`)
      } finally {
        loading.value = false
      }
    }
    
    // 由于后端API不支持搜索，移除搜索相关功能
    
    // 分页大小变化（后端暂不支持分页，仅保留UI）
    const handleSizeChange = (size) => {
      pageSize.value = size
    }
    
    // 当前页码变化（后端暂不支持分页，仅保留UI）
    const handleCurrentChange = (current) => {
      currentPage.value = current
    }
    
    // 处理表格选择
    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }
    
    // 跳转到添加用户页面
    const goToAddUser = () => {
      router.push('/users/add')
    }
    
    // 查看用户详情
    const viewUser = (user) => {
      // 由于没有详情页面，重定向到编辑页面
      router.push(`/users/edit/${user.id}`)
    }
    
    // 编辑用户
    const editUser = (user) => {
      router.push(`/users/edit/${user.id}`)
    }
    
    // 删除用户
    const deleteUser = (user) => {
      selectedUser.value = user
      deleteDialogVisible.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      if (!selectedUser.value) return
      
      try {
        await userApi.deleteUser(selectedUser.value.id)
        ElMessage.success('用户删除成功')
        deleteDialogVisible.value = false
        getUserList() // 重新加载用户列表
      } catch (error) {
        console.error('删除用户失败:', error)
        ElMessage.error(`用户删除失败: ${error.message || '未知错误'}`)
      }
    }
    
    // 重置删除对话框
    const resetDeleteDialog = () => {
      selectedUser.value = null
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
    
    // 组件挂载时获取用户列表
    onMounted(() => {
      getUserList()
    })
    
    return {
      loading,
      usersData,
      selectedRows,
      deleteDialogVisible,
      selectedUser,
      currentPage,
      pageSize,
      total,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      goToAddUser,
      viewUser,
      editUser,
      deleteUser,
      confirmDelete,
      resetDeleteDialog,
      formatDate
    }
  }
}
</script>

<style scoped>
.user-list-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

/* 搜索功能已移除 */

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>