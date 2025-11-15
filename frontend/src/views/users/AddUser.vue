<template>
  <div class="add-user-container">
    <div class="page-header">
      <h1>添加用户</h1>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-form
      ref="userFormRef"
      :model="userForm"
      :rules="rules"
      label-width="120px"
      class="user-form"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userForm.username" placeholder="请输入用户名" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="userForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="userForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
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

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user.js'

export default {
  name: 'AddUser',
  setup() {
    const router = useRouter()
    const userFormRef = ref()
    const submitting = ref(false)
    
    // 用户表单数据
    const userForm = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      phone: '',
      fullName: ''
    })
    
    // 表单验证规则
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
        { 
          pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{6,20}$/, 
          message: '密码必须包含字母和数字', 
          trigger: 'blur' 
        }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== userForm.password) {
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
    
    // 提交表单
    const handleSubmit = async () => {
      if (!userFormRef.value) return
      
      try {
        await userFormRef.value.validate()
        submitting.value = true
        
        // 创建用户（不包含确认密码字段）
        const userData = {
          username: userForm.username,
          password: userForm.password,
          email: userForm.email,
          phone: userForm.phone,
          fullName: userForm.fullName
        }
        
        await userApi.createUser(userData)
        ElMessage.success('用户创建成功')
        router.push('/users/list')
      } catch (error) {
        if (error.name !== 'Error') {
          // 表单验证失败
          return
        }
        // 更详细的错误处理
        const errorMessage = error.response?.data?.message || error.message || '用户创建失败'
        ElMessage.error(errorMessage)
        console.error('创建用户失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 重置表单
    const handleReset = () => {
      if (userFormRef.value) {
        userFormRef.value.resetFields()
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }
    
    return {
      userFormRef,
      userForm,
      rules,
      submitting,
      handleSubmit,
      handleReset,
      goBack
    }
  }
}
</script>

<style scoped>
.add-user-container {
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