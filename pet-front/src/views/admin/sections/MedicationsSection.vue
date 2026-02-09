<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">驱虫药品管理</div>
        <el-space wrap>
          <el-button type="primary" plain @click="$emit('open-create')">新增驱虫药品</el-button>
        </el-space>
      </div>
      <el-table :data="medicationData" size="small" border stripe>
        <el-table-column prop="productId" label="ID" width="80" />
        <el-table-column prop="productName" label="药品名称" width="160" />
        <el-table-column prop="dewormingType" label="类型" width="120" />
        <el-table-column prop="species" label="适用物种" width="120" />
        <el-table-column prop="validityPeriodDays" label="有效期(天)" width="120" />
        <el-table-column prop="dosageGuide" label="用量指南" />
        <el-table-column prop="active" label="启用" width="100">
          <template #default="{ row }">
            {{ row.active ? '是' : '否' }}
          </template>
        </el-table-column>
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
  medicationData: { type: Array, default: () => [] }
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
