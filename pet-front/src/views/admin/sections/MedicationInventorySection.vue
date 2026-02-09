<template>
  <section class="admin-content">
    <el-card class="panel" shadow="never">
      <div class="panel-title">药品库存</div>
      <el-space wrap style="margin-bottom: 12px">
        <el-button type="primary" plain @click="$emit('open-create')">新增药品</el-button>
      </el-space>
      <el-table :data="inventoryData" size="small" border stripe>
        <el-table-column prop="inventoryId" label="ID" width="80" />
        <el-table-column prop="medicationName" label="药品名称" min-width="160" />
        <el-table-column prop="medicationType" label="类型" width="120" />
        <el-table-column label="机构" min-width="140">
          <template #default="{ row }">
            {{ row.institutionName || '未填写' }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存数量" width="110" />
        <el-table-column prop="unit" label="单位" width="90" />
        <el-table-column prop="purchaseDate" label="采购日期" width="130" />
        <el-table-column prop="expiryDate" label="到期日期" width="130" />
        <el-table-column prop="supplier" label="供应商" min-width="140" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="storageLocation" label="存放位置" min-width="140" />
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
  inventoryData: { type: Array, default: () => [] }
})

defineEmits(['open-create', 'open-edit', 'remove'])
</script>
