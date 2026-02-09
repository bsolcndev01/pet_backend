<template>
  <div class="health-container" v-loading="loadingPets">
    <div class="page-title">健康记录</div>

    <el-card class="panel">
      <div class="toolbar">
        <el-select v-model="currentPetId" placeholder="请选择宠物" @change="loadAll">
          <el-option
            v-for="pet in allPetOptions"
            :key="pet.petId"
            :label="pet.petName"
            :value="pet.petId"
          />
        </el-select>
        <el-button type="primary" @click="loadAll">刷新</el-button>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="就诊/医疗记录" name="medical">
          <div class="tab-actions">
            <el-button type="primary" size="small" @click="openAddMedical">新增</el-button>
          </div>
          <el-table :data="medicalList" border size="small">
            <el-table-column prop="petName" label="宠物" width="140" />
            <el-table-column prop="visitDate" label="就诊日期" width="120" />
            <el-table-column prop="reason" label="主诉" />
            <el-table-column prop="diagnosis" label="诊断" />
            <el-table-column prop="treatment" label="治疗" />
            <el-table-column prop="prescription" label="处方" />
            <el-table-column prop="recordStatus" label="状态" width="100" />
            <el-table-column prop="followUpDate" label="复诊日期" width="120" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link circle size="small" @click="openMedicalDetail(row)">
                  <el-icon><Document /></el-icon>
                </el-button>
                <el-button type="primary" link circle size="small" @click="openEditMedical(row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-popconfirm
                  title="确认删除该医疗记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDeleteMedical(row)"
                >
                  <template #reference>
                    <el-button type="danger" link circle size="small">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="疫苗" name="vaccine">
          <div class="tab-actions">
            <el-button type="primary" size="small" @click="openAddVaccination">新增</el-button>
          </div>
          <el-table :data="vaccineList" border size="small">
            <el-table-column prop="petName" label="宠物" width="140" />
            <el-table-column prop="vaccinationDate" label="接种日期" width="120" />
            <el-table-column prop="nextDueDate" label="下次时间" width="120" />
            <el-table-column prop="vaccineName" label="疫苗类型" />
            <el-table-column prop="notes" label="备注" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link circle size="small" @click="openVaccineDetail(row)">
                  <el-icon><Document /></el-icon>
                </el-button>
                <el-button type="primary" link circle size="small" @click="editVaccine(row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-popconfirm
                  title="确认删除该疫苗记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDeleteVaccine(row)"
                >
                  <template #reference>
                    <el-button type="danger" link size="small" circle>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="驱虫" name="deworm">
          <div class="tab-actions">
            <el-button type="primary" size="small" @click="openAddDeworming">新增</el-button>
          </div>
          <el-table :data="dewormList" border size="small">
            <el-table-column prop="petName" label="宠物" width="140" />
            <el-table-column prop="applicationDate" label="驱虫日期" width="120" />
            <el-table-column prop="nextDueDate" label="下次时间" width="120" />
            <el-table-column prop="sourceType" label="方式" width="100" />
            <el-table-column prop="institutionName" label="医疗机构" width="140" />
            <el-table-column prop="vetName" label="医生" width="120" />
            <el-table-column prop="productName" label="驱虫药品" />
            <el-table-column prop="dosage" label="剂量" />
            <el-table-column prop="notes" label="备注" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link circle size="small" @click="editDeworm(row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-popconfirm
                  title="确认删除该驱虫记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDeleteDeworm(row)"
                >
                  <template #reference>
                    <el-button type="danger" link size="small" circle>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="体征/体况" name="vitals">
          <div class="tab-actions">
            <el-button type="primary" size="small" @click="openAddVital">新增</el-button>
            <el-select v-model="vitalType" size="small" style="width: 160px">
              <el-option label="体重" value="weight" />
              <el-option label="体温" value="temperature" />
              <el-option label="活动量" value="activity" />
              <el-option label="饮水/进食" value="intake" />
            </el-select>
          </div>
          <el-table :data="vitalTableData" border size="small">
            <el-table-column prop="petName" label="宠物" width="140" />
            <el-table-column prop="recordDate" label="记录日期" width="120" />
            <el-table-column prop="metricType" label="类型" width="120" />
            <el-table-column prop="value" label="数值" width="140">
              <template #default="{ row }">
                {{ row.value ?? '--' }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="notes" label="备注" />
            <el-table-column label="操作" width="150" align="center">
              <template #default="{ row }">
                <el-button
                  v-if="['weight','temperature','activity','intake'].includes(vitalType)"
                  type="primary"
                  link
                  size="small"
                  circle
                  @click="openEditVital(row)"
                >
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-popconfirm
                  title="确认删除该体征记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDeleteVital(row)"
                >
                  <template #reference>
                    <el-button type="danger" link size="small" circle>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="用药/处方" name="medications">
          <div class="tab-actions">
            <el-button type="primary" size="small" @click="openAddMedication">新增</el-button>
          </div>
          <el-table :data="medicationList" border size="small">
            <el-table-column prop="petName" label="宠物" width="140" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="dosage" label="剂量" />
            <el-table-column prop="frequency" label="频次" />
            <el-table-column prop="route" label="用法" />
            <el-table-column prop="startDate" label="开始" width="120" />
            <el-table-column prop="endDate" label="结束" width="120" />
            <el-table-column prop="instructions" label="说明" />
            <el-table-column prop="contraindications" label="禁忌" />
            <el-table-column label="操作" width="140" align="center">
              <template #default="{ row }">
                <el-button type="primary" link circle size="small" @click="openMedicationDetail(row)">
                  <el-icon><Document /></el-icon>
                </el-button>
                <el-button type="primary" link circle size="small" @click="editMedication(row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-popconfirm
                  title="确认删除该用药记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="deleteMedicationRecord(row)"
                >
                  <template #reference>
                    <el-button type="danger" link circle size="small">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
  </el-card>

    <el-dialog v-model="showAddMedical" :title="editingMedicalId ? '编辑医疗记录' : '新增医疗记录'" width="680px">
      <el-form ref="medicalFormRef" :model="medicalForm" :rules="medicalRules" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="宠物" prop="petId">
              <el-select v-model="medicalForm.petId" placeholder="请选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊日期" prop="visitDate">
              <el-date-picker v-model="medicalForm.visitDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="医疗机构" prop="institutionId">
              <el-select
                v-model="medicalForm.institutionId"
                placeholder="请选择医疗机构或手动输入"
                filterable
                allow-create
                default-first-option
                @change="reloadVetsByInstitution"
              >
                <el-option
                  v-for="inst in institutionOptions"
                  :key="inst.id"
                  :label="inst.name"
                  :value="inst.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生" prop="vetUserId">
              <el-select
                v-model="medicalForm.vetUserId"
                placeholder="请选择医生或手动输入"
                filterable
                allow-create
                default-first-option
              >
                <el-option
                  v-for="vet in vetOptions"
                  :key="vet.userId"
                  :label="vet.username"
                  :value="vet.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主诉" prop="reason">
          <el-input v-model="medicalForm.reason" type="textarea" :rows="2" placeholder="请输入主诉" />
        </el-form-item>
        <el-form-item label="诊断">
          <el-input v-model="medicalForm.diagnosis" type="textarea" :rows="2" placeholder="诊断结果" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="治疗方案">
              <el-input v-model="medicalForm.treatment" type="textarea" :rows="2" placeholder="治疗方案" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处方">
              <el-input v-model="medicalForm.prescription" type="textarea" :rows="2" placeholder="处方信息" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="费用">
              <el-input v-model="medicalForm.cost" type="number" placeholder="费用(可选)" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="支付状态">
              <el-select v-model="medicalForm.paymentStatus" placeholder="未支付/部分支付/已支付/保险支付">
                <el-option label="未支付" value="未支付" />
                <el-option label="部分支付" value="部分支付" />
                <el-option label="已支付" value="已支付" />
                <el-option label="保险支付" value="保险支付" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="记录状态">
              <el-select v-model="medicalForm.recordStatus" placeholder="初诊/复诊/急诊/体检">
                <el-option label="初诊" value="初诊" />
                <el-option label="复诊" value="复诊" />
                <el-option label="急诊" value="急诊" />
                <el-option label="体检" value="体检" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="复诊日期">
          <el-date-picker v-model="medicalForm.followUpDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddMedical = false">取消</el-button>
          <el-button type="primary" :loading="savingMedical" @click="submitMedical">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showAddVaccination"
      :title="editingVaccinationId ? '编辑疫苗记录' : '新增疫苗记录'"
      width="600px"
    >
      <el-form ref="vaccinationFormRef" :model="vaccinationForm" :rules="vaccinationRules" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="宠物" prop="petId">
              <el-select v-model="vaccinationForm.petId" placeholder="请选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="疫苗名称" prop="vaccineName">
              <el-select
                v-model="vaccinationForm.vaccineName"
                placeholder="选择或输入疫苗名称"
                filterable
                allow-create
                default-first-option
                @change="handleVaccineSelect"
              >
                <el-option
                  v-for="vt in vaccineTypeOptions"
                  :key="vt.vaccineTypeId"
                  :label="vt.vaccineName"
                  :value="vt.vaccineName"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="接种机构" prop="institutionId">
              <el-select
                v-model="vaccinationForm.institutionId"
                placeholder="请选择医疗机构或手动输入"
                filterable
                allow-create
                default-first-option
              >
                <el-option
                  v-for="inst in institutionOptions"
                  :key="inst.id"
                  :label="inst.name"
                  :value="inst.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生">
              <el-select
                v-model="vaccinationForm.vetUserId"
                placeholder="可选，医生或手动输入"
                filterable
                allow-create
                default-first-option
              >
                <el-option
                  v-for="vet in vetOptions"
                  :key="vet.userId"
                  :label="vet.username"
                  :value="vet.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="接种日期" prop="vaccinationDate">
              <el-date-picker v-model="vaccinationForm.vaccinationDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次时间(选填)" prop="nextDueDate">
              <el-date-picker v-model="vaccinationForm.nextDueDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="批号">
              <el-input v-model="vaccinationForm.lotNumber" placeholder="疫苗批号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="vaccinationForm.notes" placeholder="备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddVaccination = false">取消</el-button>
          <el-button type="primary" :loading="savingVaccination" @click="submitVaccination">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDeworming" title="新增驱虫记录" width="520px">
      <el-form ref="dewormingFormRef" :model="dewormingForm" :rules="dewormingRules" label-width="120px">
        <el-form-item label="宠物" prop="petId">
          <el-select v-model="dewormingForm.petId" placeholder="请选择宠物">
            <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
          </el-select>
        </el-form-item>
        <el-form-item label="驱虫方式">
          <el-radio-group v-model="dewormingForm.sourceType" @change="onDewormSourceChange">
            <el-radio label="自驱">自驱</el-radio>
            <el-radio label="医院">医院</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="驱虫药品" prop="productId">
          <el-select
            v-model="dewormingForm.productId"
            placeholder="请选择驱虫药品"
            filterable
          >
            <el-option
              v-for="prod in dewormProducts"
              :key="prod.id"
              :label="prod.name"
              :value="prod.id"
            />
          </el-select>
        </el-form-item>
        <template v-if="dewormingForm.sourceType === '医院'">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="医疗机构" prop="institutionId">
                <el-select
                  v-model="dewormingForm.institutionId"
                  placeholder="请选择医疗机构"
                  filterable
                  @change="reloadVetsForDeworm"
                >
                  <el-option
                    v-for="inst in institutionOptions"
                    :key="inst.id"
                    :label="inst.name"
                    :value="inst.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="医生">
                <el-select
                  v-model="dewormingForm.vetUserId"
                  placeholder="可选，指定医生"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="vet in vetOptions"
                    :key="vet.userId"
                    :label="vet.username"
                    :value="vet.userId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="驱虫日期" prop="applicationDate">
              <el-date-picker v-model="dewormingForm.applicationDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次时间" prop="nextDueDate">
              <el-date-picker v-model="dewormingForm.nextDueDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="剂量">
          <el-input v-model="dewormingForm.dosage" placeholder="剂量描述，如 1片/1支" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dewormingForm.notes" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDeworming = false">取消</el-button>
          <el-button type="primary" :loading="savingDeworming" @click="submitDeworming">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddVital" :title="editingVitalId ? '编辑体征记录' : '新增体征记录'" width="520px">
      <el-form ref="vitalFormRef" :model="vitalForm" :rules="vitalRules" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="宠物" prop="petId">
              <el-select v-model="vitalForm.petId" placeholder="请选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="metricType">
              <el-select v-model="vitalForm.metricType" placeholder="选择类型">
                <el-option label="体重" value="体重" />
                <el-option label="体温" value="体温" />
                <el-option label="活动量" value="活动量" />
                <el-option label="饮水/进食" value="饮水/进食" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item :label="valueLabel" prop="value">
              <el-input-number
                v-model="vitalForm.value"
                :step="0.01"
                :precision="2"
                :controls="false"
                :value-on-clear="null"
                inputmode="decimal"
                :placeholder="`请输入${valueLabel}`"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录日期" prop="recordDate">
              <el-date-picker v-model="vitalForm.recordDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="vitalForm.notes" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddVital = false">取消</el-button>
          <el-button type="primary" :loading="savingVital" @click="submitVital">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showAddMedication"
      :title="editingMedicationId ? '编辑用药记录' : '新增用药记录'"
      width="620px"
    >
      <el-form ref="medicationFormRef" :model="medicationForm" :rules="medicationRules" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="宠物" prop="petId">
              <el-select v-model="medicationForm.petId" placeholder="请选择宠物">
                <el-option v-for="pet in petOptions" :key="pet.petId" :label="pet.petName" :value="pet.petId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品名称" prop="drugName">
              <el-input v-model="medicationForm.drugName" placeholder="如 阿莫西林" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="剂量">
              <el-input v-model="medicationForm.dosage" placeholder="剂量描述，如 1片" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="频次">
              <el-input v-model="medicationForm.frequency" placeholder="如 每日2次" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="用法">
              <el-input v-model="medicationForm.route" placeholder="口服/外用等" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="medicationForm.startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="medicationForm.endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="禁忌">
              <el-input v-model="medicationForm.contraindications" placeholder="禁忌/注意事项" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="说明">
          <el-input v-model="medicationForm.instructions" type="textarea" :rows="2" placeholder="服用说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddMedication = false">取消</el-button>
          <el-button type="primary" :loading="savingMedication" @click="submitMedication">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showMedicalDetail" title="医疗记录详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物">{{ display(rowMedical.petName || rowMedical.petId) }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期">{{ display(rowMedical.visitDate) }}</el-descriptions-item>
        <el-descriptions-item label="复诊日期">{{ display(rowMedical.followUpDate) }}</el-descriptions-item>
        <el-descriptions-item label="就诊机构" :span="2">{{ display(rowMedical.institutionName || rowMedical.institutionId) }}</el-descriptions-item>
        <el-descriptions-item label="就诊医生">{{ display(rowMedical.vetName || rowMedical.vetUserId) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ display(rowMedical.recordStatus) }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ display(rowMedical.paymentStatus) }}</el-descriptions-item>
        <el-descriptions-item label="费用">{{ rowMedical.cost ?? '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">{{ display(rowMedical.reason) }}</el-descriptions-item>
        <el-descriptions-item label="诊断" :span="2">{{ display(rowMedical.diagnosis) }}</el-descriptions-item>
        <el-descriptions-item label="治疗" :span="2">{{ display(rowMedical.treatment) }}</el-descriptions-item>
        <el-descriptions-item label="处方" :span="2">{{ display(rowMedical.prescription) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showMedicalDetail = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showVaccineDetail" title="疫苗记录详情" width="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物">{{ display(rowVaccine.petName || rowVaccine.petId) }}</el-descriptions-item>
        <el-descriptions-item label="接种日期">{{ display(rowVaccine.vaccinationDate) }}</el-descriptions-item>
        <el-descriptions-item label="下次时间">{{ display(rowVaccine.nextDueDate) }}</el-descriptions-item>
        <el-descriptions-item label="疫苗类型">{{ display(rowVaccine.vaccineName || rowVaccine.vaccineTypeId) }}</el-descriptions-item>
        <el-descriptions-item label="接种机构">{{ display(rowVaccine.institutionName || rowVaccine.institutionId) }}</el-descriptions-item>
        <el-descriptions-item label="医生用户">{{ display(rowVaccine.vetName || rowVaccine.vetUserId) }}</el-descriptions-item>
        <el-descriptions-item label="批号">{{ display(rowVaccine.lotNumber) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ display(rowVaccine.notes) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showVaccineDetail = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showMedicationDetail" title="用药/处方详情" width="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物">{{ display(rowMedication.petName || rowMedication.petId) }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ display(rowMedication.drugName) }}</el-descriptions-item>
        <el-descriptions-item label="剂量">{{ display(rowMedication.dosage) }}</el-descriptions-item>
        <el-descriptions-item label="频次">{{ display(rowMedication.frequency) }}</el-descriptions-item>
        <el-descriptions-item label="用法">{{ display(rowMedication.route) }}</el-descriptions-item>
        <el-descriptions-item label="开始">{{ display(rowMedication.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束">{{ display(rowMedication.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="禁忌">{{ display(rowMedication.contraindications) }}</el-descriptions-item>
        <el-descriptions-item label="说明" :span="2">{{ display(rowMedication.instructions) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showMedicationDetail = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, EditPen, Delete } from '@element-plus/icons-vue'
import { getPetList } from '@/api/pet'
import {
  getVaccinations,
  getDeworming,
  getMedicalRecords,
  getVitals,
  getMedications,
  addVaccination,
  updateVaccination,
  deleteVaccination,
  addDeworming,
  addMedicalRecord,
  addVital,
  updateVital,
  addMedication,
  updateMedication,
  deleteMedication,
  updateMedicalRecord,
  deleteMedicalRecord,
  deleteVital,
  deleteDeworm,
  getTemperature,
  getActivity,
  getIntake,
  getInstitutions,
  getVets,
  getDewormProducts,
  getVaccineTypes
} from '@/api/health'

const ALL_KEY = 'all'
const router = useRouter()

const petOptions = ref([])
const currentPetId = ref(ALL_KEY)
const loadingPets = ref(false)
const activeTab = ref('medical')

const vaccineList = ref([])
const dewormList = ref([])
const medicalList = ref([])
const vitalList = ref([])
const temperatureList = ref([])
const activityList = ref([])
const intakeList = ref([])
const medicationList = ref([])
const showMedicalDetail = ref(false)
const rowMedical = ref({})
const showVaccineDetail = ref(false)
const rowVaccine = ref({})
const showMedicationDetail = ref(false)
const rowMedication = ref({})

const showAddVaccination = ref(false)
const showAddDeworming = ref(false)
const showAddMedical = ref(false)
const showAddVital = ref(false)
const showAddMedication = ref(false)

const savingVaccination = ref(false)
const savingDeworming = ref(false)
const savingMedical = ref(false)
const savingVital = ref(false)
const savingMedication = ref(false)

const vaccinationFormRef = ref(null)
const vaccinationFormInitial = {
  petId: '',
  vaccineName: '',
  vaccineTypeId: null,
  institutionId: '',
  vetUserId: '',
  vaccinationDate: '',
  nextDueDate: '',
  lotNumber: '',
  notes: ''
}
const vaccinationForm = reactive({ ...vaccinationFormInitial })
const editingVaccinationId = ref(null)
const vaccinationRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  vaccineName: [{ required: true, message: '请输入疫苗名称', trigger: 'blur' }],
  vaccinationDate: [{ required: true, message: '请选择接种日期', trigger: 'change' }]
}

const dewormingFormRef = ref(null)
const dewormingFormInitial = {
  petId: '',
  productId: '',
  sourceType: '自驱',
  institutionId: '',
  vetUserId: '',
  applicationDate: '',
  nextDueDate: '',
  dosage: '',
  notes: ''
}
const dewormingForm = reactive({ ...dewormingFormInitial })
const editingDewormId = ref(null)
const dewormingRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  productId: [{ required: true, message: '请选择驱虫药品', trigger: 'change' }],
  applicationDate: [{ required: true, message: '请选择驱虫日期', trigger: 'change' }],
  institutionId: [
    {
      validator: (_, value, callback) => {
        if (dewormingForm.sourceType === '医院' && !value) {
          callback(new Error('请选择医疗机构'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const medicalFormRef = ref(null)
const medicalFormInitial = {
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
  followUpDate: ''
}
const medicalForm = reactive({ ...medicalFormInitial })
const editingMedicalId = ref(null)
const medicalRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  visitDate: [{ required: true, message: '请选择就诊日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入主诉', trigger: 'blur' }]
}

const vitalFormRef = ref(null)
const vitalFormInitial = {
  petId: '',
  metricType: '体重',
  value: null,
  recordDate: '',
  notes: ''
}
const vitalForm = reactive({ ...vitalFormInitial })
const editingVitalId = ref(null)
const vitalRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  metricType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入数值', trigger: 'blur' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

const medicationFormRef = ref(null)
const medicationFormInitial = {
  petId: '',
  drugName: '',
  dosage: '',
  frequency: '',
  route: '',
  startDate: '',
  endDate: '',
  instructions: '',
  contraindications: ''
}
const medicationForm = reactive({ ...medicationFormInitial })
const editingMedicationId = ref(null)
const medicationRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  drugName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }]
}

const valueLabel = computed(() => {
  switch (vitalForm.metricType) {
    case '体温':
      return '体温(°C)'
    case '活动量':
      return '活动量(%)'
    case '饮水/进食':
      return '饮水/进食(ml)'
    default:
      return '体重(kg)'
  }
})

const allPetOptions = computed(() => {
  const list = petOptions.value || []
  return [{ petId: ALL_KEY, petName: '全部宠物' }, ...list]
})

const institutionOptions = ref([])
const vetOptions = ref([])
const dewormProducts = ref([])
const vitalType = ref('weight')
const vaccineTypeOptions = ref([])
const vitalTableData = computed(() => {
  const listMap = {
    weight: vitalList.value,
    temperature: temperatureList.value,
    activity: activityList.value,
    intake: intakeList.value
  }
  const source = listMap[vitalType.value] || []
  if (vitalType.value === 'weight') {
    const weightOnly = source.filter((item) => !item.metricType || item.metricType === '体重')
    return weightOnly.map((item) => ({
      ...item,
      metricType: '体重',
      value: item.value ?? item.weight,
      unit: item.unit || 'kg'
    }))
  }
  if (vitalType.value === 'temperature') {
    return source.map((item) => ({
      ...item,
      metricType: '体温',
      value: item.temperature,
      unit: '℃'
    }))
  }
  if (vitalType.value === 'activity') {
    return source.map((item) => ({
      ...item,
      metricType: '活动量',
      value: item.activityLevel,
      unit: ''
    }))
  }
  return source.map((item) => ({
    ...item,
    metricType: '饮水/进食',
    value: item.intakeVolume,
    unit: 'ml'
  }))
})

const getDefaultPetId = () => {
  if (currentPetId.value && currentPetId.value !== ALL_KEY) return currentPetId.value
  return petOptions.value[0]?.petId || ''
}

const resetForm = (form, initial) => Object.assign(form, initial)

const openAddVaccination = () => {
  editingVaccinationId.value = null
  resetForm(vaccinationForm, { ...vaccinationFormInitial, petId: getDefaultPetId() })
  showAddVaccination.value = true
}

const openAddDeworming = () => {
  resetForm(dewormingForm, { ...dewormingFormInitial, petId: getDefaultPetId() })
  showAddDeworming.value = true
  editingDewormId.value = null
}

const editDeworm = (row) => {
  if (!row) return
  resetForm(dewormingForm, {
    ...dewormingFormInitial,
    petId: row.petId || '',
    productId: row.productId || '',
    sourceType: row.sourceType || '自驱',
    institutionId: row.institutionId ?? '',
    vetUserId: row.vetUserId ?? '',
    applicationDate: row.applicationDate || '',
    nextDueDate: row.nextDueDate || '',
    dosage: row.dosage || '',
    notes: row.notes || ''
  })
  if (dewormingForm.sourceType === '医院' && dewormingForm.institutionId) {
    reloadVetsForDeworm()
  }
  editingDewormId.value = row.id
  showAddDeworming.value = true
}

const openAddMedical = () => {
  resetForm(medicalForm, { ...medicalFormInitial, petId: getDefaultPetId() })
  showAddMedical.value = true
  editingMedicalId.value = null
}

const openEditMedical = (row) => {
  if (!row) return
  resetForm(medicalForm, {
    ...medicalFormInitial,
    petId: row.petId || '',
    institutionId: row.institutionId ?? '',
    vetUserId: row.vetUserId ?? '',
    visitDate: row.visitDate || '',
    reason: row.reason || '',
    diagnosis: row.diagnosis || '',
    treatment: row.treatment || '',
    prescription: row.prescription || '',
    cost: row.cost ?? '',
    paymentStatus: row.paymentStatus || '',
    recordStatus: row.recordStatus || '',
    followUpDate: row.followUpDate || ''
  })
  if (medicalForm.institutionId) {
    reloadVetsByInstitution()
  }
  editingMedicalId.value = row.id
  showAddMedical.value = true
}

const openAddVital = () => {
  editingVitalId.value = null
  resetForm(vitalForm, { ...vitalFormInitial, petId: getDefaultPetId() })
  showAddVital.value = true
}

const openAddMedication = () => {
  resetForm(medicationForm, { ...medicationFormInitial, petId: getDefaultPetId() })
  showAddMedication.value = true
  editingMedicationId.value = null
}

const openEditVital = (row) => {
  if (!row) return
  editingVitalId.value = row.id || row.weightId || null
  // 根据当前选择的列表类型设置类型和值
  let metricType = '体重'
  let value = row.value ?? row.weight ?? null
  if (vitalType.value === 'temperature') {
    metricType = '体温'
    value = row.value ?? row.temperature ?? null
  } else if (vitalType.value === 'activity') {
    metricType = '活动量'
    value = row.value ?? row.activityLevel ?? null
  } else if (vitalType.value === 'intake') {
    metricType = '饮水/进食'
    value = row.value ?? row.intakeVolume ?? null
  }
  resetForm(vitalForm, {
    ...vitalFormInitial,
    petId: row.petId || getDefaultPetId(),
    metricType,
    value,
    recordDate: row.recordDate || '',
    notes: row.notes || ''
  })
  showAddVital.value = true
}

const loadPets = async () => {
  try {
    loadingPets.value = true
    const res = await getPetList({ page: 1, size: 50 })
    petOptions.value = res?.data?.records || []
    if (!currentPetId.value && petOptions.value.length > 0) {
      currentPetId.value = ALL_KEY
    }
  } catch (error) {
    console.error('加载宠物列表失败', error)
    ElMessage.error('加载宠物列表失败')
  } finally {
    loadingPets.value = false
  }
}

const reloadVetsForDeworm = async () => {
  try {
    const params = dewormingForm.institutionId ? { institutionId: dewormingForm.institutionId } : {}
    const vetRes = await getVets(params)
    vetOptions.value = vetRes?.data || []
  } catch (error) {
    console.error('加载医生失败', error)
    ElMessage.error('加载医生列表失败')
  }
}

const onDewormSourceChange = (val) => {
  if (val === '自驱') {
    dewormingForm.institutionId = ''
    dewormingForm.vetUserId = ''
  }
}

const loadMeta = async () => {
  try {
    const [instRes, vetRes, dewormRes, vaccineRes] = await Promise.all([
      getInstitutions(),
      getVets(), // 初始无机构过滤
      getDewormProducts(),
      getVaccineTypes()
    ])
    institutionOptions.value = instRes?.data || []
    vetOptions.value = vetRes?.data || []
    dewormProducts.value = dewormRes?.data || []
    vaccineTypeOptions.value = vaccineRes?.data || []
  } catch (error) {
    console.error('加载机构/医生/驱虫药品失败', error)
    ElMessage.error('加载机构、医生或驱虫药品列表失败')
  }
}

const reloadVetsByInstitution = async () => {
  try {
    const params = medicalForm.institutionId ? { institutionId: medicalForm.institutionId } : {}
    const vetRes = await getVets(params)
    vetOptions.value = vetRes?.data || []
  } catch (error) {
    console.error('加载医生失败', error)
    ElMessage.error('加载医生列表失败')
  }
}

const reloadVetsForVaccine = async () => {
  try {
    const params = vaccinationForm.institutionId ? { institutionId: vaccinationForm.institutionId } : {}
    const vetRes = await getVets(params)
    vetOptions.value = vetRes?.data || []
  } catch (error) {
    console.error('加载医生失败', error)
    ElMessage.error('加载医生列表失败')
  }
}

const loadAll = async () => {
  if (!petOptions.value.length && currentPetId.value === ALL_KEY) {
    vaccineList.value = []
    dewormList.value = []
    medicalList.value = []
    vitalList.value = []
    temperatureList.value = []
    activityList.value = []
    intakeList.value = []
    medicationList.value = []
    return
  }
  try {
    const params = currentPetId.value && currentPetId.value !== ALL_KEY ? { petId: currentPetId.value } : {}
    const [vaccRes, dewRes, medRes, vitRes, medcRes, tempRes, actRes, intakeRes] = await Promise.all([
      getVaccinations(params.petId),
      getDeworming(params.petId),
      getMedicalRecords(params.petId),
      getVitals(params.petId),
      getMedications(params.petId),
      getTemperature(params.petId),
      getActivity(params.petId),
      getIntake(params.petId)
    ])
    vaccineList.value = vaccRes?.data || []
    dewormList.value = dewRes?.data || []
    medicalList.value = medRes?.data || []
    vitalList.value = vitRes?.data || []
    temperatureList.value = tempRes?.data || []
    activityList.value = actRes?.data || []
    intakeList.value = intakeRes?.data || []
    medicationList.value = medcRes?.data || []
  } catch (error) {
    console.error('加载健康记录失败', error)
    ElMessage.error('加载健康记录失败')
  }
}

onMounted(async () => {
  await loadPets()
  await loadMeta()
  await loadAll()
})

watch(
  () => vaccinationForm.institutionId,
  () => {
    vaccinationForm.vetUserId = ''
    reloadVetsForVaccine()
  }
)

const openMedicalDetail = (row) => {
  rowMedical.value = row || {}
  showMedicalDetail.value = true
}

const openVaccineDetail = (row) => {
  rowVaccine.value = row || {}
  showVaccineDetail.value = true
}

const openMedicationDetail = (row) => {
  rowMedication.value = row || {}
  showMedicationDetail.value = true
}

const handleVaccineSelect = (val) => {
  const match = vaccineTypeOptions.value.find((i) => i.vaccineName === val)
  vaccinationForm.vaccineTypeId = match?.vaccineTypeId || null
}

const editVaccine = (row) => {
  const id = row?.id || row?.vaccinationId
  if (!id) {
    ElMessage.warning('缺少疫苗记录ID')
    return
  }
  editingVaccinationId.value = id
  resetForm(vaccinationForm, {
    ...vaccinationFormInitial,
    petId: row.petId,
    vaccineName: row.vaccineName,
    vaccineTypeId: row.vaccineTypeId,
    institutionId: row.institutionId,
    vetUserId: row.vetUserId,
    vaccinationDate: row.vaccinationDate,
    nextDueDate: row.nextDueDate,
    lotNumber: row.lotNumber,
    notes: row.notes
  })
  if (vaccinationForm.institutionId) {
    reloadVetsForVaccine()
  }
  showAddVaccination.value = true
}

const editMedication = (row) => {
  const id = row?.id || row?.medicationId
  if (!id) {
    ElMessage.warning('缺少用药记录ID')
    return
  }
  editingMedicationId.value = id
  resetForm(medicationForm, {
    ...medicationFormInitial,
    petId: row.petId,
    drugName: row.drugName,
    dosage: row.dosage,
    frequency: row.frequency,
    route: row.route,
    startDate: row.startDate,
    endDate: row.endDate,
    instructions: row.instructions,
    contraindications: row.contraindications
  })
  showAddMedication.value = true
}

const handleDeleteVaccine = (row) => {
  const id = row?.id || row?.vaccinationId
  if (!id) {
    ElMessage.warning('缺少疫苗记录ID')
    return
  }
  deleteVaccination(id)
    .then(() => {
      ElMessage.success('已删除疫苗记录')
      loadAll()
    })
    .catch((error) => {
      console.error('删除疫苗记录失败', error)
      ElMessage.error(error?.message || '删除失败')
    })
}

const toOptionalNumber = (val) => {
  if (val === '' || val === null || val === undefined) return null
  return Number(val)
}

const submitVaccination = async () => {
  try {
    await vaccinationFormRef.value?.validate()
    savingVaccination.value = true
    const payload = {
      ...vaccinationForm,
      petId: Number(vaccinationForm.petId),
      institutionId: toOptionalNumber(vaccinationForm.institutionId),
      vetUserId: toOptionalNumber(vaccinationForm.vetUserId)
    }
    if (editingVaccinationId.value) {
      await updateVaccination(editingVaccinationId.value, payload)
      ElMessage.success('疫苗记录已更新')
    } else {
      await addVaccination(payload)
      ElMessage.success('新增疫苗记录成功')
    }
    showAddVaccination.value = false
    editingVaccinationId.value = null
    await loadAll()
  } catch (error) {
    console.error('新增疫苗记录失败', error)
    ElMessage.error(error?.message || '新增疫苗记录失败')
  } finally {
    savingVaccination.value = false
  }
}

const submitDeworming = async () => {
  try {
    await dewormingFormRef.value?.validate()
    savingDeworming.value = true
    const payload = {
      ...dewormingForm,
      petId: Number(dewormingForm.petId),
      productId: Number(dewormingForm.productId),
      sourceType: dewormingForm.sourceType,
      institutionId: dewormingForm.sourceType === '医院' && dewormingForm.institutionId !== '' ? Number(dewormingForm.institutionId) : null,
      vetUserId: dewormingForm.sourceType === '医院' && dewormingForm.vetUserId !== '' ? Number(dewormingForm.vetUserId) : null,
      nextDueDate: dewormingForm.nextDueDate || null
    }
    if (editingDewormId.value) {
      // TODO: 后端暂未提供更新接口，先删除再新增以模拟更新
      await deleteDeworm(editingDewormId.value)
      await addDeworming(payload)
      ElMessage.success('驱虫记录已更新')
    } else {
      await addDeworming(payload)
      ElMessage.success('新增驱虫记录成功')
    }
    showAddDeworming.value = false
    editingDewormId.value = null
    await loadAll()
  } catch (error) {
    console.error('新增驱虫记录失败', error)
    ElMessage.error(error?.message || '保存驱虫记录失败')
  } finally {
    savingDeworming.value = false
  }
}

const submitMedical = async () => {
  try {
    await medicalFormRef.value?.validate()
    savingMedical.value = true
    const payload = {
      ...medicalForm,
      petId: Number(medicalForm.petId),
      institutionId: toOptionalNumber(medicalForm.institutionId),
      vetUserId: toOptionalNumber(medicalForm.vetUserId),
      cost: medicalForm.cost === '' || medicalForm.cost === null ? null : Number(medicalForm.cost)
    }
    if (editingMedicalId.value) {
      await updateMedicalRecord(editingMedicalId.value, payload)
      ElMessage.success('医疗记录已更新')
    } else {
      await addMedicalRecord(payload)
      ElMessage.success('新增医疗记录成功')
    }
    showAddMedical.value = false
    editingMedicalId.value = null
    await loadAll()
  } catch (error) {
    console.error('保存医疗记录失败', error)
    ElMessage.error(error?.message || '保存医疗记录失败')
  } finally {
    savingMedical.value = false
  }
}

const submitVital = async () => {
  try {
    await vitalFormRef.value?.validate()
    savingVital.value = true
    const payload = {
      ...vitalForm,
      petId: Number(vitalForm.petId),
      value: Number(vitalForm.value)
    }
    if (editingVitalId.value) {
      await updateVital(editingVitalId.value, payload)
      ElMessage.success('已更新体征记录')
    } else {
      await addVital(payload)
      ElMessage.success('新增体征记录成功')
    }
    editingVitalId.value = null
    showAddVital.value = false
    await loadAll()
  } catch (error) {
    console.error('新增体征记录失败', error)
    ElMessage.error(error?.message || '保存体征记录失败')
  } finally {
    savingVital.value = false
  }
}

const deleteMedicationRecord = async (row) => {
  const id = row?.id || row?.medicationId
  if (!id) {
    ElMessage.warning('缺少用药记录ID')
    return
  }
  try {
    await deleteMedication(id)
    ElMessage.success('已删除用药记录')
    await loadAll()
  } catch (error) {
    console.error('删除用药记录失败', error)
    ElMessage.error(error?.message || '删除失败')
  }
}

const submitMedication = async () => {
  try {
    await medicationFormRef.value?.validate()
    savingMedication.value = true
    const payload = {
      ...medicationForm,
      petId: Number(medicationForm.petId)
    }
    if (editingMedicationId.value) {
      await updateMedication(editingMedicationId.value, payload)
      ElMessage.success('用药记录已更新')
    } else {
      await addMedication(payload)
      ElMessage.success('新增用药记录成功')
    }
    showAddMedication.value = false
    editingMedicationId.value = null
    await loadAll()
  } catch (error) {
    console.error('新增/更新用药记录失败', error)
    ElMessage.error(error?.message || '保存用药记录失败')
  } finally {
    savingMedication.value = false
  }
}

const handleDeleteVital = async (row) => {
  if (!row?.id) {
    ElMessage.warning('该记录暂无可删除的ID')
    return
  }
  try {
    await deleteVital(row.id)
    ElMessage.success('已删除该体征记录')
    await loadAll()
  } catch (error) {
    console.error('删除体征记录失败', error)
    ElMessage.error(error?.message || '删除失败')
  }
}

const handleDeleteMedical = async (row) => {
  if (!row?.id) return
  try {
    await deleteMedicalRecord(row.id)
    ElMessage.success('已删除该医疗记录')
    await loadAll()
  } catch (error) {
    console.error('删除医疗记录失败', error)
    ElMessage.error(error?.message || '删除失败')
  }
}

const handleDeleteDeworm = async (row) => {
  if (!row?.id) return
  try {
    await deleteDeworm(row.id)
    ElMessage.success('已删除该驱虫记录')
    await loadAll()
  } catch (error) {
    console.error('删除驱虫记录失败', error)
    ElMessage.error(error?.message || '删除失败')
  }
}

const display = (val) => {
  if (val === null || val === undefined || val === '') return '未填写'
  return val
}
</script>

<style scoped>
.health-container {
  padding: 20px;
  background: var(--bg);
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

:global(html),
:global(body) {
  height: 100%;
  overflow: hidden;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-1);
  margin-bottom: 12px;
}

.panel {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.tab-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

</style>
