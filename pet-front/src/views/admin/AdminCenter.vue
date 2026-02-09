<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-brand" @click="goHome">
        <div class="logo-wrap">
          <img class="logo-img" :src="brandLogo" alt="logo" />
        </div>
        <div class="brand-text">
          <div class="brand-title">毛孩子的星球</div>
          <div class="brand-subtitle">后台管理</div>
        </div>
      </div>
      <div class="sidebar-menu-wrapper">
        <el-menu
          class="sidebar-menu"
          :default-active="activeKey"
          @select="onSelect"
        >
          <el-menu-item index="overview">
            <el-icon><Monitor /></el-icon>
            <span>管理主页</span>
          </el-menu-item>
          <el-sub-menu index="basic" popper-class="sidebar-submenu-popper">
            <template #title>
              <el-icon><Management /></el-icon>
              <span>基础信息管理</span>
            </template>
            <el-menu-item index="roles">
              <el-icon><Management /></el-icon>
              <span>角色管理</span>
            </el-menu-item>
            <el-menu-item index="users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="institutions">
              <el-icon><OfficeBuilding /></el-icon>
              <span>医疗机构管理</span>
            </el-menu-item>
            <el-menu-item index="veterinarians">
              <el-icon><User /></el-icon>
              <span>兽医信息管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="petInfo">
            <template #title>
              <el-icon><FolderOpened /></el-icon>
              <span>宠物信息管理</span>
            </template>
            <el-menu-item index="pets">
              <el-icon><FolderOpened /></el-icon>
              <span>宠物档案管理</span>
            </el-menu-item>
            <el-menu-item index="petPhotos">
              <el-icon><Picture /></el-icon>
              <span>宠物照片管理</span>
            </el-menu-item>
            <el-menu-item index="petPlans">
              <el-icon><Collection /></el-icon>
              <span>宠物计划</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="healthRecords">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>健康记录管理</span>
            </template>
            <el-menu-item index="medicalRecords">
              <el-icon><Document /></el-icon>
              <span>医疗记录</span>
            </el-menu-item>
            <el-menu-item index="vaccinations">
              <el-icon><FirstAidKit /></el-icon>
              <span>疫苗接种记录</span>
            </el-menu-item>
            <el-menu-item index="weightRecords">
              <el-icon><Collection /></el-icon>
              <span>体重记录</span>
            </el-menu-item>
            <el-menu-item index="temperatureRecords">
              <el-icon><Collection /></el-icon>
              <span>体温记录</span>
            </el-menu-item>
            <el-menu-item index="activityRecords">
              <el-icon><Collection /></el-icon>
              <span>活动记录</span>
            </el-menu-item>
            <el-menu-item index="intakeRecords">
              <el-icon><Collection /></el-icon>
              <span>摄入记录</span>
            </el-menu-item>
            <el-menu-item index="petMedications">
              <el-icon><Collection /></el-icon>
              <span>用药记录</span>
            </el-menu-item>
            <el-menu-item index="dewormingRecords">
              <el-icon><Collection /></el-icon>
              <span>驱虫记录管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="reminderCenter">
            <template #title>
              <el-icon><Bell /></el-icon>
              <span>预约提醒管理</span>
            </template>
            <el-menu-item index="reminders">
              <el-icon><Bell /></el-icon>
              <span>提醒记录</span>
            </el-menu-item>
            <el-menu-item index="appointments">
              <el-icon><Calendar /></el-icon>
              <span>预约管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="financeCenter">
            <template #title>
              <el-icon><Collection /></el-icon>
              <span>财务保险管理</span>
            </template>
            <el-menu-item index="financeRecords">
              <el-icon><Collection /></el-icon>
              <span>财务记录管理</span>
            </el-menu-item>
            <el-menu-item index="insurance">
              <el-icon><Collection /></el-icon>
              <span>保险信息管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="otherTypes">
            <template #title>
              <el-icon><Collection /></el-icon>
              <span>其他类型管理</span>
            </template>
            <el-menu-item index="vaccines">
              <el-icon><FirstAidKit /></el-icon>
              <span>疫苗类型管理</span>
            </el-menu-item>
            <el-menu-item index="medications">
              <el-icon><FirstAidKit /></el-icon>
              <span>驱虫药品管理</span>
            </el-menu-item>
            <el-menu-item index="medicationInventory">
              <el-icon><Collection /></el-icon>
              <span>药品库存</span>
            </el-menu-item>
            <el-menu-item index="reminderTypes">
              <el-icon><Bell /></el-icon>
              <span>提醒类型管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="feedback">
            <el-icon><Document /></el-icon>
            <span>用户反馈管理</span>
          </el-menu-item>
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            <span>系统配置</span>
          </el-menu-item>
        </el-menu>
      </div>
      <el-button class="back-to-frontend" type="primary" plain @click="goHome">
        返回前台
      </el-button>
    </aside>

    <main class="admin-main">
      <OverviewSection
        v-if="activeKey === 'overview'"
        :admin-name="adminName"
        :overview-cards="overviewCards"
        @select="onSelect"
      />
      <RolesSection
        v-else-if="activeKey === 'roles'"
        :role-data="roleData"
        @open-create="openCreate('roles')"
        @open-edit="(index) => openEdit('roles', index)"
        @remove="(index, row) => removeRecord('roles', index, row)"
      />
      <UsersSection
        v-else-if="activeKey === 'users'"
        v-model:user-filter-visible="userFilterVisible"
        :user-data="userData"
        :user-filters="userFilters"
        :role-options="roleOptions"
        @open-create="openCreate('users')"
        @open-edit="(index) => openEdit('users', index)"
        @remove="(index, row) => removeRecord('users', index, row)"
        @apply-filter="applyUserFilter"
        @reset-filter="resetUserFilter"
      />
      <VeterinariansSection
        v-else-if="activeKey === 'veterinarians'"
        :vet-data="veterinarianData"
        @open-create="openCreate('veterinarians')"
        @open-edit="(index) => openEdit('veterinarians', index)"
        @remove="(index, row) => removeRecord('veterinarians', index, row)"
      />
      <InstitutionsSection
        v-else-if="activeKey === 'institutions'"
        :institution-data="institutionData"
        @open-create="openCreate('institutions')"
        @open-edit="(index) => openEdit('institutions', index)"
        @remove="(index, row) => removeRecord('institutions', index, row)"
      />
      <VaccinesSection
        v-else-if="activeKey === 'vaccines'"
        :vaccine-data="vaccineData"
        @open-create="openCreate('vaccines')"
        @open-edit="(index) => openEdit('vaccines', index)"
        @remove="(index, row) => removeRecord('vaccines', index, row)"
      />
      <MedicationsSection
        v-else-if="activeKey === 'medications'"
        :medication-data="medicationData"
        @open-create="openCreate('medications')"
        @open-edit="(index) => openEdit('medications', index)"
        @remove="(index, row) => removeRecord('medications', index, row)"
      />
      <MedicationInventorySection
        v-else-if="activeKey === 'medicationInventory'"
        :inventory-data="medicationInventoryData"
        @open-create="openCreate('medicationInventory')"
        @open-edit="(index) => openEdit('medicationInventory', index)"
        @remove="(index, row) => removeRecord('medicationInventory', index, row)"
      />
      <PetMedicationsSection
        v-else-if="activeKey === 'petMedications'"
        :record-data="petMedicationData"
        @open-create="openCreate('petMedications')"
        @open-edit="(index) => openEdit('petMedications', index)"
        @remove="(index, row) => removeRecord('petMedications', index, row)"
      />
      <DewormingRecordsSection
        v-else-if="activeKey === 'dewormingRecords'"
        :record-data="dewormingRecordData"
        @open-create="openCreate('dewormingRecords')"
        @open-edit="(index) => openEdit('dewormingRecords', index)"
        @remove="(index, row) => removeRecord('dewormingRecords', index, row)"
      />
      <PetPlansSection
        v-else-if="activeKey === 'petPlans'"
        :record-data="petPlanData"
        @open-create="openCreate('petPlans')"
        @open-edit="(index) => openEdit('petPlans', index)"
        @remove="(index, row) => removeRecord('petPlans', index, row)"
      />
      <FinanceRecordsSection
        v-else-if="activeKey === 'financeRecords'"
        :record-data="financeRecordData"
        @open-create="openCreate('financeRecords')"
        @open-edit="(index) => openEdit('financeRecords', index)"
        @remove="(index, row) => removeRecord('financeRecords', index, row)"
      />
      <PetsSection
        v-else-if="activeKey === 'pets'"
        :pet-data="petData"
        @open-create="openCreate('pets')"
        @open-edit="(index) => openEdit('pets', index)"
        @remove="(index, row) => removeRecord('pets', index, row)"
        @notify="notify"
      />
      <PetPhotosSection
        v-else-if="activeKey === 'petPhotos'"
        :photo-data="petPhotoData"
        @open-create="openCreate('petPhotos')"
        @open-edit="(index) => openEdit('petPhotos', index)"
        @remove="(index, row) => removeRecord('petPhotos', index, row)"
        @filter-pet="filterPetPhotos"
      />
      <VaccinationsSection
        v-else-if="activeKey === 'vaccinations'"
        :vaccination-data="vaccinationData"
        @open-create="openCreate('vaccinations')"
        @open-edit="(index) => openEdit('vaccinations', index)"
        @remove="(index, row) => removeRecord('vaccinations', index, row)"
      />
      <MedicalRecordsSection
        v-else-if="activeKey === 'medicalRecords'"
        :record-data="medicalRecordData"
        @open-create="openCreate('medicalRecords')"
        @open-edit="(index) => openEdit('medicalRecords', index)"
        @remove="(index, row) => removeRecord('medicalRecords', index, row)"
      />
      <WeightRecordsSection
        v-else-if="activeKey === 'weightRecords'"
        :record-data="weightRecordData"
        @open-create="openCreate('weightRecords')"
        @open-edit="(index) => openEdit('weightRecords', index)"
        @remove="(index, row) => removeRecord('weightRecords', index, row)"
      />
      <TemperatureRecordsSection
        v-else-if="activeKey === 'temperatureRecords'"
        :record-data="temperatureRecordData"
        @open-create="openCreate('temperatureRecords')"
        @open-edit="(index) => openEdit('temperatureRecords', index)"
        @remove="(index, row) => removeRecord('temperatureRecords', index, row)"
      />
      <ActivityRecordsSection
        v-else-if="activeKey === 'activityRecords'"
        :record-data="activityRecordData"
        @open-create="openCreate('activityRecords')"
        @open-edit="(index) => openEdit('activityRecords', index)"
        @remove="(index, row) => removeRecord('activityRecords', index, row)"
      />
      <IntakeRecordsSection
        v-else-if="activeKey === 'intakeRecords'"
        :record-data="intakeRecordData"
        @open-create="openCreate('intakeRecords')"
        @open-edit="(index) => openEdit('intakeRecords', index)"
        @remove="(index, row) => removeRecord('intakeRecords', index, row)"
      />
      <RemindersSection
        v-else-if="activeKey === 'reminders'"
        :reminder-data="reminderData"
        @open-create="openCreate('reminders')"
        @open-edit="(index) => openEdit('reminders', index)"
        @remove="(index, row) => removeRecord('reminders', index, row)"
      />
      <AppointmentsSection
        v-else-if="activeKey === 'appointments'"
        :appointment-data="appointmentData"
        @open-create="openCreate('appointments')"
        @open-edit="(index) => openEdit('appointments', index)"
        @remove="(index, row) => removeRecord('appointments', index, row)"
        @notify="notify"
      />
      <ReminderTypesSection
        v-else-if="activeKey === 'reminderTypes'"
        :reminder-type-data="reminderTypeData"
        @open-create="openCreate('reminderTypes')"
        @open-edit="(index) => openEdit('reminderTypes', index)"
        @remove="(index, row) => removeRecord('reminderTypes', index, row)"
      />
      <FeedbackSection
        v-else-if="activeKey === 'feedback'"
        :feedback-data="feedbackData"
        @open-create="openCreate('feedback')"
        @open-edit="(index) => openEdit('feedback', index)"
        @remove="(index, row) => removeRecord('feedback', index, row)"
      />
      <InsuranceSection
        v-else-if="activeKey === 'insurance'"
        :insurance-data="insuranceData"
        @open-create="openCreate('insurance')"
        @open-edit="(index) => openEdit('insurance', index)"
        @remove="(index, row) => removeRecord('insurance', index, row)"
      />
      <SettingsSection v-else @notify="notify" />
    </main>

    <!-- 通用编辑弹窗（按模块动态渲染字段） -->
    <template v-for="(state, key) in dialog" :key="key">
          <el-dialog v-model="state.visible" :title="state.title" width="520px" destroy-on-close>
            <el-form label-width="120px">
          <template v-if="key === 'roles'">
            <el-form-item label="角色名称">
              <el-input v-model="state.form.roleName" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="state.form.description" type="textarea" rows="2" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'users'">
            <el-form-item label="用户名">
              <el-input v-model="state.form.username" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="state.form.email" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="state.form.phone" />
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="state.form.roleName" placeholder="请选择角色">
                <el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" />
              </el-select>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="state.form.password" type="password" show-password />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="state.form.active" size="small" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'institutions'">
            <el-form-item label="机构名称">
              <el-input v-model="state.form.institutionName" />
            </el-form-item>
            <el-form-item label="机构类型">
              <el-select v-model="state.form.institutionType" placeholder="请选择">
                <el-option label="宠物医院" value="宠物医院" />
                <el-option label="宠物诊所" value="宠物诊所" />
                <el-option label="动物防疫站" value="动物防疫站" />
                <el-option label="宠物美容医疗" value="宠物美容医疗" />
              </el-select>
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="state.form.address" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="state.form.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="state.form.email" />
            </el-form-item>
            <el-form-item label="许可证号">
              <el-input v-model="state.form.licenseNumber" />
            </el-form-item>
            <el-form-item label="创立时间">
              <el-date-picker
                v-model="state.form.createdAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择创立日期"
              />
            </el-form-item>
            <el-form-item label="认证状态">
              <el-radio-group v-model="state.form.verified">
                <el-radio :label="true">已认证</el-radio>
                <el-radio :label="false">未认证</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="机构介绍">
              <el-input v-model="state.form.description" type="textarea" rows="3" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'veterinarians'">
            <el-form-item label="关联用户">
              <el-select v-model="state.form.userId" filterable placeholder="选择用户">
                <el-option v-for="item in vetUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="所在机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="执业编号">
              <el-input v-model="state.form.licenseNumber" placeholder="填写执业证号" />
            </el-form-item>
            <el-form-item label="专长">
              <el-input v-model="state.form.specialization" placeholder="如 外科/皮肤科" />
            </el-form-item>
            <el-form-item label="职务">
              <el-input v-model="state.form.position" placeholder="如 主治医师/院长" />
            </el-form-item>
            <el-form-item label="从业年限">
              <el-input v-model.number="state.form.yearsExperience" type="number" placeholder="输入整数年限" />
            </el-form-item>
            <el-form-item label="资格资质">
              <el-input v-model="state.form.qualification" placeholder="如 执业兽医师/培训证书" />
            </el-form-item>
            <el-form-item label="认证状态">
              <el-radio-group v-model="state.form.verified">
                <el-radio :label="true">已认证</el-radio>
                <el-radio :label="false">未认证</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
          <template v-else-if="key === 'vaccines'">
            <el-form-item label="疫苗名称">
              <el-input v-model="state.form.vaccineName" />
            </el-form-item>
            <el-form-item label="物种">
              <el-input v-model="state.form.species" />
            </el-form-item>
            <el-form-item label="有效期(月)">
              <el-input v-model="state.form.validityPeriodMonths" type="number" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="state.form.active" size="small" />
            </el-form-item>
            <el-form-item label="说明">
              <el-input v-model="state.form.description" type="textarea" rows="3" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'medications'">
            <el-form-item label="药品名称">
              <el-input v-model="state.form.productName" placeholder="请输入药品名称" />
            </el-form-item>
            <el-form-item label="驱虫类型">
              <el-select v-model="state.form.dewormingType" placeholder="请选择">
                <el-option label="内驱" value="内驱" />
                <el-option label="外驱" value="外驱" />
                <el-option label="内外同驱" value="内外同驱" />
              </el-select>
            </el-form-item>
            <el-form-item label="适用物种">
              <el-select v-model="state.form.species" placeholder="请选择">
                <el-option label="狗" value="狗" />
                <el-option label="猫" value="猫" />
                <el-option label="通用" value="通用" />
              </el-select>
            </el-form-item>
            <el-form-item label="有效期(天)">
              <el-input v-model.number="state.form.validityPeriodDays" type="number" placeholder="例如 180" />
            </el-form-item>
            <el-form-item label="用药指南">
              <el-input v-model="state.form.dosageGuide" type="textarea" rows="3" placeholder="剂量/频率/注意事项" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="state.form.active" size="small" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'medicationInventory'">
            <el-form-item label="药品名称">
              <el-input v-model="state.form.medicationName" placeholder="请输入药品名称" />
            </el-form-item>
            <el-form-item label="类型">
              <el-input v-model="state.form.medicationType" placeholder="如 注射/口服" />
            </el-form-item>
            <el-form-item label="机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="库存数量">
              <el-input v-model.number="state.form.quantity" type="number" placeholder="数量" />
            </el-form-item>
            <el-form-item label="单位">
              <el-input v-model="state.form.unit" placeholder="盒/瓶/支" />
            </el-form-item>
            <el-form-item label="采购日期">
              <el-date-picker v-model="state.form.purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="到期日期">
              <el-date-picker v-model="state.form.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="供应商">
              <el-input v-model="state.form.supplier" placeholder="供应商" />
            </el-form-item>
            <el-form-item label="单价">
              <el-input v-model.number="state.form.unitPrice" type="number" placeholder="单价" />
            </el-form-item>
            <el-form-item label="存放位置">
              <el-input v-model="state.form.storageLocation" placeholder="如 药柜A-1" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'petMedications'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="药物名称">
              <el-input v-model="state.form.drugName" placeholder="药物名称" />
            </el-form-item>
            <el-form-item label="剂量">
              <el-input v-model="state.form.dosage" placeholder="剂量说明" />
            </el-form-item>
            <el-form-item label="频次">
              <el-input v-model="state.form.frequency" placeholder="如 每日两次" />
            </el-form-item>
            <el-form-item label="途径">
              <el-input v-model="state.form.route" placeholder="口服/外用/注射" />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="state.form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="state.form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="使用说明">
              <el-input v-model="state.form.instructions" type="textarea" rows="2" placeholder="补充说明" />
            </el-form-item>
            <el-form-item label="禁忌">
              <el-input v-model="state.form.contraindications" type="textarea" rows="2" placeholder="禁忌/注意事项" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'dewormingRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="驱虫药品">
              <el-select v-model="state.form.productId" filterable placeholder="选择驱虫药品">
                <el-option v-for="item in dewormingProductOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="来源">
              <el-input v-model="state.form.sourceType" placeholder="如 线上商城/线下购买" />
            </el-form-item>
            <el-form-item label="机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="兽医">
              <el-select v-model="state.form.vetUserId" filterable placeholder="选择兽医">
                <el-option v-for="item in vetUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="使用日期">
              <el-date-picker v-model="state.form.applicationDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="下次驱虫">
              <el-date-picker v-model="state.form.nextDueDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="剂量">
              <el-input v-model="state.form.dosage" placeholder="填写使用剂量" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.recordedBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'petPlans'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="计划类型">
              <el-input v-model="state.form.planType" placeholder="如 饮食/运动" />
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="state.form.title" placeholder="计划标题" />
            </el-form-item>
            <el-form-item label="目标">
              <el-input v-model="state.form.target" placeholder="达成目标" />
            </el-form-item>
            <el-form-item label="频率">
              <el-input v-model="state.form.frequency" placeholder="如 每日/每周" />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="state.form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="state.form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="创建人">
              <el-select v-model="state.form.createdBy" filterable clearable placeholder="选择创建人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'reminderTypes'">
            <el-form-item label="类型名称">
              <el-input v-model="state.form.typeName" placeholder="请输入提醒类型名称" />
            </el-form-item>
            <el-form-item label="来源表">
              <el-input v-model="state.form.sourceTable" placeholder="如 vaccinations" />
            </el-form-item>
            <el-form-item label="来源字段">
              <el-input v-model="state.form.sourceField" placeholder="如 next_due_date" />
            </el-form-item>
            <el-form-item label="提前天数">
              <el-input v-model.number="state.form.advanceDays" type="number" placeholder="如 7" />
            </el-form-item>
            <el-form-item label="模板内容">
              <el-input v-model="state.form.templateMessage" type="textarea" rows="3" placeholder="请输入提醒模板" />
            </el-form-item>
            <el-form-item label="自动生成">
              <el-switch v-model="state.form.autoGenerated" size="small" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'financeRecords'">
            <el-form-item label="用户ID">
              <el-input v-model="state.form.userId" type="number" placeholder="填写所属用户ID" />
            </el-form-item>
            <el-form-item label="宠物ID">
              <el-input v-model="state.form.petId" type="number" placeholder="填写宠物ID" />
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="state.form.title" placeholder="如 疫苗支出/理赔收入" />
            </el-form-item>
            <el-form-item label="类型">
              <el-input v-model="state.form.recordType" placeholder="支出/收入" />
            </el-form-item>
            <el-form-item label="关联就诊">
              <el-input v-model="state.form.medicalRecordId" type="number" placeholder="关联医疗记录ID（可选）" />
            </el-form-item>
            <el-form-item label="分类">
              <el-input v-model="state.form.category" placeholder="如 医疗/保险/用品" />
            </el-form-item>
            <el-form-item label="金额">
              <el-input v-model.number="state.form.amount" type="number" placeholder="填写金额" />
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="state.form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="状态">
              <el-input v-model="state.form.status" placeholder="如 已支付/待报销" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'reminders'">
            <el-form-item label="提醒类型">
              <el-select v-model="state.form.reminderTypeId" filterable placeholder="选择提醒类型">
                <el-option v-for="item in reminderTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="用户">
              <el-select v-model="state.form.userId" filterable placeholder="选择用户">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="state.form.title" placeholder="提醒标题" />
            </el-form-item>
            <el-form-item label="消息">
              <el-input v-model="state.form.message" type="textarea" rows="2" placeholder="提醒内容" />
            </el-form-item>
            <el-form-item label="来源表">
              <el-input v-model="state.form.sourceTable" placeholder="来源表名" />
            </el-form-item>
            <el-form-item label="来源ID">
              <el-input v-model="state.form.sourceRecordId" type="number" placeholder="来源记录ID" />
            </el-form-item>
            <el-form-item label="到期日">
              <el-date-picker v-model="state.form.dueDate" type="date" value-format="YYYY-MM-DD" placeholder="到期时间" />
            </el-form-item>
            <el-form-item label="提醒日">
              <el-date-picker v-model="state.form.reminderDate" type="date" value-format="YYYY-MM-DD" placeholder="提醒时间" />
            </el-form-item>
            <el-form-item label="已完成">
              <el-switch v-model="state.form.completed" size="small" />
            </el-form-item>
            <el-form-item label="完成日期">
              <el-date-picker v-model="state.form.completedDate" type="date" value-format="YYYY-MM-DD" placeholder="完成日期" />
            </el-form-item>
            <el-form-item label="发送次数">
              <el-input v-model.number="state.form.sentCount" type="number" placeholder="发送次数" />
            </el-form-item>
            <el-form-item label="最后发送">
              <el-date-picker v-model="state.form.lastSentAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="最后发送时间" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'appointments'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="主人">
              <el-select v-model="state.form.ownerUserId" filterable placeholder="选择主人">
                <el-option v-for="item in ownerUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="兽医">
              <el-select v-model="state.form.vetUserId" filterable placeholder="选择兽医">
                <el-option v-for="item in vetUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="预约日期">
              <el-date-picker v-model="state.form.appointmentDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="预约时间">
              <el-time-picker v-model="state.form.appointmentTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="选择时间" />
            </el-form-item>
            <el-form-item label="预约类型">
              <el-input v-model="state.form.appointmentType" placeholder="如 体检/疫苗接种" />
            </el-form-item>
            <el-form-item label="事由">
              <el-input v-model="state.form.reason" type="textarea" rows="2" placeholder="预约原因" />
            </el-form-item>
            <el-form-item label="状态">
              <el-input v-model="state.form.status" placeholder="如 待确认/已确认" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="补充说明" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'vaccinations'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="疫苗类型">
              <el-select v-model="state.form.vaccineTypeId" filterable placeholder="选择疫苗类型">
                <el-option v-for="item in vaccineOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="兽医">
              <el-select v-model="state.form.vetUserId" filterable placeholder="选择兽医">
                <el-option v-for="item in vetUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="接种日期">
              <el-date-picker v-model="state.form.vaccinationDate" type="date" value-format="YYYY-MM-DD" placeholder="选择接种日期" />
            </el-form-item>
            <el-form-item label="下次接种">
              <el-date-picker v-model="state.form.nextDueDate" type="date" value-format="YYYY-MM-DD" placeholder="选择下次接种日期" />
            </el-form-item>
            <el-form-item label="批号">
              <el-input v-model="state.form.lotNumber" placeholder="填写疫苗批号" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.createdBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'medicalRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="机构">
              <el-select v-model="state.form.institutionId" filterable placeholder="选择机构">
                <el-option v-for="item in institutionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="兽医">
              <el-select v-model="state.form.vetUserId" filterable placeholder="选择兽医">
                <el-option v-for="item in vetUserOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="就诊日期">
              <el-date-picker v-model="state.form.visitDate" type="date" value-format="YYYY-MM-DD" placeholder="选择就诊日期" />
            </el-form-item>
            <el-form-item label="就诊事由">
              <el-input v-model="state.form.reason" type="textarea" rows="2" placeholder="填写事由" />
            </el-form-item>
            <el-form-item label="诊断">
              <el-input v-model="state.form.diagnosis" placeholder="填写诊断" />
            </el-form-item>
            <el-form-item label="治疗方案">
              <el-input v-model="state.form.treatment" type="textarea" rows="2" placeholder="填写治疗方案" />
            </el-form-item>
            <el-form-item label="处方">
              <el-input v-model="state.form.prescription" type="textarea" rows="2" placeholder="填写处方" />
            </el-form-item>
            <el-form-item label="费用">
              <el-input v-model.number="state.form.cost" type="number" placeholder="费用金额" />
            </el-form-item>
            <el-form-item label="支付状态">
              <el-input v-model="state.form.paymentStatus" placeholder="如 未支付/已支付" />
            </el-form-item>
            <el-form-item label="记录状态">
              <el-input v-model="state.form.recordStatus" placeholder="如 处理中/完成" />
            </el-form-item>
            <el-form-item label="理赔编号">
              <el-input v-model="state.form.insuranceClaimId" placeholder="填写理赔编号" />
            </el-form-item>
            <el-form-item label="复诊日期">
              <el-date-picker v-model="state.form.followUpDate" type="date" value-format="YYYY-MM-DD" placeholder="选择复诊日期" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'weightRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="体重(kg)">
              <el-input v-model.number="state.form.weight" type="number" placeholder="填写体重" />
            </el-form-item>
            <el-form-item label="记录日期">
              <el-date-picker v-model="state.form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.recordedBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'temperatureRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="体温(℃)">
              <el-input v-model.number="state.form.temperature" type="number" placeholder="填写体温" />
            </el-form-item>
            <el-form-item label="记录日期">
              <el-date-picker v-model="state.form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.recordedBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'activityRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="活动水平">
              <el-input v-model.number="state.form.activityLevel" type="number" placeholder="填写活动水平数值" />
            </el-form-item>
            <el-form-item label="记录日期">
              <el-date-picker v-model="state.form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.recordedBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'intakeRecords'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="摄入量">
              <el-input v-model.number="state.form.intakeVolume" type="number" placeholder="填写摄入量" />
            </el-form-item>
            <el-form-item label="记录日期">
              <el-date-picker v-model="state.form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item label="记录人">
              <el-select v-model="state.form.recordedBy" filterable placeholder="选择记录人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </template>
          <template v-else-if="key === 'insurance'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="保险公司">
              <el-input v-model="state.form.insuranceCompany" placeholder="请输入保险公司名称" />
            </el-form-item>
            <el-form-item label="保单号">
              <el-input v-model="state.form.policyNumber" placeholder="请输入保单号" />
            </el-form-item>
            <el-form-item label="保障类型">
              <el-input v-model="state.form.coverageType" placeholder="如 基础医疗/全面医疗" />
            </el-form-item>
            <el-form-item label="年保费">
              <el-input v-model.number="state.form.annualPremium" type="number" placeholder="输入金额" />
            </el-form-item>
            <el-form-item label="保额">
              <el-input v-model.number="state.form.coverageLimit" type="number" placeholder="输入保额" />
            </el-form-item>
            <el-form-item label="免赔额">
              <el-input v-model.number="state.form.deductible" type="number" placeholder="输入免赔额" />
            </el-form-item>
            <el-form-item label="生效日期">
              <el-date-picker v-model="state.form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" />
            </el-form-item>
            <el-form-item label="到期日期">
              <el-date-picker v-model="state.form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="state.form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.notes" type="textarea" rows="2" placeholder="补充说明" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'feedback'">
            <el-form-item label="用户">
              <el-select v-model="state.form.userId" filterable placeholder="选择用户">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="反馈类型">
              <el-input v-model="state.form.feedbackType" placeholder="如 系统建议 / 服务投诉" />
            </el-form-item>
            <el-form-item label="反馈内容">
              <el-input v-model="state.form.content" type="textarea" rows="3" placeholder="请输入反馈内容" />
            </el-form-item>
            <el-form-item label="评分">
              <el-input v-model.number="state.form.rating" type="number" min="1" max="5" placeholder="1-5 分" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="state.form.status" placeholder="选择状态">
                <el-option v-for="item in feedbackStatusOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="回复内容">
              <el-input v-model="state.form.response" type="textarea" rows="2" placeholder="处理/回复内容" />
            </el-form-item>
            <el-form-item label="处理人">
              <el-select v-model="state.form.respondedBy" filterable placeholder="选择处理人">
                <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="处理时间">
              <el-date-picker v-model="state.form.respondedAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择时间" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'pets'">
            <el-form-item label="宠物名">
              <el-input v-model="state.form.petName" />
            </el-form-item>
            <el-form-item label="主人">
              <el-select
                v-model="state.form.ownerUsername"
                filterable
                clearable
                placeholder="请选择或输入主人用户名"
              >
                <el-option
                  v-for="owner in ownerOptions"
                  :key="owner.userId"
                  :label="owner.label"
                  :value="owner.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="物种">
              <el-input v-model="state.form.species" />
            </el-form-item>
            <el-form-item label="品种">
              <el-input v-model="state.form.breed" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="state.form.gender" placeholder="请选择">
                <el-option label="公" value="M" />
                <el-option label="母" value="F" />
                <el-option label="未知" value="U" />
              </el-select>
            </el-form-item>
            <el-form-item label="出生日期">
              <el-date-picker v-model="state.form.birthDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="毛色">
              <el-input v-model="state.form.color" />
            </el-form-item>
            <el-form-item label="性格">
              <el-input v-model="state.form.temperament" />
            </el-form-item>
            <el-form-item label="初始体重(kg)">
              <el-input v-model="state.form.weight" type="number" />
            </el-form-item>
            <el-form-item label="当前体重(kg)">
              <el-input v-model="state.form.currentWeight" type="number" />
            </el-form-item>
            <el-form-item label="是否绝育">
              <el-switch v-model="state.form.sterilized" size="small" />
            </el-form-item>
            <el-form-item label="芯片号">
              <el-input v-model="state.form.microchipId" />
            </el-form-item>
            <el-form-item label="耳号">
              <el-input v-model="state.form.earId" />
            </el-form-item>
            <el-form-item label="体型">
              <el-input v-model="state.form.bodyType" />
            </el-form-item>
            <el-form-item label="特殊需求">
              <el-input v-model="state.form.specialNeeds" type="textarea" rows="2" />
            </el-form-item>
            <el-form-item label="照片地址">
              <el-input v-model="state.form.photoUrl" />
            </el-form-item>
            <el-form-item label="健康状态">
              <el-input v-model="state.form.healthStatus" />
            </el-form-item>
            <el-form-item label="领养/购入日期">
              <el-date-picker v-model="state.form.adoptDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="登记/证件">
              <el-input v-model="state.form.registrationInfo" />
            </el-form-item>
            <el-form-item label="血型">
              <el-input v-model="state.form.bloodType" />
            </el-form-item>
            <el-form-item label="过敏源">
              <el-input v-model="state.form.allergies" />
            </el-form-item>
            <el-form-item label="慢性病">
              <el-input v-model="state.form.chronicDiseases" />
            </el-form-item>
            <el-form-item label="遗传风险">
              <el-input v-model="state.form.geneticRisks" />
            </el-form-item>
            <el-form-item label="禁用药物">
              <el-input v-model="state.form.bannedDrugs" />
            </el-form-item>
            <el-form-item label="最近体检日期">
              <el-date-picker v-model="state.form.lastMedicalCheck" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="state.form.remark" type="textarea" rows="2" />
            </el-form-item>
          </template>
          <template v-else-if="key === 'petPhotos'">
            <el-form-item label="宠物">
              <el-select v-model="state.form.petId" filterable placeholder="选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.value" :label="pet.label" :value="pet.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="文件名">
              <el-input v-model="state.form.fileName" placeholder="默认使用上传文件名" />
            </el-form-item>
            <el-form-item label="内容类型">
              <el-input v-model="state.form.contentType" placeholder="image/jpeg" />
            </el-form-item>
            <el-form-item label="坐标">
              <el-space>
                <el-input v-model.number="state.form.posX" type="number" placeholder="posX" style="width: 120px" />
                <el-input v-model.number="state.form.posY" type="number" placeholder="posY" style="width: 120px" />
              </el-space>
            </el-form-item>
            <el-form-item label="上传图片">
              <el-upload
                action=""
                :auto-upload="false"
                :show-file-list="false"
                :on-change="(file) => handlePhotoFile(file, state)"
              >
                <el-button type="primary" plain size="small">选择文件</el-button>
                <span style="margin-left: 8px; color: #8a8fa3">{{ state.form.fileName || '未选择文件' }}</span>
              </el-upload>
              <div class="hint" v-if="state.form.photoDataBase64">
                已载入 {{ Math.round((state.form.photoDataBase64.length * 3) / 4 / 1024) }} KB
              </div>
              <div class="hint" v-else>编辑时可不重新上传，新增时需上传文件</div>
            </el-form-item>
          </template>
          <template v-else>
            <el-form-item v-for="(val, field) in state.form" :key="field" :label="field">
              <el-input v-model="state.form[field]" />
            </el-form-item>
          </template>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="state.visible = false">取消</el-button>
            <el-button type="primary" @click="saveRecord(key)">保存</el-button>
          </div>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { reactive, ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Monitor,
  User,
  Setting,
  Collection,
  Bell,
  OfficeBuilding,
  FirstAidKit,
  FolderOpened,
  Management,
  Calendar,
  Document,
  Picture,
  Edit,
  Delete,
  Filter
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import brandLogo from '@/assets/images/logo.png'
import { adminApi } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import OverviewSection from './sections/OverviewSection.vue'
import RolesSection from './sections/RolesSection.vue'
import UsersSection from './sections/UsersSection.vue'
import InstitutionsSection from './sections/InstitutionsSection.vue'
import VeterinariansSection from './sections/VeterinariansSection.vue'
import VaccinesSection from './sections/VaccinesSection.vue'
import MedicationsSection from './sections/MedicationsSection.vue'
import PetsSection from './sections/PetsSection.vue'
import PetPhotosSection from './sections/PetPhotosSection.vue'
import VaccinationsSection from './sections/VaccinationsSection.vue'
import MedicalRecordsSection from './sections/MedicalRecordsSection.vue'
import WeightRecordsSection from './sections/WeightRecordsSection.vue'
import TemperatureRecordsSection from './sections/TemperatureRecordsSection.vue'
import ActivityRecordsSection from './sections/ActivityRecordsSection.vue'
import IntakeRecordsSection from './sections/IntakeRecordsSection.vue'
import DewormingRecordsSection from './sections/DewormingRecordsSection.vue'
import FinanceRecordsSection from './sections/FinanceRecordsSection.vue'
import MedicationInventorySection from './sections/MedicationInventorySection.vue'
import PetMedicationsSection from './sections/PetMedicationsSection.vue'
import PetPlansSection from './sections/PetPlansSection.vue'
import RemindersSection from './sections/RemindersSection.vue'
import AppointmentsSection from './sections/AppointmentsSection.vue'
import ReminderTypesSection from './sections/ReminderTypesSection.vue'
import FeedbackSection from './sections/FeedbackSection.vue'
import InsuranceSection from './sections/InsuranceSection.vue'
import SettingsSection from './sections/SettingsSection.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const overviewCards = [
  { title: '活跃用户', value: '1,248', desc: '最近30天登录', icon: User },
  { title: '数据总览', value: '3,582', desc: '宠物档案/健康记录', icon: Collection },
  { title: '系统通知', value: '8', desc: '待处理告警', icon: Bell }
]

const adminName = computed(() => authStore.userInfo?.username || authStore.username || '管理员')

const rawUserData = ref([])
const roleData = ref([])
const userFilters = reactive({
  username: '',
  email: '',
  phone: '',
  roleName: '',
  active: ''
})
const userFilterVisible = ref(false)
const userData = ref([])
const institutionData = ref([])
const veterinarianData = ref([])
const vaccineData = ref([])
const medicationData = ref([])
const medicationInventoryData = ref([])
const petMedicationData = ref([])
const dewormingRecordData = ref([])
const petPlanData = ref([])
const financeRecordData = ref([])
const reminderData = ref([])
const petData = ref([])
const petPhotoFilter = ref('')
const appointmentData = ref([])
const vaccinationData = ref([])
const medicalRecordData = ref([])
const weightRecordData = ref([])
const temperatureRecordData = ref([])
const activityRecordData = ref([])
const intakeRecordData = ref([])
const reminderTypeData = ref([])
const feedbackData = ref([])
const insuranceData = ref([])
const petPhotoData = ref([])

const feedbackStatusOptions = ['待处理', '处理中', '已解决']
const userNameMap = computed(() => {
  const map = {}
  ;(rawUserData.value || []).forEach((u) => {
    if (u?.userId) {
      map[u.userId] = u.username || u.nickname || `用户${u.userId}`
    }
  })
  return map
})
const petNameMap = computed(() => {
  const map = {}
  ;(petData.value || []).forEach((pet) => {
    if (pet?.petId != null) {
      map[pet.petId] = pet.petName || `宠物${pet.petId}`
    }
  })
  return map
})
const institutionNameMap = computed(() => {
  const map = {}
  ;(institutionData.value || []).forEach((ins) => {
    if (ins?.institutionId != null) {
      map[ins.institutionId] = ins.institutionName || `机构${ins.institutionId}`
    }
  })
  return map
})
const vetNameMap = computed(() => {
  const map = {}
  ;(veterinarianData.value || []).forEach((vet) => {
    const userId = vet.userId || vet.user?.userId
    if (userId != null) {
      map[userId] = vet.userName || vet.user?.username || userNameMap.value[userId] || `兽医${userId}`
    }
  })
  return map
})
const vaccineTypeNameMap = computed(() => {
  const map = {}
  ;(vaccineData.value || []).forEach((vaccine) => {
    if (vaccine?.vaccineTypeId != null) {
      map[vaccine.vaccineTypeId] = vaccine.vaccineName || `疫苗${vaccine.vaccineTypeId}`
    }
  })
  return map
})
const dewormingProductNameMap = computed(() => {
  const map = {}
  ;(medicationData.value || []).forEach((product) => {
    if (product?.productId != null) {
      map[product.productId] = product.productName || `药品${product.productId}`
    }
  })
  return map
})
const reminderTypeNameMap = computed(() => {
  const map = {}
  ;(reminderTypeData.value || []).forEach((type) => {
    if (type?.reminderTypeId != null) {
      map[type.reminderTypeId] = type.typeName || `类型${type.reminderTypeId}`
    }
  })
  return map
})

const sectionKeys = [
  'overview',
  'roles',
  'users',
  'institutions',
  'veterinarians',
  'vaccines',
  'medications',
  'medicationInventory',
  'petMedications',
  'dewormingRecords',
  'petPlans',
  'financeRecords',
  'pets',
  'petPhotos',
  'reminders',
  'vaccinations',
  'medicalRecords',
  'weightRecords',
  'temperatureRecords',
  'activityRecords',
  'intakeRecords',
  'appointments',
  'reminderTypes',
  'feedback',
  'insurance',
  'settings'
]

const typeLabels = {
  roles: '角色',
  users: '用户',
  institutions: '医疗机构',
  veterinarians: '兽医信息',
  vaccines: '疫苗类型',
  pets: '宠物档案',
  petPhotos: '宠物照片',
  medications: '驱虫药品',
  medicationInventory: '药品库存',
  petMedications: '用药记录',
  dewormingRecords: '驱虫记录',
  petPlans: '宠物计划',
  financeRecords: '财务记录',
  reminders: '提醒记录',
  vaccinations: '疫苗接种',
  medicalRecords: '医疗记录',
  weightRecords: '体重记录',
  temperatureRecords: '体温记录',
  activityRecords: '活动记录',
  intakeRecords: '摄入记录',
  appointments: '预约',
  reminderTypes: '提醒类型',
  feedback: '用户反馈',
  insurance: '保险',
  settings: '系统配置'
}

const dataMap = {
  roles: roleData,
  users: userData,
  institutions: institutionData,
  veterinarians: veterinarianData,
  vaccines: vaccineData,
  pets: petData,
  petPhotos: petPhotoData,
  medications: medicationData,
  medicationInventory: medicationInventoryData,
  petMedications: petMedicationData,
  dewormingRecords: dewormingRecordData,
  petPlans: petPlanData,
  financeRecords: financeRecordData,
  reminders: reminderData,
  vaccinations: vaccinationData,
  medicalRecords: medicalRecordData,
  weightRecords: weightRecordData,
  temperatureRecords: temperatureRecordData,
  activityRecords: activityRecordData,
  intakeRecords: intakeRecordData,
  appointments: appointmentData,
  reminderTypes: reminderTypeData,
  feedback: feedbackData,
  insurance: insuranceData
}

const dialog = reactive({
  roles: { visible: false, title: '', index: -1, form: createEmptyForm('roles') },
  users: { visible: false, title: '', index: -1, form: createEmptyForm('users') },
  institutions: { visible: false, title: '', index: -1, form: createEmptyForm('institutions') },
  veterinarians: { visible: false, title: '', index: -1, form: createEmptyForm('veterinarians') },
  vaccines: { visible: false, title: '', index: -1, form: createEmptyForm('vaccines') },
  pets: { visible: false, title: '', index: -1, form: createEmptyForm('pets') },
  petPhotos: { visible: false, title: '', index: -1, form: createEmptyForm('petPhotos') },
  medications: { visible: false, title: '', index: -1, form: createEmptyForm('medications') },
  medicationInventory: { visible: false, title: '', index: -1, form: createEmptyForm('medicationInventory') },
  petMedications: { visible: false, title: '', index: -1, form: createEmptyForm('petMedications') },
  dewormingRecords: { visible: false, title: '', index: -1, form: createEmptyForm('dewormingRecords') },
  petPlans: { visible: false, title: '', index: -1, form: createEmptyForm('petPlans') },
  financeRecords: { visible: false, title: '', index: -1, form: createEmptyForm('financeRecords') },
  reminders: { visible: false, title: '', index: -1, form: createEmptyForm('reminders') },
  vaccinations: { visible: false, title: '', index: -1, form: createEmptyForm('vaccinations') },
  medicalRecords: { visible: false, title: '', index: -1, form: createEmptyForm('medicalRecords') },
  weightRecords: { visible: false, title: '', index: -1, form: createEmptyForm('weightRecords') },
  temperatureRecords: { visible: false, title: '', index: -1, form: createEmptyForm('temperatureRecords') },
  activityRecords: { visible: false, title: '', index: -1, form: createEmptyForm('activityRecords') },
  intakeRecords: { visible: false, title: '', index: -1, form: createEmptyForm('intakeRecords') },
  appointments: { visible: false, title: '', index: -1, form: createEmptyForm('appointments') },
  reminderTypes: { visible: false, title: '', index: -1, form: createEmptyForm('reminderTypes') },
  feedback: { visible: false, title: '', index: -1, form: createEmptyForm('feedback') },
  insurance: { visible: false, title: '', index: -1, form: createEmptyForm('insurance') }
})

onMounted(() => {
  loadAll()
})

const activeKey = ref(resolveSection(route.query.section))

watch(
  () => route.query.section,
  (section) => {
    activeKey.value = resolveSection(section)
  }
)

watch(
  () => rawUserData.value,
  () => {
    feedbackData.value = mapFeedbackWithNames(feedbackData.value || [])
    financeRecordData.value = mapFinanceWithNames(financeRecordData.value || [])
    veterinarianData.value = formatVeterinarianData(veterinarianData.value || [])
    petPlanData.value = mapPetPlanNames(petPlanData.value || [])
    reminderData.value = mapReminderWithNames(reminderData.value || [])
    appointmentData.value = mapAppointmentNames(appointmentData.value || [])
  },
  { deep: true }
)

function resolveSection(section) {
  return sectionKeys.includes(section) ? section : 'overview'
}

const onSelect = (key) => {
  activeKey.value = key
  router.replace({ path: route.path, query: { section: key } })
}

const goHome = async () => {
  try {
    if (!authStore.userInfo) {
      await authStore.fetchUserInfo()
    }
  } catch (e) {
    console.error('刷新用户信息失败', e)
  } finally {
    router.push('/')
  }
}

const loadUsers = async () => {
  try {
    const res = await adminApi.listUsers()
    rawUserData.value = (res?.data || []).map((u) => ({
      ...u,
      role: u.role?.roleName,
      roleName: u.role?.roleName,
      status: u.active ? '启用' : '禁用',
      lastLogin: u.lastLogin
    }))
    applyUserFilter()
    medicalRecordData.value = mapMedicalRecordNames(medicalRecordData.value || [])
    vaccinationData.value = mapVaccinationNames(vaccinationData.value || [])
    dewormingRecordData.value = mapDewormingRecordNames(dewormingRecordData.value || [])
    weightRecordData.value = mapRecordedByNames(weightRecordData.value || [])
    temperatureRecordData.value = mapRecordedByNames(temperatureRecordData.value || [])
    activityRecordData.value = mapRecordedByNames(activityRecordData.value || [])
    intakeRecordData.value = mapRecordedByNames(intakeRecordData.value || [])
    feedbackData.value = mapFeedbackWithNames(feedbackData.value || [])
    petPlanData.value = mapPetPlanNames(petPlanData.value || [])
    reminderData.value = mapReminderWithNames(reminderData.value || [])
    appointmentData.value = mapAppointmentNames(appointmentData.value || [])
  } catch (error) {
    console.error('加载用户失败', error)
  }
}

const loadRoles = async () => {
  try {
    const res = await adminApi.listRoles()
    roleData.value = res?.data || []
  } catch (error) {
    console.error('加载角色失败', error)
  }
}

const loadInstitutions = async () => {
  try {
    const res = await adminApi.listInstitutions()
    institutionData.value = res?.data || []
    medicalRecordData.value = mapMedicalRecordNames(medicalRecordData.value || [])
    vaccinationData.value = mapVaccinationNames(vaccinationData.value || [])
    dewormingRecordData.value = mapDewormingRecordNames(dewormingRecordData.value || [])
    medicationInventoryData.value = mapMedicationInventoryNames(medicationInventoryData.value || [])
    appointmentData.value = mapAppointmentNames(appointmentData.value || [])
  } catch (error) {
    console.error('加载机构失败', error)
  }
}

const loadVeterinarians = async () => {
  try {
    const res = await adminApi.listVeterinarians()
    veterinarianData.value = formatVeterinarianData(res?.data || [])
    medicalRecordData.value = mapMedicalRecordNames(medicalRecordData.value || [])
    vaccinationData.value = mapVaccinationNames(vaccinationData.value || [])
    dewormingRecordData.value = mapDewormingRecordNames(dewormingRecordData.value || [])
    appointmentData.value = mapAppointmentNames(appointmentData.value || [])
  } catch (error) {
    console.error('加载兽医信息失败', error)
  }
}

const loadVaccines = async () => {
  try {
    const res = await adminApi.listVaccineTypes()
    vaccineData.value = res?.data || []
    vaccinationData.value = mapVaccinationNames(vaccinationData.value || [])
  } catch (error) {
    console.error('加载疫苗类型失败', error)
  }
}

const loadMedications = async () => {
  try {
    const res = await adminApi.listMedications()
    medicationData.value = res?.data || []
    dewormingRecordData.value = mapDewormingRecordNames(dewormingRecordData.value || [])
  } catch (error) {
    console.error('加载药品失败', error)
  }
}

const loadMedicationInventory = async () => {
  try {
    const res = await adminApi.listMedicationInventory()
    medicationInventoryData.value = mapMedicationInventoryNames(res?.data || [])
  } catch (error) {
    console.error('加载药品库存失败', error)
  }
}

const loadPetMedications = async () => {
  try {
    const res = await adminApi.listPetMedications()
    petMedicationData.value = normalizePetInfo(res?.data || [])
  } catch (error) {
    console.error('加载用药记录失败', error)
  }
}

const loadDewormingRecords = async () => {
  try {
    const res = await adminApi.listDewormingRecords()
    dewormingRecordData.value = mapDewormingRecordNames(res?.data || [])
  } catch (error) {
    console.error('加载驱虫记录失败', error)
  }
}

const loadPetPlans = async () => {
  try {
    const res = await adminApi.listPetPlans()
    petPlanData.value = mapPetPlanNames(res?.data || [])
  } catch (error) {
    console.error('加载宠物计划失败', error)
  }
}

const loadFinanceRecords = async () => {
  try {
    const res = await adminApi.listFinanceRecords()
    financeRecordData.value = mapFinanceWithNames(res?.data || [])
  } catch (error) {
    console.error('加载财务记录失败', error)
  }
}

const loadReminders = async () => {
  try {
    const res = await adminApi.listReminders()
    reminderData.value = mapReminderWithNames(res?.data || [])
  } catch (error) {
    console.error('加载提醒记录失败', error)
  }
}

const loadPets = async () => {
  try {
    const res = await adminApi.listPets()
    petData.value = res?.data || []
    dewormingRecordData.value = mapDewormingRecordNames(dewormingRecordData.value || [])
    insuranceData.value = mapInsuranceNames(insuranceData.value || [])
    weightRecordData.value = mapRecordedByNames(weightRecordData.value || [])
    temperatureRecordData.value = mapRecordedByNames(temperatureRecordData.value || [])
    activityRecordData.value = mapRecordedByNames(activityRecordData.value || [])
    intakeRecordData.value = mapRecordedByNames(intakeRecordData.value || [])
    petPlanData.value = mapPetPlanNames(petPlanData.value || [])
    reminderData.value = mapReminderWithNames(reminderData.value || [])
    appointmentData.value = mapAppointmentNames(appointmentData.value || [])
  } catch (error) {
    console.error('加载宠物失败', error)
  }
}

const loadPetPhotos = async (petId) => {
  try {
    const res = await adminApi.listPetPhotos({ petId: petId || undefined, limit: 200 })
    const token = authStore.token || localStorage.getItem('token') || ''
    petPhotoData.value = (res?.data || []).map((item) => {
      if (!item?.previewUrl || !token) {
        return item
      }
      const separator = item.previewUrl.includes('?') ? '&' : '?'
      return {
        ...item,
        previewUrl: `${item.previewUrl}${separator}token=${encodeURIComponent(token)}`
      }
    })
  } catch (error) {
    console.error('加载照片失败', error)
  }
}

const filterPetPhotos = (petId) => {
  petPhotoFilter.value = petId || ''
  loadPetPhotos(petPhotoFilter.value || undefined)
}

const loadAppointments = async () => {
  try {
    const res = await adminApi.listAppointments({ page: 1, size: 200 })
    const records = res?.data?.records || res?.data || []
    appointmentData.value = mapAppointmentNames(records)
  } catch (error) {
    console.error('加载预约失败', error)
  }
}

const loadVaccinations = async () => {
  try {
    const res = await adminApi.listVaccinations()
    vaccinationData.value = mapVaccinationNames(normalizePetInfo(res?.data || []))
  } catch (error) {
    console.error('加载接种记录失败', error)
  }
}

const loadMedicalRecords = async () => {
  try {
    const res = await adminApi.listMedicalRecords()
    medicalRecordData.value = mapMedicalRecordNames(normalizePetInfo(res?.data || []))
  } catch (error) {
    console.error('加载医疗记录失败', error)
  }
}

const loadWeightRecords = async () => {
  try {
    const res = await adminApi.listWeightRecords()
    weightRecordData.value = mapRecordedByNames(res?.data || [])
  } catch (error) {
    console.error('加载体重记录失败', error)
  }
}

const loadTemperatureRecords = async () => {
  try {
    const res = await adminApi.listTemperatureRecords()
    temperatureRecordData.value = mapRecordedByNames(res?.data || [])
  } catch (error) {
    console.error('加载体温记录失败', error)
  }
}

const loadActivityRecords = async () => {
  try {
    const res = await adminApi.listActivityRecords()
    activityRecordData.value = mapRecordedByNames(res?.data || [])
  } catch (error) {
    console.error('加载活动记录失败', error)
  }
}

const loadIntakeRecords = async () => {
  try {
    const res = await adminApi.listIntakeRecords()
    intakeRecordData.value = mapRecordedByNames(res?.data || [])
  } catch (error) {
    console.error('加载摄入记录失败', error)
  }
}

const loadReminderTypes = async () => {
  try {
    const res = await adminApi.listReminderTypes()
    reminderTypeData.value = res?.data || []
    reminderData.value = mapReminderWithNames(reminderData.value || [])
  } catch (error) {
    console.error('加载提醒类型失败', error)
  }
}

const loadFeedback = async () => {
  try {
    const res = await adminApi.listFeedback()
    feedbackData.value = mapFeedbackWithNames(res?.data || [])
  } catch (error) {
    console.error('加载反馈失败', error)
  }
}

const loadInsurance = async () => {
  try {
    const res = await adminApi.listInsurance()
    insuranceData.value = mapInsuranceNames(res?.data || [])
  } catch (error) {
    console.error('加载保险失败', error)
  }
}

const loadAll = async () => {
  const tasks = [
    loadRoles,
    loadUsers,
    loadInstitutions,
    loadVeterinarians,
    loadVaccines,
    loadMedications,
    loadMedicationInventory,
    loadPetMedications,
    loadDewormingRecords,
    loadPetPlans,
    loadFinanceRecords,
    loadPets,
    () => loadPetPhotos(petPhotoFilter.value || undefined),
    loadVaccinations,
    loadMedicalRecords,
    loadWeightRecords,
    loadTemperatureRecords,
    loadActivityRecords,
    loadIntakeRecords,
    loadReminders,
    loadAppointments,
    loadReminderTypes,
    loadFeedback,
    loadInsurance
  ]
  const results = await Promise.allSettled(tasks.map((fn) => fn()))
  const firstError = results.find((r) => r.status === 'rejected')
  if (firstError) {
    console.error('加载后台数据失败', firstError.reason || firstError)
    ElMessage.error(firstError.reason?.message || '加载后台数据失败')
  }
}

function createEmptyForm(type) {
  switch (type) {
    case 'roles':
      return { roleName: '', description: '', createdAt: '' }
    case 'users':
      return { username: '', roleName: '宠物主人', email: '', phone: '', password: '', active: true }
    case 'institutions':
      return { institutionName: '', institutionType: '宠物医院', address: '', phone: '', email: '', licenseNumber: '', description: '', verified: false, createdAt: '' }
    case 'veterinarians':
      return { userId: null, institutionId: null, licenseNumber: '', specialization: '', yearsExperience: null, qualification: '', position: '', verified: false }
    case 'vaccines':
      return { vaccineName: '', species: '通用', validityPeriodMonths: 12, description: '', active: true }
    case 'pets':
      return {
        petName: '',
        userId: null,
        ownerUsername: '',
        species: '狗',
        breed: '',
        gender: 'M',
        birthDate: '',
        color: '',
        temperament: '',
        weight: null,
        currentWeight: null,
        sterilized: false,
        microchipId: '',
        earId: '',
        bodyType: '',
        specialNeeds: '',
        photoUrl: '',
        healthStatus: '良好',
        adoptDate: '',
        registrationInfo: '',
        bloodType: '',
        allergies: '',
        chronicDiseases: '',
        geneticRisks: '',
        bannedDrugs: '',
        lastMedicalCheck: '',
        remark: '',
        createdAt: '',
        updatedAt: ''
      }
    case 'petPhotos':
      return {
        petId: null,
        fileName: '',
        contentType: '',
        photoDataBase64: '',
        posX: null,
        posY: null
      }
    case 'medications':
      return {
        productName: '',
        dewormingType: '内驱',
        species: '狗',
        validityPeriodDays: 0,
        dosageGuide: '',
        active: true
      }
    case 'medicationInventory':
      return {
        medicationName: '',
        medicationType: '',
        institutionId: '',
        quantity: '',
        unit: '',
        expiryDate: '',
        purchaseDate: '',
        supplier: '',
        unitPrice: '',
        storageLocation: '',
        createdAt: '',
        updatedAt: ''
      }
    case 'petMedications':
      return {
        petId: '',
        drugName: '',
        dosage: '',
        frequency: '',
        route: '',
        startDate: '',
        endDate: '',
        instructions: '',
        contraindications: '',
        createdAt: ''
      }
    case 'dewormingRecords':
      return {
        petId: '',
        productId: '',
        sourceType: '',
        institutionId: '',
        vetUserId: '',
        applicationDate: '',
        nextDueDate: '',
        dosage: '',
        notes: '',
        recordedBy: '',
        createdAt: ''
      }
    case 'petPlans':
      return {
        petId: '',
        planType: '',
        title: '',
        target: '',
        frequency: '',
        startDate: '',
        endDate: '',
        notes: '',
        createdBy: '',
        createdAt: ''
      }
    case 'financeRecords':
      return {
        userId: '',
        petId: '',
        title: '',
        recordType: '',
        medicalRecordId: '',
        category: '',
        amount: '',
        recordDate: '',
        status: '',
        notes: '',
        createdAt: ''
      }
    case 'reminders':
      return {
        reminderTypeId: '',
        petId: '',
        userId: '',
        sourceTable: '',
        sourceRecordId: '',
        title: '',
        message: '',
        dueDate: '',
        reminderDate: '',
        completed: false,
        completedDate: '',
        sentCount: '',
        lastSentAt: '',
        createdAt: ''
      }
    case 'appointments':
      return {
        petId: '',
        ownerUserId: '',
        institutionId: '',
        vetUserId: '',
        appointmentDate: '',
        appointmentTime: '09:00:00',
        appointmentType: '体检',
        reason: '',
        status: '待确认',
        notes: '',
        createdAt: '',
        updatedAt: ''
      }
    case 'vaccinations':
      return {
        petId: '',
        vaccineTypeId: '',
        institutionId: '',
        vetUserId: '',
        vaccinationDate: '',
        nextDueDate: '',
        lotNumber: '',
        notes: '',
        createdBy: '',
        createdAt: ''
      }
    case 'medicalRecords':
      return {
        petId: '',
        institutionId: '',
        vetUserId: '',
        visitDate: '',
        reason: '',
        diagnosis: '',
        treatment: '',
        prescription: '',
        cost: '',
        paymentStatus: '',
        recordStatus: '',
        insuranceClaimId: '',
        followUpDate: '',
        createdAt: ''
      }
    case 'weightRecords':
      return {
        petId: '',
        weight: '',
        recordDate: '',
        notes: '',
        recordedBy: '',
        createdAt: ''
      }
    case 'temperatureRecords':
      return {
        petId: '',
        temperature: '',
        recordDate: '',
        notes: '',
        recordedBy: '',
        createdAt: ''
      }
    case 'activityRecords':
      return {
        petId: '',
        activityLevel: '',
        recordDate: '',
        notes: '',
        recordedBy: '',
        createdAt: ''
      }
    case 'intakeRecords':
      return {
        petId: '',
        intakeVolume: '',
        recordDate: '',
        notes: '',
        recordedBy: '',
        createdAt: ''
      }
    case 'reminderTypes':
      return { typeName: '', sourceTable: 'vaccinations', sourceField: 'next_due_date', advanceDays: 7, templateMessage: '', autoGenerated: true }
    case 'feedback':
      return { userId: null, feedbackType: '系统建议', content: '', rating: 5, status: '待处理', response: '', respondedBy: null, respondedAt: '', createdAt: '' }
    case 'insurance':
      return {
        petId: null,
        insuranceCompany: '',
        policyNumber: '',
        coverageType: '基础医疗',
        startDate: '',
        endDate: '',
        annualPremium: null,
        coverageLimit: null,
        deductible: null,
        contactPhone: '',
        notes: '',
        createdAt: ''
      }
    default:
      return {}
  }
}

const mapFeedbackWithNames = (list) =>
  (list || []).map((item) => ({
    ...item,
    userName: userNameMap.value[item.userId] || item.userId,
    responderName: userNameMap.value[item.respondedBy] || item.respondedBy
  }))

const formatVeterinarianData = (list) =>
  (list || []).map((item) => {
    const userId = item.userId || item.user?.userId || item.user?.id
    const institutionId = item.institutionId || item.institution?.institutionId || item.institution?.id
    return {
      ...item,
      vetId: item.vetId || item.id,
      userId,
      userName: item.userName || item.user?.username || userNameMap.value[userId] || item.user?.nickname,
      institutionId,
      institutionName: item.institutionName || item.institution?.institutionName || item.institution?.name,
      verified: item.verified ?? item.isVerified ?? false
    }
  })

const normalizePetInfo = (list) =>
  (list || []).map((item) => ({
    ...item,
    petId: item.petId || item.pet?.petId || item.pet?.id,
    petName: item.petName || item.pet?.petName || item.pet?.name
  }))

const mapMedicalRecordNames = (list) =>
  (list || []).map((item) => {
    const institutionId = item.institutionId
    const vetUserId = item.vetUserId
    return {
      ...item,
      institutionName:
        item.institutionName ||
        item.institution?.institutionName ||
        (institutionId != null ? institutionNameMap.value[institutionId] : undefined),
      vetName:
        item.vetName ||
        item.vet?.username ||
        item.vet?.name ||
        (vetUserId != null ? (vetNameMap.value[vetUserId] || userNameMap.value[vetUserId]) : undefined)
    }
  })

const mapVaccinationNames = (list) =>
  (list || []).map((item) => {
    const institutionId = item.institutionId
    const vetUserId = item.vetUserId
    const vaccineTypeId = item.vaccineTypeId
    const createdBy = item.createdBy
    return {
      ...item,
      vaccineTypeName:
        item.vaccineTypeName ||
        item.vaccineType?.vaccineName ||
        (vaccineTypeId != null ? vaccineTypeNameMap.value[vaccineTypeId] : undefined),
      institutionName:
        item.institutionName ||
        item.institution?.institutionName ||
        (institutionId != null ? institutionNameMap.value[institutionId] : undefined),
      vetName:
        item.vetName ||
        item.vet?.username ||
        item.vet?.name ||
        (vetUserId != null ? (vetNameMap.value[vetUserId] || userNameMap.value[vetUserId]) : undefined),
      createdByName:
        item.createdByName ||
        (createdBy != null ? userNameMap.value[createdBy] : undefined)
    }
  })

const mapDewormingRecordNames = (list) =>
  (list || []).map((item) => {
    const normalized = normalizePetInfo([item])[0]
    const petId = normalized.petId
    const productId = normalized.productId
    const institutionId = normalized.institutionId
    const vetUserId = normalized.vetUserId
    const recordedBy = normalized.recordedBy
    return {
      ...normalized,
      petName: normalized.petName || (petId != null ? petNameMap.value[petId] : undefined),
      productName:
        normalized.productName ||
        (productId != null ? dewormingProductNameMap.value[productId] : undefined),
      institutionName:
        normalized.institutionName ||
        (institutionId != null ? institutionNameMap.value[institutionId] : undefined),
      vetName:
        normalized.vetName ||
        (vetUserId != null ? (vetNameMap.value[vetUserId] || userNameMap.value[vetUserId]) : undefined),
      recordedByName:
        normalized.recordedByName || (recordedBy != null ? userNameMap.value[recordedBy] : undefined)
    }
  })

const mapRecordedByNames = (list) =>
  (list || []).map((item) => {
    const normalized = normalizePetInfo([item])[0]
    const petId = normalized.petId
    const recordedBy = normalized.recordedBy
    return {
      ...normalized,
      petName: normalized.petName || (petId != null ? petNameMap.value[petId] : undefined),
      recordedByName:
        normalized.recordedByName || (recordedBy != null ? userNameMap.value[recordedBy] : undefined)
    }
  })

const mapInsuranceNames = (list) =>
  (list || []).map((item) => ({
    ...item,
    petName: item.petName || (item.petId != null ? petNameMap.value[item.petId] : undefined)
  }))

const mapFinanceWithNames = (list) =>
  (list || []).map((item) => {
    const userId = item.userId || item.owner?.userId
    return {
      ...normalizePetInfo([item])[0],
      userId,
      ownerName: userNameMap.value[userId] || item.owner?.username || userId
    }
  })

const mapPetPlanNames = (list) =>
  (list || []).map((item) => {
    const normalized = normalizePetInfo([item])[0]
    const petId = normalized.petId
    const createdBy = normalized.createdBy
    return {
      ...normalized,
      petName: normalized.petName || (petId != null ? petNameMap.value[petId] : undefined),
      createdByName:
        normalized.createdByName || (createdBy != null ? userNameMap.value[createdBy] : undefined)
    }
  })

const mapMedicationInventoryNames = (list) =>
  (list || []).map((item) => ({
    ...item,
    institutionName:
      item.institutionName ||
      (item.institutionId != null ? institutionNameMap.value[item.institutionId] : undefined)
  }))

const mapReminderWithNames = (list) =>
  (list || []).map((item) => {
    const normalized = normalizePetInfo([item])[0]
    const userId = normalized.userId || normalized.user?.userId
    const petId = normalized.petId
    return {
      ...normalized,
      userId,
      userName: normalized.userName || (userId != null ? userNameMap.value[userId] : undefined) || normalized.user?.username,
      petName: normalized.petName || (petId != null ? petNameMap.value[petId] : undefined),
      reminderTypeId: normalized.reminderTypeId || normalized.reminderType?.reminderTypeId,
      reminderTypeName:
        normalized.reminderType?.typeName ||
        normalized.typeName ||
        (normalized.reminderTypeId != null ? reminderTypeNameMap.value[normalized.reminderTypeId] : undefined)
    }
  })

const mapAppointmentNames = (list) =>
  (list || []).map((item) => {
    const petId = item.petId
    const ownerUserId = item.ownerUserId
    const institutionId = item.institutionId
    const vetUserId = item.vetUserId
    return {
      ...item,
      petName: item.petName || (petId != null ? petNameMap.value[petId] : undefined),
      ownerUsername: item.ownerUsername || (ownerUserId != null ? userNameMap.value[ownerUserId] : undefined),
      institutionName: item.institutionName || (institutionId != null ? institutionNameMap.value[institutionId] : undefined),
      vetUsername: item.vetUsername || (vetUserId != null ? (vetNameMap.value[vetUserId] || userNameMap.value[vetUserId]) : undefined)
    }
  })

const openCreate = (type) => {
  if (!dialog[type]) return
  dialog[type].title = `新增${typeLabels[type] || ''}`
  dialog[type].index = -1
  dialog[type].form = createEmptyForm(type)
  dialog[type].visible = true
}

const openEdit = (type, index) => {
  if (!dialog[type]) return
  const list = dataMap[type]?.value || []
  dialog[type].title = `编辑${typeLabels[type] || ''}`
  dialog[type].index = index
  dialog[type].form = { ...list[index] }
  dialog[type].visible = true
}

const handlePhotoFile = async (uploadFile, state) => {
  if (!uploadFile?.raw || !state?.form) return
  try {
    const base64 = await fileToBase64(uploadFile.raw)
    state.form.photoDataBase64 = base64.split(',')[1] || base64
    state.form.fileName = state.form.fileName || uploadFile.name
    state.form.contentType = state.form.contentType || uploadFile.raw.type
  } catch (error) {
    console.error('文件读取失败', error)
    ElMessage.error('读取图片失败，请重试')
  }
}

const fileToBase64 = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = (err) => reject(err)
    reader.readAsDataURL(file)
  })

const saveRecord = async (type) => {
  const state = dialog[type]
  if (!state) return
  const payload = { ...state.form }
  const current = dataMap[type]?.value?.[state.index]
  try {
    switch (type) {
      case 'roles':
        if (state.index === -1) {
          await adminApi.createRole(payload)
        } else {
          await adminApi.updateRole(current?.roleId, payload)
        }
        await loadRoles()
        break
      case 'users':
        if (state.index === -1) {
          await adminApi.createUser(payload)
        } else {
          await adminApi.updateUser(current?.userId, payload)
        }
        await loadUsers()
        break
      case 'institutions':
        if (!payload.createdAt) {
          ElMessage.error('请填写创立时间')
          return
        }
        if (state.index === -1) {
          await adminApi.createInstitution(payload)
        } else {
          await adminApi.updateInstitution(current?.institutionId, payload)
        }
        await loadInstitutions()
        break
      case 'veterinarians':
        if (state.index === -1) {
          await adminApi.createVeterinarian(payload)
        } else {
          await adminApi.updateVeterinarian(current?.vetId || current?.veterinarianId || current?.id, payload)
        }
        await loadVeterinarians()
        break
      case 'veterinarians': {
        const { vetId, id, userName, institutionName, user, institution, ...vetPayload } = payload
        if (!vetPayload.userId || !vetPayload.institutionId) {
          ElMessage.error('请选择关联的用户和机构')
          return
        }
        if (state.index === -1) {
          await adminApi.createVeterinarian(vetPayload)
        } else {
          await adminApi.updateVeterinarian(current?.vetId || current?.id, vetPayload)
        }
        await loadVeterinarians()
        break
      }
      case 'vaccines':
        if (state.index === -1) {
          await adminApi.createVaccineType(payload)
        } else {
          await adminApi.updateVaccineType(current?.vaccineTypeId, payload)
        }
        await loadVaccines()
        break
      case 'medications':
        if (state.index === -1) {
          await adminApi.createMedication(payload)
        } else {
          await adminApi.updateMedication(current?.productId, payload)
        }
        await loadMedications()
        break
      case 'medicationInventory':
        if (state.index === -1) {
          await adminApi.createMedicationInventory(payload)
        } else {
          await adminApi.updateMedicationInventory(current?.inventoryId || current?.id, payload)
        }
        await loadMedicationInventory()
        break
      case 'petMedications':
        if (state.index === -1) {
          await adminApi.createPetMedication(payload)
        } else {
          await adminApi.updatePetMedication(current?.medicationId || current?.id, payload)
        }
        await loadPetMedications()
        break
      case 'dewormingRecords':
        if (state.index === -1) {
          await adminApi.createDewormingRecord(payload)
        } else {
          await adminApi.updateDewormingRecord(current?.dewormingId || current?.id, payload)
        }
        await loadDewormingRecords()
        break
      case 'petPlans':
        if (state.index === -1) {
          await adminApi.createPetPlan(payload)
        } else {
          await adminApi.updatePetPlan(current?.planId || current?.id, payload)
        }
        await loadPetPlans()
        break
      case 'financeRecords': {
        const { recordId, owner, pet, createdAt, ...body } = payload
        if (state.index === -1) {
          await adminApi.createFinanceRecord(body)
        } else {
          await adminApi.updateFinanceRecord(current?.recordId || current?.id, body)
        }
        await loadFinanceRecords()
        break
      }
      case 'reminders':
        if (state.index === -1) {
          await adminApi.createReminder(payload)
        } else {
          await adminApi.updateReminder(current?.reminderId || current?.id, payload)
        }
        await loadReminders()
        break
      case 'pets': {
        const { ownerUsername, createdAt, updatedAt, petId, id, ...petPayload } = payload
        const targetOwner =
          (ownerOptions.value || []).find((o) => o.value === ownerUsername) ||
          (petPayload.userId
            ? (ownerOptions.value || []).find((o) => o.userId === petPayload.userId)
            : null)
        if (!targetOwner) {
          ElMessage.error('请选择存在的主人用户名')
          return
        }
        petPayload.userId = targetOwner.userId
        if (petPayload.currentWeight && !petPayload.weight) {
          petPayload.weight = petPayload.currentWeight
        }
        if (!petPayload.gender) {
          petPayload.gender = 'M'
        }
        if (state.index === -1) {
          await adminApi.createPet(petPayload)
        } else {
          await adminApi.updatePet(current?.id || current?.petId, petPayload)
        }
        await loadPets()
        break
      }
      case 'appointments':
        {
          const {
            appointmentId,
            id,
            petName,
            ownerUsername,
            institutionName,
            vetUsername,
            createdAt,
            updatedAt,
            ...appointmentPayload
          } = payload
          if (state.index === -1) {
            await adminApi.createAppointment(appointmentPayload)
          } else {
            await adminApi.updateAppointment(current?.id || current?.appointmentId, appointmentPayload)
          }
          await loadAppointments()
          break
      }
      case 'reminderTypes':
        if (state.index === -1) {
          await adminApi.createReminderType(payload)
        } else {
          await adminApi.updateReminderType(current?.reminderTypeId, payload)
        }
        await loadReminderTypes()
        break
      case 'vaccinations':
        if (state.index === -1) {
          await adminApi.createVaccination(payload)
        } else {
          await adminApi.updateVaccination(current?.vaccinationId || current?.id, payload)
        }
        await loadVaccinations()
        break
      case 'medicalRecords':
        if (state.index === -1) {
          await adminApi.createMedicalRecord(payload)
        } else {
          await adminApi.updateMedicalRecord(current?.recordId || current?.id, payload)
        }
        await loadMedicalRecords()
        break
      case 'weightRecords':
        if (state.index === -1) {
          await adminApi.createWeightRecord(payload)
        } else {
          await adminApi.updateWeightRecord(current?.weightId || current?.id, payload)
        }
        await loadWeightRecords()
        break
      case 'temperatureRecords':
        if (state.index === -1) {
          await adminApi.createTemperatureRecord(payload)
        } else {
          await adminApi.updateTemperatureRecord(current?.temperatureId || current?.id, payload)
        }
        await loadTemperatureRecords()
        break
      case 'activityRecords':
        if (state.index === -1) {
          await adminApi.createActivityRecord(payload)
        } else {
          await adminApi.updateActivityRecord(current?.activityId || current?.id, payload)
        }
        await loadActivityRecords()
        break
      case 'intakeRecords':
        if (state.index === -1) {
          await adminApi.createIntakeRecord(payload)
        } else {
          await adminApi.updateIntakeRecord(current?.intakeId || current?.id, payload)
        }
        await loadIntakeRecords()
        break
      case 'feedback':
        if (state.index === -1) {
          await adminApi.createFeedback(payload)
        } else {
          await adminApi.updateFeedback(current?.feedbackId, payload)
        }
        await loadFeedback()
        break
      case 'insurance':
        if (state.index === -1) {
          await adminApi.createInsurance(payload)
        } else {
          await adminApi.updateInsurance(current?.insuranceId, payload)
        }
        await loadInsurance()
        break
      case 'petPhotos': {
        const { photoId, previewUrl, sizeBytes, uploadedAt, ...body } = payload
        if (!body.petId) {
          ElMessage.error('请填写宠物ID')
          return
        }
        if (state.index === -1 && !body.photoDataBase64) {
          ElMessage.error('请先上传图片文件')
          return
        }
        if (!body.photoDataBase64) {
          delete body.photoDataBase64
        }
        if (state.index === -1) {
          await adminApi.createPetPhoto(body)
        } else {
          await adminApi.updatePetPhoto(current?.photoId, body)
        }
        await loadPetPhotos(petPhotoFilter.value || undefined)
        break
      }
      default:
        break
    }
    state.visible = false
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error(error?.message || '保存失败')
  }
}

const toggleUserStatus = async (row, index) => {
  try {
    await adminApi.updateUser(row.userId, {
      username: row.username,
      email: row.email,
      phone: row.phone,
      roleName: row.roleName || row.role,
      active: row.active
    })
    // 同步原始数据后重新过滤
    const rawIdx = rawUserData.value.findIndex((u) => u.userId === row.userId)
    if (rawIdx !== -1) {
      rawUserData.value.splice(rawIdx, 1, { ...rawUserData.value[rawIdx], active: row.active })
    }
    applyUserFilter()
    ElMessage.success('状态已更新')
  } catch (error) {
    console.error('更新状态失败', error)
    ElMessage.error('状态更新失败')
    // revert
    userData.value[index].active = !row.active
  }
}

const toggleMedicationStatus = async (row) => {
  try {
    await adminApi.updateMedication(row.productId, {
      productName: row.productName,
      dewormingType: row.dewormingType,
      species: row.species,
      validityPeriodDays: row.validityPeriodDays,
      dosageGuide: row.dosageGuide,
      active: row.active
    })
    ElMessage.success('状态已更新')
  } catch (error) {
    console.error('更新驱虫药品状态失败', error)
    ElMessage.error('状态更新失败')
    row.active = !row.active
  }
}

const toggleVaccineStatus = async (row, index) => {
  try {
    await adminApi.updateVaccineType(row.vaccineTypeId, { ...row, active: row.active })
    ElMessage.success('状态已更新')
  } catch (error) {
    console.error('更新状态失败', error)
    ElMessage.error('状态更新失败')
    vaccineData.value[index].active = !row.active
  }
}

const toggleReminderAuto = async (row, index) => {
  try {
    await adminApi.updateReminderType(row.reminderTypeId, { ...row, autoGenerated: row.autoGenerated })
    ElMessage.success('状态已更新')
  } catch (error) {
    console.error('更新状态失败', error)
    ElMessage.error('状态更新失败')
    reminderTypeData.value[index].autoGenerated = !row.autoGenerated
  }
}

const removeRecord = (type, index, row) => {
  const listRef = dataMap[type]
  if (!listRef) return
  const name = row?.name || row?.title || row?.username || row?.petName || row?.pet || row?.institutionName || typeLabels[type]
  ElMessageBox.confirm(`确认删除「${name || '记录'}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      switch (type) {
        case 'roles':
          await adminApi.deleteRole(row?.roleId)
          await loadRoles()
          break
        case 'users':
          await adminApi.deleteUser(row?.userId)
          await loadUsers()
          break
        case 'veterinarians':
          await adminApi.deleteVeterinarian(row?.vetId || row?.id)
          await loadVeterinarians()
          break
        case 'institutions':
          await adminApi.deleteInstitution(row?.institutionId)
          await loadInstitutions()
          break
        case 'veterinarians':
          await adminApi.deleteVeterinarian(row?.vetId || row?.id)
          await loadVeterinarians()
          break
        case 'vaccines':
          await adminApi.deleteVaccineType(row?.vaccineTypeId)
          await loadVaccines()
          break
        case 'medications':
          await adminApi.deleteMedication(row?.productId)
          await loadMedications()
          break
        case 'medicationInventory':
          await adminApi.deleteMedicationInventory(row?.inventoryId || row?.id)
          await loadMedicationInventory()
          break
        case 'petMedications':
          await adminApi.deletePetMedication(row?.medicationId || row?.id)
          await loadPetMedications()
          break
        case 'dewormingRecords':
          await adminApi.deleteDewormingRecord(row?.dewormingId || row?.id)
          await loadDewormingRecords()
          break
        case 'petPlans':
          await adminApi.deletePetPlan(row?.planId || row?.id)
          await loadPetPlans()
          break
        case 'financeRecords':
          await adminApi.deleteFinanceRecord(row?.recordId || row?.id)
          await loadFinanceRecords()
          break
        case 'reminders':
          await adminApi.deleteReminder(row?.reminderId || row?.id)
          await loadReminders()
          break
        case 'pets':
          await adminApi.deletePet(row?.id || row?.petId)
          await loadPets()
          break
        case 'appointments':
          await adminApi.deleteAppointment(row?.id || row?.appointmentId)
          await loadAppointments()
          break
        case 'reminderTypes':
          await adminApi.deleteReminderType(row?.reminderTypeId)
          await loadReminderTypes()
          break
        case 'vaccinations':
          await adminApi.deleteVaccination(row?.vaccinationId || row?.id)
          await loadVaccinations()
          break
        case 'medicalRecords':
          await adminApi.deleteMedicalRecord(row?.recordId || row?.id)
          await loadMedicalRecords()
          break
        case 'weightRecords':
          await adminApi.deleteWeightRecord(row?.weightId || row?.id)
          await loadWeightRecords()
          break
        case 'temperatureRecords':
          await adminApi.deleteTemperatureRecord(row?.temperatureId || row?.id)
          await loadTemperatureRecords()
          break
        case 'activityRecords':
          await adminApi.deleteActivityRecord(row?.activityId || row?.id)
          await loadActivityRecords()
          break
        case 'intakeRecords':
          await adminApi.deleteIntakeRecord(row?.intakeId || row?.id)
          await loadIntakeRecords()
          break
        case 'feedback':
          await adminApi.deleteFeedback(row?.feedbackId)
          await loadFeedback()
          break
        case 'insurance':
          await adminApi.deleteInsurance(row?.insuranceId)
          await loadInsurance()
          break
        case 'petPhotos':
          await adminApi.deletePetPhoto(row?.photoId)
          await loadPetPhotos(petPhotoFilter.value || undefined)
          break
        default:
          break
      }
      ElMessage.success('已删除')
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error(error?.message || '删除失败')
    }
  })
}

const roleOptions = computed(() => {
  const defaults = ['宠物主人', '兽医', '系统管理员']
  const set = new Set([
    ...(roleData.value || []).map((r) => r.roleName),
    ...(rawUserData.value || []).map((u) => u.roleName || u.role),
    ...defaults
  ])
  return Array.from(set).filter(Boolean)
})

const userOptions = computed(() =>
  (rawUserData.value || []).map((u) => ({
    label: u.roleName ? `${u.username}（${u.roleName}）` : u.username,
    value: u.userId
  }))
)

const vetUserOptions = computed(() => {
  const allUsers = rawUserData.value || []
  const vets = allUsers.filter((u) => (u.roleName || u.role || '').includes('兽医'))
  const source = vets.length ? vets : allUsers
  return source.map((u) => ({
    label: u.roleName ? `${u.username}（${u.roleName}）` : u.username,
    value: u.userId
  }))
})

const ownerOptions = computed(() => {
  const allUsers = rawUserData.value || []
  const owners = allUsers.filter((u) => {
    const roleText = (u.roleName || u.role || '').toString()
    return roleText === '宠物主人' || roleText.includes('管理员')
  })
  return owners.map((u) => ({
    label: u.roleName ? `${u.username}（${u.roleName}）` : u.username,
    value: u.username,
    userId: u.userId
  }))
})

const ownerUserOptions = computed(() => {
  const allUsers = rawUserData.value || []
  const owners = allUsers.filter((u) => {
    const roleText = (u.roleName || u.role || '').toString()
    return roleText === '宠物主人' || roleText.includes('管理员')
  })
  return owners.map((u) => ({
    label: u.roleName ? `${u.username}（${u.roleName}）` : u.username,
    value: u.userId
  }))
})

const petOptions = computed(() =>
  (petData.value || []).map((p) => ({
    label: `${p.petName || '宠物'}${p.ownerUsername ? `（${p.ownerUsername}）` : ''}`,
    value: p.petId
  }))
)

const vaccineOptions = computed(() =>
  (vaccineData.value || []).map((v) => ({
    label: v.vaccineName || `疫苗${v.vaccineTypeId}`,
    value: v.vaccineTypeId
  }))
)

const dewormingProductOptions = computed(() =>
  (medicationData.value || []).map((product) => ({
    label: product.productName || `药品${product.productId}`,
    value: product.productId
  }))
)

const reminderTypeOptions = computed(() =>
  (reminderTypeData.value || []).map((type) => ({
    label: type.typeName || `类型${type.reminderTypeId}`,
    value: type.reminderTypeId
  }))
)

const institutionOptions = computed(() =>
  (institutionData.value || []).map((ins) => ({
    label: `${ins.institutionName || '机构'}（ID:${ins.institutionId}）`,
    value: ins.institutionId
  }))
)

const applyUserFilter = () => {
  const { username, email, phone, roleName, active } = userFilters
  userData.value = (rawUserData.value || []).filter((u) => {
    const matchUsername = username ? String(u.username || '').includes(username) : true
    const matchEmail = email ? String(u.email || '').includes(email) : true
    const matchPhone = phone ? String(u.phone || '').includes(phone) : true
    const matchRole = roleName ? (u.roleName || u.role || '').includes(roleName) : true
    const matchActive = active === '' ? true : u.active === (active === 'true')
    return matchUsername && matchEmail && matchPhone && matchRole && matchActive
  })
}

const resetUserFilter = () => {
  userFilters.username = ''
  userFilters.email = ''
  userFilters.phone = ''
  userFilters.roleName = ''
  userFilters.active = ''
  applyUserFilter()
}

const notify = (module, action) => {
  ElMessage.success(`${module}：${action}（示例占位）`)
}
</script>

<style>
.admin-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 100vh;
  background: #f5f7fb;
}

.admin-sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  background: #0f172a;
  color: #fff;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 4px 0 18px rgba(15, 23, 42, 0.18);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.18), rgba(59, 130, 246, 0.12));
}

.logo-wrap {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #fff;
  padding: 4px;
  border: 1px solid #d8e3f3;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transform: translateY(4px) scale(1.5);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-weight: 800;
  font-size: 16px;
}

.brand-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.sidebar-menu {
  border-right: none;
  background: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.92);
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fff;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  border-radius: 10px;
  background: #f6f6f7;
  margin-bottom: 6px;
  color: #0f172a;
  font-weight: 700;
}

.sidebar-menu :deep(.el-sub-menu__title span),
.sidebar-menu :deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
  color: #0f172a !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu),
.sidebar-menu :deep(.el-menu--inline),
.sidebar-menu :deep(.el-menu--popup),
.sidebar-menu .el-sub-menu .el-menu,
.sidebar-menu .el-menu--inline,
.sidebar-menu .el-menu--popup,
.sidebar-submenu-popper .el-menu,
.sidebar-submenu-popper .el-menu--popup {
  background: #99b1e2 !important;
  color: #1f2937 !important;
  --el-menu-bg-color: #98afde;
  --el-menu-text-color: #1f2937;
  --el-menu-hover-text-color: #111827;
  --el-menu-hover-bg-color: #a5bceb;
  --el-menu-active-color: #111827;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 6px 10px;
  margin: 6px 0 10px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.16);
}

.sidebar-submenu-popper {
  background: transparent !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item),
.sidebar-menu :deep(.el-menu--inline .el-menu-item),
.sidebar-menu :deep(.el-menu--popup .el-menu-item),
.sidebar-menu .el-sub-menu .el-menu-item,
.sidebar-menu .el-menu--inline .el-menu-item,
.sidebar-menu .el-menu--popup .el-menu-item,
.sidebar-submenu-popper .el-menu-item {
  margin: 4px 0;
  color: #1f2937 !important;
  font-weight: 600;
  border-radius: 8px;
  padding: 8px 12px;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item span),
.sidebar-menu :deep(.el-menu--inline .el-menu-item span),
.sidebar-menu :deep(.el-menu--popup .el-menu-item span),
.sidebar-menu .el-sub-menu .el-menu-item span,
.sidebar-menu .el-menu--inline .el-menu-item span,
.sidebar-menu .el-menu--popup .el-menu-item span,
.sidebar-submenu-popper .el-menu-item span {
  color: #1f2937 !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item.is-active),
.sidebar-menu :deep(.el-sub-menu .el-menu-item:hover),
.sidebar-menu :deep(.el-menu--inline .el-menu-item.is-active),
.sidebar-menu :deep(.el-menu--inline .el-menu-item:hover),
.sidebar-menu :deep(.el-menu--popup .el-menu-item.is-active),
.sidebar-menu :deep(.el-menu--popup .el-menu-item:hover),
.sidebar-menu .el-sub-menu .el-menu-item.is-active,
.sidebar-menu .el-sub-menu .el-menu-item:hover,
.sidebar-menu .el-menu--inline .el-menu-item.is-active,
.sidebar-menu .el-menu--inline .el-menu-item:hover,
.sidebar-menu .el-menu--popup .el-menu-item.is-active,
.sidebar-menu .el-menu--popup .el-menu-item:hover,
.sidebar-submenu-popper .el-menu-item.is-active,
.sidebar-submenu-popper .el-menu-item:hover {
  background: #6d89c0 !important;
  color: #111827 !important;
}

/* fallback: ensure collapsed popper menus also use浅灰底和深色文字 */
.el-menu--popup {
  background: #a5b7db !important;
  --el-menu-bg-color: #6b8bca;
  --el-menu-text-color: #1f2937;
  --el-menu-hover-text-color: #111827;
  --el-menu-hover-bg-color: #779ce7;
  --el-menu-active-color: #111827;
  border-radius: 10px !important;
  border: 1px solid #d1d5db;
}

.el-menu--popup .el-menu-item {
  color: #1f2937 !important;
}

.el-menu--popup .el-menu-item.is-active,
.el-menu--popup .el-menu-item:hover {
  background: #7795cf !important;
  color: #111827 !important;
}

.sidebar-menu-wrapper {
  flex: 1;
  overflow-y: auto;
  margin: 8px 0;
  padding-right: 4px;
}

.sidebar-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 8px;
  height: 46px;
  line-height: 46px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #409eff, #2f86eb);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.28);
}

.back-to-frontend {
  margin-top: 8px;
  width: 100%;
  border-radius: 10px;
  position: sticky;
  bottom: 12px;
}

.admin-main {
  height: 100vh;
  overflow-y: auto;
  padding: 20px 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.admin-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-grid {
  width: 100%;
}

.stat-card {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}

.stat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-weight: 600;
}

.stat-icon {
  color: #409eff;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  margin: 6px 0;
}

.stat-desc {
  color: #94a3b8;
  font-size: 13px;
}

.welcome-banner {
  background: linear-gradient(135deg, #e0f2fe, #eff6ff);
  border: 1px solid #dce7f7;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.welcome-subtitle {
  color: #60708a;
  margin-top: 4px;
}

.panel {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}

.panel-title {
  font-weight: 800;
  font-size: 18px;
  color: #0f172a;
  margin-bottom: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cell-main {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.cell-sub {
  color: #8a8fa3;
  font-size: 12px;
}
.hint {
  color: #8a8fa3;
  font-size: 12px;
  margin-top: 6px;
}

@media (max-width: 1024px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }
  .admin-sidebar {
    position: relative;
    height: auto;
    flex-direction: row;
    align-items: center;
    gap: 8px;
  }
  .sidebar-menu {
    flex: 1;
  }
  .admin-main {
    height: auto;
    overflow: visible;
    padding: 16px;
  }
}
</style>
