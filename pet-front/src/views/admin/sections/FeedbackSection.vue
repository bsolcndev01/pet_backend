<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">用户反馈管理</div>
        <el-space wrap>
          <el-button type="primary" plain @click="$emit('open-create')">新增反馈</el-button>
        </el-space>
      </div>
      <el-table :data="feedbackData" size="small" border stripe>
        <el-table-column prop="feedbackId" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="140" />
        <el-table-column prop="feedbackType" label="类型" width="140" />
        <el-table-column prop="rating" label="评分" width="100" />
        <el-table-column prop="status" label="状态" width="110" />
        <el-table-column prop="response" label="回复" />
        <el-table-column prop="responderName" label="处理人" width="120" />
        <el-table-column prop="respondedAt" label="处理时间" width="180" />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column prop="content" label="反馈内容" />
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
  feedbackData: { type: Array, default: () => [] }
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
