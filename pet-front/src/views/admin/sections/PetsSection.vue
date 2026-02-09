<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-header">
        <div class="panel-title">宠物档案管理</div>
        <el-space wrap>
          <el-button type="primary" plain @click="$emit('open-create')">新增档案</el-button>
          <el-button type="success" plain @click="$emit('notify', '宠物档案管理', '标记重点关注')">标记重点关注</el-button>
          <el-button type="info" plain @click="$emit('notify', '宠物档案管理', '导出报表')">导出报表</el-button>
        </el-space>
      </div>
      <el-table :data="petData" size="small" border stripe>
        <el-table-column prop="petId" label="ID" width="80" />
        <el-table-column prop="petName" label="宠物名" width="120" />
        <el-table-column prop="ownerUsername" label="主人" width="120" />
        <el-table-column prop="species" label="物种" width="120" />
        <el-table-column prop="breed" label="品种" width="120" />
        <el-table-column prop="gender" label="性别" width="100" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="color" label="毛色" width="120" />
        <el-table-column prop="temperament" label="性格" width="120" />
        <el-table-column prop="currentWeight" label="体重" width="100" />
        <el-table-column prop="sterilized" label="绝育" width="90">
          <template #default="{ row }">
            {{ row.sterilized ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="microchipId" label="芯片号" width="140" />
        <el-table-column prop="earId" label="耳号" width="120" />
        <el-table-column prop="bodyType" label="体型" width="120" />
        <el-table-column prop="specialNeeds" label="特殊需求" />
        <el-table-column prop="photoUrl" label="照片" />
        <el-table-column prop="healthStatus" label="健康状态" width="110" />
        <el-table-column prop="adoptDate" label="领养/购入" width="120" />
        <el-table-column prop="registrationInfo" label="登记/证件" width="140" />
        <el-table-column prop="bloodType" label="血型" width="120" />
        <el-table-column prop="allergies" label="过敏源" />
        <el-table-column prop="chronicDiseases" label="慢性病" />
        <el-table-column prop="geneticRisks" label="遗传风险" />
        <el-table-column prop="bannedDrugs" label="禁用药物" />
        <el-table-column prop="lastMedicalCheck" label="最近体检" width="140" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
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
  petData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove', 'notify'])
</script>

<style scoped>
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
