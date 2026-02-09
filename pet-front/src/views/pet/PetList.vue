<template>
  <div class="pet-list-container">
    <div class="page-title">我的宠物列表</div>
    
    <el-row :gutter="20" class="mt-4">
      <el-col :span="4">
        <el-button type="primary" @click="handleAddPet">
          <el-icon><Plus /></el-icon> 添加宠物
        </el-button>
      </el-col>
      <el-col :span="12">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入宠物名称搜索"
          prefix-icon="Search"
          @clear="loadPetList"
          @keyup.enter="loadPetList"
        >
          <template #append>
            <el-button @click="loadPetList">搜索</el-button>
          </template>
        </el-input>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <el-table 
        v-loading="loading"
        :data="petList" 
        border 
        style="width: 100%"
        empty-text="暂无宠物数据，请先添加宠物"
      >
        <el-table-column label="宠物照片" width="100">
          <template #default="scope">
            <el-avatar :src="scope.row.photoUrl || defaultPetAvatar" />
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="宠物名称" />
        <el-table-column prop="species" label="种类" />
        <el-table-column prop="breed" label="品种" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 'M' ? '公' : scope.row.gender === 'F' ? '母' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="healthStatus" label="健康状态">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.healthStatus)">
              {{ scope.row.healthStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleViewDetail(scope.row.petId)"
            >
              查看详情
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleEditPet(scope.row.petId)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        class="mt-4"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetList } from '@/api/pet'
import defaultPetAvatar from '@/assets/images/R-C.jpg'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')
// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
// 宠物列表
const petList = ref([])

// 添加宠物
const handleAddPet = () => {
  router.push('/pets/add')
}

// 查看详情
const handleViewDetail = (petId) => {
  router.push(`/pets/${petId}`)
}

// 编辑宠物
const handleEditPet = (petId) => {
  router.push(`/pets/${petId}/edit`)
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    '优秀': 'success',
    '良好': 'info',
    '一般': 'warning',
    '需要关注': 'warning',
    '不佳': 'danger'
  }
  return statusMap[status] || 'default'
}

// 加载宠物列表
const loadPetList = async () => {
  try {
    loading.value = true
    const res = await getPetList({
      keyword: searchKeyword.value || undefined,
      page: pageNum.value,
      size: pageSize.value
    })
    const pageData = res?.data || {}
    petList.value = pageData.records || []
    total.value = pageData.total || 0
    // 服务端分页可能回传页码/大小，优先使用
    pageNum.value = pageData.page || pageNum.value
    pageSize.value = pageData.size || pageSize.value
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    ElMessage.error(error?.message || '加载宠物列表失败')
  } finally {
    loading.value = false
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  loadPetList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageNum.value = val
  loadPetList()
}

// 页面加载时获取列表
onMounted(() => {
  loadPetList()
})
</script>

<style scoped>
.pet-list-container {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.mt-4 {
  margin-top: 20px;
}
</style>
