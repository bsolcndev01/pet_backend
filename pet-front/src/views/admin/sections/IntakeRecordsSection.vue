<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">摄入记录</div>
      <el-space wrap style="margin-bottom: 12px">
        <el-button type="primary" plain @click="$emit('open-create')">新增摄入记录</el-button>
      </el-space>
      <el-table :data="recordData" size="small" border stripe>
        <el-table-column prop="intakeId" label="ID" width="80" />
        <el-table-column label="宠物" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || row.pet?.petName || '未填写' }}</span>
              <span v-if="row.petId || row.pet?.petId" class="cell-sub">ID: {{ row.petId || row.pet?.petId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="intakeVolume" label="摄入量" width="120" />
        <el-table-column prop="recordDate" label="记录日期" width="130" />
        <el-table-column prop="notes" label="备注" min-width="160" />
        <el-table-column label="记录人" width="140">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.recordedByName || row.recordedBy || '未填写' }}</span>
              <span v-if="row.recordedBy" class="cell-sub">ID: {{ row.recordedBy }}</span>
            </div>
          </template>
        </el-table-column>
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
  recordData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
