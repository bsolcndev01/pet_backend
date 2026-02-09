<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">兽医专业信息</div>
        <el-button type="primary" plain size="small" @click="$emit('open-create')">新增兽医</el-button>
      </div>
      <el-table :data="vetData" size="small" border stripe>
        <el-table-column prop="vetId" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="140">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.userName || '-' }}</span>
              <span class="cell-sub">ID: {{ row.userId || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="institutionName" label="机构" width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.institutionName || '-' }}</span>
              <span class="cell-sub">ID: {{ row.institutionId || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="licenseNumber" label="执业编号" width="160" />
        <el-table-column prop="specialization" label="专长" width="140" />
        <el-table-column prop="position" label="职务" width="120" />
        <el-table-column prop="yearsExperience" label="年限" width="90" />
        <el-table-column prop="qualification" label="资质" />
        <el-table-column prop="verified" label="认证" width="100">
          <template #default="{ row }">
            <el-tag :type="row.verified ? 'success' : 'info'" size="small">
              {{ row.verified ? '已认证' : '未认证' }}
            </el-tag>
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
  vetData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>

<style scoped>
.cell-main {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.cell-sub {
  color: #8a8fa3;
  font-size: 12px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
</style>
