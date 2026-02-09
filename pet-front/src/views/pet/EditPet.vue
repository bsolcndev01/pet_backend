<template>
  <div class="edit-pet-container">
    <el-page-header @back="handleBack" content="编辑宠物信息" />
    
    <el-card class="mt-4" v-loading="loading">
      <el-form
        ref="petFormRef"
        :model="petForm"
        :rules="petRules"
        label-width="120px"
        class="pet-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物名称" prop="petName">
              <el-input v-model="petForm.petName" placeholder="请输入宠物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="种类" prop="species">
              <el-select v-model="petForm.species" placeholder="请选择宠物种类">
                <el-option label="狗" value="狗" />
                <el-option label="猫" value="猫" />
                <el-option label="兔子" value="兔子" />
                <el-option label="仓鼠" value="仓鼠" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品种" prop="breed">
              <el-input v-model="petForm.breed" placeholder="请输入宠物品种" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="petForm.gender">
                <el-radio label="M">公</el-radio>
                <el-radio label="F">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="petForm.birthDate"
                type="date"
                placeholder="请选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input 
                v-model.number="petForm.weight" 
                type="number" 
                placeholder="请输入宠物体重" 
                step="0.1"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="健康状态">
        <el-select v-model="petForm.healthStatus" placeholder="请选择健康状态">
          <el-option label="优秀" value="优秀" />
          <el-option label="良好" value="良好" />
          <el-option label="一般" value="一般" />
          <el-option label="需要关注" value="需要关注" />
          <el-option label="不佳" value="不佳" />
        </el-select>
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="petForm.remark"
          type="textarea"
          :rows="4"
          placeholder="请输入备注信息（选填）"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="毛色">
            <el-input v-model="petForm.color" placeholder="请输入毛色" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="体型">
            <el-input v-model="petForm.bodyType" placeholder="小型/中型/大型" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="芯片号">
            <el-input v-model="petForm.microchipId" placeholder="芯片或编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="耳号">
            <el-input v-model="petForm.earId" placeholder="耳号/标识" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="绝育状态">
            <el-switch v-model="petForm.sterilized" active-text="已绝育" inactive-text="未绝育" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="领养/购入日期">
            <el-date-picker
              v-model="petForm.adoptDate"
              type="date"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="血型">
            <el-input v-model="petForm.bloodType" placeholder="血型，如 DEA1.1+" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="证件/注册信息">
            <el-input v-model="petForm.registrationInfo" placeholder="登记证/注册编号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="健康风险标签">
        <el-input v-model="petForm.allergies" placeholder="过敏源，如：牛肉、鸡肉" />
        <el-input v-model="petForm.chronicDiseases" class="mt-2" placeholder="慢性病，如：心脏、肾脏、糖尿" />
        <el-input v-model="petForm.geneticRisks" class="mt-2" placeholder="遗传风险，如：多囊、髋关节" />
        <el-input v-model="petForm.bannedDrugs" class="mt-2" placeholder="禁用药物，如：布洛芬、阿司匹林" />
      </el-form-item>

      <el-form-item label="宠物头像">
        <el-upload
          class="avatar-uploader"
          :auto-upload="false"
          :limit="1"
          :file-list="fileList"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept="image/*"
          list-type="picture-card"
        >
          <el-icon><Plus /></el-icon>
          <template #file="{ file }">
            <img
              class="avatar"
              :src="file.url || (file.raw && URL.createObjectURL(file.raw))"
              alt="pet"
            />
          </template>
        </el-upload>
        <span class="upload-tip">支持单张 jpg/png，上传后会存入数据库，可通过接口直接访问。</span>
      </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存修改</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPetDetail, updatePet, uploadPetPhoto } from '@/api/pet'

const router = useRouter()
const route = useRoute()
const petId = computed(() => route.params.id)

// 表单引用
const petFormRef = ref(null)
const fileList = ref([])
const photoFile = ref(null)

// 宠物表单
const petForm = reactive({
  petName: '',
  species: '',
  breed: '',
  gender: '',
  birthDate: '',
  weight: '',
  healthStatus: '',
  remark: '',
  color: '',
  bodyType: '',
  microchipId: '',
  earId: '',
  sterilized: false,
  adoptDate: '',
  bloodType: '',
  registrationInfo: '',
  allergies: '',
  chronicDiseases: '',
  geneticRisks: '',
  bannedDrugs: ''
})

// 表单验证规则
const petRules = reactive({
  petName: [
    { required: true, message: '请输入宠物名称', trigger: 'blur' },
    { max: 50, message: '宠物名称长度不能超过50个字符', trigger: 'blur' }
  ],
  species: [
    { required: true, message: '请选择宠物种类', trigger: 'change' }
  ],
  breed: [
    { required: true, message: '请输入宠物品种', trigger: 'blur' },
    { max: 50, message: '品种长度不能超过50个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 0.1, max: 200, message: '体重必须在0.1-200kg之间', trigger: 'blur' }
  ]
})

const loading = ref(false)
const submitting = ref(false)

// 返回上一页：若有历史则后退，否则回列表/详情
const handleBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else if (petId.value) {
    router.push(`/pets/${petId.value}`)
  } else {
    router.push('/pets')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!petId.value) {
    ElMessage.error('未找到宠物ID')
    return
  }
  try {
    await petFormRef.value.validate()
    submitting.value = true
    await updatePet(petId.value, {
      ...petForm,
      weight: petForm.weight ? Number(petForm.weight) : null
    })
    if (photoFile.value) {
      await uploadPetPhoto(petId.value, photoFile.value)
    }
    ElMessage.success('宠物信息修改成功')
    router.push(`/pets/${petId.value}`)
  } catch (error) {
    console.error('修改宠物信息失败:', error)
    ElMessage.error(error?.message || '修改宠物信息失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const loadPetDetail = async () => {
  if (!petId.value) {
    ElMessage.error('未找到宠物ID')
    handleBack()
    return
  }
  try {
    loading.value = true
    const res = await getPetDetail(petId.value)
    const data = res?.data || {}
    petForm.petName = data.petName || ''
    petForm.species = data.species || ''
    petForm.breed = data.breed || ''
    petForm.gender = data.gender || ''
    petForm.birthDate = data.birthDate || ''
    petForm.weight = data.weight ?? ''
    petForm.healthStatus = data.healthStatus || ''
    petForm.remark = data.remark || ''
    petForm.color = data.color || ''
    petForm.bodyType = data.bodyType || ''
    petForm.microchipId = data.microchipId || ''
    petForm.earId = data.earId || ''
    petForm.sterilized = data.sterilized ?? false
    petForm.adoptDate = data.adoptDate || ''
    petForm.bloodType = data.bloodType || ''
    petForm.registrationInfo = data.registrationInfo || ''
    petForm.allergies = data.allergies || ''
    petForm.chronicDiseases = data.chronicDiseases || ''
    petForm.geneticRisks = data.geneticRisks || ''
    petForm.bannedDrugs = data.bannedDrugs || ''
    if (data.photoUrl) {
      fileList.value = [{ name: 'avatar', url: data.photoUrl }]
    }
  } catch (error) {
    console.error('加载宠物信息失败:', error)
    ElMessage.error(error?.message || '加载宠物信息失败')
    handleBack()
  } finally {
    loading.value = false
  }
}

const handleFileChange = (file, files) => {
  photoFile.value = file.raw
  fileList.value = files.slice(-1)
}

const handleFileRemove = () => {
  photoFile.value = null
  fileList.value = []
}

// 页面加载时获取宠物信息
onMounted(loadPetDetail)
</script>

<style scoped>
.edit-pet-container {
  padding: 20px;
}

.pet-form {
  max-width: 800px;
}

.mt-4 {
  margin-top: 20px;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.avatar-uploader :deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.upload-tip {
  display: inline-block;
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
