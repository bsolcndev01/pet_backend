<!-- src/views/reminder/ReminderList.vue（健康提醒列表页） -->
<template>
  <div class="reminder-container appointment-list-container">
    <div class="page-title">提醒列表</div>
    <div class="list-card-wrapper">
      <el-card class="list-card" v-loading="loading">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-select v-model="currentPetId" placeholder="全部宠物" clearable style="width: 180px" @change="filterByPet">
              <el-option
                v-for="pet in petOptions"
                :key="pet.petId"
                :label="pet.petName"
                :value="pet.petId"
              />
            </el-select>
            <el-button @click="resetPetFilter" text size="small">重置</el-button>
          </div>
          <div class="toolbar-right">
            <el-button type="primary" size="small" class="calendar-btn" @click="showCalendar = true">
              <el-icon><Calendar /></el-icon>
              日历
            </el-button>
          </div>
        </div>
        <el-table
          :data="filteredReminders"
          border
          style="width: 100%"
          empty-text="暂无健康提醒"
        >
          <el-table-column prop="petName" label="宠物名称" width="120" />
          <el-table-column prop="reminderTypeName" label="提醒类型" width="120">
            <template #default="scope">
              <el-tag :type="getTypeTagType(scope.row.reminderTypeName)">
                {{ scope.row.reminderTypeName || '其他' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reminderDate" label="提醒日期" width="120" />
          <el-table-column prop="dueDate" label="到期日" width="120" />
          <el-table-column prop="title" label="提醒标题" width="200" />
          <el-table-column prop="message" label="提醒内容" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.completed ? 'success' : 'warning'">
                {{ scope.row.completed ? '已完成' : '未处理' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button
                v-if="!scope.row.completed"
                type="primary"
                size="small"
                @click="handleComplete(scope.row.reminderId)"
              >
                标记完成
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 日历弹窗 -->
    <el-dialog v-model="showCalendar" title="提醒日历" width="800px">
      <div v-loading="calendarLoading">
        <el-calendar v-model="calendarValue">
          <template #date-cell="{ data }">
            <div class="date-cell">
              <div class="date-number">{{ data.day.split('-')[2] }}</div>
              <div class="date-reminders">
                <el-tag
                  v-for="item in calendarMap[data.day] || []"
                  :key="item.reminderId"
                  :type="getTypeTagType(item.reminderTypeName)"
                  size="small"
                  disable-transitions
                >
                  {{ item.title || item.reminderTypeName || '提醒' }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-calendar>
      </div>
      <template #footer>
        <el-button @click="showCalendar = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar } from '@element-plus/icons-vue'
import { getAllReminders, getUpcomingReminders } from '@/api/reminder'
import { getPetList } from '@/api/pet'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const currentUserId = computed(
  () => authStore.userInfo?.id ?? authStore.userInfo?.userId ?? authStore.userInfo?.userID ?? null
)

// 健康提醒列表
const reminderList = ref([])
const calendarReminders = ref([])
const loading = ref(false)
const calendarLoading = ref(false)
const calendarValue = ref(new Date())
const showCalendar = ref(false)
const petOptions = ref([])
const currentPetId = ref(null)
const ownedPetIdSet = computed(() => new Set(petOptions.value.map((pet) => pet.petId)))

const userScopedReminders = computed(() => {
  if (!currentUserId.value) return []
  const scopedByUser = reminderList.value.filter((item) => item.userId === currentUserId.value)
  if (!petOptions.value.length) return scopedByUser
  const petIds = ownedPetIdSet.value
  return scopedByUser.filter((item) => !item.petId || petIds.has(item.petId))
})

const filteredReminders = computed(() => {
  const source = userScopedReminders.value
  if (!currentPetId.value) return source
  return source.filter((item) => item.petId === currentPetId.value)
})

const calendarScopedReminders = computed(() => {
  if (!currentUserId.value) return []
  const scopedByUser = calendarReminders.value.filter((item) => item.userId === currentUserId.value)
  if (!petOptions.value.length) return scopedByUser
  const petIds = ownedPetIdSet.value
  return scopedByUser.filter((item) => !item.petId || petIds.has(item.petId))
})

const calendarMap = computed(() => {
  const map = {}
  calendarScopedReminders.value.forEach((item) => {
    const day = item.dueDate || item.reminderDate
    if (!day) return
    const key = day.split('T')[0] || day
    if (!map[key]) map[key] = []
    map[key].push(item)
  })
  return map
})

// 标记为完成
const handleComplete = (id) => {
  // 暂无后端完成接口，先前端标记
  const item = reminderList.value.find((it) => it.reminderId === id)
  if (!item) return
  item.completed = true
  ElMessage.success('提醒已标记为完成')
}

// 选择宠物过滤
const filterByPet = () => {
  // computed filteredReminders 已处理
}

const resetPetFilter = () => {
  currentPetId.value = null
}

// 获取类型标签样式
const getTypeTagType = (type) => {
  const map = {
    疫苗: 'primary',
    驱虫: 'success',
    复诊: 'warning',
    其他: 'info'
  }
  return map[type] || 'info'
}

const loadReminders = async () => {
  if (!currentUserId.value) {
    console.warn('缺少用户ID，已阻止加载提醒以避免误显示其他账号数据')
    reminderList.value = []
    return
  }
  try {
    loading.value = true
    const res = await getAllReminders({
      page: 1,
      size: 100,
      ...(currentUserId.value ? { userId: currentUserId.value } : {})
    })
    reminderList.value = res?.data?.records || []
  } catch (error) {
    console.error('加载提醒失败:', error)
    ElMessage.error(error?.message || '加载提醒失败')
  } finally {
    loading.value = false
  }
}

const loadCalendarReminders = async () => {
  if (!currentUserId.value) {
    console.warn('缺少用户ID，已阻止加载提醒日历数据')
    calendarReminders.value = []
    return
  }
  try {
    calendarLoading.value = true
    const res = await getUpcomingReminders({
      page: 1,
      size: 100,
      ...(currentUserId.value ? { userId: currentUserId.value } : {})
    })
    calendarReminders.value = res?.data?.records || []
  } catch (error) {
    console.error('加载提醒日历失败:', error)
    ElMessage.error(error?.message || '加载提醒日历失败')
  } finally {
    calendarLoading.value = false
  }
}

const loadPets = async () => {
  if (!currentUserId.value) {
    console.warn('缺少用户ID，已阻止加载宠物列表以避免误显示其他账号数据')
    petOptions.value = []
    return
  }
  try {
    const res = await getPetList({
      page: 1,
      size: 100,
      ...(currentUserId.value ? { userId: currentUserId.value } : {})
    })
    petOptions.value = res?.data?.records || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

const ensureUserReady = async () => {
  if (!authStore.userInfo) {
    await authStore.fetchUserInfo?.()
  }
}

onMounted(async () => {
  await ensureUserReady()
  await Promise.all([loadPets(), loadReminders(), loadCalendarReminders()])
})
</script>

<style scoped>
.reminder-container {
  padding: 20px;
  background: var(--bg);
  min-height: calc(100vh - 72px);
}

.list-card-wrapper {
  margin-top: 12px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-1);
  margin-bottom: 12px;
}

.list-card {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.calendar-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.date-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date-number {
  font-weight: 700;
}

.date-reminders {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.calendar-preview {
  min-height: 60px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.mr-1 {
  margin-right: 4px;
}

.empty-preview {
  color: #909399;
  font-size: 13px;
}

.mt-4 {
  margin-top: 20px;
}
</style>
