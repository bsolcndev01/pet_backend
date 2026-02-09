<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">用户管理</div>
      <el-space wrap>
        <el-button type="primary" plain @click="$emit('open-create')">新增用户</el-button>
        <el-popover
          placement="bottom-start"
          :width="360"
          trigger="click"
          v-model:visible="filterVisible"
        >
          <div class="filter-form">
            <el-form label-width="80px" size="small">
              <el-form-item label="用户名">
                <el-input v-model="userFilters.username" placeholder="模糊匹配" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userFilters.email" placeholder="模糊匹配" />
              </el-form-item>
              <el-form-item label="电话">
                <el-input v-model="userFilters.phone" placeholder="模糊匹配" />
              </el-form-item>
              <el-form-item label="角色">
                <el-select v-model="userFilters.roleName" clearable placeholder="全部">
                  <el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="userFilters.active" clearable placeholder="全部">
                  <el-option label="启用" value="true" />
                  <el-option label="禁用" value="false" />
                </el-select>
              </el-form-item>
              <div class="filter-actions">
                <el-button size="small" @click="$emit('reset-filter')">重置</el-button>
                <el-button size="small" type="primary" @click="apply">应用</el-button>
              </div>
            </el-form>
          </div>
          <template #reference>
            <el-button type="info" plain>筛选</el-button>
          </template>
        </el-popover>
      </el-space>
      <el-table :data="userData" size="small" border stripe style="margin-top: 12px">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="roleName" label="角色" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="lastLogin" label="最近登录" width="180" />
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
import { computed } from 'vue'

const props = defineProps({
  userData: { type: Array, default: () => [] },
  userFilters: { type: Object, default: () => ({}) },
  userFilterVisible: { type: Boolean, default: false },
  roleOptions: { type: Array, default: () => [] }
})

const emit = defineEmits(['open-create', 'open-edit', 'remove', 'apply-filter', 'reset-filter', 'update:userFilterVisible'])

const filterVisible = computed({
  get: () => props.userFilterVisible,
  set: (val) => emit('update:userFilterVisible', val)
})

const apply = () => {
  emit('apply-filter')
  filterVisible.value = false
}
</script>
