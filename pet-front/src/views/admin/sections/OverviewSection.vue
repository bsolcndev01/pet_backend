<template>
  <section class="admin-content">
    <div class="welcome-banner">
      <div class="welcome-text">
        <div class="welcome-title">欢迎回来，{{ adminName }}</div>
        <div class="welcome-subtitle">管理中心 · 统一查看各类业务数据</div>
      </div>
    </div>
    <el-row :gutter="16" class="stat-grid">
      <el-col :span="8" v-for="card in overviewCards" :key="card.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-title">
            <el-icon class="stat-icon"><component :is="card.icon" /></el-icon>
            <span>{{ card.title }}</span>
          </div>
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-desc">{{ card.desc }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="panel" shadow="never">
      <div class="panel-title">快捷操作</div>
      <el-space wrap>
        <el-button type="primary" @click="$emit('select', 'users')">用户管理</el-button>
        <el-button type="success" plain @click="$emit('select', 'settings')">系统配置</el-button>
      </el-space>
    </el-card>
  </section>
</template>

<script setup>
defineProps({
  adminName: {
    type: String,
    default: ''
  },
  overviewCards: {
    type: Array,
    default: () => []
  }
})

defineEmits(['select'])
</script>
