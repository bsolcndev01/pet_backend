<template>
  <div class="medical-detail-container">
    <el-page-header @back="handleBack" content="医疗记录详情" />
    
    <el-card class="mt-4">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ recordInfo.recordId || '无' }}</el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ recordInfo.petName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="就诊医院">{{ recordInfo.hospitalName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="主治医生">{{ recordInfo.doctorName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期">{{ recordInfo.visitDate || '无' }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果">{{ recordInfo.diagnosis || '无' }}</el-descriptions-item>
        <el-descriptions-item label="治疗方案" :span="2">{{ recordInfo.treatmentPlan || '无' }}</el-descriptions-item>
        <el-descriptions-item label="用药记录" :span="2">{{ recordInfo.medication || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ recordInfo.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="mt-4">
        <el-button type="primary" @click="handleEdit">编辑记录</el-button>
        <el-button @click="handleBack">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const recordId = route.params.id

// 记录信息
const recordInfo = ref({
  recordId: recordId,
  petName: '小白',
  hospitalName: '宠物友好医院',
  doctorName: '王医生',
  visitDate: '2024-01-15',
  diagnosis: '肠胃炎',
  treatmentPlan: '禁食24小时，服用益生菌，静脉补液',
  medication: '益生菌（每日2次，每次1包）、消炎针（每日1次，连续3天）',
  remark: '一周后复查，注意饮食清淡'
})

// 返回上一页
const handleBack = () => {
  router.push('/medical')
}

// 编辑记录
const handleEdit = () => {
  router.push(`/medical/${recordId}/edit`)
}

// 页面加载时获取详情
onMounted(() => {
  console.log('获取医疗记录详情，ID：', recordId)
})
</script>

<style scoped>
.medical-detail-container {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}
</style>