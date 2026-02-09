<template>
  <el-container class="layout-container">
    <!-- 顶部导航 -->
    <el-header class="top-header">
      <div class="brand" @click="router.push('/')">
        <div class="brand-logo">
          <img class="brand-logo-img" :src="brandLogo" alt="品牌Logo" />
        </div>
        <div class="brand-text">
          <div class="brand-title">毛孩子的星球</div>
          <div class="brand-subtitle">宠物健康档案</div>
        </div>
      </div>

      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        class="nav-menu"
        background-color="transparent"
        text-color="#ffffff"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/pets">宠物档案</el-menu-item>
        <el-menu-item index="/medical-records">健康记录</el-menu-item>
        <el-menu-item index="/appointments">预约</el-menu-item>
        <el-menu-item index="/reminders">提醒与日历</el-menu-item>
        <el-menu-item index="/plans">饮食/运动计划</el-menu-item>
        <el-menu-item index="/finance">费用/保险</el-menu-item>
        <el-input
          class="nav-search"
          size="small"
          placeholder="搜索宠物/预约/记录"
          prefix-icon="Search"
          clearable
        />
      </el-menu>
      <div class="user-actions">
        <el-dropdown ref="userDropdown">
          <span class="user-trigger">
            {{ userDisplayName }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <el-dropdown-item v-if="isAdmin" @click="handleAdmin()">管理中心</el-dropdown-item>
              <el-dropdown-item v-if="isVeterinarian" @click="handleMedicalOps">
                医疗操作中心
              </el-dropdown-item>
              <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
              <el-dropdown-item @click="goFeedback">意见反馈</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 内容区域 -->
    <el-main :class="['main-area', { 'main-area-narrow': isNarrowPage }]">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import brandLogo from '@/assets/images/logo.png'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const userDropdown = ref(null)
const userDisplayName = computed(() => {
  const info = authStore.userInfo
  return info?.username || authStore.username || info?.nickname || '用户'
})

const isAdmin = computed(() => {
  const role = (authStore.userRole || '').toLowerCase()
  return role.includes('管理员') || role === 'admin' || role === 'administrator'
})

const normalizeRole = (role) => (role || '').toString().toLowerCase().trim()
const isVetRole = (roleText) => {
  const normalized = normalizeRole(roleText)
  const keywords = ['兽医', '医生', '医师', 'veterinarian', 'doctor', 'clinic', 'medical']
  if (keywords.some((kw) => normalized.includes(kw))) return true
  return ['vet', 'dr'].includes(normalized)
}

const isVeterinarian = computed(() => {
  const info = authStore.userInfo
  const roleSources = [
    authStore.userRole,
    info?.roleName,
    info?.role,
    localStorage.getItem('userRole')
  ]
  if (Array.isArray(info?.roles)) {
    roleSources.push(
      ...info.roles.map((item) => (typeof item === 'string' ? item : item?.name || ''))
    )
  }
  const mergedText = roleSources.filter(Boolean).map(normalizeRole).join(' ')
  return isVetRole(mergedText) || roleSources.some((role) => isVetRole(role))
})

// 页面加载时获取用户信息
onMounted(() => {
  if (authStore.isAuthenticated && !authStore.userInfo) {
    authStore.fetchUserInfo()
  }
})

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/pets')) return '/pets'
  if (path.startsWith('/medical-records')) return '/medical-records'
  if (path.startsWith('/appointments')) return '/appointments'
  if (path.startsWith('/reminders')) return '/reminders'
  if (path.startsWith('/plans')) return '/plans'
  if (path.startsWith('/finance')) return '/finance'
  return '/'
})

const isNarrowPage = computed(() => {
  const path = route.path
  return (
    path.startsWith('/pets') ||
    path.startsWith('/medical-records') ||
    path.startsWith('/appointments') ||
    path.startsWith('/reminders') ||
    path.startsWith('/plans') ||
    path.startsWith('/finance')
  )
})

const closeUserDropdown = () => {
  userDropdown.value?.handleClose?.()
}

// 跳转到个人信息页
const handleProfile = () => {
  closeUserDropdown()
  router.push('/profile')
}

const handleAdmin = () => {
  router.push({ path: '/admin' })
  closeUserDropdown()
}

const handleMedicalOps = () => {
  router.push({ path: '/medical-ops' })
  closeUserDropdown()
}

const goFeedback = () => {
  closeUserDropdown()
  router.push('/feedback')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    authStore.logout()
    router.push('/login')
    closeUserDropdown()
  })
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.top-header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #409eff;
  border-bottom: 4px solid #409eff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  height: 96px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #ffffff;
  cursor: pointer;
  margin-left: 182px;
  margin-right: 28px;
}

.brand-logo {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #ffffff;
  border: 1px solid #d8e3f3;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  padding: 4px;
  overflow: hidden;
}

.brand-logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
  display: block;
  transform: translateY(4px) scale(2.0);
  transform-origin: center;
}

.brand-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  line-height: 1.2;
  gap: 6px;
}

.brand-title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.8px;
  text-transform: none;
  font-family: "SF Pro Display", "PingFang SC", "Inter", "Microsoft YaHei", sans-serif;
  color: #ffffff;
}

.brand-subtitle {
  font-size: 12px;
  color: #e7f1ff;
  font-weight: 600;
  letter-spacing: 0.4px;
  font-family: "Inter", "PingFang SC", "Microsoft YaHei", sans-serif;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
  height: 96px;
  line-height: 96px;
  display: flex;
  justify-content: flex-start;
  /* 28px brand margin-right + 122px = 150px spacing to the system name */
  margin-left: 122px;
  padding-right: 8px;
  transform: translateX(0);
  --el-menu-active-color: #1f2d3d;
  --el-menu-hover-bg-color: transparent;
  --el-menu-item-padding: 0 18px;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  font-weight: 600;
  font-size: 16px;
  color: #ffffff;
  transition: color 0.2s ease, transform 0.2s ease;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  color: #eaf2ff;
}

.nav-menu :deep(.el-menu-item.is-active),
.nav-menu :deep(.is-opened > .el-sub-menu__title) {
  color: #ffffff;
  position: relative;
}

.nav-menu :deep(.el-menu-item.is-active::after),
.nav-menu :deep(.is-opened > .el-sub-menu__title::after) {
  content: '';
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 12px;
  height: 3px;
  border-radius: 999px;
  background: linear-gradient(90deg, var(--brand), #3b82f6);
}

.user-actions {
  display: flex;
  align-items: center;
  margin-left: 12px;
  gap: 12px;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.user-dropdown-menu {
  width: 240px;
  padding-bottom: 6px;
}

.nav-search :deep(.el-input__wrapper) {
  border-radius: 14px;
  background: linear-gradient(135deg, #ffffff 0%, #f2f6fb 100%);
  border: 1px solid #d8e3f3;
  box-shadow: none !important;
  height: 36px;
  padding: 0 12px;
  transition: background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.nav-search :deep(.el-input__wrapper:hover) {
  background: #fff;
  border-color: #b7c8e5;
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.08);
}

.nav-search :deep(.is-focus),
.nav-search :deep(.is-focus .el-input__wrapper) {
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.12) !important;
  border-color: #9ab6e8;
  background: #fff;
}

.nav-search :deep(.el-input__inner) {
  color: #1f2d3d;
  line-height: 34px;
}

.nav-search {
  width: 280px;
  margin-left: 20px;
  margin-right: 16px;
  align-self: center;
  --el-input-height: 36px;
}

.main-area {
  flex: 1;
  min-height: 0;
  padding: 20px 24px 24px;
  overflow-y: auto;
}

.main-area-narrow {
  width: 100%;
  max-width: 80%;
  margin: 0 auto;
  align-self: center;
}
</style>
