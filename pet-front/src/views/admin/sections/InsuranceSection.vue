<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">保险信息管理</div>
        <el-space wrap>
          <el-button type="primary" plain @click="$emit('open-create')">新增保险</el-button>
        </el-space>
      </div>
      <el-table :data="insuranceData" size="small" border stripe>
        <el-table-column prop="insuranceId" label="ID" width="80" />
        <el-table-column label="宠物" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || '未填写' }}</span>
              <span v-if="row.petId" class="cell-sub">ID: {{ row.petId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="insuranceCompany" label="保险公司" width="160" />
        <el-table-column prop="policyNumber" label="保单号" width="140" />
        <el-table-column prop="coverageType" label="保障类型" width="140" />
        <el-table-column prop="annualPremium" label="年保费" width="120" />
        <el-table-column prop="coverageLimit" label="保额" width="120" />
        <el-table-column prop="deductible" label="免赔额" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="startDate" label="开始" width="120" />
        <el-table-column prop="endDate" label="结束" width="120" />
        <el-table-column prop="notes" label="备注" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ $index, row }">
            <el-button size="small" type="primary" link circle @click="$emit('open-edit', $index)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button size="small" type="danger" link circle @click="$emit('remove', $index, row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup>
defineProps({
  insuranceData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>

<style scoped>
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
