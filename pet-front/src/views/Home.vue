<template>
  <div class="home-container">
    <div class="welcome-card">
      <div class="welcome-left">
        <div class="welcome-text">
          <p class="welcome-date">今天是 {{ formatDate(new Date()) }}</p>
          <h2>欢迎回来，{{ userInfo?.nickname || '用户' }}！</h2>
          <p class="welcome-sub">集中管理宠物档案、提醒、就诊与预约。</p>
        </div>
        <div class="welcome-badges">
          <el-tag type="warning" effect="dark">今日提醒 {{ upcomingReminders.length }}</el-tag>
          <el-tag type="info" effect="plain">即将预约 {{ upcomingAppointments.length }}</el-tag>
          <el-dropdown @command="handleQuickAction" class="badge-dropdown">
            <el-button size="small" type="primary" plain>
              快捷操作
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="action in quickActions"
                  :key="action.label"
                  :command="action.handler"
                >
                  {{ action.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <el-row :gutter="20" class="section-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <div class="card-title">宠物总览</div>
              <div class="pet-switcher">
                <el-avatar :size="34" :src="currentPetAvatar" />
                <el-select v-model="currentPetId" placeholder="选择宠物" size="small" @change="handlePetChange">
                  <el-option v-for="pet in petProfiles" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
                </el-select>
              </div>
            </div>
          </template>
          <div class="overview">
            <div class="overview-main">
              <div class="pet-face">
                <img :src="currentPetAvatar" class="pet-avatar" alt="宠物头像" />
                <div class="pet-basic">
                  <div class="pet-name">{{ currentPet?.petName || '未选择宠物' }}</div>
                  <div class="pet-tags">
                    <el-tag size="small" type="info">{{ currentPet?.species || '未知种类' }}</el-tag>
                    <el-tag size="small" type="info">{{ currentPet?.breed || '未填写' }}</el-tag>
                    <el-tag size="small" :type="genderTag.type">{{ genderTag.label }}</el-tag>
                    <el-tag size="small">{{ ageText }}</el-tag>
                    <el-tag size="small" :type="neuterTag.type">{{ neuterTag.label }}</el-tag>
                    <el-tag size="small" type="warning" v-if="allergyTag">{{ allergyTag }}</el-tag>
                  </div>
                </div>
              </div>
              <div class="pet-actions">
                <el-button type="primary" size="small" @click="goEditPet" :disabled="!currentPetId">编辑档案</el-button>
                <el-button size="small" @click="handleShare" :disabled="!currentPetId">分享档案</el-button>
              </div>
            </div>
            <el-divider class="overview-divider" />
            <div class="overview-stats">
              <div class="stat-item">
                <p>健康状态</p>
                <el-tag :type="getStatusTagType(currentSummary?.healthStatus)">{{ currentSummary?.healthStatus || '未知' }}</el-tag>
              </div>
              <div class="stat-item">
                <p>当前体重</p>
                <span class="stat-number">{{ currentSummary?.currentWeight ?? '-' }} kg</span>
              </div>
              <div class="stat-item">
                <p>上次体检</p>
                <span>{{ currentSummary?.lastMedicalCheck || '未记录' }}</span>
              </div>
              <div class="stat-item">
                <p>疫苗 / 驱虫</p>
                <span>{{ currentSummary?.vaccineCount || 0 }} / {{ currentSummary?.dewormingCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section-row photo-wall">
      <template #header>
        <div class="card-header">
          <div class="card-title">照片墙</div>
          <div class="photo-wall-actions">
            <el-button
              class="photo-edit-btn"
              size="small"
              :type="editMode ? 'danger' : 'default'"
              :disabled="!photoList.length"
              @click="handleEditClick"
            >
              {{ editMode ? '删除' : '编辑' }}
            </el-button>
            <el-upload
              class="photo-upload-btn"
              :http-request="handleUploadPhoto"
              :show-file-list="false"
              :disabled="photoUploading || !currentPetId"
            >
              <el-button type="primary" size="small" :loading="photoUploading" :disabled="!currentPetId">
                上传照片
              </el-button>
            </el-upload>
          </div>
        </div>
      </template>
      <el-skeleton v-if="photoLoading" animated :rows="3" />
      <el-empty v-else-if="!photoList.length" description="暂无照片，上传一张吧" />
      <div
        v-else
        ref="photoWallRef"
        class="photo-grid"
        :style="photoGridStyle"
        @dragover.prevent="handlePhotoDragOver"
        @drop.prevent="handlePhotoDropOnWall"
      >
        <div
          v-for="(photo, index) in photoList"
          :key="photo.photoId"
          class="photo-item"
          :class="{ dragging: dragPhotoIndex === index, editing: editMode }"
          draggable="true"
          :data-index="index"
          @dragstart="(e) => handlePhotoDragStart(e, index, photo.photoId)"
          @dragenter.prevent="handlePhotoDragOver"
          @dragover="handlePhotoDragOver"
          @drop.prevent="(e) => handlePhotoDrop(e, photo)"
          @dragend="handlePhotoDragEnd"
          @dblclick.stop="openPhotoPreview(photo.url)"
          :style="[
            photoItemStyle,
            getPhotoPositionStyle(photo, index),
            { transform: `rotate(${getTilt(index)}deg)` }
          ]"
        >
          <div class="photo-sticker">🐾</div>
          <div v-if="editMode" class="photo-select" :class="{ checked: isSelected(photo.photoId) }" @click.stop="toggleSelect(photo.photoId)">
            <span v-if="isSelected(photo.photoId)">✔</span>
          </div>
          <div class="photo-img-wrapper" :style="{ height: photoSize + 'px' }">
            <el-image class="photo-img" :src="photo.url" fit="cover" />
          </div>
          <div class="photo-meta">
            <div class="photo-name">{{ photo.fileName || '照片' }}</div>
            <div class="photo-date">{{ formatDateTime(photo.uploadedAt) }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="photoPreviewVisible"
      width="60%"
      append-to-body
      :show-close="true"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      :destroy-on-close="true"
      @close="closePhotoPreview"
    >
      <div class="preview-box">
        <el-image
          v-if="photoPreviewUrl"
          class="preview-img"
          :src="photoPreviewUrl"
          fit="contain"
          :preview-src-list="[photoPreviewUrl]"
          :initial-index="0"
          :preview-teleported="true"
        />
      </div>
    </el-dialog>

    <el-dialog
      v-model="deleteConfirmVisible"
      width="360px"
      align-center
      append-to-body
      :show-close="false"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
    >
      <template #header>
        <span>删除照片</span>
      </template>
      <div>确认删除选中的照片吗？此操作不可恢复。</div>
      <template #footer>
        <el-button @click="cancelDeletePhotos">取消</el-button>
        <el-button type="danger" @click="confirmDeletePhotos">删除</el-button>
      </template>
    </el-dialog>

    <el-row :gutter="20" class="section-row">
      <el-col :span="12">
        <el-card class="fixed-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">今日提醒与待办</div>
              <el-button text size="small" @click="goToReminders">查看全部</el-button>
            </div>
          </template>
          <el-empty v-if="!upcomingReminders.length && !reminderLoading" description="暂无提醒" />
          <el-timeline v-else class="reminder-timeline">
            <el-timeline-item
              v-for="item in sortedReminders"
              :key="item.reminderId"
              :type="item.completed ? 'success' : 'warning'"
              :hollow="item.completed"
            >
              <div class="reminder-item">
                <el-checkbox v-model="item.completed" @change="handleReminderComplete(item)">
                  {{ item.title || '提醒' }}
                </el-checkbox>
                <div class="reminder-meta">
                  <span>{{ item.dueDate || item.reminderDate || '日期未填' }}</span>
                  <el-tag size="small" :type="getReminderTagType(item.daysRemaining)">
                    {{ item.daysRemaining != null ? `${item.daysRemaining}天后` : '尽快处理' }}
                  </el-tag>
                </div>
                <p class="reminder-message">{{ item.message || item.content }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="fixed-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">就诊与预约</div>
              <el-button text size="small" @click="goToAddAppointment">新建预约</el-button>
            </div>
          </template>
          <div class="visit-box">
            <div class="visit-block">
              <div class="visit-label">最近一次就诊</div>
              <div class="visit-value">
                <span>{{ lastVisitText }}</span>
              </div>
              <div class="visit-meta">诊断/医嘱：{{ lastVisitNote }}</div>
            </div>
            <el-divider />
            <div class="visit-block">
              <div class="visit-label">下一次预约</div>
              <div class="visit-value">
                <span>{{ nextAppointmentText }}</span>
                <el-tag size="small" :type="getStatusType(nextAppointment?.status)" v-if="nextAppointment?.status">
                  {{ nextAppointment?.status }}
                </el-tag>
              </div>
              <div class="visit-meta">
                {{ nextAppointment?.appointmentDate || '-' }} {{ nextAppointment?.appointmentTime || '' }}
                <span v-if="nextAppointment?.institutionName || nextAppointment?.institutionId">
                  · {{ nextAppointment.institutionName || `机构ID ${nextAppointment.institutionId}` }}
                </span>
              </div>
              <div class="visit-actions">
                <el-button size="small" @click="goToAppointments">查看/改期</el-button>
                <el-button size="small" plain @click="navigateToHospital" :disabled="!nextAppointment">导航</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="section-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <div class="card-title">健康趋势</div>
              <div class="trend-controls">
                <el-checkbox-group v-model="activeTrends" size="small">
                  <el-checkbox-button
                    v-for="item in trendMenuOptions"
                    :key="item.key"
                    :label="item.key"
                  >
                    {{ item.label }}
                  </el-checkbox-button>
                </el-checkbox-group>
              </div>
            </div>
          </template>
          <div class="trend-grid">
            <div class="trend-item trend-line half" v-if="shownTrends.includes('weight')">
              <div class="trend-header">
                <span>体重</span>
                <span class="trend-sub">{{ weightDisplaySeries[weightDisplaySeries.length - 1]?.value ?? '--' }} kg</span>
                <el-button text circle size="small" class="trend-add" @click="openAddMetric('weight')">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
              <div class="line-chart">
                <div ref="weightChartRef" class="echart"></div>
                <div class="chart-legend">
                  <span>日均体重</span>
                </div>
              </div>
            </div>

            <div class="trend-item trend-line half" v-if="shownTrends.includes('temp')">
              <div class="trend-header">
                <span>体温</span>
                <span class="trend-sub">{{ tempDisplaySeries[tempDisplaySeries.length - 1]?.value ?? '--' }} °C</span>
                <el-button text circle size="small" class="trend-add" @click="openAddMetric('temperature')">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
              <div class="line-chart">
                <div ref="tempChartRef" class="echart"></div>
                <div class="chart-legend">
                  <span>每日体温</span>
                </div>
              </div>
            </div>

            <div class="trend-item trend-line half" v-if="shownTrends.includes('activity')">
              <div class="trend-header">
                <span>活动量</span>
                <span class="trend-sub">{{ activityDisplaySeries[activityDisplaySeries.length - 1]?.value ?? '--' }} %</span>
                <el-button text circle size="small" class="trend-add" @click="openAddMetric('activity')">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
              <div class="line-chart">
                <div ref="activityChartRef" class="echart"></div>
                <div class="chart-legend">
                  <span>每日活动量</span>
                </div>
              </div>
            </div>

            <div class="trend-item trend-line half" v-if="shownTrends.includes('intake')">
              <div class="trend-header">
                <span>饮水/进食</span>
                <span class="trend-sub">{{ intakeDisplaySeries[intakeDisplaySeries.length - 1]?.value ?? '--' }} ml</span>
                <el-button text circle size="small" class="trend-add" @click="openAddMetric('intake')">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
              <div class="line-chart">
                <div ref="intakeChartRef" class="echart"></div>
                <div class="chart-legend">
                  <span>每日饮水/进食</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section-row">
      <template #header>
        <div class="card-header">
          <div class="card-title">养宠常识小贴士</div>
        </div>
      </template>
      <div class="tips-grid">
        <div v-for="tip in petTips" :key="tip.title" class="tip-item">
          <div class="tip-title">
            <el-tag size="small" :type="tip.type" effect="plain">{{ tip.tag }}</el-tag>
            <span>{{ tip.title }}</span>
          </div>
          <p class="tip-desc">{{ tip.desc }}</p>
        </div>
      </div>
    </el-card>

  </div>

  <el-dialog
    v-model="addMetricDialog"
    :title="`添加${metricLabels[addMetricForm.type] || ''}记录`"
    width="420px"
    :close-on-click-modal="false"
  >
    <el-form label-width="80px" size="small" class="metric-form">
      <el-form-item label="日期">
        <el-date-picker v-model="addMetricForm.date" type="date" placeholder="选择日期" style="width: 100%" />
      </el-form-item>
      <el-form-item :label="metricLabels[addMetricForm.type] || '数值'">
        <el-input v-model="addMetricForm.value" type="number" placeholder="请输入数值" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="addMetricForm.notes" type="textarea" rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="addMetricDialog = false">取消</el-button>
      <el-button type="primary" @click="handleAddMetricConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, ArrowDown, Delete as DeleteIcon } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  addPetMetric,
  getHealthSummary,
  getPetList,
  getPetMetrics,
  getPetPhotos,
  uploadPetGalleryPhoto,
  deletePetPhoto,
  updatePetPhotoPosition
} from '@/api/pet'
import { getUpcomingReminders } from '@/api/reminder.js'
import { getUpcomingAppointments } from '@/api/appointment.js'
import defaultPetAvatar from '@/assets/images/R-C.jpg'
import { formatDate } from '@/utils/date'

const router = useRouter()
const authStore = useAuthStore()
const userInfo = computed(() => authStore.userInfo)
const currentUserId = computed(
  () => userInfo.value?.id ?? userInfo.value?.userId ?? userInfo.value?.userID ?? null
)

const loading = ref(false)
const reminderLoading = ref(false)
const appointmentLoading = ref(false)
const metricsLoading = ref(false)
const addMetricDialog = ref(false)
const addMetricForm = reactive({
  type: 'weight',
  date: new Date(),
  value: '',
  notes: ''
})

const healthSummary = ref([])
const upcomingReminders = ref([])
const upcomingAppointments = ref([])
const petProfiles = ref([])
const photoList = ref([])
const dragPhotoIndex = ref(null)
const dragPhotoId = ref(null)
const photoPositions = reactive({})
const photoWallRef = ref(null)
const photoPreviewVisible = ref(false)
const photoPreviewUrl = ref('')
const editMode = ref(false)
const selectedPhotos = ref([])
const deleteConfirmVisible = ref(false)
const photoLoading = ref(false)
const photoUploading = ref(false)
const photoGridStyle = computed(() => ({}))
const photoSize = computed(() => {
  const count = photoList.value.length || 1
  if (count <= 4) return 160
  if (count <= 9) return 140
  if (count <= 16) return 120
  return 100
})
const photoMetaHeight = 0
const photoItemStyle = computed(() => ({
  width: `${photoSize.value}px`,
  height: `${photoSize.value + photoMetaHeight}px`
}))
const currentPetId = ref(null)
const weightMetrics = ref([])
const tempMetrics = ref([])
const activityMetrics = ref([])
const intakeMetrics = ref([])
const metricLabels = {
  weight: '体重',
  temperature: '体温',
  activity: '活动量',
  intake: '饮水/进食'
}

const trendMenuOptions = [
  { key: 'weight', label: '体重' },
  { key: 'temp', label: '体温' },
  { key: 'activity', label: '活动量' },
  { key: 'intake', label: '饮水/进食' }
]
const activeTrends = ref(['weight', 'temp', 'activity', 'intake'])
const shownTrends = computed(() =>
  activeTrends.value.length ? activeTrends.value : trendMenuOptions.map((i) => i.key)
)

const quickActions = [
  { label: '添加就诊记录', type: 'primary', handler: () => router.push('/medical-records') },
  { label: '上传检验结果', type: 'success', handler: () => router.push('/medical-records') },
  { label: '记录用药/驱虫', type: 'warning', handler: () => router.push('/medical-records') },
  { label: '添加提醒', type: 'info', handler: () => router.push('/reminders') }
]

const handleQuickAction = (cmd) => {
  if (typeof cmd === 'function') {
    cmd()
  }
}

const petTips = [
  { title: '每年体检 1-2 次', desc: '成犬/成猫建议每年一次综合体检，老年宠物或有慢病的建议每半年体检一次。', tag: '健康', type: 'success' },
  { title: '疫苗与驱虫要按时', desc: '核心疫苗遵循医生流程，体内外驱虫按月/季度执行，做好记录以防遗漏。', tag: '防护', type: 'warning' },
  { title: '饮食逐步更换', desc: '更换猫粮/狗粮需 7-10 天过渡，观察便便和食欲，避免突然更换导致肠胃不适。', tag: '饮食', type: 'info' },
  { title: '控制体重与运动', desc: '保持每日适量运动与定时定量喂食，体况评分 4-5 分为宜，预防肥胖相关疾病。', tag: '运动', type: 'primary' }
]

const currentPet = computed(() => petProfiles.value.find((p) => p.petId === currentPetId.value))
const currentSummary = computed(() => healthSummary.value.find((p) => p.petId === currentPetId.value))
const absoluteApiBase =
  import.meta.env.VITE_API_BASE_URL && import.meta.env.VITE_API_BASE_URL.startsWith('http')
    ? import.meta.env.VITE_API_BASE_URL.replace(/\/$/, '')
    : ''

const normalizePhotoUrl = (url) => {
  if (!url) return url
  if (/^https?:\/\//i.test(url)) return url
  if (!absoluteApiBase) return url
  try {
    return new URL(url, absoluteApiBase).toString()
  } catch (error) {
    return url
  }
}

const buildPhotoUrl = (url) => {
  if (!url) return url
  const normalizedUrl = normalizePhotoUrl(url)
  if (normalizedUrl?.startsWith('data:')) return normalizedUrl
  const token = authStore.token
  if (!token || /[?&]token=/.test(normalizedUrl)) return normalizedUrl
  const delimiter = normalizedUrl.includes('?') ? '&' : '?'
  return `${normalizedUrl}${delimiter}token=${encodeURIComponent(token)}`
}

const currentPetAvatar = computed(() => buildPhotoUrl(currentPet.value?.photoUrl) || defaultPetAvatar)

const genderTag = computed(() => {
  const gender = currentPet.value?.gender || ''
  if (gender === 'M') return { label: '公', type: 'info' }
  if (gender === 'F') return { label: '母', type: 'success' }
  return { label: '性别未填', type: 'default' }
})

const neuterTag = computed(() => {
  const sterilizedFlag = currentPet.value?.sterilized
  if (sterilizedFlag === true) return { label: '已绝育', type: 'success' }
  if (sterilizedFlag === false) return { label: '未绝育', type: 'info' }
  const remark = currentPet.value?.remark || ''
  const hasNeuter = /绝育|已绝育/.test(remark)
  return hasNeuter ? { label: '已绝育', type: 'success' } : { label: '绝育未填', type: 'default' }
})

const allergyTag = computed(() => {
  const remark = currentPet.value?.remark || ''
  return /过敏|敏感|慢病|慢性/.test(remark) ? remark.slice(0, 12) : ''
})

const ageText = computed(() => {
  const birth = currentPet.value?.birthDate
  if (!birth) return '年龄未填'
  const age = calcAge(birth)
  return age || '年龄未填'
})

const sortedReminders = computed(() => {
  return [...upcomingReminders.value].sort((a, b) => {
    const aVal = a.daysRemaining ?? 999
    const bVal = b.daysRemaining ?? 999
    return aVal - bVal
  })
})

const nextAppointment = computed(() => {
  if (!upcomingAppointments.value.length) return null
  const petScoped = currentPetId.value
    ? upcomingAppointments.value.find((a) => a.petId === currentPetId.value)
    : null
  return petScoped || upcomingAppointments.value[0]
})

const lastVisitText = computed(() => currentSummary.value?.lastMedicalCheck || '暂无就诊记录')
const lastVisitNote = computed(() => currentSummary.value?.healthStatus || '等待录入')
const nextAppointmentText = computed(() => nextAppointment.value?.appointmentType || '暂无预约')

function generateSeries(base, len, variance) {
  return Array.from({ length: len }).map((_, idx) => {
    const wave = Math.sin(idx / 2.5) * variance
    const drift = (idx % 4 === 0 ? variance * 0.2 : -variance * 0.1)
    return Number((base + wave + drift).toFixed(1))
  })
}

function buildTimeSeries(base, len, variance) {
  const series = generateSeries(base, len, variance)
  const today = new Date()
  return series.map((value, idx) => {
    const date = new Date(today)
    date.setDate(today.getDate() - (len - 1 - idx))
    const label = `${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
    return { value, label, date }
  })
}

function normalizeSeries(records) {
  if (!records || !records.length) return []
  const sorted = [...records].sort((a, b) => {
    const da = new Date(a.recordDate || a.date || a.label || '')
    const db = new Date(b.recordDate || b.date || b.label || '')
    return da.getTime() - db.getTime()
  })
  return sorted.map((item) => {
    const date = item.recordDate ? new Date(item.recordDate) : new Date()
    const label = `${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
    const value = Number(item.value ?? item.weight ?? item.temperature ?? item.activityLevel ?? item.intakeVolume ?? 0)
    return {
      value: Number.isNaN(value) ? 0 : Number(value.toFixed(1)),
      label,
      date: item.recordDate || date
    }
  })
}

function pickTicks(points, count) {
  if (!points.length) return []
  if (points.length <= count) return points
  const step = (points.length - 1) / (count - 1)
  return Array.from({ length: count }).map((_, idx) => points[Math.round(idx * step)])
}

function buildLineChart(series, width, height) {
  const safeWidth = width || 260
  const safeHeight = height || 120
  const values = series.map((s) => s.value)
  const maxValue = Math.max(...values, 1)
  const displayMax = maxValue + 8
  const minValue = Math.min(...values, 0)
  const range = displayMax - minValue || 1
  const step = series.length > 1 ? safeWidth / (series.length - 1) : safeWidth
  const points = series.map((item, idx) => {
    const x = idx * step
    const y = safeHeight - ((item.value - minValue) / range) * safeHeight
    return { x: Number(x.toFixed(2)), y: Number(y.toFixed(2)), label: item.label }
  })
  const linePath = points.reduce((acc, p, idx) => acc + `${idx === 0 ? 'M' : ' L'} ${p.x} ${p.y}`, '')
  const areaPath = `${linePath} L ${safeWidth} ${safeHeight} L 0 ${safeHeight} Z`

  const xTicks = pickTicks(points, 4).map((p) => ({ x: p.x, label: p.label }))
  const yTicks = [
    { y: safeHeight, label: minValue.toFixed(1) },
    { y: safeHeight / 2, label: (minValue + range / 2).toFixed(1) },
    { y: 0, label: displayMax.toFixed(1) }
  ]

  return { points, linePath, areaPath, xTicks, yTicks }
}

const chartSize = { width: 320, height: 160 }
const chartCanvasHeight = chartSize.height + 40
const trendDisplayCount = 7
const weightFullSeries = computed(() => normalizeSeries(weightMetrics.value))
const tempFullSeries = computed(() => normalizeSeries(tempMetrics.value))
const activityFullSeries = computed(() => normalizeSeries(activityMetrics.value))
const intakeFullSeries = computed(() => normalizeSeries(intakeMetrics.value))

const weightWindowStart = ref(0)
const tempWindowStart = ref(0)
const activityWindowStart = ref(0)
const intakeWindowStart = ref(0)

const weightSliderMax = computed(() => Math.max(0, (weightFullSeries.value?.length || 0) - trendDisplayCount))
const tempSliderMax = computed(() => Math.max(0, (tempFullSeries.value?.length || 0) - trendDisplayCount))
const activitySliderMax = computed(() => Math.max(0, (activityFullSeries.value?.length || 0) - trendDisplayCount))
const intakeSliderMax = computed(() => Math.max(0, (intakeFullSeries.value?.length || 0) - trendDisplayCount))

const weightDisplaySeries = computed(() =>
  weightFullSeries.value.slice(weightWindowStart.value, weightWindowStart.value + trendDisplayCount)
)
const tempDisplaySeries = computed(() =>
  tempFullSeries.value.slice(tempWindowStart.value, tempWindowStart.value + trendDisplayCount)
)
const activityDisplaySeries = computed(() =>
  activityFullSeries.value.slice(activityWindowStart.value, activityWindowStart.value + trendDisplayCount)
)
const intakeDisplaySeries = computed(() =>
  intakeFullSeries.value.slice(intakeWindowStart.value, intakeWindowStart.value + trendDisplayCount)
)

const weightDisplayRangeText = computed(() => {
  if (!weightDisplaySeries.value.length) return ''
  const start = weightDisplaySeries.value[0]?.label
  const end = weightDisplaySeries.value[weightDisplaySeries.value.length - 1]?.label
  return `${start} - ${end}`
})
const tempDisplayRangeText = computed(() => {
  if (!tempDisplaySeries.value.length) return ''
  const start = tempDisplaySeries.value[0]?.label
  const end = tempDisplaySeries.value[tempDisplaySeries.value.length - 1]?.label
  return `${start} - ${end}`
})
const activityDisplayRangeText = computed(() => {
  if (!activityDisplaySeries.value.length) return ''
  const start = activityDisplaySeries.value[0]?.label
  const end = activityDisplaySeries.value[activityDisplaySeries.value.length - 1]?.label
  return `${start} - ${end}`
})
const intakeDisplayRangeText = computed(() => {
  if (!intakeDisplaySeries.value.length) return ''
  const start = intakeDisplaySeries.value[0]?.label
  const end = intakeDisplaySeries.value[intakeDisplaySeries.value.length - 1]?.label
  return `${start} - ${end}`
})

const weightChart = computed(() => buildLineChart(weightDisplaySeries.value, chartSize.width, chartSize.height))
const tempChart = computed(() => buildLineChart(tempDisplaySeries.value, chartSize.width, chartSize.height))
const activityChart = computed(() => buildLineChart(activityDisplaySeries.value, chartSize.width, chartSize.height))
const intakeChart = computed(() => buildLineChart(intakeDisplaySeries.value, chartSize.width, chartSize.height))

const weightChartRef = ref(null)
const tempChartRef = ref(null)
const activityChartRef = ref(null)
const intakeChartRef = ref(null)
const chartInstances = {
  weight: null,
  temperature: null,
  activity: null,
  intake: null
}

const setWindowStart = (type, start) => {
  const safeStart = Math.max(0, start)
  if (type === 'weight') weightWindowStart.value = Math.min(safeStart, weightSliderMax.value)
  if (type === 'temperature') tempWindowStart.value = Math.min(safeStart, tempSliderMax.value)
  if (type === 'activity') activityWindowStart.value = Math.min(safeStart, activitySliderMax.value)
  if (type === 'intake') intakeWindowStart.value = Math.min(safeStart, intakeSliderMax.value)
}

const renderLineChart = (type, elRef, series, color, unit, windowStart, windowSize) => {
  const el = elRef?.value
  const echarts = window.echarts
  if (!el || !echarts) return
  let inst = chartInstances[type]
  if (!inst) {
    inst = echarts.init(el)
    chartInstances[type] = inst
  }
  const labels = series.map((s) => s.label)
  const values = series.map((s) => s.value)
  const maxValue = Math.max(...values, 1)
  const minValue = Math.min(...values, 0)
  const displayMax = Math.ceil(maxValue)
  const len = series.length
  const start = 0
  const end = len > 0 ? len - 1 : 0
  const minSpan = len > 0 ? Math.min((trendDisplayCount / len) * 100, 100) : 0

  inst.off('dataZoom')
  inst.setOption({
    grid: { left: 40, right: 20, top: 20, bottom: 40 },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: labels,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#94a3b8' }
    },
    yAxis: {
      type: 'value',
      min: minValue,
      max: displayMax,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#94a3b8' }
    },
    dataZoom: [
      {
        type: 'inside',
        startValue: start,
        endValue: end,
        minSpan
      }
    ],
    series: [
      {
        data: values,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        lineStyle: { width: 3, color },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color, opacity: 0.35 },
            { offset: 1, color, opacity: 0 }
          ])
        },
        label: {
          show: true,
          position: 'top',
          color: '#475569',
          formatter: (p) => `${p.value}${unit || ''}`
        }
      }
    ]
  })

  inst.on('dataZoom', (params) => {
    const zoom = params.batch ? params.batch[0] : params
    const sv = zoom.startValue ?? Math.round(((zoom.start ?? 0) / 100) * (len - 1))
    setWindowStart(type, sv)
  })
}

const getMetricRef = (type) => {
  if (type === 'weight') return weightMetrics
  if (type === 'temperature') return tempMetrics
  if (type === 'activity') return activityMetrics
  if (type === 'intake') return intakeMetrics
  return null
}

const openAddMetric = (type) => {
  addMetricForm.type = type
  addMetricForm.date = new Date()
  addMetricForm.value = ''
  addMetricForm.notes = ''
  addMetricDialog.value = true
}

const handleAddMetricConfirm = () => {
  if (!addMetricForm.type) {
    ElMessage.warning('请选择指标')
    return
  }
  const dateObj = new Date(addMetricForm.date)
  if (Number.isNaN(dateObj.getTime())) {
    ElMessage.warning('请选择日期')
    return
  }
  const valueNum = Number(addMetricForm.value)
  if (Number.isNaN(valueNum)) {
    ElMessage.warning('请填写数值')
    return
  }
  const recordDate = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(
    dateObj.getDate()
  ).padStart(2, '0')}`
  if (!currentPetId.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  addPetMetric(currentPetId.value, {
    type: addMetricForm.type,
    value: valueNum,
    recordDate,
    notes: addMetricForm.notes
  })
    .then(() => {
      addMetricDialog.value = false
      ElMessage.success('已添加记录')
      loadMetricSeries()
    })
    .catch((error) => {
      console.error('添加记录失败', error)
      ElMessage.error('添加记录失败')
    })
}

const extractMetrics = (res) => {
  return res?.data?.data || res?.data?.records || res?.data || []
}

const loadMetricSeries = async () => {
  if (!currentPetId.value) return
  try {
    metricsLoading.value = true
    const [weightRes, tempRes, activityRes, intakeRes] = await Promise.all([
      getPetMetrics(currentPetId.value, 'weight', 180),
      getPetMetrics(currentPetId.value, 'temperature', 180),
      getPetMetrics(currentPetId.value, 'activity', 180),
      getPetMetrics(currentPetId.value, 'intake', 180)
    ])
    weightMetrics.value = extractMetrics(weightRes)
    tempMetrics.value = extractMetrics(tempRes)
    activityMetrics.value = extractMetrics(activityRes)
    intakeMetrics.value = extractMetrics(intakeRes)

    console.info('Metrics loaded', {
      pet: currentPetId.value,
      weight: weightMetrics.value.length,
      temp: tempMetrics.value.length,
      activity: activityMetrics.value.length,
      intake: intakeMetrics.value.length
    })
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    ElMessage.error(error?.message || '加载趋势数据失败，请稍后重试')
  } finally {
    metricsLoading.value = false
  }
}

const loadPhotos = async () => {
  if (!currentPetId.value) {
    photoList.value = []
    Object.keys(photoPositions).forEach((k) => delete photoPositions[k])
    return
  }
  try {
    photoLoading.value = true
    const res = await getPetPhotos(currentPetId.value)
    const list = Array.isArray(res?.data)
      ? res.data
      : Array.isArray(res?.data?.records)
        ? res.data.records
        : Array.isArray(res?.data?.data)
          ? res.data.data
          : []
    photoList.value = list.map((photo) => ({
      ...photo,
      url: buildPhotoUrl(photo.url)
    }))
    await nextTick()
    list.forEach((p, idx) => {
      const key = String(p.photoId)
      if (p.posX !== null && p.posX !== undefined && p.posY !== null && p.posY !== undefined) {
        photoPositions[key] = { left: p.posX, top: p.posY }
      }
    })
    syncPhotoPositions()
  } catch (error) {
    console.error('加载照片失败:', error)
    ElMessage.error('加载照片失败')
  } finally {
    photoLoading.value = false
  }
}

const handleUploadPhoto = async (options) => {
  const { file } = options
  if (!currentPetId.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  try {
    photoUploading.value = true
    await uploadPetGalleryPhoto(currentPetId.value, file)
    ElMessage.success('上传成功')
    await loadPhotos()
  } catch (error) {
    console.error('上传照片失败:', error)
    ElMessage.error(error?.message || '上传失败')
  } finally {
    photoUploading.value = false
  }
}

const handleDeletePhoto = async (photoId) => {
  if (!photoId || !currentPetId.value) return
  selectedPhotos.value = [photoId]
  deleteConfirmVisible.value = true
}

const clamp = (value, min, max) => Math.min(Math.max(value, min), max)
const clampPositionToWall = (pos) => {
  const wall = photoWallRef.value
  if (!wall || !pos) return pos
  const rect = wall.getBoundingClientRect()
  const size = photoSize.value + photoMetaHeight
  const padding = 8
  const maxLeft = rect.width - size - padding
  const maxTop = rect.height - size - padding
  return {
    left: clamp(pos.left ?? 0, padding, maxLeft),
    top: clamp(pos.top ?? 0, padding, maxTop)
  }
}

const syncPhotoPositions = () => {
  const wall = photoWallRef.value
  if (!wall) return
  const rect = wall.getBoundingClientRect()
  const size = photoSize.value + photoMetaHeight
  const gap = 14
  const padding = 8
  const cols = Math.max(1, Math.floor((rect.width - padding * 2 + gap) / (photoSize.value + gap)))
  const ids = new Set(photoList.value.map((p) => String(p.photoId)))

  Object.keys(photoPositions).forEach((key) => {
    if (!ids.has(key)) delete photoPositions[key]
  })

  photoList.value.forEach((photo, idx) => {
    const key = String(photo.photoId)
    if (photoPositions[key]) return
    const row = Math.floor(idx / cols)
    const col = idx % cols
    photoPositions[key] = {
      top: padding + row * (size + gap),
      left: padding + col * (photoSize.value + gap)
    }
  })
}

const getPhotoPositionStyle = (photo, index) => {
  const key = String(photo.photoId)
  const pos = clampPositionToWall(photoPositions[key])
  const size = photoSize.value + photoMetaHeight
  const gap = 14
  const padding = 8
  if (pos) {
    return { top: `${pos.top}px`, left: `${pos.left}px` }
  }
  const col = index % 3
  const row = Math.floor(index / 3)
  return {
    top: `${padding + row * (size + gap)}px`,
    left: `${padding + col * (photoSize.value + gap)}px`
  }
}

const handlePhotoDragStart = (event, index, photoId) => {
  dragPhotoIndex.value = index
  dragPhotoId.value = photoId
  if (event?.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', `${photoId}`)
  }
}

const handlePhotoDragOver = (event) => {
  event.preventDefault()
  if (event?.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
}

const placePhotoAt = (event, photoId) => {
  if (!photoId) return
  const wall = photoWallRef.value
  if (!wall) return null
  const rect = wall.getBoundingClientRect()
  const size = photoSize.value + photoMetaHeight
  const padding = 8
  const maxLeft = rect.width - size - padding
  const maxTop = rect.height - size - padding
  const left = clamp(event.clientX - rect.left - size / 2, padding, maxLeft)
  const top = clamp(event.clientY - rect.top - size / 2, padding, maxTop)
  photoPositions[String(photoId)] = { left, top }
  return photoPositions[String(photoId)]
}

const persistPhotoPosition = async (photoId, pos) => {
  if (!photoId || !currentPetId.value || !pos) return
  try {
    await updatePetPhotoPosition(photoId, currentPetId.value, pos.left, pos.top)
  } catch (error) {
    console.error('保存照片位置失败', error)
    ElMessage.error('保存照片位置失败')
    await loadPhotos()
  }
}

const handlePhotoDrop = async (event, photo) => {
  const idFromEvent = event?.dataTransfer?.getData('text/plain')
  const targetId = photo?.photoId || dragPhotoId.value || idFromEvent
  if (!targetId) return
  const pos = placePhotoAt(event, targetId)
  await persistPhotoPosition(targetId, pos)
  dragPhotoIndex.value = null
  dragPhotoId.value = null
}

const handlePhotoDropOnWall = async (event) => {
  const idFromEvent = event?.dataTransfer?.getData('text/plain')
  const targetId = dragPhotoId.value || idFromEvent
  if (!targetId) return
  const pos = placePhotoAt(event, targetId)
  await persistPhotoPosition(targetId, pos)
  dragPhotoIndex.value = null
  dragPhotoId.value = null
}

const handlePhotoDragEnd = () => {
  dragPhotoIndex.value = null
  dragPhotoId.value = null
}

const openPhotoPreview = (url) => {
  if (!url) return
  photoPreviewUrl.value = url
  photoPreviewVisible.value = true
}

const closePhotoPreview = () => {
  photoPreviewVisible.value = false
  photoPreviewUrl.value = ''
}

const handlePetChange = () => {
  loadMetricSeries()
  loadPhotos()
}

const isSelected = (photoId) => selectedPhotos.value.includes(photoId)

const toggleSelect = (photoId) => {
  if (!editMode.value) return
  if (isSelected(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter((id) => id !== photoId)
  } else {
    selectedPhotos.value = [...selectedPhotos.value, photoId]
  }
}

const handleEditClick = () => {
  if (!editMode.value) {
    editMode.value = true
    selectedPhotos.value = []
    return
  }
  if (!selectedPhotos.value.length) {
    ElMessage.warning('请选择要删除的照片')
    return
  }
  deleteConfirmVisible.value = true
}

const confirmDeletePhotos = async () => {
  if (!currentPetId.value || !selectedPhotos.value.length) {
    deleteConfirmVisible.value = false
    return
  }
  try {
    await Promise.all(selectedPhotos.value.map((id) => deletePetPhoto(id, currentPetId.value)))
    ElMessage.success('已删除选中照片')
    selectedPhotos.value = []
    editMode.value = false
    await loadPhotos()
  } catch (error) {
    console.error('删除照片失败:', error)
    ElMessage.error(error?.message || '删除失败')
  } finally {
    deleteConfirmVisible.value = false
  }
}

const cancelDeletePhotos = () => {
  deleteConfirmVisible.value = false
}

watch(weightFullSeries, () => {
  if (weightWindowStart.value > weightSliderMax.value) {
    weightWindowStart.value = weightSliderMax.value
  }
})

watch(currentPetId, () => {
  loadMetricSeries()
  loadPhotos()
})

watch(tempFullSeries, () => {
  if (tempWindowStart.value > tempSliderMax.value) {
    tempWindowStart.value = tempSliderMax.value
  }
})

watch(
  photoList,
  () => {
    nextTick(() => syncPhotoPositions())
  },
  { deep: true }
)

watch(photoSize, () => {
  nextTick(() => syncPhotoPositions())
})

watch(activityFullSeries, () => {
  if (activityWindowStart.value > activitySliderMax.value) {
    activityWindowStart.value = activitySliderMax.value
  }
})

watch(intakeFullSeries, () => {
  if (intakeWindowStart.value > intakeSliderMax.value) {
    intakeWindowStart.value = intakeSliderMax.value
  }
})

watch(
  () => [weightFullSeries.value, weightWindowStart.value],
  () => nextTick(() => renderLineChart('weight', weightChartRef, weightFullSeries.value, '#5c7cfa', ' kg', weightWindowStart.value, trendDisplayCount)),
  { deep: true }
)

watch(
  () => [tempFullSeries.value, tempWindowStart.value],
  () => nextTick(() => renderLineChart('temperature', tempChartRef, tempFullSeries.value, '#34d399', ' °C', tempWindowStart.value, trendDisplayCount)),
  { deep: true }
)

watch(
  () => [activityFullSeries.value, activityWindowStart.value],
  () => nextTick(() => renderLineChart('activity', activityChartRef, activityFullSeries.value, '#f59e0b', '', activityWindowStart.value, trendDisplayCount)),
  { deep: true }
)

watch(
  () => [intakeFullSeries.value, intakeWindowStart.value],
  () => nextTick(() => renderLineChart('intake', intakeChartRef, intakeFullSeries.value, '#2563eb', ' ml', intakeWindowStart.value, trendDisplayCount)),
  { deep: true }
)

const handleShare = () => {
  if (!currentPetId.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  ElMessage.success('已生成分享链接（示例），可将档案发送给医生或家人')
}

const handleReminderComplete = (item) => {
  item.completed = true
  upcomingReminders.value = upcomingReminders.value.filter((r) => !r.completed)
}

const goToPetDetail = (petId) => {
  router.push(`/pets/${petId}`)
}

const goEditPet = () => {
  if (!currentPetId.value) return
  router.push(`/pets/${currentPetId.value}/edit`)
}

const goToReminders = () => {
  router.push('/reminders')
}

const goToAddAppointment = () => {
  router.push('/appointments/add')
}

const goToAppointments = () => {
  router.push('/appointments')
}

const navigateToHospital = () => {
  if (!nextAppointment.value) {
    ElMessage.info('暂无预约信息可导航')
    return
  }
  ElMessage.success('已为你预留导航入口，请在移动端打开导航应用')
}

const getStatusTagType = (status) => {
  const statusMap = {
    优秀: 'success',
    良好: 'info',
    一般: 'warning',
    需要关注: 'warning',
    不佳: 'danger'
  }
  return statusMap[status] || 'default'
}

const getReminderTagType = (days) => {
  if (days == null) return 'info'
  if (days <= 1) return 'danger'
  if (days <= 3) return 'warning'
  return 'info'
}

const getStatusType = (status) => {
  const statusMap = {
    待确认: 'info',
    已确认: 'success',
    已完成: 'success',
    已取消: 'danger',
    已过期: 'danger',
    患者爽约: 'danger',
    pending: 'info',
    confirmed: 'success',
    completed: 'success',
    cancelled: 'danger'
  }
  const normalized = status?.toLowerCase?.()
  return statusMap[status] || statusMap[normalized] || 'default'
}

const getTilt = (index) => {
  const tilts = [-3, -1.5, 2, 1, -2.5, 3.5, -1]
  return tilts[index % tilts.length]
}

const formatDateTime = (ts) => {
  if (!ts) return ''
  const d = new Date(ts)
  if (Number.isNaN(d.getTime())) return ''
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${m}-${day}`
}

const calcAge = (birth) => {
  const birthDate = new Date(birth)
  if (Number.isNaN(birthDate.getTime())) return ''
  const now = new Date()
  let years = now.getFullYear() - birthDate.getFullYear()
  let months = now.getMonth() - birthDate.getMonth()
  if (months < 0) {
    years -= 1
    months += 12
  }
  return years > 0 ? `${years}岁${months}月` : `${months}个月`
}

const enrichPetProfiles = (list, summaries = []) => {
  const summaryMap = summaries.reduce((acc, cur) => {
    if (cur?.petId) acc[cur.petId] = cur
    return acc
  }, {})
  const merged = list.map((p) => ({
    ...p,
    summary: summaryMap[p.petId] || null
  }))
  petProfiles.value = merged
  const hasCurrent = merged.find((p) => p.petId === currentPetId.value)
  if (!hasCurrent && merged.length) {
    currentPetId.value = merged[0].petId
  }
}

const ensureUserReady = async () => {
  if (!authStore.userInfo) {
    await authStore.fetchUserInfo?.()
  }
}

const loadDashboardData = async () => {
  try {
    loading.value = true
    reminderLoading.value = true
    appointmentLoading.value = true

    await ensureUserReady()
    const userParams = currentUserId.value ? { userId: currentUserId.value } : {}

    const [healthRes, reminderRes, appointmentRes, petListRes] = await Promise.all([
      getHealthSummary(),
      getUpcomingReminders({ size: 8, ...userParams }),
      getUpcomingAppointments({ size: 8, ...userParams }),
      getPetList({ size: 50, ...userParams })
    ])
    healthSummary.value = healthRes?.data || []
    const now = new Date()
    upcomingReminders.value = (reminderRes?.data?.records || []).map((item) => {
      const due = item.dueDate ? new Date(item.dueDate) : item.reminderDate ? new Date(item.reminderDate) : null
      const daysRemaining = due ? Math.ceil((due.getTime() - now.getTime()) / 86400000) : null
      return { ...item, daysRemaining, completed: item.completed }
    })
    upcomingAppointments.value = appointmentRes?.data?.records || []
    const petListData = petListRes?.data?.records || []
    enrichPetProfiles(petListData, healthSummary.value)
    await loadMetricSeries()
    await loadPhotos()
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  } finally {
    loading.value = false
    reminderLoading.value = false
    appointmentLoading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
  nextTick(() => {
    renderLineChart('weight', weightChartRef, weightFullSeries.value, '#5c7cfa', ' kg', weightWindowStart.value, trendDisplayCount)
    renderLineChart('temperature', tempChartRef, tempFullSeries.value, '#34d399', ' °C', tempWindowStart.value, trendDisplayCount)
    renderLineChart('activity', activityChartRef, activityFullSeries.value, '#f59e0b', '', activityWindowStart.value, trendDisplayCount)
    renderLineChart('intake', intakeChartRef, intakeFullSeries.value, '#2563eb', ' ml', intakeWindowStart.value, trendDisplayCount)
  })
  window.addEventListener('resize', syncPhotoPositions)
})

onBeforeUnmount(() => {
  Object.keys(chartInstances).forEach((key) => {
    if (chartInstances[key]) {
      chartInstances[key].dispose()
      chartInstances[key] = null
    }
  })
  window.removeEventListener('resize', syncPhotoPositions)
})
</script>

<style scoped>
.home-container {
  padding: 16px 18px 32px;
  background: #f5f7fb;
  max-width: 80%;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 16px;
  padding: 18px 20px;
  background: linear-gradient(120deg, #f2f7ff, #ecf5ff);
  border: 1px solid #e1e8f5;
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(33, 58, 94, 0.08);
}

.welcome-left {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.welcome-text h2 {
  margin: 4px 0 6px;
}

.welcome-sub {
  margin: 0;
  color: #607080;
  font-size: 14px;
}

.welcome-date {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
}

.welcome-badges .el-tag + .el-tag {
  margin-left: 8px;
}

.badge-dropdown {
  margin-left: 5px;
}

.section-row {
  margin-top: 18px;
}

.photo-wall {
  overflow: hidden;
}

.photo-wall :deep(.el-card__body) {
  height: 680px;
  padding: 0 !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-wall-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.photo-edit-btn {
  order: 0;
}

.photo-upload-btn {
  order: 1;
}

.card-title {
  font-weight: 700;
  color: #1f2d3d;
}

.pet-switcher {
  display: flex;
  align-items: center;
  gap: 10px;
}

.overview {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.overview-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pet-face {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pet-avatar {
  width: 86px;
  height: 86px;
  border-radius: 14px;
  object-fit: cover;
  border: 2px solid #eef2ff;
}

.pet-basic {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pet-name {
  font-size: 20px;
  font-weight: 700;
}

.pet-tags .el-tag {
  margin-right: 6px;
  margin-bottom: 4px;
}

.pet-actions {
  display: flex;
  gap: 10px;
}

.overview-divider {
  margin: 0;
}

.overview-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-item {
  background: #f7f9ff;
  border: 1px solid #e6ecf5;
  border-radius: 10px;
  padding: 10px;
}

.stat-item p {
  margin: 0 0 6px;
  color: #64748b;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.reminder-timeline {
  padding-left: 6px;
}

.reminder-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reminder-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.reminder-message {
  margin: 0;
  color: #475569;
}

.visit-box {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.visit-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.visit-label {
  color: #64748b;
  font-size: 13px;
}

.visit-value {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
}

.visit-meta {
  color: #475569;
  font-size: 13px;
}

.visit-actions {
  display: flex;
  gap: 8px;
}

.trend-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.trend-item {
  background: #f8fbff;
  border: 1px solid #e6edf7;
  border-radius: 12px;
  padding: 12px;
}

.trend-line {
  grid-column: span 1;
}

.trend-header {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  margin-bottom: 6px;
}

.trend-sub {
  margin-left: auto;
  color: #94a3b8;
  font-size: 13px;
}

.trend-add {
  padding: 4px;
  color: #475569;
}

.trend-add :deep(.el-icon) {
  font-size: 16px;
}

.photo-grid {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 680px;
  max-height: 680px;
  margin: 0 auto;
  overflow: hidden;
  padding: 0;
  background-color: #ffffff;
  border: 10px solid rgba(64, 158, 255, 0.1);
  box-shadow:
    inset 0 0 0 1px rgba(64, 158, 255, 0.05),
    0 6px 18px rgba(59, 130, 246, 0.02);
}

.photo-grid::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(to right, transparent 0, transparent 24px, rgba(64, 158, 255, 0.06) 24px, rgba(64, 158, 255, 0.035) 25.4px, rgba(64, 158, 255, 0.06) 26.8px, transparent 28px, transparent 50px),
    repeating-linear-gradient(to bottom, transparent 0, transparent 24px, rgba(64, 158, 255, 0.06) 24px, rgba(64, 158, 255, 0.035) 25.4px, rgba(64, 158, 255, 0.06) 26.8px, transparent 28px, transparent 50px),
    repeating-linear-gradient(135deg, rgba(64, 158, 255, 0.015) 0, rgba(64, 158, 255, 0.015) 1px, transparent 1px, transparent 30px),
    radial-gradient(circle at 18% 18%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 50% 18%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 82% 18%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 18% 50%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 82% 50%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 18% 82%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 50% 82%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px),
    radial-gradient(circle at 82% 82%, rgba(64, 158, 255, 0.06) 0 3px, transparent 6px);
  background-size: 100% 100%, 100% 100%, 30px 30px, auto, auto, auto, auto, auto, auto, auto, auto;
  background-position: center, center, 0 0, center, center, center, center, center, center, center, center, center;
  background-repeat: no-repeat, no-repeat, repeat, no-repeat, no-repeat, no-repeat, no-repeat, no-repeat, no-repeat, no-repeat, no-repeat, no-repeat;
  transform: none;
  pointer-events: none;
  z-index: 0;
}

.photo-grid::after {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image:
    radial-gradient(circle at 16% 18%, rgba(96, 165, 250, 0.28) 0 14px, transparent 15px),
    radial-gradient(circle at 82% 22%, rgba(99, 102, 241, 0.22) 0 13px, transparent 14px),
    radial-gradient(circle at 12% 72%, rgba(244, 114, 182, 0.2) 0 16px, transparent 17px),
    radial-gradient(circle at 74% 68%, rgba(59, 130, 246, 0.18) 0 18px, transparent 20px),
    radial-gradient(circle at 44% 82%, rgba(250, 204, 21, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 28% 38%, rgba(52, 211, 153, 0.18) 0 13px, transparent 14px),
    radial-gradient(circle at 60% 36%, rgba(248, 113, 113, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 88% 52%, rgba(125, 211, 252, 0.2) 0 14px, transparent 15px),
    radial-gradient(circle at 10% 54%, rgba(59, 130, 246, 0.16) 0 10px, transparent 12px),
    radial-gradient(circle at 70% 30%, rgba(52, 211, 153, 0.14) 0 10px, transparent 12px),
    radial-gradient(circle at 52% 12%, rgba(244, 114, 182, 0.12) 0 9px, transparent 11px),
    radial-gradient(circle at 32% 60%, rgba(255, 255, 255, 0.32) 0 18px, transparent 19px),
    radial-gradient(circle at 64% 46%, rgba(236, 254, 255, 0.28) 0 16px, transparent 17px),
    radial-gradient(circle at 22% 32%, rgba(219, 234, 254, 0.26) 0 14px, transparent 15px),
    radial-gradient(circle at 78% 80%, rgba(214, 225, 255, 0.22) 0 20px, transparent 21px),
    radial-gradient(circle at 40% 78%, rgba(226, 232, 240, 0.28) 0 18px, transparent 19px),
    radial-gradient(circle at 18% 88%, rgba(191, 219, 254, 0.22) 0 16px, transparent 17px),
    radial-gradient(circle at 86% 38%, rgba(186, 230, 253, 0.24) 0 14px, transparent 15px),
    radial-gradient(circle at 58% 26%, rgba(221, 214, 254, 0.2) 0 15px, transparent 16px),
    radial-gradient(circle at 46% 70%, rgba(219, 234, 254, 0.24) 0 14px, transparent 15px),
    radial-gradient(circle at 72% 48%, rgba(236, 254, 255, 0.22) 0 18px, transparent 19px),
    radial-gradient(circle at 34% 22%, rgba(194, 230, 255, 0.2) 0 12px, transparent 13px),
    radial-gradient(circle at 62% 84%, rgba(209, 213, 219, 0.2) 0 16px, transparent 17px),
    radial-gradient(circle at 24% 46%, rgba(214, 188, 250, 0.16) 0 13px, transparent 14px),
    radial-gradient(circle at 54% 56%, rgba(196, 181, 253, 0.18) 0 14px, transparent 15px),
    radial-gradient(circle at 12% 14%, rgba(240, 249, 255, 0.26) 0 12px, transparent 14px),
    radial-gradient(circle at 90% 16%, rgba(224, 242, 254, 0.22) 0 11px, transparent 13px),
    radial-gradient(circle at 8% 44%, rgba(219, 234, 254, 0.2) 0 13px, transparent 15px),
    radial-gradient(circle at 68% 12%, rgba(226, 232, 240, 0.2) 0 10px, transparent 12px),
    radial-gradient(circle at 56% 92%, rgba(191, 219, 254, 0.22) 0 14px, transparent 15px),
    radial-gradient(circle at 30% 16%, rgba(214, 225, 255, 0.2) 0 10px, transparent 12px),
    radial-gradient(circle at 48% 28%, rgba(236, 254, 255, 0.2) 0 13px, transparent 15px),
    radial-gradient(circle at 76% 58%, rgba(209, 213, 219, 0.18) 0 12px, transparent 14px),
    radial-gradient(circle at 20% 58%, rgba(236, 254, 255, 0.22) 0 11px, transparent 13px),
    radial-gradient(circle at 62% 42%, rgba(191, 219, 254, 0.2) 0 12px, transparent 14px),
    radial-gradient(circle at 14% 26%, rgba(236, 254, 255, 0.22) 0 12px, transparent 13px),
    radial-gradient(circle at 84% 74%, rgba(209, 213, 219, 0.18) 0 14px, transparent 15px),
    radial-gradient(circle at 32% 86%, rgba(214, 188, 250, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 68% 66%, rgba(125, 211, 252, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 92% 44%, rgba(191, 219, 254, 0.18) 0 10px, transparent 12px),
    radial-gradient(circle at 6% 24%, rgba(219, 234, 254, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 26% 12%, rgba(236, 254, 255, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 54% 8%, rgba(191, 219, 254, 0.16) 0 10px, transparent 11px),
    radial-gradient(circle at 78% 14%, rgba(214, 188, 250, 0.14) 0 11px, transparent 12px),
    radial-gradient(circle at 94% 70%, rgba(226, 232, 240, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 8% 68%, rgba(236, 254, 255, 0.2) 0 13px, transparent 14px),
    radial-gradient(circle at 52% 92%, rgba(209, 213, 219, 0.18) 0 13px, transparent 14px),
    radial-gradient(circle at 74% 92%, rgba(219, 234, 254, 0.2) 0 12px, transparent 13px),
    radial-gradient(circle at 46% 46%, rgba(191, 219, 254, 0.16) 0 10px, transparent 11px),
    radial-gradient(circle at 14% 58%, rgba(226, 232, 240, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 62% 18%, rgba(191, 219, 254, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 88% 30%, rgba(236, 254, 255, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 36% 72%, rgba(209, 213, 219, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 20% 20%, rgba(214, 225, 255, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 66% 34%, rgba(219, 234, 254, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 6% 10%, rgba(236, 254, 255, 0.18) 0 10px, transparent 11px),
    radial-gradient(circle at 18% 14%, rgba(219, 234, 254, 0.16) 0 9px, transparent 10px),
    radial-gradient(circle at 42% 16%, rgba(214, 225, 255, 0.16) 0 11px, transparent 12px),
    radial-gradient(circle at 58% 12%, rgba(191, 219, 254, 0.14) 0 10px, transparent 11px),
    radial-gradient(circle at 72% 10%, rgba(219, 234, 254, 0.14) 0 9px, transparent 10px),
    radial-gradient(circle at 88% 8%, rgba(226, 232, 240, 0.14) 0 9px, transparent 10px),
    radial-gradient(circle at 10% 82%, rgba(214, 225, 255, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 30% 92%, rgba(191, 219, 254, 0.18) 0 10px, transparent 11px),
    radial-gradient(circle at 70% 90%, rgba(219, 234, 254, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 88% 86%, rgba(226, 232, 240, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 12% 50%, rgba(236, 254, 255, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 26% 64%, rgba(214, 188, 250, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 42% 82%, rgba(191, 219, 254, 0.18) 0 12px, transparent 13px),
    radial-gradient(circle at 58% 74%, rgba(219, 234, 254, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 78% 64%, rgba(236, 254, 255, 0.16) 0 12px, transparent 13px),
    radial-gradient(circle at 92% 58%, rgba(226, 232, 240, 0.16) 0 11px, transparent 12px),
    radial-gradient(circle at 6% 60%, rgba(214, 225, 255, 0.18) 0 10px, transparent 11px),
    radial-gradient(circle at 18% 36%, rgba(191, 219, 254, 0.18) 0 10px, transparent 11px),
    radial-gradient(circle at 52% 34%, rgba(219, 234, 254, 0.16) 0 10px, transparent 11px),
    radial-gradient(circle at 66% 52%, rgba(236, 254, 255, 0.18) 0 11px, transparent 12px),
    radial-gradient(circle at 84% 66%, rgba(214, 225, 255, 0.18) 0 10px, transparent 11px),
    radial-gradient(circle at 34% 74%, rgba(226, 232, 240, 0.18) 0 11px, transparent 12px);
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
  mix-blend-mode: multiply;
  z-index: 0;
}

.photo-item {
  position: absolute;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: visible;
  border: 1px solid #e5e7eb;
  background: #fafafa;
  z-index: 1;
  box-shadow: 0 10px 18px rgba(0, 0, 0, 0.08);
  cursor: grab;
  transition: box-shadow 0.18s ease, transform 0.18s ease, top 0.18s ease, left 0.18s ease;
}

.photo-item.editing {
  box-shadow: 0 12px 20px rgba(239, 68, 68, 0.16);
  border-color: #fecaca;
}

.photo-item.dragging {
  opacity: 0.8;
  z-index: 2;
  box-shadow: 0 18px 26px rgba(0, 0, 0, 0.18);
  cursor: grabbing;
}

.photo-item::before {
  content: '\2665';
  position: absolute;
  top: -18px;
  left: 50%;
  transform: translateX(-50%) rotate(-2deg);
  width: 26px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border: 1px solid #fecdd3;
  border-radius: 8px;
  color: #ef4444;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.14);
}

.photo-item::after {
  content: '';
  position: absolute;
  top: -16px;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 12px;
  background: rgba(125, 211, 252, 0.7);
}

.photo-item :deep(.el-image) {
  width: 100%;
  height: 100%;
  display: block;
  border-radius: 8px;
  overflow: hidden;
}

.photo-img-wrapper {
  width: 100%;
  overflow: hidden;
  border-radius: 8px;
}

.photo-img {
  width: 100%;
  height: 100%;
  display: block;
}

.photo-meta {
  display: none;
}

.photo-name {
  font-weight: 600;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-date {
  color: #94a3b8;
  white-space: nowrap;
}

.photo-sticker {
  position: absolute;
  top: 6px;
  left: 6px;
  background: rgba(255, 255, 255, 0.85);
  padding: 4px 6px;
  border-radius: 6px;
  font-size: 13px;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
}

.photo-select {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid #94a3b8;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  color: transparent;
  cursor: pointer;
  transition: all 0.15s ease;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.15);
  z-index: 3;
  pointer-events: auto;
}

.photo-select.checked {
  background: #ef4444;
  border-color: #ef4444;
  color: #fff;
}

.photo-select span {
  font-size: 13px;
  font-weight: 700;
  line-height: 1;
}

.preview-box {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-img {
  max-width: 100%;
  max-height: 70vh;
  display: block;
}

.trend-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.line-chart {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 30px;
}

.echart {
  width: 100%;
  height: 220px;
}

.point-label {
  paint-order: stroke;
  stroke: #fff;
  stroke-width: 3px;
  stroke-linejoin: round;
}

.chart-legend {
  display: flex;
  justify-content: space-between;
  color: #94a3b8;
  font-size: 12px;
}

.chart-range {
  color: #475569;
}

.trend-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 6px;
}

.trend-number {
  font-size: 20px;
  font-weight: 800;
}

.trend-unit {
  color: #94a3b8;
}

.trend-spark {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 3px;
  margin-top: 8px;
  height: 46px;
  align-items: end;
}

.trend-spark span {
  display: block;
  width: 100%;
  background: linear-gradient(180deg, #60a5fa, #2563eb);
  border-radius: 4px 4px 2px 2px;
}

.fixed-card {
  height: 520px;
  display: flex;
  flex-direction: column;
}

.fixed-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
}


.contact-actions {
  display: flex;
  gap: 8px;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.tip-item {
  border: 1px solid #e6ecf5;
  border-radius: 10px;
  padding: 12px;
  background: #fdfefe;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.tip-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  margin-bottom: 6px;
}

.tip-desc {
  color: #475569;
  line-height: 1.5;
  font-size: 13px;
}

.metric-form .el-input,
.metric-form .el-textarea {
  width: 100%;
}
</style>
