<template>
  <div class="deworming-container">
    <el-page-header @back="handleBack" content="驱虫记录列表" />
    
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
        <el-button type="primary" @click="handleAddDeworming">
          <el-icon><Plus /></el-icon> 添加记录
        </el-button>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <el-table 
        :data="dewormingList" 
        border 
        style="width: 100%"
        empty-text="暂无驱虫记录，请先添加"
      >
        <el-table-column prop="dewormingId" label="记录ID" width="100" />
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="dewormingType" label="驱虫类型">
          <template #default="scope">
            {{ scope.row.dewormingType === 'internal' ? '体内驱虫' : '体外驱虫' }}
          </template>
        </el-table-column>
        <el-table-column prop="dewormingDate" label="驱虫日期" width="120" />
        <el-table-column prop="medicineName" label="驱虫药名称" />
        <el-table-column prop="nextDewormingDate" label="下次驱虫日期" width="120">
          <template #default="scope">
            <span :style="{ color: isExpired(scope.row.nextDewormingDate) ? 'red' : 'inherit' }">
              {{ scope.row.nextDewormingDate || '暂无' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleViewDetail(scope.row.dewormingId)"
            >
              查看详情
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleEditDeworming(scope.row.dewormingId)"
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
// 驱虫记录列表
const dewormingList = ref([
  {
    dewormingId: 1,
    petName: '小白',
    dewormingType: 'internal',
    dewormingDate: '2024-03-01',
    medicineName: '拜宠清',
    nextDewormingDate: '2024-06-01'
  },
  {
    dewormingId: 2,
    petName: '咪咪',
    dewormingType: 'external',
    dewormingDate: '2024-04-10',
    medicineName: '福来恩',
    nextDewormingDate: '2024-05-10'
  }
])

// 返回上一页
const handleBack = () => {
  router.back()
}

// 添加驱虫记录
const handleAddDeworming = () => {
  router.push('/deworming/add')
}

// 查看详情
const handleViewDetail = (id) => {
  router.push(`/deworming/${id}`)
}

// 编辑记录
const handleEditDeworming = (id) => {
  router.push(`/deworming/${id}/edit`)
}

// 判断是否过期
const isExpired = (date) => {
  if (!date) return false
  return new Date(date) < new Date()
}

onMounted(() => {})
</script>

<style scoped>
.deworming-container {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}
</style>