<template>
  <div class="ops-layout">
    <aside class="ops-sider">
      <div class="sider-brand" @click="router.push('/')">
        <div class="brand-logo">
          <img class="brand-logo-img" :src="brandLogo" alt="logo" />
        </div>
        <div class="brand-text">
          <div class="brand-title">毛孩子的星球</div>
          <div class="brand-sub">医疗操作中心</div>
        </div>
      </div>
      <div class="sider-title">医疗操作中心</div>
      <div class="sider-sub">为兽医准备的诊疗工作台</div>
      <div class="sider-menu-wrapper">
        <el-menu
          class="sider-menu"
          :default-active="activeMenu"
          @select="handleMenuSelect"
          background-color="transparent"
          text-color="#e5eaf5"
          active-text-color="#ffffff"
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.key"
            :index="item.key"
          >
            <el-icon v-if="item.icon" class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            <div class="menu-text">
              <span class="menu-label">{{ item.label }}</span>
            <span class="menu-desc">{{ item.desc }}</span>
          </div>
        </el-menu-item>
      </el-menu>
      </div>
      <div class="account-block">
        <div class="account-label">当前账号</div>
        <div class="account-name">{{ veterinarianName }}</div>
      </div>
      <el-button class="back-home" type="primary" plain @click="handleLogout">
        退出登录
      </el-button>
    </aside>

    <div class="ops-main">
      <section v-if="activeMenu === 'overview'" class="section-block">
        <el-row :gutter="12" class="stat-row">
          <el-col :md="6" :span="12">
            <div class="stat-card">
              <div class="stat-label">待诊</div>
              <div class="stat-value">{{ workloadStats.pendingCount }}</div>
            </div>
          </el-col>
          <el-col :md="6" :span="12">
            <div class="stat-card">
              <div class="stat-label">已确认</div>
              <div class="stat-value">{{ workloadStats.confirmedCount }}</div>
            </div>
          </el-col>
          <el-col :md="6" :span="12">
            <div class="stat-card">
              <div class="stat-label">已完成</div>
              <div class="stat-value">{{ workloadStats.completedCount }}</div>
            </div>
          </el-col>
          <el-col :md="6" :span="12">
            <div class="stat-card">
              <div class="stat-label">已取消</div>
              <div class="stat-value">{{ workloadStats.cancelledCount }}</div>
            </div>
          </el-col>
        </el-row>

        <div class="ops-hero">
        <div class="hero-text">
          <p class="eyebrow">Veterinary Workspace</p>
          <h1 class="hero-title">医疗操作中心</h1>
          <p class="hero-sub">
            你好，{{ veterinarianName }}，这里汇总了诊疗高频动作，便于快速进入预约、病历与提醒流。
          </p>
          <div class="hero-actions">
            <el-button type="primary" @click="goAppointments">预约面板</el-button>
            <el-button plain @click="goMedicalRecords">医疗记录</el-button>
          </div>
        </div>
        <div class="hero-meta">
          <el-tag type="primary" effect="dark">角色：兽医</el-tag>
        </div>
      </div>

        <el-card class="ops-card quick-card" shadow="hover">
          <div class="card-header">
            <div>
            <div class="card-title">快捷操作</div>
              <div class="card-sub">常用诊疗动作的入口</div>
            </div>
          </div>
          <el-row :gutter="12">
            <el-col
              v-for="item in quickActions"
              :key="item.title"
              :sm="12"
              :md="6"
              :span="6"
            >
              <div class="quick-item" @click="item.action()">
                <div class="quick-name">{{ item.title }}</div>
                <div class="quick-desc">{{ item.desc }}</div>
                <el-button :type="item.type || 'primary'" text size="small">立即前往</el-button>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </section>

      <section v-else-if="activeMenu === 'medications'" class="section-block">
        <el-card shadow="hover" class="ops-card">
          <div class="card-header">
            <div>
              <div class="card-title">用药记录</div>
              <div class="card-sub">由当前兽医开立/维护的用药方案</div>
            </div>
          </div>
          <el-skeleton :loading="medicationLoading" animated :count="3">
            <template #template>
              <el-skeleton-item variant="text" style="width: 60%" />
              <el-skeleton-item variant="text" style="width: 40%" />
            </template>
            <template #default>
              <el-empty v-if="!medicationRecords.length" description="暂无用药记录" />
              <el-table
                v-else
                :data="medicationRecords"
                size="small"
                border
                style="width: 100%"
              >
                <el-table-column prop="petName" label="宠物" width="120" />
                <el-table-column prop="drugName" label="药品" />
                <el-table-column prop="dosage" label="剂量" width="120" />
                <el-table-column prop="frequency" label="频次" width="120" />
                <el-table-column prop="startDate" label="开始日期" width="120" />
                <el-table-column prop="endDate" label="结束日期" width="120" />
                <el-table-column prop="status" label="状态" width="120" />
              </el-table>
            </template>
          </el-skeleton>
        </el-card>
      </section>

      <section v-else-if="activeMenu === 'treat'" class="section-block">
        <TreatWorkbench
          :pending-list="todayPending"
          :confirmed-list="todayConfirmed"
          :loading="appointmentLoading"
          :status-type="statusType"
          :status-text="statusText"
        />
      </section>

      <section v-else-if="activeMenu === 'appointments'" class="section-block">
        <AppointmentManager
          :appointments="vetAppointments"
          :loading="appointmentLoading"
          :pet-options="petOptions"
          :institution-name="institutionName"
          :status-type="statusType"
          :status-text="statusText"
          :on-create="handleCreateAppointment"
          :on-go-list="goAppointments"
          :on-confirm="handleConfirmAppointment"
          :on-cancel="handleCancelAppointment"
        />
      </section>

      <section v-else-if="activeMenu === 'records'" class="section-block">
        <el-card shadow="hover" class="ops-card">
          <div class="card-header">
            <div>
              <div class="card-title">诊疗记录</div>
              <div class="card-sub">最近病历与诊疗方案</div>
            </div>
          </div>
          <el-skeleton :loading="recordLoading" animated :count="3">
            <template #template>
              <el-skeleton-item variant="text" style="width: 50%" />
              <el-skeleton-item variant="text" style="width: 70%" />
            </template>
            <template #default>
              <el-empty v-if="!recentRecords.length" description="暂无医疗记录" />
              <el-table
                v-else
                :data="recentRecords"
                size="small"
                border
                style="width: 100%"
              >
                <el-table-column prop="petName" label="宠物" width="120" />
                <el-table-column prop="diagnosis" label="诊断" />
                <el-table-column prop="vetName" label="医生" width="120">
                  <template #default="scope">
                    {{ scope.row.vetName || scope.row.vetUserId || '本账号' }}
                  </template>
                </el-table-column>
                <el-table-column prop="visitDate" label="就诊日期" width="130" />
              </el-table>
            </template>
          </el-skeleton>
        </el-card>
      </section>

      <section v-else-if="activeMenu === 'reminders'" class="section-block">
        <el-card shadow="hover" class="ops-card">
          <div class="card-header">
            <div>
              <div class="card-title">随访与提醒</div>
              <div class="card-sub">复诊、用药、疫苗的跟进提醒</div>
            </div>
            <el-button text type="primary" @click="router.push('/reminders')">管理提醒</el-button>
          </div>
          <el-skeleton :loading="reminderLoading" animated :count="3">
            <template #template>
              <el-skeleton-item variant="text" style="width: 60%" />
              <el-skeleton-item variant="text" style="width: 40%" />
            </template>
            <template #default>
              <el-empty v-if="!reminders.length" description="暂无提醒" />
              <el-timeline v-else>
                <el-timeline-item
                  v-for="item in reminders"
                  :key="item.reminderId || item.id"
                  :timestamp="formatReminderDate(item)"
                  :type="reminderTagType(item)"
                >
                  <div class="timeline-row">
                    <div>
                      <div class="timeline-title">{{ item.title || item.reminderType || '复诊提醒' }}</div>
                      <div class="timeline-sub">
                        {{ item.petName ? `宠物：${item.petName}` : '全局提醒' }}
                      </div>
                    </div>
                    <el-tag size="small" :type="reminderTagType(item)">
                      {{ item.reminderType || item.type || '提醒' }}
                    </el-tag>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </template>
          </el-skeleton>
        </el-card>
      </section>

      <section v-else-if="activeMenu === 'tools'" class="section-block">
        <el-card shadow="hover" class="ops-card quick-card">
          <div class="card-header">
            <div>
              <div class="card-title">工具与模板</div>
              <div class="card-sub">诊疗模板、接诊设置与偏好</div>
            </div>
          </div>
          <el-row :gutter="12">
            <el-col v-for="tool in toolkits" :key="tool.title" :sm="12" :md="6" :span="6">
              <div class="quick-item tool-item" @click="tool.action()">
                <div class="quick-name">{{ tool.title }}</div>
                <div class="quick-desc">{{ tool.desc }}</div>
                <el-button :type="tool.type || 'primary'" text size="small">打开</el-button>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Monitor, List, Document, Bell, Tools } from '@element-plus/icons-vue'
import {
  getUpcomingAppointments,
  getAppointmentList,
  addAppointment,
  confirmAppointment,
  deleteAppointment
} from '@/api/appointment'
import {
  getDailyStats,
  getMedications,
  getMedicalRecords,
  getOverviewStats,
  getVetInstitution
} from '@/api/health'
import { getUpcomingReminders } from '@/api/reminder'
import { getPetList } from '@/api/pet'
import { useAuthStore } from '@/stores/auth'
import brandLogo from '@/assets/images/logo.png'
import TreatWorkbench from './components/TreatWorkbench.vue'
import AppointmentManager from './components/AppointmentManager.vue'

const router = useRouter()
const authStore = useAuthStore()
const handleLogout = async () => {
  await authStore.logout?.()
  router.push('/login')
}

const appointmentLoading = ref(false)
const recordLoading = ref(false)
const reminderLoading = ref(false)
const medicationLoading = ref(false)
const upcomingAppointments = ref([])
const recentRecords = ref([])
const reminders = ref([])
const medicationRecords = ref([])
const activeMenu = ref('overview')
const petOptions = ref([])
const institutionName = ref('')
const institutionId = ref(null)
const workloadStats = ref({
  pendingCount: 0,
  confirmedCount: 0,
  completedCount: 0,
  cancelledCount: 0
})
const overviewStats = ref({
  today: { appointments: 0, patients: 0, medications: 0 },
  week: { appointments: 0, patients: 0, medications: 0 },
  month: { appointments: 0, patients: 0, medications: 0 }
})

const veterinarianName = computed(
  () => authStore.userInfo?.username || authStore.username || '兽医'
)

const normalizeRole = (role) => (role || '').toString().toLowerCase().trim()
const isVetRole = (roleText) => {
  const normalized = normalizeRole(roleText)
  const keywords = ['兽医', '医生', '医师', 'veterinarian', 'doctor', 'clinic', 'medical']
  if (keywords.some((kw) => normalized.includes(kw))) return true
  return ['vet', 'dr'].includes(normalized)
}

const isVeterinarian = computed(() => {
  const roles = [
    authStore.userRole,
    authStore.userInfo?.roleName,
    authStore.userInfo?.role,
    localStorage.getItem('userRole')
  ]
  if (Array.isArray(authStore.userInfo?.roles)) {
    roles.push(
      ...authStore.userInfo.roles.map((item) =>
        typeof item === 'string' ? item : item?.name || ''
      )
    )
  }
  const mergedText = roles.filter(Boolean).map(normalizeRole).join(' ')
  return isVetRole(mergedText) || roles.some((role) => isVetRole(role))
})

const menuItems = [
  { key: 'overview', label: '工作台总览', desc: '今日重点与入口', icon: Monitor },
  { key: 'treat', label: '接诊工作台', desc: '今日待诊与现场接诊', icon: Monitor },
  { key: 'appointments', label: '预约管理', desc: '预约确认与分配', icon: List },
  { key: 'medications', label: '用药记录', desc: '用药方案与进度', icon: Document },
  { key: 'records', label: '诊疗记录', desc: '病历与治疗方案', icon: Document },
  { key: 'reminders', label: '随访提醒', desc: '复诊与用药提醒', icon: Bell },
  { key: 'tools', label: '工具与模板', desc: '配置与常用模板', icon: Tools }
]

const currentVetId = computed(() => authStore.userInfo?.userId || authStore.userInfo?.id || null)
const vetAppointments = computed(() => {
  if (!currentVetId.value) return upcomingAppointments.value
  return upcomingAppointments.value.filter((item) => {
    const vid =
      item.vetUserId ??
      item.vetId ??
      item.doctorId ??
      item.veterinarianId ??
      item.veterinarianUserId
    return Number(vid) === Number(currentVetId.value)
  })
})
const todayPending = computed(() =>
  vetAppointments.value.filter(
    (item) => isToday(item.appointmentDate) && ['待确认', 'pending'].includes(statusText(item.status))
  )
)
const todayConfirmed = computed(() =>
  vetAppointments.value.filter(
    (item) =>
      isToday(item.appointmentDate) &&
      ['已确认', 'confirmed', '已完成', 'completed'].includes(statusText(item.status))
  )
)

const handleMenuSelect = (key) => {
  activeMenu.value = key
}

const goAppointments = () => {
  // 在医疗操作中心内直接切换到预约管理，无需跳转前台
  activeMenu.value = 'appointments'
  loadAppointments()
}

const goMedicalRecords = () => {
  activeMenu.value = 'records'
  loadMedicalRecords()
}

const quickActions = [
  {
    title: '接诊面板',
    desc: '查看并确认待诊/预约',
    type: 'primary',
    action: () => goAppointments()
  },
  {
    title: '快速记录诊疗',
    desc: '录入诊断、用药与建议',
    type: 'success',
    action: () => goMedicalRecords()
  },
  {
    title: '免疫 / 驱虫',
    desc: '维护疫苗与驱虫记录',
    type: 'warning',
    action: () => router.push('/vaccinations')
  },
  {
    title: '随访提醒',
    desc: '安排复诊、用药与随访提醒',
    type: 'info',
    action: () => router.push('/reminders')
  }
]

const toolkits = [
  {
    title: '诊疗模板',
    desc: '复用常用病历/处置模板',
    type: 'primary',
    action: () => router.push('/reminders')
  },
  {
    title: '诊疗记录面板',
    desc: '一键跳转到医疗记录页面',
    type: 'success',
    action: () => goMedicalRecords()
  },
  {
    title: '接诊/预约设置',
    desc: '调整接诊时间、类型与数量',
    type: 'warning',
    action: () => router.push('/appointments')
  },
  {
    title: '通知与偏好',
    desc: '桌面通知、主题等偏好',
    type: 'info',
    action: () => router.push('/settings')
  }
]

const loadAppointments = async () => {
  try {
    appointmentLoading.value = true
    const params = { size: 50 }
    if (currentVetId.value) params.vetUserId = currentVetId.value
    const res = await getAppointmentList(params)
    upcomingAppointments.value = res?.data?.records || []
  } catch (error) {
    console.error('加载预约列表失败', error)
    ElMessage.error(error?.message || '加载预约列表失败')
  } finally {
    appointmentLoading.value = false
  }
}

const loadMedicalRecords = async () => {
  try {
    recordLoading.value = true
    const res = await getMedicalRecords(
      currentVetId.value ? { vetUserId: currentVetId.value } : {}
    )
    const list = res?.data || []
    recentRecords.value = list.slice(0, 5)
  } catch (error) {
    console.error('加载医疗记录失败', error)
    ElMessage.error(error?.message || '加载医疗记录失败')
  } finally {
    recordLoading.value = false
  }
}

const loadReminders = async () => {
  try {
    reminderLoading.value = true
    const res = await getUpcomingReminders({ size: 5 })
    reminders.value = res?.data?.records || []
  } catch (error) {
    console.error('加载提醒失败', error)
    ElMessage.error(error?.message || '加载提醒失败')
  } finally {
    reminderLoading.value = false
  }
}

const loadMedications = async () => {
  try {
    medicationLoading.value = true
    if (!currentVetId.value) {
      medicationRecords.value = []
      return
    }
    const params = { vetUserId: currentVetId.value, size: 50 }
    const res = await getMedications(params)
    const list = res?.data?.records || res?.data || []
    medicationRecords.value = list
  } catch (error) {
    console.error('加载用药记录失败', error)
    ElMessage.error(error?.message || '加载用药记录失败')
  } finally {
    medicationLoading.value = false
  }
}

const loadStats = async () => {
  if (!currentVetId.value) return
  try {
    const [daily, overview] = await Promise.all([
      getDailyStats({ vetUserId: currentVetId.value }),
      getOverviewStats({ vetUserId: currentVetId.value })
    ])
    workloadStats.value = {
      pendingCount: daily?.data?.pendingCount ?? 0,
      confirmedCount: daily?.data?.confirmedCount ?? 0,
      completedCount: daily?.data?.completedCount ?? 0,
      cancelledCount: daily?.data?.cancelledCount ?? 0
    }
    overviewStats.value = overview?.data || {
      today: { appointments: 0, patients: 0, medications: 0 },
      week: { appointments: 0, patients: 0, medications: 0 },
      month: { appointments: 0, patients: 0, medications: 0 }
    }
  } catch (error) {
    console.error('加载工作量统计失败', error)
    ElMessage.error(error?.message || '加载工作量统计失败')
  }
}

const loadInstitution = async () => {
  if (!currentVetId.value) {
    institutionId.value = null
    institutionName.value = ''
    return
  }
  try {
    const res = await getVetInstitution(currentVetId.value)
    institutionId.value = res?.data?.id ?? null
    institutionName.value = res?.data?.name ?? ''
  } catch (error) {
    console.error('加载机构失败', error)
    institutionId.value = null
    institutionName.value = ''
  }
}

const loadPets = async () => {
  try {
    const res = await getPetList({ size: 100, scope: 'all' })
    petOptions.value = res?.data?.records || []
  } catch (error) {
    console.error('加载宠物列表失败', error)
  }
}

const handleCreateAppointment = async (formPayload) => {
  try {
    if (!formPayload.petId || !formPayload.appointmentDate) {
      ElMessage.warning('请选择宠物并填写预约日期')
      return
    }
    const payload = {
      ...formPayload,
      petId: Number(formPayload.petId),
      appointmentTime: formPayload.appointmentTime || null,
      institutionId: formPayload.institutionId || null,
      vetUserId: currentVetId.value || formPayload.vetUserId || null
    }
    await addAppointment(payload)
    ElMessage.success('已创建预约')
    await loadAppointments()
  } catch (error) {
    console.error('创建预约失败', error)
    ElMessage.error(error?.message || '创建预约失败')
  }
}

const handleConfirmAppointment = async (id) => {
  try {
    await confirmAppointment(id)
    ElMessage.success('预约已确认')
    await loadAppointments()
  } catch (error) {
    console.error('确认预约失败', error)
    ElMessage.error(error?.message || '确认预约失败')
  }
}

const handleCancelAppointment = async (id) => {
  try {
    await deleteAppointment(id)
    ElMessage.success('预约已取消')
    await loadAppointments()
  } catch (error) {
    console.error('取消预约失败', error)
    ElMessage.error(error?.message || '取消预约失败')
  }
}

const statusType = (status) => {
  const map = {
    待确认: 'warning',
    已确认: 'primary',
    已完成: 'success',
    已取消: 'danger',
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  const normalized = status?.toLowerCase?.() || status
  return map[status] || map[normalized] || 'info'
}

const statusText = (status) => {
  const normalized = status?.toLowerCase?.()
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[normalized] || status || '预约'
}

const formatAppointmentTime = (item) => {
  const date = item.appointmentDate || item.date || ''
  const time = item.appointmentTime || item.timeSlot || item.time || ''
  return [date, time].filter(Boolean).join(' ')
}

const isToday = (dateStr) => {
  if (!dateStr) return false
  const today = new Date()
  const [y, m, d] = [today.getFullYear(), today.getMonth(), today.getDate()]
  const target = new Date(dateStr)
  return (
    !Number.isNaN(target.getTime()) &&
    target.getFullYear() === y &&
    target.getMonth() === m &&
    target.getDate() === d
  )
}

const reminderTagType = (item) => {
  const typeText = (item?.reminderType || item?.type || '').toLowerCase()
  if (typeText.includes('疫苗') || typeText.includes('免疫')) return 'success'
  if (typeText.includes('复诊') || typeText.includes('预约')) return 'warning'
  return 'info'
}

const formatReminderDate = (item) => {
  const date = item.dueDate || item.reminderDate || item.nextDueDate || ''
  const time = item.reminderTime || ''
  return [date, time].filter(Boolean).join(' ')
}

onMounted(async () => {
  // 确保最新的用户信息已加载，避免角色判断为空
  if (!authStore.userInfo || !authStore.userRole) {
    try {
      await authStore.fetchUserInfo()
    } catch (error) {
      console.error('刷新用户信息失败', error)
    }
  }

  if (!isVeterinarian.value) {
    // 不再直接拦截，允许进入，避免角色字符串不一致导致无法访问
    ElMessage.warning('未识别为兽医账号，将以通用模式打开医疗操作中心')
  }
  loadPets()
  loadInstitution()
  loadAppointments()
  loadMedicalRecords()
  loadReminders()
  loadMedications()
  loadStats()
})
</script>

<style scoped>
.ops-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 100vh;
  background: #f5f7fb;
}

.ops-sider {
  position: sticky;
  top: 0;
  height: 100vh;
  background: #0f172a;
  color: #fff;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 4px 0 18px rgba(15, 23, 42, 0.18);
}

.sider-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.18), rgba(59, 130, 246, 0.12));
}

.brand-logo {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #fff;
  padding: 4px;
  border: 1px solid #d8e3f3;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transform: translateY(4px) scale(1.5);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-weight: 800;
  font-size: 16px;
}

.brand-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.sider-title {
  font-weight: 800;
  font-size: 18px;
  color: #ffffff;
}

.sider-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin: 4px 0 12px;
}

.sider-menu-wrapper {
  flex: 1;
  overflow-y: auto;
  margin: 8px 0;
  padding-right: 4px;
}

.sider-menu {
  border-right: none;
  background: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.92);
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fff;
}

.sider-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 8px;
  height: 46px;
  line-height: 46px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
}

.sider-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #409eff, #2f86eb);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.28);
}

.menu-icon {
  color: #cde0ff;
}

.menu-label {
  font-weight: 700;
}

.menu-desc {
  font-size: 12px;
  color: #d8e4ff;
}

.menu-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.account-block {
  color: #e5eaf5;
  margin-top: auto;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.account-label {
  font-size: 12px;
  opacity: 0.8;
}

.account-name {
  font-size: 16px;
  font-weight: 700;
  margin-top: 4px;
  word-break: break-all;
}

.back-home {
  margin-top: 8px;
  width: 100%;
  border-radius: 10px;
  position: sticky;
  bottom: 12px;
}

.ops-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 20px 24px 28px;
  height: 100vh;
  overflow-y: auto;
}

.section-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-row {
  margin-bottom: 4px;
}

.stat-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.06);
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #1f2d3d;
  margin-top: 4px;
}

.ops-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 22px;
  border-radius: 18px;
  background: linear-gradient(130deg, #0f172a 0%, #18345f 45%, #1a4ca7 100%);
  border: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 18px 42px rgba(12, 74, 160, 0.3);
  gap: 12px;
  color: #e7efff;
}

.hero-text {
  max-width: 720px;
}

.eyebrow {
  font-size: 13px;
  font-weight: 700;
  color: #7dd3fc;
  letter-spacing: 0.4px;
  margin-bottom: 6px;
}

.hero-title {
  font-size: 26px;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 8px;
}

.hero-sub {
  color: #d7e4ff;
  margin-bottom: 12px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 10px;
}

.hero-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.ops-card {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 14px 28px rgba(31, 44, 80, 0.1);
}

.quick-card .el-row {
  margin-top: 6px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2d3d;
}

.card-sub {
  font-size: 13px;
  color: #6b7280;
  margin-top: 2px;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-sub {
  font-size: 12px;
  color: #6b7280;
}

.quick-item {
  background: #f7f9fc;
  border: 1px solid #e3eaf6;
  border-radius: 12px;
  padding: 12px;
  min-height: 120px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.2s ease;
}

.quick-item:hover {
  border-color: #5c8ff7;
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.12);
  transform: translateY(-2px);
}

.quick-name {
  font-weight: 700;
  color: #1f2d3d;
  margin-bottom: 4px;
}

.quick-desc {
  font-size: 13px;
  color: #6b7280;
  flex: 1;
}

.tool-item {
  min-height: 140px;
}

.timeline-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.timeline-title {
  font-weight: 700;
  color: #1f2d3d;
}

.timeline-sub {
  font-size: 13px;
  color: #6b7280;
}

@media (max-width: 960px) {
  .ops-layout {
    flex-direction: column;
  }

  .ops-sider {
    position: relative;
    width: 100%;
    top: auto;
  }

  .ops-hero {
    flex-direction: column;
  }

  .hero-meta {
    align-items: flex-start;
  }
}
</style>
