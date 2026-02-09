<template>
  <div class="reset-container">
    <el-card class="reset-card">
      <template #header>
        <div class="reset-header">
          <h2>重置密码</h2>
          <p>通过邮箱验证码重置密码</p>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="reset-form"
        @submit.prevent="handleReset"
      >
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入注册邮箱"
            prefix-icon="Message"
            size="large"
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item prop="code">
          <el-row :gutter="10">
            <el-col :span="14">
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                prefix-icon="Ticket"
                size="large"
                autocomplete="off"
              />
            </el-col>
            <el-col :span="10">
              <el-button
                size="large"
                :disabled="codeCountdown > 0 || sending"
                @click="handleSendCode"
              >
                {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            prefix-icon="Lock"
            size="large"
            show-password
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="再次输入新密码"
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
            class="reset-btn"
            :loading="loading"
            @click="handleReset"
          >
            重置密码
          </el-button>
          <el-button
            type="default"
            size="large"
            class="login-btn"
            @click="goLogin"
          >
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendResetCode, resetPassword } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const sending = ref(false)
const codeCountdown = ref(0)
let timer = null

const form = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入注册邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const startCountdown = () => {
  codeCountdown.value = 60
  timer = setInterval(() => {
    codeCountdown.value -= 1
    if (codeCountdown.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

const handleSendCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先输入注册邮箱')
    return
  }
  sending.value = true
  try {
    await sendResetCode({ email: form.email })
    ElMessage.success('验证码已发送，请检查邮箱')
    startCountdown()
  } catch (error) {
    ElMessage.error(error?.message || '发送失败，请稍后再试')
  } finally {
    sending.value = false
  }
}

const handleReset = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    await resetPassword({
      email: form.email,
      code: form.code,
      newPassword: form.newPassword
    })
    ElMessage.success('密码重置成功，请使用新密码登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error?.message || '重置失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.reset-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.reset-card {
  width: 440px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.reset-header {
  text-align: center;
}

.reset-header h2 {
  margin-bottom: 8px;
  color: #1989fa;
}

.reset-form {
  padding: 20px 0;
}

.reset-btn,
.login-btn {
  width: 100%;
}

.login-btn {
  margin-top: 10px;
}
</style>
