import request from './request'

export const adminApi = {
  // 用户
  listUsers() {
    return request({ url: '/admin/users', method: 'get' })
  },
  createUser(data) {
    return request({ url: '/admin/users', method: 'post', data })
  },
  updateUser(id, data) {
    return request({ url: `/admin/users/${id}`, method: 'put', data })
  },
  deleteUser(id) {
    return request({ url: `/admin/users/${id}`, method: 'delete' })
  },

  // 医疗机构
  listInstitutions() {
    return request({ url: '/admin/institutions', method: 'get' })
  },
  createInstitution(data) {
    return request({ url: '/admin/institutions', method: 'post', data })
  },
  updateInstitution(id, data) {
    return request({ url: `/admin/institutions/${id}`, method: 'put', data })
  },
  deleteInstitution(id) {
    return request({ url: `/admin/institutions/${id}`, method: 'delete' })
  },

  // 疫苗类型
  listVaccineTypes() {
    return request({ url: '/admin/vaccine-types', method: 'get' })
  },
  createVaccineType(data) {
    return request({ url: '/admin/vaccine-types', method: 'post', data })
  },
  updateVaccineType(id, data) {
    return request({ url: `/admin/vaccine-types/${id}`, method: 'put', data })
  },
  deleteVaccineType(id) {
    return request({ url: `/admin/vaccine-types/${id}`, method: 'delete' })
  },

  // 宠物档案（管理员）
  listPets(params) {
    return request({ url: '/admin/pets', method: 'get', params })
  },
  createPet(data) {
    const { userId, ...payload } = data || {}
    return request({ url: '/admin/pets', method: 'post', params: { userId }, data: payload })
  },
  updatePet(id, data) {
    const { userId, ...payload } = data || {}
    return request({ url: `/admin/pets/${id}`, method: 'put', params: { userId }, data: payload })
  },
  deletePet(id) {
    return request({ url: `/admin/pets/${id}`, method: 'delete' })
  },

  // 驱虫药品
  listMedications() {
    return request({ url: '/admin/deworming-products', method: 'get' })
  },
  createMedication(data) {
    return request({ url: '/admin/deworming-products', method: 'post', data })
  },
  updateMedication(id, data) {
    return request({ url: `/admin/deworming-products/${id}`, method: 'put', data })
  },
  deleteMedication(id) {
    return request({ url: `/admin/deworming-products/${id}`, method: 'delete' })
  },

  // 提醒类型
  listReminderTypes() {
    return request({ url: '/admin/reminder-types', method: 'get' })
  },
  createReminderType(data) {
    return request({ url: '/admin/reminder-types', method: 'post', data })
  },
  updateReminderType(id, data) {
    return request({ url: `/admin/reminder-types/${id}`, method: 'put', data })
  },
  deleteReminderType(id) {
    return request({ url: `/admin/reminder-types/${id}`, method: 'delete' })
  },

  // 健康记录
  listVaccinations() {
    return request({ url: '/admin/vaccinations', method: 'get' })
  },
  createVaccination(data) {
    return request({ url: '/admin/vaccinations', method: 'post', data })
  },
  updateVaccination(id, data) {
    return request({ url: `/admin/vaccinations/${id}`, method: 'put', data })
  },
  deleteVaccination(id) {
    return request({ url: `/admin/vaccinations/${id}`, method: 'delete' })
  },
  listMedicalRecords() {
    return request({ url: '/admin/medical-records', method: 'get' })
  },
  createMedicalRecord(data) {
    return request({ url: '/admin/medical-records', method: 'post', data })
  },
  updateMedicalRecord(id, data) {
    return request({ url: `/admin/medical-records/${id}`, method: 'put', data })
  },
  deleteMedicalRecord(id) {
    return request({ url: `/admin/medical-records/${id}`, method: 'delete' })
  },
  listWeightRecords() {
    return request({ url: '/admin/weight-records', method: 'get' })
  },
  createWeightRecord(data) {
    return request({ url: '/admin/weight-records', method: 'post', data })
  },
  updateWeightRecord(id, data) {
    return request({ url: `/admin/weight-records/${id}`, method: 'put', data })
  },
  deleteWeightRecord(id) {
    return request({ url: `/admin/weight-records/${id}`, method: 'delete' })
  },
  listTemperatureRecords() {
    return request({ url: '/admin/temperature-records', method: 'get' })
  },
  createTemperatureRecord(data) {
    return request({ url: '/admin/temperature-records', method: 'post', data })
  },
  updateTemperatureRecord(id, data) {
    return request({ url: `/admin/temperature-records/${id}`, method: 'put', data })
  },
  deleteTemperatureRecord(id) {
    return request({ url: `/admin/temperature-records/${id}`, method: 'delete' })
  },
  listDewormingRecords() {
    return request({ url: '/admin/deworming-records', method: 'get' })
  },
  createDewormingRecord(data) {
    return request({ url: '/admin/deworming-records', method: 'post', data })
  },
  updateDewormingRecord(id, data) {
    return request({ url: `/admin/deworming-records/${id}`, method: 'put', data })
  },
  deleteDewormingRecord(id) {
    return request({ url: `/admin/deworming-records/${id}`, method: 'delete' })
  },
  listFinanceRecords() {
    return request({ url: '/admin/finance-records', method: 'get' })
  },
  createFinanceRecord(data) {
    return request({ url: '/admin/finance-records', method: 'post', data })
  },
  updateFinanceRecord(id, data) {
    return request({ url: `/admin/finance-records/${id}`, method: 'put', data })
  },
  deleteFinanceRecord(id) {
    return request({ url: `/admin/finance-records/${id}`, method: 'delete' })
  },
  listMedicationInventory() {
    return request({ url: '/admin/medications', method: 'get' })
  },
  createMedicationInventory(data) {
    return request({ url: '/admin/medications', method: 'post', data })
  },
  updateMedicationInventory(id, data) {
    return request({ url: `/admin/medications/${id}`, method: 'put', data })
  },
  deleteMedicationInventory(id) {
    return request({ url: `/admin/medications/${id}`, method: 'delete' })
  },
  listPetMedications() {
    return request({ url: '/admin/pet-medications', method: 'get' })
  },
  createPetMedication(data) {
    return request({ url: '/admin/pet-medications', method: 'post', data })
  },
  updatePetMedication(id, data) {
    return request({ url: `/admin/pet-medications/${id}`, method: 'put', data })
  },
  deletePetMedication(id) {
    return request({ url: `/admin/pet-medications/${id}`, method: 'delete' })
  },
  listPetPlans() {
    return request({ url: '/admin/pet-plans', method: 'get' })
  },
  createPetPlan(data) {
    return request({ url: '/admin/pet-plans', method: 'post', data })
  },
  updatePetPlan(id, data) {
    return request({ url: `/admin/pet-plans/${id}`, method: 'put', data })
  },
  deletePetPlan(id) {
    return request({ url: `/admin/pet-plans/${id}`, method: 'delete' })
  },
  listReminders() {
    return request({ url: '/admin/reminders', method: 'get' })
  },
  createReminder(data) {
    return request({ url: '/admin/reminders', method: 'post', data })
  },
  updateReminder(id, data) {
    return request({ url: `/admin/reminders/${id}`, method: 'put', data })
  },
  deleteReminder(id) {
    return request({ url: `/admin/reminders/${id}`, method: 'delete' })
  },
  listActivityRecords() {
    return request({ url: '/admin/activity-records', method: 'get' })
  },
  createActivityRecord(data) {
    return request({ url: '/admin/activity-records', method: 'post', data })
  },
  updateActivityRecord(id, data) {
    return request({ url: `/admin/activity-records/${id}`, method: 'put', data })
  },
  deleteActivityRecord(id) {
    return request({ url: `/admin/activity-records/${id}`, method: 'delete' })
  },
  listIntakeRecords() {
    return request({ url: '/admin/intake-records', method: 'get' })
  },
  createIntakeRecord(data) {
    return request({ url: '/admin/intake-records', method: 'post', data })
  },
  updateIntakeRecord(id, data) {
    return request({ url: `/admin/intake-records/${id}`, method: 'put', data })
  },
  deleteIntakeRecord(id) {
    return request({ url: `/admin/intake-records/${id}`, method: 'delete' })
  },

  // 用户反馈
  listFeedback() {
    return request({ url: '/admin/feedback', method: 'get' })
  },
  createFeedback(data) {
    return request({ url: '/admin/feedback', method: 'post', data })
  },
  updateFeedback(id, data) {
    return request({ url: `/admin/feedback/${id}`, method: 'put', data })
  },
  deleteFeedback(id) {
    return request({ url: `/admin/feedback/${id}`, method: 'delete' })
  },

  // 保险
  listInsurance() {
    return request({ url: '/admin/insurance', method: 'get' })
  },
  createInsurance(data) {
    return request({ url: '/admin/insurance', method: 'post', data })
  },
  updateInsurance(id, data) {
    return request({ url: `/admin/insurance/${id}`, method: 'put', data })
  },
  deleteInsurance(id) {
    return request({ url: `/admin/insurance/${id}`, method: 'delete' })
  },

  // 预约管理（管理员）
  listAppointments(params) {
    return request({ url: '/admin/appointments', method: 'get', params })
  },
  createAppointment(data) {
    return request({ url: '/admin/appointments', method: 'post', data })
  },
  updateAppointment(id, data) {
    return request({ url: `/admin/appointments/${id}`, method: 'put', data })
  },
  deleteAppointment(id) {
    return request({ url: `/admin/appointments/${id}`, method: 'delete' })
  },
  confirmAppointment(id) {
    return request({ url: `/admin/appointments/${id}/confirm`, method: 'put' })
  },

  // 角色管理
  listRoles() {
    return request({ url: '/admin/roles', method: 'get' })
  },
  createRole(data) {
    return request({ url: '/admin/roles', method: 'post', data })
  },
  updateRole(id, data) {
    return request({ url: `/admin/roles/${id}`, method: 'put', data })
  },
  deleteRole(id) {
    return request({ url: `/admin/roles/${id}`, method: 'delete' })
  },

  // 兽医管理
  listVeterinarians() {
    return request({ url: '/admin/veterinarians', method: 'get' })
  },
  createVeterinarian(data) {
    return request({ url: '/admin/veterinarians', method: 'post', data })
  },
  updateVeterinarian(id, data) {
    return request({ url: `/admin/veterinarians/${id}`, method: 'put', data })
  },
  deleteVeterinarian(id) {
    return request({ url: `/admin/veterinarians/${id}`, method: 'delete' })
  },

  // 宠物照片（后台）
  listPetPhotos(params) {
    return request({ url: '/admin/pet-photos', method: 'get', params })
  },
  createPetPhoto(data) {
    return request({ url: '/admin/pet-photos', method: 'post', data })
  },
  updatePetPhoto(id, data) {
    return request({ url: `/admin/pet-photos/${id}`, method: 'put', data })
  },
  deletePetPhoto(id) {
    return request({ url: `/admin/pet-photos/${id}`, method: 'delete' })
  }
}
