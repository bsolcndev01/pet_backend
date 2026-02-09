<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">预约管理</div>
        <el-space wrap>
          <el-button type="primary" plain @click="$emit('open-create')">新增预约</el-button>
          <el-button type="success" plain @click="$emit('notify', '预约管理', '导出报表')">导出报表</el-button>
        </el-space>
      </div>
      <el-table :data="appointmentData" size="small" border stripe>
        <el-table-column prop="appointmentId" label="ID" width="80" />
        <el-table-column label="宠物" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="主人" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.ownerUsername || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="机构" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.institutionName || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="兽医" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.vetUsername || '未分配' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="日期" width="130" />
        <el-table-column prop="appointmentTime" label="时间" width="130" />
        <el-table-column prop="appointmentType" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="notes" label="备注" min-width="180" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
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
  appointmentData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove', 'notify'])
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
