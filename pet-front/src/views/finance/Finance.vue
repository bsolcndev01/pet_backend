<template>
  <div class="page-card">
    <el-page-header content="费用 / 保险" />

    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="typeFilter" size="small" style="width: 140px">
          <el-option label="全部类型" value="全部" />
          <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" size="small" @click="openDialog">新增记录</el-button>
      </div>
    </div>

    <el-table :data="filteredRecords" v-loading="loading" border>
      <el-table-column prop="title" label="标题" min-width="120" />
      <el-table-column prop="petName" label="宠物" width="120" />
      <el-table-column prop="medicalRecordId" label="关联就诊" width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.medicalRecordId" size="small">#{{ scope.row.medicalRecordId }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="recordType" label="类型" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.recordType === '报销' ? 'success' : 'info'">{{ scope.row.recordType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="类别" width="120" />
      <el-table-column prop="amount" label="金额 (元)" width="120">
        <template #default="scope">
          {{ scope.row.amount?.toFixed ? scope.row.amount.toFixed(2) : scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="recordDate" label="日期" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.status)" effect="plain">{{ scope.row.status || '未报销' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="notes" label="备注" min-width="180" show-overflow-tooltip />
    </el-table>

    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :page-size="pageSize"
        :current-page="page"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>
  </div>

  <el-dialog v-model="dialogVisible" title="新增费用/保险记录" width="520px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="financeForm" :rules="rules" label-width="90px" size="small">
      <el-form-item label="标题" prop="title">
        <el-input v-model="financeForm.title" placeholder="例如：年度体检/保险报销" />
      </el-form-item>
      <el-form-item label="类型" prop="recordType">
        <el-radio-group v-model="financeForm.recordType">
          <el-radio-button label="消费" />
          <el-radio-button label="报销" />
        </el-radio-group>
      </el-form-item>
      <el-form-item label="类别">
        <el-select v-model="financeForm.category" placeholder="选择类别">
          <el-option v-for="cat in categoryOptions" :key="cat" :label="cat" :value="cat" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额 (元)" prop="amount">
        <el-input v-model.number="financeForm.amount" type="number" min="0" step="0.01" placeholder="请输入金额" />
      </el-form-item>
      <el-form-item label="日期" prop="recordDate">
        <el-date-picker
          v-model="financeForm.recordDate"
          type="date"
          placeholder="选择日期"
          style="width: 100%"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="宠物">
        <el-select v-model="financeForm.petId" placeholder="可选" clearable filterable>
          <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
        </el-select>
      </el-form-item>
      <el-form-item label="关联就诊">
        <el-select v-model="financeForm.medicalRecordId" placeholder="可选" clearable filterable>
          <el-option
            v-for="rec in medicalOptions"
            :key="rec.id || rec.recordId"
            :label="`${rec.petName || ''} ${rec.visitDate || ''} ${rec.reason || ''}`"
            :value="rec.id || rec.recordId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="financeForm.status">
          <el-option v-for="status in statusOptions" :key="status" :label="status" :value="status" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="financeForm.notes" type="textarea" rows="2" placeholder="补充说明" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFinanceList, addFinanceRecord } from '@/api/finance'
import { getPetList } from '@/api/pet'
import { getMedicalRecords } from '@/api/health'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const financeRecords = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const typeFilter = ref('全部')
const dialogVisible = ref(false)
const submitting = ref(false)
const petOptions = ref([])
const medicalOptions = ref([])

const typeOptions = ['消费', '报销']
const categoryOptions = ['诊疗', '药品', '体检', '保险理赔', '日常用品', '其他']
const statusOptions = ['未报销', '处理中', '已报销', '自费']

const financeForm = reactive({
  title: '',
  recordType: '消费',
  category: '诊疗',
  amount: null,
  recordDate: formatDate(new Date()),
  petId: null,
  medicalRecordId: null,
  status: '未报销',
  notes: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  recordType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  recordDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const formRef = ref()

const filteredRecords = computed(() => {
  if (typeFilter.value === '全部') return financeRecords.value
  return financeRecords.value.filter((item) => item.recordType === typeFilter.value)
})

const statusTag = (status) => {
  const map = {
    未报销: 'warning',
    处理中: 'info',
    已报销: 'success',
    自费: 'info'
  }
  return map[status] || 'info'
}

const loadFinance = async () => {
  loading.value = true
  try {
    const res = await getFinanceList({ page: page.value, size: pageSize.value })
    financeRecords.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch (error) {
    console.error('加载费用/保险记录失败:', error)
    ElMessage.error(error?.message || '加载费用/保险记录失败')
  } finally {
    loading.value = false
  }
}

const loadPets = async () => {
  try {
    const res = await getPetList({ page: 1, size: 100 })
    petOptions.value = res?.data?.records || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

const loadMedicalRecords = async () => {
  try {
    const res = await getMedicalRecords()
    medicalOptions.value = res?.data || []
  } catch (error) {
    console.error('加载就诊记录失败:', error)
  }
}

const resetForm = () => {
  financeForm.title = ''
  financeForm.recordType = '消费'
  financeForm.category = '诊疗'
  financeForm.amount = null
  financeForm.recordDate = formatDate(new Date())
  financeForm.petId = null
  financeForm.medicalRecordId = null
  financeForm.status = '未报销'
  financeForm.notes = ''
}

const openDialog = () => {
  resetForm()
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await addFinanceRecord({
        ...financeForm,
        amount: Number(financeForm.amount)
      })
      ElMessage.success('新增成功')
      dialogVisible.value = false
      loadFinance()
    } catch (error) {
      console.error('保存失败', error)
      ElMessage.error(error?.message || '保存失败')
    } finally {
      submitting.value = false
    }
  })
}

const handlePageChange = (p) => {
  page.value = p
  loadFinance()
}

onMounted(() => {
  loadFinance()
  loadPets()
  loadMedicalRecords()
})
</script>

<style scoped>
.page-card {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px 0;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
