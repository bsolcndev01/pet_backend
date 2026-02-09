<template>
<div class="appointment-container appointment-list-container">
  <div class="page-title">预约管理列表</div>
    
    <el-row :gutter="20" class="mt-4">
      <el-col :span="6">
        <el-select v-model="selectedPetId" placeholder="请选择宠物" clearable @change="applyFilter">
          <el-option 
            v-for="pet in petList" 
            :key="pet.petId" 
            :label="pet.petName" 
            :value="pet.petId"
          />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-select v-model="appointmentStatus" placeholder="预约状态" clearable @change="applyFilter">
          <el-option label="全部" value="" />
          <el-option label="待确认" value="待确认" />
          <el-option label="已确认" value="已确认" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="handleAddAppointment">
          <el-icon><Plus /></el-icon> 新增预约
        </el-button>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <el-table 
        v-loading="loading"
        :data="filteredAppointments" 
        border 
        style="width: 100%"
        empty-text="暂无预约记录，请先创建预约"
      >
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="institutionName" label="就诊机构" />
        <el-table-column prop="appointmentType" label="预约类型">
          <template #default="scope">
            <el-tag>{{ scope.row.appointmentType || '诊疗' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="140" />
        <el-table-column prop="appointmentTime" label="预约时间" width="120">
          <template #default="scope">
            {{ scope.row.appointmentTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              link
              circle
              size="small"
              @click="openDetail(scope.row)"
            >
              <el-icon><Document /></el-icon>
            </el-button>
            <el-button
              type="success"
              link
              circle
              size="small"
              @click="openEdit(scope.row)"
            >
              <el-icon><EditPen /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="预约详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约编号">{{ display(activeAppointment.appointmentId) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(activeAppointment.status) }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{ display(activeAppointment.petName || activeAppointment.petId) }}</el-descriptions-item>
        <el-descriptions-item label="预约类型">{{ display(activeAppointment.appointmentType) }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ display(activeAppointment.appointmentDate) }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ display(activeAppointment.appointmentTime) }}</el-descriptions-item>
        <el-descriptions-item label="就诊机构" :span="2">
          {{ display(activeAppointment.institutionName || activeAppointment.institutionId) }}
        </el-descriptions-item>
        <el-descriptions-item label="医生用户">
          {{ display(activeAppointment.vetUsername || activeAppointment.vetUserId) }}
        </el-descriptions-item>
        <el-descriptions-item label="预约人">
          {{ display(activeAppointment.ownerUsername || activeAppointment.ownerUserId) }}
        </el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">{{ display(activeAppointment.reason) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ display(activeAppointment.notes) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ display(activeAppointment.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ display(activeAppointment.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑预约" width="680px">
      <el-form :model="editForm" label-width="100px" class="edit-form">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="预约编号">
              <span>{{ display(editForm.appointmentId) }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宠物">
              <span>{{ display(editForm.petName || editForm.petId) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="预约类型">
              <el-select
                v-model="editForm.appointmentType"
                placeholder="请选择预约类型"
                :disabled="!editAllEnabled"
              >
                <el-option
                  v-for="option in appointmentTypeOptions"
                  :key="option"
                  :label="option"
                  :value="option"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约状态">
              <el-select v-model="editForm.status" placeholder="请选择状态">
                <el-option
                  v-for="option in statusOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="就诊机构">
              <el-select
                v-model="editForm.institutionId"
                placeholder="请选择机构或手动输入"
                filterable
                allow-create
                default-first-option
                clearable
                :disabled="!editAllEnabled"
              >
                <el-option
                  v-for="inst in institutionOptions"
                  :key="inst.id"
                  :label="inst.name"
                  :value="inst.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接诊医生">
              <el-select
                v-model="editForm.vetUserId"
                placeholder="可选，指定医生或手动输入ID"
                clearable
                filterable
                allow-create
                default-first-option
                :disabled="!editAllEnabled"
              >
                <el-option label="不指定" :value="null" />
                <el-option
                  v-for="vet in vetOptions"
                  :key="vet.userId"
                  :label="vet.username"
                  :value="vet.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="预约时间">
          <div class="edit-datetime-group">
            <el-date-picker
              v-model="editForm.appointmentDate"
              type="date"
              placeholder="请选择预约日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledPastDate"
              :disabled="!editAllEnabled"
            />
            <el-time-picker
              v-model="editForm.appointmentTime"
              placeholder="请选择时间"
              format="HH:mm:ss"
              value-format="HH:mm:ss"
              :disabled="!editAllEnabled"
            />
          </div>
        </el-form-item>
        <el-form-item label="预约原因">
          <el-input
            v-model="editForm.reason"
            type="textarea"
            :rows="2"
            placeholder="请输入预约原因（选填）"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入预约备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, EditPen } from '@element-plus/icons-vue'
import { getAppointmentList, editAppointment } from '@/api/appointment'
import { getPetList } from '@/api/pet'
import { getInstitutions, getVets } from '@/api/health'

const router = useRouter()
const petList = ref([])
// 筛选条件
const selectedPetId = ref('')
const appointmentStatus = ref('')
// 预约列表
const appointmentList = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const activeAppointment = ref({})
const editVisible = ref(false)
const editSubmitting = ref(false)
const initializingEdit = ref(false)
const editAllEnabled = ref(false)
const institutionOptions = ref([])
const vetOptions = ref([])
const editForm = ref({
  appointmentId: null,
  petId: null,
  petName: '',
  vetUserId: null,
  institutionId: null,
  appointmentDate: null,
  appointmentTime: null,
  appointmentType: '',
  reason: '',
  status: '',
  notes: ''
})
const petIdSet = computed(() => new Set(petList.value.map((p) => p.petId)))
const statusOptions = [
  { label: '待确认', value: '待确认' },
  { label: '已确认', value: '已确认' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' }
]
const appointmentTypeOptions = ['诊疗', '体检', '疫苗接种', '复诊', '急诊', '其他']

// 过滤后的列表
const filteredAppointments = computed(() => {
  return appointmentList.value.filter((item) => {
    if (petIdSet.value.size && !petIdSet.value.has(item.petId)) return false
    const matchPet = selectedPetId.value ? item.petId === selectedPetId.value : true
    const matchStatus = appointmentStatus.value ? item.status === appointmentStatus.value : true
    return matchPet && matchStatus
  })
})

// 新增预约
const handleAddAppointment = () => {
  router.push('/appointments/add')
}

const loadPets = async () => {
  try {
    const res = await getPetList({ page: 1, size: 100 })
    petList.value = res?.data?.records || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

const loadAppointments = async () => {
  try {
    loading.value = true
    const res = await getAppointmentList({ page: 1, size: 100 })
    appointmentList.value = res?.data?.records || []
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error(error?.message || '加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  // 触发 computed 重新计算即可，无需额外处理
}

const openDetail = (row) => {
  activeAppointment.value = row || {}
  detailVisible.value = true
}

const normalizeTimeValue = (value) => {
  if (!value) return ''
  if (typeof value === 'string' && value.length === 5) return `${value}:00`
  return value
}

const normalizeStatusValue = (status) => {
  const normalized = status?.toLowerCase?.()
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[normalized] || status || '待确认'
}

const openEdit = (row) => {
  if (!row) return
  initializingEdit.value = true
  editAllEnabled.value = normalizeStatusValue(row.status) === '待确认'
  editForm.value = {
    appointmentId: row.appointmentId,
    petId: row.petId,
    petName: row.petName,
    vetUserId: row.vetUserId,
    institutionId: row.institutionId,
    appointmentDate: row.appointmentDate,
    appointmentTime: normalizeTimeValue(row.appointmentTime),
    appointmentType: row.appointmentType || '诊疗',
    reason: row.reason,
    status: normalizeStatusValue(row.status),
    notes: row.notes
  }
  editVisible.value = true
  reloadVets(true).finally(() => {
    initializingEdit.value = false
  })
}

const submitEdit = async () => {
  if (!editForm.value.appointmentId) return
  if (!editForm.value.appointmentDate) {
    ElMessage.error('缺少预约日期，无法保存')
    return
  }
  try {
    editSubmitting.value = true
    await editAppointment(editForm.value.appointmentId, {
      petId: editForm.value.petId,
      vetUserId: editForm.value.vetUserId || null,
      institutionId: editForm.value.institutionId || null,
      appointmentDate: editForm.value.appointmentDate,
      appointmentTime: editForm.value.appointmentTime || null,
      appointmentType: editForm.value.appointmentType || '诊疗',
      reason: editForm.value.reason,
      status: editForm.value.status,
      notes: editForm.value.notes
    })
    ElMessage.success('预约已更新')
    editVisible.value = false
    loadAppointments()
  } catch (error) {
    console.error('更新预约失败:', error)
    ElMessage.error(error?.message || '更新预约失败')
  } finally {
    editSubmitting.value = false
  }
}

const disabledPastDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() <= today.getTime()
}

const loadMeta = async () => {
  try {
    const [instRes, vetRes] = await Promise.all([getInstitutions(), getVets()])
    institutionOptions.value = instRes?.data || []
    vetOptions.value = vetRes?.data || []
  } catch (error) {
    console.error('加载机构/医生失败:', error)
    ElMessage.error('加载机构或医生列表失败')
  }
}

const reloadVets = async (preserveVet = false) => {
  try {
    const params = editForm.value.institutionId ? { institutionId: editForm.value.institutionId } : {}
    const vetRes = await getVets(params)
    vetOptions.value = vetRes?.data || []
    if (!preserveVet) {
      editForm.value.vetUserId = ''
    }
  } catch (error) {
    console.error('加载医生失败:', error)
    ElMessage.error('加载医生列表失败')
  }
}

watch(
  () => editForm.value.institutionId,
  (next, prev) => {
    if (!editVisible.value || !editAllEnabled.value || initializingEdit.value || next === prev) return
    reloadVets()
  }
)

// 获取状态标签类型
const getStatusType = (status) => {
  const normalized = status?.toLowerCase?.() || status
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
  return map[status] || map[normalized] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const normalized = status?.toLowerCase?.()
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[normalized] || status || '未知'
}

const display = (value) => {
  if (value === null || value === undefined || value === '') return '未填写'
  return value
}

onMounted(() => {
  loadPets()
  loadAppointments()
  loadMeta()
})
</script>

<style scoped>
.appointment-container {
  padding: 20px;
  background: var(--bg);
  min-height: calc(100vh - 72px);
  width: 80vw;
  margin: 0 auto;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

:global(.appointment-container::-webkit-scrollbar) {
  width: 0 !important;
  height: 0 !important;
  display: none !important;
}

:global(.appointment-container .el-table__body-wrapper) {
  scrollbar-width: none;
}

:global(.appointment-container .el-table__body-wrapper::-webkit-scrollbar) {
  width: 0 !important;
  height: 0 !important;
  display: none !important;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-1);
  margin-bottom: 12px;
}

.mt-4 {
  margin-top: 20px;
}

.edit-form .edit-datetime-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
