<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">宠物照片管理</div>
      <div class="actions">
        <el-input
          v-model="petIdFilter"
          placeholder="按宠物ID筛选"
          clearable
          size="small"
          style="width: 200px"
          @change="$emit('filter-pet', petIdFilter)"
        />
        <el-button type="primary" plain size="small" @click="$emit('open-create')">新增照片</el-button>
      </div>
      <el-table :data="photoData" size="small" border stripe>
        <el-table-column prop="photoId" label="ID" width="80" />
        <el-table-column prop="petName" label="宠物名称" width="140">
          <template #default="{ row }">
            {{ row.petName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="160" />
        <el-table-column prop="contentType" label="类型" width="140" />
        <el-table-column prop="sizeBytes" label="大小" width="120">
          <template #default="{ row }">
            {{ formatSize(row.sizeBytes) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadedAt" label="上传时间" width="160" />
        <el-table-column prop="posX" label="posX" width="90" />
        <el-table-column prop="posY" label="posY" width="90" />
        <el-table-column label="预览" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.previewUrl"
              :src="row.previewUrl"
              :preview-src-list="[row.previewUrl]"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 8px; overflow: hidden"
            />
            <span v-else>-</span>
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
import { ref } from 'vue'

const props = defineProps({
  photoData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove', 'filter-pet'])

const petIdFilter = ref('')

const formatSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) return `${size} B`
  const kb = size / 1024
  if (kb < 1024) return `${kb.toFixed(1)} KB`
  return `${(kb / 1024).toFixed(2)} MB`
}
</script>

<style scoped>
.actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}
</style>
