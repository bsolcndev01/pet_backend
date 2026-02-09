<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">医疗机构管理</div>
      <el-space wrap>
        <el-button type="primary" plain @click="$emit('open-create')">新增机构</el-button>
      </el-space>
      <el-table :data="institutionData" size="small" border stripe style="margin-top: 12px">
        <el-table-column prop="institutionId" label="ID" width="80" />
        <el-table-column prop="institutionName" label="名称" width="180" />
        <el-table-column prop="institutionType" label="类型" width="120" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="licenseNumber" label="许可证号" width="160" />
        <el-table-column prop="verified" label="认证" width="100">
          <template #default="{ row }">
            {{ row.verified ? '已认证' : '未认证' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创立时间" width="180" />
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
  institutionData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
