<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>设置</h2>
      <p>管理通知、账户、界面偏好与语言</p>
    </div>

    <el-card class="settings-card" shadow="never">
      <div class="section">
        <div class="section-title">通知偏好</div>
        <el-form label-position="left" label-width="140px" class="section-form">
          <el-form-item label="邮件提醒">
            <el-switch v-model="state.notifications.email" />
          </el-form-item>
          <el-form-item label="短信提醒">
            <el-switch v-model="state.notifications.sms" />
          </el-form-item>
          <el-form-item label="应用内提醒">
            <el-switch v-model="state.notifications.app" />
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <div class="section">
        <div class="section-title">账户与隐私</div>
        <el-form label-position="left" label-width="140px" class="section-form">
          <el-form-item label="公开我的昵称">
            <el-switch v-model="state.privacy.publicProfile" />
          </el-form-item>
          <el-form-item label="接收产品更新">
            <el-switch v-model="state.privacy.productUpdates" />
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <div class="section">
        <div class="section-title">界面与语言</div>
        <el-form label-position="left" label-width="140px" class="section-form">
          <el-form-item label="色彩模式">
            <el-radio-group v-model="state.appearance.themeMode" @change="handleThemeChange">
              <el-radio-button label="light">浅色</el-radio-button>
              <el-radio-button label="dark">深色</el-radio-button>
              <el-radio-button label="system">跟随系统</el-radio-button>
            </el-radio-group>
            <div class="helper-text">当前：{{ themeLabel }}</div>
          </el-form-item>
          <el-form-item label="界面语言">
            <el-select
              v-model="state.appearance.language"
              placeholder="请选择语言"
              style="width: 220px"
              @change="handleLanguageChange"
            >
              <el-option
                v-for="item in languageOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="紧凑模式">
            <el-switch v-model="state.ui.compactMode" />
          </el-form-item>
          <el-form-item label="强调动画">
            <el-switch v-model="state.ui.motion" />
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <div class="section">
        <div class="section-title">版本信息</div>
        <el-form label-position="left" label-width="140px" class="section-form">
          <el-form-item label="当前版本">
            <el-tag type="info">{{ appVersion }}</el-tag>
          </el-form-item>
          <el-form-item label="主题/语言">
            <el-tag>{{ themeLabel }}</el-tag>
            <el-tag class="ml-2" type="success">{{ languageLabel }}</el-tag>
          </el-form-item>
        </el-form>
      </div>

      <div class="actions">
        <el-button type="primary" @click="handleSave">保存设置</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { applyLanguage, applyThemeMode, getAppVersion, loadPreferences } from '@/utils/preferences'

const defaultState = {
  notifications: {
    email: true,
    sms: false,
    app: true
  },
  privacy: {
    publicProfile: false,
    productUpdates: true
  },
  ui: {
    compactMode: false,
    motion: true
  },
  appearance: {
    themeMode: 'light',
    language: 'zh-CN'
  }
}

const createStateFromPreferences = () => ({
  notifications: { ...defaultState.notifications },
  privacy: { ...defaultState.privacy },
  ui: { ...defaultState.ui },
  appearance: {
    ...defaultState.appearance,
    ...loadPreferences()
  }
})

const createDefaultState = () => ({
  notifications: { ...defaultState.notifications },
  privacy: { ...defaultState.privacy },
  ui: { ...defaultState.ui },
  appearance: { ...defaultState.appearance }
})

const state = reactive(createStateFromPreferences())
const resolvedTheme = ref(applyThemeMode(state.appearance.themeMode, false))
applyLanguage(state.appearance.language)

const appVersion = getAppVersion()
const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' }
]

const themeLabel = computed(() => {
  if (state.appearance.themeMode === 'system') {
    return `跟随系统（当前：${resolvedTheme.value === 'dark' ? '深色' : '浅色'}）`
  }
  return state.appearance.themeMode === 'dark' ? '深色' : '浅色'
})

const languageLabel = computed(() => {
  const found = languageOptions.find((item) => item.value === state.appearance.language)
  return found?.label || state.appearance.language
})

const handleThemeChange = (value) => {
  resolvedTheme.value = applyThemeMode(value)
}

const handleLanguageChange = (value) => {
  applyLanguage(value)
}

const handleSave = () => {
  resolvedTheme.value = applyThemeMode(state.appearance.themeMode)
  applyLanguage(state.appearance.language)
  ElMessage.success('设置已保存')
}

const handleReset = () => {
  Object.assign(state, createDefaultState())
  resolvedTheme.value = applyThemeMode(defaultState.appearance.themeMode)
  applyLanguage(defaultState.appearance.language)
  ElMessage.info('已恢复默认设置')
}
</script>

<style scoped>
.settings-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 12px;
}

.page-header h2 {
  margin: 0 0 4px;
  font-size: 24px;
  font-weight: 700;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

.settings-card {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
}

.section {
  padding: 4px 4px 12px;
}

.section-title {
  font-weight: 700;
  margin-bottom: 12px;
  color: #111827;
}

.section-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

.helper-text {
  margin-top: 6px;
  color: var(--text-3);
  font-size: 13px;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
