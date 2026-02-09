<template>
  <div class="feedback-page">
    <div class="hero">
      <div>
        <div class="eyebrow">我们在意每一次声音</div>
        <h2>意见反馈</h2>
        <p>告诉我们您的体验、问题或建议，帮助我们把「毛孩子的星球」做得更好。</p>
      </div>
      <div class="hero-right">
        <el-button type="primary" plain class="my-feedback-btn" @click="goMyFeedback">
          <el-icon><Document /></el-icon>
          我的反馈
        </el-button>
      </div>
    </div>

    <el-card shadow="never" class="feedback-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        status-icon
        class="feedback-form"
      >
        <el-form-item label="反馈类型" prop="feedbackType">
          <el-select v-model="form.feedbackType" placeholder="请选择反馈类型">
            <el-option v-for="item in feedbackTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>

        <el-form-item label="满意度">
          <el-rate v-model="form.rating" show-text :texts="['很差', '一般', '满意', '推荐', '力荐']" />
        </el-form-item>

        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            maxlength="300"
            show-word-limit
            placeholder="请详细描述遇到的问题或您的建议"
          />
        </el-form-item>

        <div class="form-actions">
          <el-button :loading="submitting" type="primary" @click="submit">提交反馈</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-drawer v-model="myFeedbackVisible" title="我的反馈" size="480px">
      <div class="drawer-body">
        <div v-if="myFeedbackLoading" class="drawer-loading">
          <el-skeleton :rows="5" animated />
        </div>
        <el-empty v-else-if="!myFeedbackList.length" description="还没有提交过反馈" />
        <el-timeline v-else class="feedback-timeline">
          <el-timeline-item
            v-for="item in myFeedbackList"
            :key="item.feedbackId"
            :timestamp="formatDateTime(item.createdAt)"
            placement="top"
          >
            <div class="timeline-card">
              <div class="timeline-header">
                <div class="type">{{ item.feedbackType }}</div>
                <el-tag size="small" :type="statusType(item.status)">{{ item.status || '待处理' }}</el-tag>
              </div>
              <div class="timeline-content">{{ item.content }}</div>
              <div class="timeline-meta">
                <div class="rating">
                  满意度：
                  <el-rate :model-value="item.rating" size="small" disabled show-score />
                </div>
                <div v-if="item.response" class="response">
                  回复：{{ item.response }}
                </div>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { Document } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { feedbackApi } from '@/api/feedback'

const authStore = useAuthStore()
const formRef = ref(null)
const submitting = ref(false)
const myFeedbackVisible = ref(false)
const myFeedbackLoading = ref(false)
const myFeedbackList = ref([])

const feedbackTypeOptions = ['系统建议', '服务投诉', '功能需求', '问题报告']
const form = reactive({
  feedbackType: '系统建议',
  content: '',
  rating: 5
})

const rules = {
  feedbackType: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 5, message: '反馈内容不少于5个字', trigger: 'blur' }
  ]
}

onMounted(async () => {
  if (!authStore.userInfo && authStore.isAuthenticated) {
    await authStore.fetchUserInfo()
  }
})

const reset = () => {
  form.feedbackType = '系统建议'
  form.content = ''
  form.rating = 5
  formRef.value?.clearValidate?.()
}

const goMyFeedback = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.error('请登录后查看反馈')
    return
  }
  if (!authStore.userInfo?.id) {
    await authStore.fetchUserInfo()
  }
  myFeedbackVisible.value = true
  await loadMyFeedback()
}

const loadMyFeedback = async () => {
  myFeedbackLoading.value = true
  try {
    const res = await feedbackApi.listMyFeedback(authStore.userInfo?.id)
    myFeedbackList.value = res?.data || []
  } catch (error) {
    console.error('获取反馈失败', error)
    ElMessage.error(error?.message || '获取反馈失败')
  } finally {
    myFeedbackLoading.value = false
  }
}

const formatDateTime = (value) => {
  if (!value) return '--'
  return String(value).replace('T', ' ').replace(/\.\d+/, '')
}

const statusType = (status) => {
  switch (status) {
    case '已解决':
      return 'success'
    case '处理中':
      return 'info'
    case '已关闭':
      return 'info'
    default:
      return 'warning'
  }
}

const submit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  if (!authStore.userInfo?.id) {
    ElMessage.error('请重新登录后再提交反馈')
    return
  }
  submitting.value = true
  try {
    const payload = {
      feedbackType: form.feedbackType,
      content: form.content,
      rating: form.rating,
      userId: authStore.userInfo.id,
      status: '待处理'
    }
    await feedbackApi.submitFeedback(payload)
    ElMessage.success('感谢反馈，我们会尽快处理')
    reset()
  } catch (error) {
    console.error('提交反馈失败', error)
    ElMessage.error(error?.message || '提交失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.feedback-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 12px 0 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(120deg, #f3f8ff 0%, #eef4ff 45%, #f8fbff 100%);
  border: 1px solid #dfe8fb;
}

.hero h2 {
  margin: 6px 0;
  font-size: 26px;
  font-weight: 800;
  color: #1f2d3d;
}

.hero p {
  margin: 0;
  color: #5c6b7a;
  line-height: 1.6;
}

.hero-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.my-feedback-btn {
  font-weight: 700;
}

.eyebrow {
  color: #3b82f6;
  font-weight: 700;
  font-size: 13px;
  letter-spacing: 0.4px;
  text-transform: uppercase;
}

.feedback-card {
  border-radius: 16px;
  border: 1px solid #e5eaf3;
}

.feedback-form {
  max-width: 680px;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-left: 100px;
}

.drawer-body {
  min-height: 260px;
}

.drawer-loading {
  padding: 8px 4px;
}

.feedback-timeline {
  margin: 0;
}

.timeline-card {
  padding: 10px 12px;
  border: 1px solid #e6eaf2;
  border-radius: 10px;
  background: #fff;
}

.timeline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  gap: 8px;
}

.timeline-header .type {
  font-weight: 700;
  color: #1f2d3d;
}

.timeline-content {
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 6px;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #6b7280;
  font-size: 13px;
}

.timeline-meta .response {
  flex: 1;
  color: #374151;
}

@media (max-width: 768px) {
  .feedback-page {
    padding: 0 6px 20px;
  }
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }
  .form-actions {
    padding-left: 0;
    justify-content: flex-start;
  }
}
</style>
