<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">疫苗接种记录</div>
      <el-space wrap style="margin-bottom: 12px">
        <el-button type="primary" plain @click="$emit('open-create')">新增接种记录</el-button>
      </el-space>
      <el-table :data="vaccinationData" size="small" border stripe>
        <el-table-column prop="vaccinationId" label="ID" width="80" />
        <el-table-column label="宠物" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.petName || row.pet?.petName || '未填写' }}</span>
              <span v-if="row.petId || row.pet?.petId" class="cell-sub">ID: {{ row.petId || row.pet?.petId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="疫苗类型" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.vaccineTypeName || row.vaccineType?.vaccineName || '未填写' }}</span>
              <span v-if="row.vaccineTypeId" class="cell-sub">ID: {{ row.vaccineTypeId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="机构" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.institutionName || row.institution?.institutionName || '未填写' }}</span>
              <span v-if="row.institutionId" class="cell-sub">ID: {{ row.institutionId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="兽医" min-width="140">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.vetName || row.vet?.username || row.vet?.name || '未填写' }}</span>
              <span v-if="row.vetUserId" class="cell-sub">ID: {{ row.vetUserId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="vaccinationDate" label="接种日期" width="130" />
        <el-table-column prop="nextDueDate" label="下次接种" width="130" />
        <el-table-column prop="lotNumber" label="批号" width="120" />
        <el-table-column prop="notes" label="备注" min-width="180" />
        <el-table-column label="记录人" width="140">
          <template #default="{ row }">
            <div class="cell-main">
              <span>{{ row.createdByName || row.createdBy || '未填写' }}</span>
              <span v-if="row.createdBy" class="cell-sub">ID: {{ row.createdBy }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
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
  vaccinationData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
