<template>
  <el-card shadow="hover" class="ops-card">
    <div class="card-header">
      <div>
        <div class="card-title">预约管理</div>
        <div class="card-sub">预约确认、分配与时间段</div>
      </div>
      <div class="card-actions">
        <el-button type="primary" size="small" @click="createDialogVisible = true">
          新增预约
        </el-button>
        <el-button text type="primary" @click="onGoList?.()">查看当前列表</el-button>
      </div>
    </div>
    <el-skeleton :loading="loading" animated :count="3">
      <template #template>
        <el-skeleton-item variant="text" style="width: 60%" />
        <el-skeleton-item variant="text" style="width: 40%" />
      </template>
      <template #default>
        <el-empty v-if="!appointments.length" description="暂无预约" />
        <el-table
          v-else
          :data="appointments"
          size="small"
          border
          style="width: 100%"
        >
          <el-table-column prop="petName" label="宠物" width="120" />
          <el-table-column prop="appointmentType" label="类型/机构">
            <template #default="scope">
              {{ scope.row.appointmentType || '诊疗' }}
              <div class="table-sub" v-if="scope.row.institutionName">
                {{ scope.row.institutionName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="appointmentDate" label="日期" width="130" />
          <el-table-column prop="appointmentTime" label="时间" width="120">
            <template #default="scope">
              {{ scope.row.appointmentTime || scope.row.timeSlot || scope.row.time || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="statusType(scope.row.status)">
                {{ statusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="scope">
              <el-button
                v-if="['待确认', 'pending'].includes(statusText(scope.row.status))"
                type="primary"
                size="small"
                @click="handleConfirm(scope.row)"
              >
                确认
              </el-button>
              <el-button
                v-if="['待确认', 'pending'].includes(statusText(scope.row.status))"
                type="danger"
                size="small"
                @click="handleCancel(scope.row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-skeleton>

    <div class="table-actions-tip" v-if="appointments.length">
      <span>提示：列表中可直接确认或取消预约</span>
    </div>

    <el-dialog v-model="createDialogVisible" title="新增预约" width="520px" destroy-on-close>
      <el-form label-width="100px" label-position="left">
        <el-form-item label="宠物">
          <el-select v-model="createForm.petId" placeholder="请选择宠物">
            <el-option
              v-for="pet in petOptions"
              :key="pet.petId"
              :label="pet.petName"
              :value="pet.petId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约类型">
          <el-input v-model="createForm.appointmentType" placeholder="如 诊疗/疫苗" />
        </el-form-item>
        <el-form-item label="医疗机构">
          <el-input :model-value="institutionName || '当前账号所属机构'" disabled />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="createForm.appointmentDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="时间段">
          <el-time-picker
            v-model="createForm.appointmentTime"
            value-format="HH:mm"
            placeholder="选择时间"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="createForm.status">
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'

const props = defineProps({
  appointments: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  petOptions: { type: Array, default: () => [] },
  statusType: { type: Function, default: () => () => 'info' },
  statusText: { type: Function, default: () => (val) => val || '' },
  institutionName: { type: String, default: '' },
  onCreate: { type: Function, default: null },
  onGoList: { type: Function, default: null },
  onConfirm: { type: Function, default: null },
  onCancel: { type: Function, default: null }
})

const createDialogVisible = ref(false)
const createForm = reactive({
  petId: '',
  appointmentDate: '',
  appointmentTime: '',
  appointmentType: '',
  status: '待确认'
})

const submit = async () => {
  if (!props.onCreate) {
    createDialogVisible.value = false
    return
  }
  await props.onCreate({ ...createForm })
  createDialogVisible.value = false
  createForm.petId = ''
  createForm.appointmentDate = ''
  createForm.appointmentTime = ''
  createForm.appointmentType = ''
  createForm.status = '待确认'
}

const handleConfirm = async (row) => {
  if (!props.onConfirm) return
  await props.onConfirm(row.appointmentId || row.id)
}

const handleCancel = async (row) => {
  if (!props.onCancel) return
  await props.onCancel(row.appointmentId || row.id)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}

.ops-card {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 14px 28px rgba(31, 44, 80, 0.1);
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2d3d;
}

.card-sub {
  font-size: 13px;
  color: #6b7280;
  margin-top: 2px;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-sub {
  font-size: 12px;
  color: #6b7280;
}

.table-actions-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
}
</style>
