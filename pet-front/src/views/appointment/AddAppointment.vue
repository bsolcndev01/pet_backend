<template>
  <div class="add-appointment-container">
    <el-page-header @back="handleBack" content="新增预约" />
    
    <el-card class="mt-4">
      <el-form
        ref="appointmentFormRef"
        :model="appointmentForm"
        :rules="appointmentRules"
        label-width="120px"
        class="appointment-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物名称" prop="petId">
              <el-select v-model="appointmentForm.petId" placeholder="请选择宠物">
                <el-option 
                  v-for="pet in petList" 
                  :key="pet.petId" 
                  :label="pet.petName" 
                  :value="pet.petId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约类型" prop="appointmentType">
              <el-select v-model="appointmentForm.appointmentType" placeholder="请选择预约类型">
                <el-option label="诊疗" value="诊疗" />
                <el-option label="体检" value="体检" />
                <el-option label="疫苗接种" value="疫苗接种" />
                <el-option label="复诊" value="复诊" />
                <el-option label="急诊" value="急诊" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="就诊机构" prop="institutionId">
              <el-select
                v-model="appointmentForm.institutionId"
                placeholder="请选择机构或手动输入"
                filterable
                allow-create
                default-first-option
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
            <el-form-item label="接诊医生" prop="vetUserId">
              <el-select
                v-model="appointmentForm.vetUserId"
                placeholder="可选，指定医生或手动输入ID"
                clearable
                filterable
                allow-create
                default-first-option
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
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约时间" prop="appointmentDate">
              <div class="datetime-group">
                <el-date-picker
                  v-model="appointmentForm.appointmentDate"
                  type="date"
                  placeholder="请选择预约日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledPastDate"
                />
                <el-time-picker
                  v-model="appointmentForm.appointmentTime"
                  placeholder="请选择时间"
                  format="HH:mm:ss"
                  value-format="HH:mm:ss"
                />
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="预约原因" prop="reason">
          <el-input
            v-model="appointmentForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入预约原因（选填）"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="appointmentForm.notes"
            type="textarea"
            :rows="4"
            placeholder="请输入预约备注（选填），如宠物特殊情况、需求等"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交预约</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addAppointment } from '@/api/appointment'
import { getPetList } from '@/api/pet'
import { getInstitutions, getVets } from '@/api/health'

const router = useRouter()

// 宠物列表（从后端获取）
const petList = ref([])
const institutionOptions = ref([])
const vetOptions = ref([])

// 表单引用
const appointmentFormRef = ref(null)

// 预约表单
const appointmentForm = reactive({
  petId: '',
  vetUserId: '',
  appointmentType: '诊疗',
  institutionId: '',
  appointmentDate: '',
  appointmentTime: '',
  reason: '',
  notes: ''
})

// 表单验证规则
const appointmentRules = reactive({
  petId: [
    { required: true, message: '请选择宠物', trigger: 'change' }
  ],
  appointmentType: [
    { required: true, message: '请选择预约类型', trigger: 'change' }
  ],
  institutionId: [
    { required: true, message: '请选择就诊机构', trigger: 'change' }
  ],
  appointmentDate: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ],
  appointmentTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ]
})

// 禁用过去的日期
const disabledPastDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime() // 仅禁用过去日期
}

const submitting = ref(false)

// 返回上一页
const handleBack = () => {
  router.push('/appointments')
}

const loadPets = async () => {
  try {
    const res = await getPetList({ page: 1, size: 100 })
    petList.value = res?.data?.records || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    ElMessage.error('加载宠物列表失败')
  }
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

const reloadVets = async () => {
  try {
    const params = appointmentForm.institutionId ? { institutionId: appointmentForm.institutionId } : {}
    const vetRes = await getVets(params)
    vetOptions.value = vetRes?.data || []
  } catch (error) {
    console.error('加载医生失败:', error)
    ElMessage.error('加载医生列表失败')
  }
}

// 提交预约
const handleSubmit = async () => {
  try {
    await appointmentFormRef.value.validate()
    submitting.value = true
    await addAppointment({
      petId: Number(appointmentForm.petId),
      institutionId: appointmentForm.institutionId ? Number(appointmentForm.institutionId) : null,
      vetUserId: appointmentForm.vetUserId ? Number(appointmentForm.vetUserId) : null,
      appointmentDate: appointmentForm.appointmentDate,
      appointmentTime: appointmentForm.appointmentTime,
      appointmentType: appointmentForm.appointmentType || '诊疗',
      reason: appointmentForm.reason,
      notes: appointmentForm.notes,
      status: '待确认'
    })
    ElMessage.success('预约提交成功，等待医院确认')
    router.push('/appointments')
  } catch (error) {
    console.error('提交预约失败:', error)
    ElMessage.error(error?.message || '提交预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPets()
  loadMeta()
})

watch(
  () => appointmentForm.institutionId,
  () => {
    appointmentForm.vetUserId = ''
    reloadVets()
  }
)
</script>

<style scoped>
.add-appointment-container {
  padding: 20px;
}

.appointment-form {
  max-width: 800px;
}

.mt-4 {
  margin-top: 20px;
}

.datetime-group {
  display: flex;
  gap: 12px;
}
</style>
