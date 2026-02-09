<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">角色管理</div>
      <el-space wrap>
        <el-button type="primary" plain @click="$emit('open-create')">新增角色</el-button>
      </el-space>
      <el-table :data="roleData" size="small" border stripe style="margin-top: 12px">
        <el-table-column prop="roleId" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
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
  roleData: {
    type: Array,
    default: () => []
  }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
