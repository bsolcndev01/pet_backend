<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>欢迎登录毛孩子的星球</h2>
          <p>开启你的宠物健康旅程</p>
        </div>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            autocomplete="off"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            autocomplete="off"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
          <el-button
            type="default"
            size="large"
            class="register-btn"
            @click="handleRegister"
          >
            注册
          </el-button>
          <el-button
            type="text"
            size="large"
            class="forgot-btn"
            @click="handleForgot"
          >
            忘记密码？
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 登录表单
const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

/**
 * 处理登录逻辑
 */
const handleLogin = async () => {
  // 表单验证
  try {
    await loginFormRef.value.validate()
  } catch (error) {
    return
  }

  loading.value = true
  
  try {
    const success = await authStore.login(loginForm.username, loginForm.password)
    if (success) {
      ElMessage.success('登录成功')
      // 跳转到之前的页面或首页
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    ElMessage.error('登录失败，请稍后重试')
    console.error('登录异常:', error)
  } finally {
    loading.value = false
  }
}

// 注册按钮（当前未实现注册，提示联系管理员或跳转到自定义注册页）
const handleRegister = () => {
  router.push('/register')
}

const handleForgot = () => {
  router.push('/reset-password')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.35)),
    url('@/assets/images/petBG.png');
  background-repeat: no-repeat, no-repeat;
  background-position: center, center;
  background-size: cover, contain;
}

.login-card {
  width: 400px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 12px;
}

.login-header {
  text-align: center;
}

.login-header h2 {
  margin-bottom: 8px;
}

.login-header p {
  margin: 0;
  color: #999;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
}

.register-btn {
  width: 100%;
  margin-top: 10px;
}

.forgot-btn {
  width: 100%;
  margin-top: 10px;
  text-align: right;
}
</style>
