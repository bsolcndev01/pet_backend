<template>
  <div class="plan-container">
    <div class="page-title">饮食 / 运动计划</div>

    <el-card class="toolbar-card" shadow="hover">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="currentPetId" placeholder="选择宠物" clearable style="width: 200px" @change="loadPlans">
            <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
          </el-select>
          <el-button type="primary" plain size="small" @click="loadPlans">刷新</el-button>
        </div>
        <div class="toolbar-right">
          <el-button type="primary" size="small" @click="openDrawer">新增计划</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16" class="mt-2">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-title">饮食计划</div>
            </div>
          </template>
          <el-timeline v-if="dietPlans.length">
            <el-timeline-item
              v-for="item in dietPlans"
              :key="item.planId"
              :timestamp="formatRange(item.startDate, item.endDate)"
              type="primary"
            >
              <div class="plan-header">
                <div class="plan-title">{{ item.title }}</div>
                <div class="plan-actions">
                  <el-button link size="small" type="primary" @click="openEdit(item)">
                    <el-icon><EditPen /></el-icon>
                  </el-button>
                  <el-button link size="small" type="danger" @click="handleDelete(item)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="plan-meta">
                <el-tag size="small" type="success">饮食</el-tag>
                <span class="plan-target">{{ item.target || '目标未填' }}</span>
                <span class="plan-frequency">{{ item.frequency || '频率未填' }}</span>
              </div>
              <div class="plan-notes">{{ item.notes || '暂无备注' }}</div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无饮食计划" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-title">运动计划</div>
            </div>
          </template>
          <el-timeline v-if="exercisePlans.length">
            <el-timeline-item
              v-for="item in exercisePlans"
              :key="item.planId"
              :timestamp="formatRange(item.startDate, item.endDate)"
              type="warning"
            >
              <div class="plan-header">
                <div class="plan-title">{{ item.title }}</div>
                <div class="plan-actions">
                  <el-button link size="small" type="primary" @click="openEdit(item)">
                    <el-icon><EditPen /></el-icon>
                  </el-button>
                  <el-button link size="small" type="danger" @click="handleDelete(item)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="plan-meta">
                <el-tag size="small" type="warning">运动</el-tag>
                <span class="plan-target">{{ item.target || '目标未填' }}</span>
                <span class="plan-frequency">{{ item.frequency || '频率未填' }}</span>
              </div>
              <div class="plan-notes">{{ item.notes || '暂无备注' }}</div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无运动计划" />
        </el-card>
      </el-col>
    </el-row>

    <el-drawer v-model="drawerVisible" title="新增计划" size="35%">
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="100px" class="plan-form">
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="planForm.planType" placeholder="选择类型">
            <el-option label="饮食" value="饮食" />
            <el-option label="运动" value="运动" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划标题" prop="title">
          <el-input v-model="planForm.title" placeholder="例如：控制体重-轻食方案" />
        </el-form-item>
        <el-form-item label="目标/指标">
          <el-input v-model="planForm.target" placeholder="如：每日热量900 kcal / 每日步行40分钟" />
        </el-form-item>
        <el-form-item label="频率">
          <el-input v-model="planForm.frequency" placeholder="如：每日三餐 / 每周5天" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="planForm.startDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="planForm.endDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input type="textarea" :rows="3" v-model="planForm.notes" placeholder="补充说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlans, addPlan, updatePlan, deletePlan } from '@/api/plan'
import { getPetList } from '@/api/pet'

const petOptions = ref([])
const currentPetId = ref(null)
const planList = ref([])
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const planFormRef = ref(null)
const editingPlanId = ref(null)
const planForm = reactive({
  planType: '饮食',
  title: '',
  target: '',
  frequency: '',
  startDate: '',
  endDate: '',
  notes: ''
})

const planRules = reactive({
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入计划标题', trigger: 'blur' }]
})

const dietPlans = computed(() => planList.value.filter((p) => p.planType === '饮食'))
const exercisePlans = computed(() => planList.value.filter((p) => p.planType === '运动'))

const formatRange = (start, end) => {
  if (!start && !end) return '未设置时间'
  if (start && end) return `${start} ~ ${end}`
  return start || end
}

const openDrawer = () => {
  if (!currentPetId.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  editingPlanId.value = null
  Object.assign(planForm, {
    planType: '饮食',
    title: '',
    target: '',
    frequency: '',
    startDate: '',
    endDate: '',
    notes: ''
  })
  drawerVisible.value = true
}

const openEdit = (plan) => {
  editingPlanId.value = plan.planId
  Object.assign(planForm, {
    planType: plan.planType || '饮食',
    title: plan.title || '',
    target: plan.target || '',
    frequency: plan.frequency || '',
    startDate: plan.startDate || '',
    endDate: plan.endDate || '',
    notes: plan.notes || ''
  })
  drawerVisible.value = true
}

const handleDelete = (plan) => {
  ElMessageBox.confirm('确定删除该计划吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deletePlan(plan.planId)
      ElMessage.success('已删除')
      loadPlans()
    })
    .catch(() => {})
}

const loadPets = async () => {
  try {
    const res = await getPetList({ page: 1, size: 100 })
    petOptions.value = res?.data?.records || []
    if (!currentPetId.value && petOptions.value.length) {
      currentPetId.value = petOptions.value[0].petId
    }
  } catch (error) {
    console.error('加载宠物列表失败', error)
    ElMessage.error('加载宠物列表失败')
  }
}

const loadPlans = async () => {
  if (!currentPetId.value) return
  try {
    loading.value = true
    const res = await getPlans(currentPetId.value)
    planList.value = res?.data || []
  } catch (error) {
    console.error('加载计划失败', error)
    ElMessage.error('加载计划失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!currentPetId.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  try {
    await planFormRef.value.validate()
    saving.value = true
    const payload = {
      petId: currentPetId.value,
      ...planForm
    }
    if (editingPlanId.value) {
      await updatePlan(editingPlanId.value, payload)
      ElMessage.success('计划已更新')
    } else {
      await addPlan(payload)
      ElMessage.success('计划已保存')
    }
    drawerVisible.value = false
    loadPlans()
  } catch (error) {
    console.error('保存计划失败', error)
    if (error?.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('保存计划失败')
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadPets()
  await loadPlans()
})
</script>

<style scoped>
.plan-container {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #1f2d3d;
}

.toolbar-card {
  margin-bottom: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  align-items: center;
}

.mt-2 {
  margin-top: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-weight: 700;
}

.plan-title {
  font-weight: 700;
  color: #1f2d3d;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-actions {
  display: flex;
  gap: 4px;
}

.plan-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  margin: 6px 0;
  color: #64748b;
}

.plan-target,
.plan-frequency {
  font-size: 13px;
}

.plan-notes {
  color: #475569;
  font-size: 13px;
}

.plan-form {
  padding-right: 8px;
}
</style>
