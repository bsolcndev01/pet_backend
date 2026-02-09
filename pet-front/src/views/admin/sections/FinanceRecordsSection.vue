<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">财务记录</div>
      <el-space wrap style="margin-bottom: 12px">
        <el-button type="primary" plain @click="$emit('open-create')">新增财务记录</el-button>
      </el-space>
      <el-table :data="recordData" size="small" border stripe>
        <el-table-column prop="recordId" label="ID" width="80" />
        <el-table-column label="用户" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.ownerName || row.owner?.username || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="宠物" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || row.pet?.petName || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="recordType" label="类型" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="recordDate" label="日期" width="130" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="medicalRecordId" label="关联就诊ID" width="130" />
        <el-table-column prop="notes" label="备注" min-width="160" />
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
