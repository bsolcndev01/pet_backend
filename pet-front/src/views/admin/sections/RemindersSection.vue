<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">提醒记录</div>
      <el-space wrap style="margin-bottom: 12px">
        <el-button type="primary" plain @click="$emit('open-create')">新增提醒</el-button>
      </el-space>
      <el-table :data="reminderData" size="small" border stripe>
        <el-table-column prop="reminderId" label="ID" width="80" />
        <el-table-column prop="reminderTypeName" label="类型" min-width="140" />
        <el-table-column label="用户" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.userName || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="宠物" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || '未填写' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="message" label="内容" min-width="200" />
        <el-table-column prop="dueDate" label="到期日" width="120" />
        <el-table-column prop="reminderDate" label="提醒日" width="120" />
        <el-table-column prop="completed" label="完成" width="90">
          <template #default="{ row }">
            <el-tag :type="row.completed ? 'success' : 'info'" size="small">
              {{ row.completed ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sentCount" label="发送次数" width="110" />
        <el-table-column prop="lastSentAt" label="最后发送" width="160" />
        <el-table-column prop="sourceTable" label="来源表" min-width="130" />
        <el-table-column prop="sourceRecordId" label="来源ID" width="100" />
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
  reminderData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
