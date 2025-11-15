<template>
  <div class="product-add-container">
    <div class="page-header">
      <h1>添加产品</h1>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card shadow="hover">
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="rules"
        label-width="120px"
        class="product-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="productForm.name" placeholder="请输入产品名称" />
            </el-form-item>

            <el-form-item label="产品类别" prop="category">
              <el-select v-model="productForm.category" placeholder="请选择产品类别">
                <el-option label="手机数码" value="手机数码" />
                <el-option label="电脑办公" value="电脑办公" />
                <el-option label="家用电器" value="家用电器" />
                <el-option label="服装鞋包" value="服装鞋包" />
                <el-option label="食品生鲜" value="食品生鲜" />
              </el-select>
            </el-form-item>

            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="productForm.price"
                :min="0"
                :precision="2"
                :step="0.01"
                placeholder="请输入价格"
                style="width: 100%"
              >
                <template #prefix>¥</template>
              </el-input-number>
            </el-form-item>

            <el-form-item label="库存" prop="stock">
              <el-input-number
                v-model="productForm.stock"
                :min="0"
                :step="1"
                placeholder="请输入库存"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="产品状态" prop="status">
              <el-radio-group v-model="productForm.status">
                <el-radio label="ACTIVE">上架</el-radio>
                <el-radio label="INACTIVE">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="产品图片" prop="imageUrl">
              <el-upload
                class="upload-demo"
                :action="'/upload'"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :show-file-list="false"
                accept="image/*"
              >
                <div v-if="productForm.imageUrl" class="image-preview">
                  <el-image
                    :src="productForm.imageUrl"
                    :preview-src-list="[productForm.imageUrl]"
                  />
                  <el-button type="danger" @click.stop="removeImage">删除图片</el-button>
                </div>
                <div v-else>
                  <el-button size="small" type="primary">点击上传</el-button>
                  <div class="el-upload__tip">
                    请上传 JPG、PNG 格式图片，不超过 2MB
                  </div>
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="6"
            placeholder="请输入产品描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/product.js'

export default {
  name: 'ProductAdd',
  setup() {
    const router = useRouter()
    const productFormRef = ref()
    const submitting = ref(false)
    
    // 产品表单数据
    const productForm = reactive({
      name: '',
      category: '',
      price: null,
      stock: null,
      description: '',
      imageUrl: '',
      status: 'ACTIVE'
    })
    
    // 表单验证规则
    const rules = {
      name: [
        { required: true, message: '请输入产品名称', trigger: 'blur' },
        { min: 1, max: 100, message: '产品名称长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      category: [
        { required: true, message: '请选择产品类别', trigger: 'change' }
      ],
      price: [
        { required: true, message: '请输入价格', trigger: 'blur' },
        { type: 'number', min: 0, message: '价格必须大于等于 0', trigger: 'blur' }
      ],
      stock: [
        { required: true, message: '请输入库存', trigger: 'blur' },
        { type: 'integer', min: 0, message: '库存必须是大于等于 0 的整数', trigger: 'blur' }
      ],
      description: [
        { required: false, message: '请输入产品描述', trigger: 'blur' },
        { max: 500, message: '产品描述不能超过 500 个字符', trigger: 'blur' }
      ]
    }
    
    // 图片上传前处理
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        ElMessage.error('只能上传图片文件！')
        return false
      }
      
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        ElMessage.error('上传图片大小不能超过 2MB！')
        return false
      }
      
      return true
    }
    
    // 图片上传成功处理
    const handleUploadSuccess = (response) => {
      if (response && response.url) {
        productForm.imageUrl = response.url
      } else {
        ElMessage.error('图片上传失败')
      }
    }
    
    // 图片上传失败处理
    const handleUploadError = () => {
      ElMessage.error('图片上传失败')
    }
    
    // 移除图片
    const removeImage = () => {
      productForm.imageUrl = ''
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!productFormRef.value) return
      
      try {
        await productFormRef.value.validate()
        submitting.value = true
        
        // 创建产品数据
        const productData = {
          ...productForm
        }
        
        await productApi.createProduct(productData)
        ElMessage.success('产品创建成功')
        router.push('/products/list')
      } catch (error) {
        if (error.name !== 'Error') {
          // 表单验证失败
          return
        }
        // 更详细的错误处理
        const errorMessage = error.response?.data?.message || error.message || '产品创建失败'
        ElMessage.error(errorMessage)
        console.error('创建产品失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 重置表单
    const handleReset = () => {
      if (productFormRef.value) {
        productFormRef.value.resetFields()
        productForm.imageUrl = ''
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }
    
    return {
      productFormRef,
      productForm,
      rules,
      submitting,
      beforeUpload,
      handleUploadSuccess,
      handleUploadError,
      removeImage,
      handleSubmit,
      handleReset,
      goBack
    }
  }
}
</script>

<style scoped>
.product-add-container {
  padding: 20px;
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

.product-form {
  max-width: 100%;
}

.image-preview {
  position: relative;
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 10px;
}

.image-preview .el-image {
  width: 150px;
  height: 150px;
}
</style>