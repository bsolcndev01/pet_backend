<template>
  <div class="pet-detail-container">
    <el-page-header @back="handleBack" content="宠物详情" />
    
    <el-card class="mt-4" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="6">
          <!-- 宠物头像 -->
          <div class="pet-avatar">
            <img :src="petInfo.photoUrl || defaultPetAvatar" alt="宠物头像" class="avatar-img" />
          </div>
        </el-col>
        <el-col :span="18">
          <!-- 宠物基本信息 -->
          <el-descriptions :column="3" border>
            <el-descriptions-item label="宠物名称">{{ displayValue(petInfo.petName) }}</el-descriptions-item>
            <el-descriptions-item label="种类">{{ displayValue(petInfo.species) }}</el-descriptions-item>
            <el-descriptions-item label="品种">{{ displayValue(petInfo.breed) }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ formatGender(petInfo.gender) }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ displayValue(petInfo.birthDate) }}</el-descriptions-item>
            <el-descriptions-item label="体重(kg)">{{ displayValue(petInfo.weight) }}</el-descriptions-item>
            <el-descriptions-item label="毛色">{{ displayValue(petInfo.color) }}</el-descriptions-item>
            <el-descriptions-item label="体型">{{ displayValue(petInfo.bodyType) }}</el-descriptions-item>
            <el-descriptions-item label="绝育状态">{{ formatSterilized(petInfo.sterilized) }}</el-descriptions-item>
            <el-descriptions-item label="芯片号">{{ displayValue(petInfo.microchipId) }}</el-descriptions-item>
            <el-descriptions-item label="耳号">{{ displayValue(petInfo.earId) }}</el-descriptions-item>
            <el-descriptions-item label="领养/购入日期">{{ displayValue(petInfo.adoptDate) }}</el-descriptions-item>
            <el-descriptions-item label="血型">{{ displayValue(petInfo.bloodType) }}</el-descriptions-item>
            <el-descriptions-item label="登记/证件">{{ displayValue(petInfo.registrationInfo) }}</el-descriptions-item>
            <el-descriptions-item label="健康状态">
              <el-tag :type="getStatusTagType(petInfo.healthStatus)">{{ displayValue(petInfo.healthStatus) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="过敏源" :span="3">{{ displayValue(petInfo.allergies) }}</el-descriptions-item>
            <el-descriptions-item label="慢性病" :span="3">{{ displayValue(petInfo.chronicDiseases) }}</el-descriptions-item>
            <el-descriptions-item label="遗传风险" :span="3">{{ displayValue(petInfo.geneticRisks) }}</el-descriptions-item>
            <el-descriptions-item label="禁用药物" :span="3">{{ displayValue(petInfo.bannedDrugs) }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ petInfo.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      
      <div class="mt-4">
        <el-button type="primary" @click="handleEdit">编辑宠物信息</el-button>
        <el-button @click="handleBack">返回列表</el-button>
      </div>
    </el-card>
    
    <!-- 健康记录概览 -->
    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>健康记录概览</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="医疗记录" name="medical">
          <el-table
            v-if="medicalOverview.length"
            :data="medicalOverview"
            size="small"
            border
            :show-header="true"
            empty-text="暂无医疗记录"
          >
            <el-table-column prop="visitDate" label="就诊日期" width="120" />
            <el-table-column prop="institutionName" label="机构" />
            <el-table-column prop="diagnosis" label="诊断" />
            <el-table-column prop="treatment" label="治疗" />
          </el-table>
          <el-empty v-else description="暂无医疗记录" />
        </el-tab-pane>
        <el-tab-pane label="疫苗接种" name="vaccine">
          <el-table
            v-if="vaccineOverview.length"
            :data="vaccineOverview"
            size="small"
            border
            empty-text="暂无疫苗记录"
          >
            <el-table-column prop="vaccinationDate" label="接种日期" width="120" />
            <el-table-column prop="vaccineName" label="疫苗" />
            <el-table-column prop="institutionName" label="机构" />
            <el-table-column prop="vetName" label="医生" />
          </el-table>
          <el-empty v-else description="暂无疫苗记录" />
        </el-tab-pane>
        <el-tab-pane label="驱虫记录" name="deworming">
          <el-table
            v-if="dewormOverview.length"
            :data="dewormOverview"
            size="small"
            border
            empty-text="暂无驱虫记录"
          >
            <el-table-column prop="applicationDate" label="日期" width="120" />
            <el-table-column prop="productName" label="药品" />
            <el-table-column prop="dosage" label="剂量" />
            <el-table-column prop="notes" label="备注" />
          </el-table>
          <el-empty v-else description="暂无驱虫记录" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail } from '@/api/pet'
import { getMedicalRecords, getVaccinations, getDeworming } from '@/api/health'
import defaultPetAvatar from '@/assets/images/R-C.jpg'

const router = useRouter()
const route = useRoute()
const petId = computed(() => route.params.id)

// 宠物信息
const petInfo = ref({})
const loading = ref(false)
const medicalOverview = ref([])
const vaccineOverview = ref([])
const dewormOverview = ref([])

// 激活的标签页
const activeTab = ref('medical')

// 返回上一页：若无历史则回列表
const handleBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/pets')
  }
}

// 编辑宠物信息
const handleEdit = () => {
  if (!petId.value) {
    ElMessage.error('未找到宠物ID')
    return
  }
  router.push(`/pets/${petId.value}/edit`)
}

// 获取健康状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    '优秀': 'success',
    '良好': 'info',
    '一般': 'warning',
    '需要关注': 'warning',
    '不佳': 'danger'
  }
  return statusMap[status] || 'default'
}

const fetchPetDetail = async () => {
  if (!petId.value) {
    ElMessage.error('未找到宠物ID')
    handleBack()
    return
  }
  try {
    loading.value = true
    const res = await getPetDetail(petId.value)
    petInfo.value = res?.data || {}
    await loadOverviews()
  } catch (error) {
    console.error('获取宠物详情失败:', error)
    ElMessage.error(error?.message || '获取宠物详情失败')
  } finally {
    loading.value = false
  }
}

const loadOverviews = async () => {
  if (!petId.value) return
  try {
    const params = { petId: petId.value }
    const [medRes, vacRes, dewRes] = await Promise.all([
      getMedicalRecords(params.petId),
      getVaccinations(params.petId),
      getDeworming(params.petId)
    ])
    medicalOverview.value = (medRes?.data || []).slice(0, 5)
    vaccineOverview.value = (vacRes?.data || []).slice(0, 5)
    dewormOverview.value = (dewRes?.data || []).slice(0, 5)
  } catch (error) {
    console.error('加载健康概览失败:', error)
  }
}

const displayValue = (val, fallback = '未填写') => {
  return val === null || val === undefined || val === '' ? fallback : val
}

const formatGender = (gender) => {
  if (!gender) return '未填写'
  if (gender === 'M' || gender === '公') return '公'
  if (gender === 'F' || gender === '母') return '母'
  return gender
}

const formatSterilized = (flag) => {
  if (flag === true) return '已绝育'
  if (flag === false) return '未绝育'
  return '未填写'
}

// 页面加载时获取宠物详情
onMounted(fetchPetDetail)
watch(
  () => route.params.id,
  () => {
    fetchPetDetail()
  }
)
</script>

<style scoped>
.pet-detail-container {
  padding: 20px;
}

.pet-avatar {
  display: flex;
  justify-content: center;
}

.avatar-img {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  object-fit: cover;
}

.mt-4 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
