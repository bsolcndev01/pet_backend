<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">系统配置</div>
      <el-space wrap>
        <el-button type="primary" plain @click="$emit('notify', '系统配置', '字典管理')">字典管理</el-button>
        <el-button type="success" plain @click="$emit('notify', '系统配置', '通知模板')">通知模板</el-button>
        <el-button type="info" plain @click="$emit('notify', '系统配置', '安全设置')">安全设置</el-button>
      </el-space>
    </el-card>
  </section>
</template>

<script setup>
defineEmits(['notify'])
</script>
