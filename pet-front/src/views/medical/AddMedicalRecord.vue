<template>
  <div class="add-medical-container">
    <el-page-header @back="handleBack" content="添加医疗记录" />
    
    <el-card class="mt-4">
      <el-form
        ref="medicalFormRef"
        :model="medicalForm"
        :rules="medicalRules"
        label-width="120px"
        class="medical-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物名称" prop="petId">
              <el-select v-model="medicalForm.petId" placeholder="请选择宠物">
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
            <el-form-item label="就诊医院" prop="hospitalName">
              <el-input v-model="medicalForm.hospitalName" placeholder="请输入就诊医院" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主治医生" prop="doctorName">
              <el-input v-model="medicalForm.doctorName" placeholder="请输入主治医生" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊日期" prop="visitDate">
              <el-date-picker
                v-model="medicalForm.visitDate"
                type="date"
                placeholder="请选择就诊日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input v-model="medicalForm.diagnosis" placeholder="请输入诊断结果" />
        </el-form-item>
        
        <el-form-item label="治疗方案">
          <el-input
            v-model="medicalForm.treatmentPlan"
            type="textarea"
            :rows="3"
            placeholder="请输入治疗方案（选填）"
          />
        </el-form-item>
        
        <el-form-item label="用药记录">
          <el-input
            v-model="medicalForm.medication"
            type="textarea"
            :rows="3"
            placeholder="请输入用药记录（选填）"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="medicalForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 宠物列表（模拟数据）
const petList = ref([
  { petId: 1, petName: '小白' },
  { petId: 2, petName: '咪咪' }
])

// 表单引用
const medicalFormRef = ref(null)

// 医疗记录表单
const medicalForm = reactive({
  petId: '',
  hospitalName: '',
  doctorName: '',
  visitDate: '',
  diagnosis: '',
  treatmentPlan: '',
  medication: '',
  remark: ''
})

// 表单验证规则
const medicalRules = reactive({
  petId: [
    { required: true, message: '请选择宠物', trigger: 'change' }
  ],
  hospitalName: [
    { required: true, message: '请输入就诊医院', trigger: 'blur' }
  ],
  doctorName: [
    { required: true, message: '请输入主治医生', trigger: 'blur' }
  ],
  visitDate: [
    { required: true, message: '请选择就诊日期', trigger: 'change' }
  ],
  diagnosis: [
    { required: true, message: '请输入诊断结果', trigger: 'blur' }
  ]
})

// 返回上一页
const handleBack = () => {
  router.push('/medical')
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await medicalFormRef.value.validate()
    
    // 模拟添加记录
    ElMessage.success('医疗记录添加成功')
    router.push('/medical')
  } catch (error) {
    console.error('添加医疗记录失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}
</script>

<style scoped>
.add-medical-container {
  padding: 20px;
}

.medical-form {
  max-width: 800px;
}

.mt-4 {
  margin-top: 20px;
}
</style>