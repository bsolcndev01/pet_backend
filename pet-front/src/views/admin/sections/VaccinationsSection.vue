<template>
  <div class="vaccine-container">
    <el-page-header @back="handleBack" content="疫苗接种记录" />
    
    <el-row :gutter="20" class="mt-4">
      <el-col :span="6">
        <el-select v-model="selectedPetId" placeholder="请选择宠物">
          <el-option 
            v-for="pet in petList" 
            :key="pet.petId" 
            :label="pet.petName" 
            :value="pet.petId"
          />
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="handleAddVaccine">
          <el-icon><Plus /></el-icon> 添加记录
        </el-button>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <el-table 
        :data="vaccineList" 
        border 
        style="width: 100%"
        empty-text="暂无疫苗接种记录，请先添加"
      >
        <el-table-column prop="vaccineId" label="记录ID" width="100" />
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="vaccineName" label="疫苗名称" />
        <el-table-column prop="vaccineDate" label="接种日期" width="120" />
        <el-table-column prop="hospitalName" label="接种医院" />
        <el-table-column prop="nextVaccineDate" label="下次接种日期" width="120">
          <template #default="scope">
            <span :style="{ color: isExpired(scope.row.nextVaccineDate) ? 'red' : 'inherit' }">
              {{ scope.row.nextVaccineDate || '无需接种' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleViewDetail(scope.row.vaccineId)"
            >
              查看详情
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleEditVaccine(scope.row.vaccineId)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 宠物列表（模拟数据）
const petList = ref([
  { petId: 1, petName: '小白' },
  { petId: 2, petName: '咪咪' }
])
// 选中的宠物ID
const selectedPetId = ref('')
// 疫苗记录列表
const vaccineList = ref([
  {
    vaccineId: 1,
    petName: '小白',
    vaccineName: '犬四联',
    vaccineDate: '2024-01-10',
    hospitalName: '宠物友好医院',
    nextVaccineDate: '2025-01-10'
  },
  {
    vaccineId: 2,
    petName: '咪咪',
    vaccineName: '猫三联',
    vaccineDate: '2024-02-15',
    hospitalName: '爱心宠物诊所',
    nextVaccineDate: '2025-02-15'
  }
])

// 返回上一页
const handleBack = () => {
  router.back()
}

// 添加疫苗记录
const handleAddVaccine = () => {
  router.push('/vaccine/add')
}

// 查看详情
const handleViewDetail = (vaccineId) => {
  router.push(`/vaccine/${vaccineId}`)
}

// 编辑记录
const handleEditVaccine = (vaccineId) => {
  router.push(`/vaccine/${vaccineId}/edit`)
}

// 判断是否过期
const isExpired = (date) => {
  if (!date) return false
  return new Date(date) < new Date()
}

// 页面加载初始化
onMounted(() => {})
</script>

<style scoped>
.vaccine-container {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}
</style>