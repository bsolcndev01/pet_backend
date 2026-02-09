<template>
  <div class="forbidden-container">
    <div class="forbidden-content">
      <h1>403</h1>
      <p>抱歉，你没有权限访问该页面</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.forbidden-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.forbidden-content {
  text-align: center;
}

.forbidden-content h1 {
  font-size: 120px;
  color: #e6a23c;
  margin-bottom: 20px;
}

.forbidden-content p {
  font-size: 24px;
  color: #666;
  margin-bottom: 30px;
}
</style>