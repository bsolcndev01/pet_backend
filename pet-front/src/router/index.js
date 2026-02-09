import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

// 导入页面组件
const Login = () => import('@/views/Login.vue')
const Home = () => import('@/views/Home.vue')
const MainLayout = () => import('@/layouts/MainLayout.vue')
const NotFound = () => import('@/views/404.vue')
const Forbidden = () => import('@/views/403.vue')

// 路由规则
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/ResetPassword.vue'),
    meta: { requiresAuth: false, title: '重置密码' }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: Forbidden,
    meta: { requiresAuth: false, title: '权限不足' }
  },
  {
    path: '/',
    name: 'MainLayout',
    component: MainLayout,
    meta: { requiresAuth: true, role: ['宠物主人', '管理员', '系统管理员'], title: '宠物健康档案管理系统' },
    children: [
      { path: '', name: 'Home', component: Home, meta: { title: '首页' } },
      // 宠物管理
      { path: 'pets', name: 'PetList', component: () => import('@/views/pet/PetList.vue'), meta: { title: '我的宠物' } },
      { path: 'pets/add', name: 'AddPet', component: () => import('@/views/pet/AddPet.vue'), meta: { title: '添加宠物' } },
      { path: 'pets/:id', name: 'PetDetail', component: () => import('@/views/pet/PetDetail.vue'), meta: { title: '宠物详情' } },
      { path: 'pets/:id/edit', name: 'EditPet', component: () => import('@/views/pet/EditPet.vue'), meta: { title: '编辑宠物' } },
      // 医疗/健康记录
      { path: 'medical-records', name: 'HealthRecords', component: () => import('@/views/health/HealthRecords.vue'), meta: { title: '健康记录' } },
      // 医疗管理中心（单独的医疗记录模块）
      { path: 'medical', name: 'MedicalRecordList', component: () => import('@/views/medical/MedicalRecordList.vue'), meta: { title: '医疗记录列表' } },
      { path: 'medical/add', name: 'AddMedicalRecordPage', component: () => import('@/views/medical/AddMedicalRecord.vue'), meta: { title: '添加医疗记录' } },
      { path: 'medical/:recordId', name: 'MedicalRecordDetailPage', component: () => import('@/views/medical/MedicalRecordDetail.vue'), meta: { title: '医疗记录详情' } },
      { path: 'medical/:recordId/edit', name: 'EditMedicalRecordPage', component: () => import('@/views/medical/EditMedicalRecord.vue'), meta: { title: '编辑医疗记录' } },
      // 疫苗接种
      { path: 'vaccinations', name: 'VaccinationList', component: () => import('@/views/vaccine/VaccinationList.vue'), meta: { title: '疫苗接种记录' } },
      // 驱虫记录
      { path: 'deworming', name: 'DewormingList', component: () => import('@/views/deworming/DewormingList.vue'), meta: { title: '驱虫记录' } },
      // 预约管理
      { path: 'appointments', name: 'AppointmentList', component: () => import('@/views/appointment/AppointmentList.vue'), meta: { title: '我的预约' } },
      { path: 'appointments/add', name: 'AddAppointment', component: () => import('@/views/appointment/AddAppointment.vue'), meta: { title: '新增预约' } },
      // 提醒管理
      { path: 'reminders', name: 'ReminderList', component: () => import('@/views/reminder/ReminderList.vue'), meta: { title: '健康提醒' } },
      // 饮食/运动计划
      { path: 'plans', name: 'PlanList', component: () => import('@/views/plan/Plans.vue'), meta: { title: '饮食与运动计划' } },
      // 费用/保险
      { path: 'finance', name: 'Finance', component: () => import('@/views/finance/Finance.vue'), meta: { title: '费用与保险' } },
      // 意见反馈
      { path: 'feedback', name: 'Feedback', component: () => import('@/views/Feedback.vue'), meta: { title: '意见反馈' } },
      // 设置
      { path: 'settings', name: 'Settings', component: () => import('@/views/Settings.vue'), meta: { title: '设置' } },
      // 用户信息
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/user/Profile.vue'), meta: { title: '个人信息' } },
    ]
  },
  // 管理中心（独立布局，无前台导航）
  {
    path: '/admin',
    name: 'AdminCenter',
    component: () => import('@/views/admin/AdminCenter.vue'),
    meta: { requiresAuth: true, role: ['管理员', '系统管理员'], title: '管理中心' }
  },
  // 兽医操作中心（独立布局）
  {
    path: '/medical-ops',
    name: 'MedicalOpsCenter',
    component: () => import('@/views/medical/MedicalOpsCenter.vue'),
    meta: { requiresAuth: true, role: '兽医', title: '医疗操作中心' }
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { requiresAuth: false, title: '页面不存在' }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
const normalizeRole = (role) => (role || '').toString().trim()
const isRoleAllowed = (requiredRole, userRole) => {
  if (!requiredRole) return true
  const normalizedUserRole = normalizeRole(userRole)
  if (Array.isArray(requiredRole)) {
    return requiredRole.map(normalizeRole).includes(normalizedUserRole)
  }
  return normalizeRole(requiredRole) === normalizedUserRole
}

router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  const authStore = useAuthStore()
  // 刷新后尝试恢复会话
  if (localStorage.getItem('token') && (!authStore.isAuthenticated || !authStore.userInfo)) {
    await authStore.restoreSession?.()
  }
  
  // 需要登录验证
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated || authStore.isSessionExpired?.()) {
      await authStore.logout?.()
      // 未登录，跳转到登录页
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
      // 角色匹配直接进入，否则提示
      if (to.meta.role && !isRoleAllowed(to.meta.role, authStore.userRole)) {
        // 兽医自动跳转医疗操作中心
        if (authStore.userRole === '兽医' && to.path !== '/medical-ops') {
          ElMessage.info('已为兽医账号跳转到医疗操作中心')
          next('/medical-ops')
        } else {
          next('/403')
        }
      } else {
        next()
      }
    }
  } else {
    // 不需要登录验证
    next()
  }
})

export default router
