<!-- src/views/user/Profile.vue（个人信息页） -->
<template>
  <div class="profile-container">
    <el-page-header @back="handleBack" content="个人信息" />
    
    <el-card class="mt-4" v-loading="loadingProfile">
      <div class="profile-avatar-block">
        <div class="avatar-container">
          <el-avatar :size="150" :src="profileForm.avatarUrl || userInfo.avatarUrl || defaultAvatar">
            <UserFilled />
          </el-avatar>
          <el-upload
            class="mt-2"
            :show-file-list="false"
            accept="image/*"
            :auto-upload="false"
            :before-upload="beforeAvatarUpload"
            :on-change="handleAvatarChange"
          >
            <el-button type="text" :loading="avatarUploading">更换头像</el-button>
          </el-upload>
        </div>
      </div>

      <!-- 个人信息表单 -->
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="address">
              <el-input v-model="profileForm.address" placeholder="请输入联系地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="showPwdDialog = true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="480px">
      <el-form
        ref="pwdFormRef"
        :model="pwdForm"
        :rules="pwdRules"
        label-width="100px"
        class="pwd-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import defaultAvatar from '@/assets/images/images.webp'
import { useAuthStore } from '@/stores/auth'
import { updateProfile, changePassword } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const profileFormRef = ref(null)
const pwdFormRef = ref(null)
const loadingProfile = ref(false)
const showPwdDialog = ref(false)
const avatarUploading = ref(false)

// 用户信息
const userInfo = computed(() => authStore.userInfo || {})

// 个人信息表单
const profileForm = reactive({
  username: '',
  phone: '',
  email: '',
  address: '',
  avatarUrl: ''
})

// 修改密码表单
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 个人信息验证规则
const profileRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
})

// 密码验证规则
const pwdRules = reactive({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 返回上一页
const handleBack = () => {
  router.back()
}

// 图片转 base64
const fileToBase64 = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = (error) => reject(error)
  })

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('请上传图片文件')
  if (!isLt2M) ElMessage.error('图片大小不能超过 2MB')
  return isImage && isLt2M
}

const handleAvatarChange = async (uploadFile) => {
  if (!uploadFile?.raw) return
  try {
    avatarUploading.value = true
    const base64 = await fileToBase64(uploadFile.raw)
    await updateProfile({ avatarUrl: base64 })
    profileForm.avatarUrl = base64
    await authStore.fetchUserInfo()
    ElMessage.success('头像已更新')
  } catch (error) {
    console.error('更新头像失败', error)
    ElMessage.error(error?.message || '更新头像失败')
  } finally {
    avatarUploading.value = false
  }
}

const syncProfileForm = () => {
  Object.assign(profileForm, {
    username: userInfo.value.username || '',
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || '',
    address: userInfo.value.address || '',
    avatarUrl: userInfo.value.avatarUrl || ''
  })
}

// 加载个人信息
const loadProfile = async () => {
  try {
    loadingProfile.value = true
    if (!authStore.userInfo) {
      await authStore.fetchUserInfo()
    }
    syncProfileForm()
  } catch (error) {
    console.error('加载个人信息失败', error)
    ElMessage.error('加载个人信息失败')
  } finally {
    loadingProfile.value = false
  }
}

// 保存个人信息
const handleSave = async () => {
  try {
    await profileFormRef.value.validate()
    await updateProfile({
      phone: profileForm.phone,
      email: profileForm.email,
      address: profileForm.address,
      avatarUrl: profileForm.avatarUrl
    })
    ElMessage.success('个人信息修改成功')
    await authStore.fetchUserInfo()
    syncProfileForm()
  } catch (error) {
    console.error('保存个人信息失败', error)
    ElMessage.error(error?.message || '保存失败，请检查表单填写')
  }
}

// 重置表单
const handleReset = () => {
  profileFormRef.value?.resetFields()
  syncProfileForm()
}

// 修改密码
const handleChangePwd = async () => {
  try {
    await pwdFormRef.value.validate()
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessageBox.confirm(
      '密码修改成功，是否重新登录？',
      '提示',
      {
        type: 'warning'
      }
    ).then(() => {
      authStore.logout()
      router.push('/login')
    })
    showPwdDialog.value = false
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch (error) {
    console.error('修改密码失败', error)
    ElMessage.error(error?.message || '修改密码失败，请检查表单填写')
  }
}

// 页面加载时初始化
onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 80%;
  margin: 0 auto;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-container :deep(.el-upload) {
  width: auto;
}

.profile-avatar-block {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.mt-2 {
  margin-top: 10px;
}

.mt-4 {
  margin-top: 20px;
}

.pwd-form {
  max-width: 600px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pwd-tip {
  color: #666;
  font-size: 13px;
}
</style>
