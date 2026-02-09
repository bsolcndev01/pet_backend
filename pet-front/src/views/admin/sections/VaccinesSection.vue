<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">疫苗类型管理</div>
      <el-space wrap>
        <el-button type="primary" plain @click="$emit('open-create')">新增疫苗类型</el-button>
      </el-space>
      <el-table :data="vaccineData" size="small" border stripe style="margin-top: 12px">
        <el-table-column prop="vaccineTypeId" label="ID" width="80" />
        <el-table-column prop="vaccineName" label="疫苗名称" width="160" />
        <el-table-column prop="species" label="物种" width="120" />
        <el-table-column prop="validityPeriodMonths" label="有效期(月)" width="120" />
        <el-table-column prop="description" label="描述" />
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
  vaccineData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
