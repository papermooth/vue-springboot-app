<template>
  <div class="user-edit-container">
    <div class="page-header">
      <h1>编辑用户</h1>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-form
      v-if="userForm.id"
      ref="userFormRef"
      :model="userForm"
      :rules="rules"
      label-width="120px"
      class="user-form"
    >
      <el-form-item label="用户名">
        <el-input v-model="userForm.username" disabled placeholder="用户名不可修改" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="userForm.password"
          type="password"
          placeholder="请输入新密码（留空表示不修改）"
          show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="userForm.confirmPassword"
          type="password"
          placeholder="请确认新密码（留空表示不修改）"
          show-password
        />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userForm.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="userForm.phone" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item label="姓名" prop="fullName">
        <el-input v-model="userForm.fullName" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-switch
          v-model="userForm.status"
          active-value="ACTIVE"
          inactive-value="INACTIVE"
          active-text="启用"
          inactive-text="禁用"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">更新</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-else :description="loading ? '加载中...' : '用户不存在'" />
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user.js'

export default {
  name: 'UserEdit',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const userFormRef = ref()
    const submitting = ref(false)
    const loading = ref(false)
    
    // 用户表单数据
    const userForm = reactive({
      id: null,
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      phone: '',
      fullName: '',
      status: 'ACTIVE'
    })
    
    // 表单验证规则
    const rules = {
      password: [
        {
          pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{6,20}$/,
          message: '密码必须包含字母和数字，长度6-20位',
          trigger: 'blur',
          required: false
        }
      ],
      confirmPassword: [
        {
          validator: (rule, value, callback) => {
            if (userForm.password && value !== userForm.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { required: false, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
      ],
      fullName: [
        { required: false, message: '请输入姓名', trigger: 'blur' },
        { max: 50, message: '姓名长度不能超过 50 个字符', trigger: 'blur' }
      ]
    }
    
    // 获取用户详情
    const fetchUserDetails = async (userId) => {
      if (!userId) return
      
      try {
        loading.value = true
        const userData = await userApi.getUserById(userId)
        
        // 填充表单数据，但不包含密码
        userForm.id = userData.id
        userForm.username = userData.username
        userForm.email = userData.email
        userForm.phone = userData.phone || ''
        userForm.fullName = userData.fullName || ''
        userForm.status = userData.status || 'ACTIVE'
      } catch (error) {
        ElMessage.error('获取用户信息失败')
        console.error('获取用户信息失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!userFormRef.value || !userForm.id) return
      
      try {
        await userFormRef.value.validate()
        submitting.value = true
        
        // 准备更新数据，只更新需要的字段
        const updateData = {
          id: userForm.id,
          email: userForm.email,
          phone: userForm.phone,
          fullName: userForm.fullName,
          status: userForm.status
        }
        
        // 如果提供了密码，则更新密码
        if (userForm.password) {
          updateData.password = userForm.password
        }
        
        await userApi.updateUser(updateData)
        ElMessage.success('用户更新成功')
        router.push('/users/list')
      } catch (error) {
        if (error.name !== 'Error') {
          // 表单验证失败
          return
        }
        ElMessage.error('用户更新失败')
        console.error('更新用户失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 重置表单
    const handleReset = () => {
      if (userFormRef.value) {
        userForm.password = ''
        userForm.confirmPassword = ''
        userFormRef.value.clearValidate()
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }
    
    // 监听路由参数变化
    watch(() => route.params.id, (newId) => {
      if (newId) {
        fetchUserDetails(newId)
      }
    }, { immediate: true })
    
    return {
      userFormRef,
      userForm,
      rules,
      submitting,
      loading,
      handleSubmit,
      handleReset,
      goBack
    }
  }
}
</script>

<style scoped>
.user-edit-container {
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

.user-form {
  max-width: 600px;
}
</style>