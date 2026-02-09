<template>
  <div class="medical-record-container">
    <el-page-header @back="handleBack" content="医疗记录列表" />
    
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
      <el-col :span="10">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入诊断结果/医院名称搜索"
          prefix-icon="Search"
          @keyup.enter="loadMedicalRecords"
        >
          <template #append>
            <el-button @click="loadMedicalRecords">搜索</el-button>
          </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="handleAddRecord">
          <el-icon><Plus /></el-icon> 添加记录
        </el-button>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <el-table 
        :data="medicalRecordList" 
        border 
        style="width: 100%"
        empty-text="暂无医疗记录，请先添加"
      >
        <el-table-column prop="recordId" label="记录ID" width="100" />
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="hospitalName" label="就诊医院" />
        <el-table-column prop="diagnosis" label="诊断结果" />
        <el-table-column prop="visitDate" label="就诊日期" width="120" />
        <el-table-column prop="doctorName" label="主治医生" width="120" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleViewDetail(scope.row.recordId)"
            >
              查看详情
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleEditRecord(scope.row.recordId)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        class="mt-4"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
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
// 搜索关键词
const searchKeyword = ref('')
// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 医疗记录列表
const medicalRecordList = ref([
  {
    recordId: 1,
    petName: '小白',
    hospitalName: '宠物友好医院',
    diagnosis: '肠胃炎',
    visitDate: '2024-01-15',
    doctorName: '王医生'
  },
  {
    recordId: 2,
    petName: '咪咪',
    hospitalName: '爱心宠物诊所',
    diagnosis: '猫藓',
    visitDate: '2024-02-20',
    doctorName: '李医生'
  }
])

// 返回上一页
const handleBack = () => {
  router.back()
}

// 添加医疗记录
const handleAddRecord = () => {
  router.push('/medical/add')
}

// 查看详情
const handleViewDetail = (recordId) => {
  router.push(`/medical/${recordId}`)
}

// 编辑记录
const handleEditRecord = (recordId) => {
  router.push(`/medical/${recordId}/edit`)
}

// 加载医疗记录
const loadMedicalRecords = () => {
  console.log('筛选宠物ID：', selectedPetId.value, '搜索关键词：', searchKeyword.value)
  total.value = medicalRecordList.value.length
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadMedicalRecords()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageNum.value = val
  loadMedicalRecords()
}

// 页面加载时初始化
onMounted(() => {
  loadMedicalRecords()
})
</script>

<style scoped>
.medical-record-container {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}
</style>