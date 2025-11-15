<template>
  <div class="product-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>产品列表</span>
        </div>
      </template>

      <!-- 操作按钮区域 -->
      <div class="action-buttons" style="margin-bottom: 20px;">
        <el-button type="primary" @click="handleAddProduct">添加产品</el-button>
      </div>

      <!-- 产品列表表格 -->
      <el-table
        v-loading="loading"
        :data="productList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="产品ID" width="80" />
        <el-table-column prop="name" label="产品名称" min-width="180">
          <template #default="scope">
            <div class="product-name-container">
              <el-image
                v-if="scope.row.imageUrl"
                :src="scope.row.imageUrl"
                class="product-image"
                :preview-src-list="[scope.row.imageUrl]"
              />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="产品类别" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="description" label="描述" min-width="150">
          <template #default="scope">
            <el-tooltip :content="scope.row.description || ''" placement="top">
              <span class="description">{{ scope.row.description || '-' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'"
              effect="light"
            >
              {{ scope.row.status === 'ACTIVE' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleEditProduct(scope.row.id)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDeleteProduct(scope.row)"
            >
              删除
            </el-button>
            <el-button
              :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'ACTIVE' ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除产品 "{{ deleteProduct?.name }}" 吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete" :loading="deleting">
            确认删除
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/product.js'

export default {
  name: 'ProductList',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const deleting = ref(false)
    const deleteDialogVisible = ref(false)
    const deleteProduct = ref(null)
    const selectedProducts = ref([])
    
    // 分页信息（仅保留UI展示）
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 产品列表
    const productList = ref([])
    
    // 获取产品列表
    const fetchProducts = async () => {
      try {
        loading.value = true
        
        // 根据API文档，getAllProducts接口不支持搜索和分页参数
        const response = await productApi.getAllProducts()
        productList.value = response || []
        pagination.total = response?.length || 0
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '获取产品列表失败'
        ElMessage.error(errorMessage)
        console.error('获取产品列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 分页大小变化（仅UI展示）
    const handleSizeChange = (size) => {
      pagination.pageSize = size
    }
    
    // 当前页码变化（仅UI展示）
    const handleCurrentChange = (current) => {
      pagination.currentPage = current
    }
    
    // 选择项变化
    const handleSelectionChange = (selection) => {
      selectedProducts.value = selection
    }
    
    // 添加产品
    const handleAddProduct = () => {
      router.push('/products/add')
    }
    
    // 编辑产品
    const handleEditProduct = (id) => {
      router.push(`/products/edit/${id}`)
    }
    
    // 删除产品
    const handleDeleteProduct = (product) => {
      deleteProduct.value = product
      deleteDialogVisible.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      if (!deleteProduct.value) return
      
      try {
        deleting.value = true
        await productApi.deleteProduct(deleteProduct.value.id)
        ElMessage.success('产品删除成功')
        deleteDialogVisible.value = false
        fetchProducts()
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '产品删除失败'
        ElMessage.error(errorMessage)
        console.error('删除产品失败:', error)
      } finally {
        deleting.value = false
      }
    }
    
    // 切换产品状态
    const handleToggleStatus = async (product) => {
      try {
        const newStatus = product.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
        // 根据API文档，updateProduct接口需要完整的产品信息
        const updatedProduct = {
          ...product,
          status: newStatus
        }
        await productApi.updateProduct(updatedProduct)
        ElMessage.success('产品状态更新成功')
        fetchProducts()
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '产品状态更新失败'
        ElMessage.error(errorMessage)
        console.error('更新产品状态失败:', error)
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchProducts()
    })
    
    return {
      loading,
      deleting,
      deleteDialogVisible,
      deleteProduct,
      selectedProducts,
      pagination,
      productList,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleAddProduct,
      handleEditProduct,
      handleDeleteProduct,
      confirmDelete,
      handleToggleStatus
    }
  }
}
</script>

<style scoped>
.product-list-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.product-name-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}

.price {
  color: #ff4d4f;
  font-weight: bold;
}

.description {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  word-break: break-all;
}
</style>