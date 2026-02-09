import request from './request'

export function getVaccinations(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/vaccinations',
    method: 'get',
    params
  })
}

export function getDeworming(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/deworming',
    method: 'get',
    params
  })
}

export function getMedicalRecords(params = {}) {
  const query = {}
  if (typeof params === 'object') {
    Object.assign(query, params)
  } else if (params !== undefined && params !== null && params !== '') {
    query.petId = params
  }
  return request({
    url: '/health/medical-records',
    method: 'get',
    params: query
  })
}

export function getVitals(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/vitals',
    method: 'get',
    params
  })
}

export function getTemperature(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/temperature',
    method: 'get',
    params
  })
}

export function getActivity(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/activity',
    method: 'get',
    params
  })
}

export function getIntake(petId) {
  const params = {}
  if (petId !== undefined && petId !== null && petId !== '') params.petId = petId
  return request({
    url: '/health/intake',
    method: 'get',
    params
  })
}

export function getMedications(params = {}) {
  const query = {}
  // 兼容旧调用：传入 petId 时支持非对象参数
  if (typeof params === 'string' || typeof params === 'number') {
    if (params !== '' && params !== null && params !== undefined) query.petId = params
  } else {
    Object.assign(query, params)
  }
  return request({
    url: '/health/medications',
    method: 'get',
    params: query
  })
}

export function addVaccination(data) {
  return request({
    url: '/health/vaccinations',
    method: 'post',
    data
  })
}

export function updateVaccination(id, data) {
  return request({
    url: `/health/vaccinations/${id}`,
    method: 'put',
    data
  })
}

export function deleteVaccination(id) {
  return request({
    url: `/health/vaccinations/${id}`,
    method: 'delete'
  })
}

export function addDeworming(data) {
  return request({
    url: '/health/deworming',
    method: 'post',
    data
  })
}

export function deleteDeworm(id) {
  return request({
    url: `/health/deworming/${id}`,
    method: 'delete'
  })
}

export function getDewormProducts() {
  return request({
    url: '/health/deworming/products',
    method: 'get'
  })
}

export function addMedicalRecord(data) {
  return request({
    url: '/health/medical-records',
    method: 'post',
    data
  })
}

export function addVital(data) {
  return request({
    url: '/health/vitals',
    method: 'post',
    data
  })
}

export function updateVital(id, data) {
  return request({
    url: `/health/vitals/${id}`,
    method: 'put',
    data
  })
}

export function deleteVital(id) {
  return request({
    url: `/health/vitals/${id}`,
    method: 'delete'
  })
}

export function addMedication(data) {
  return request({
    url: '/health/medications',
    method: 'post',
    data
  })
}

export function updateMedication(id, data) {
  return request({
    url: `/health/medications/${id}`,
    method: 'put',
    data
  })
}

export function deleteMedication(id) {
  return request({
    url: `/health/medications/${id}`,
    method: 'delete'
  })
}

export function updateMedicalRecord(recordId, data) {
  return request({
    url: `/health/medical-records/${recordId}`,
    method: 'put',
    data
  })
}

export function deleteMedicalRecord(recordId) {
  return request({
    url: `/health/medical-records/${recordId}`,
    method: 'delete'
  })
}

export function getInstitutions() {
  return request({
    url: '/health/institutions',
    method: 'get'
  })
}

export function getVets(params = {}) {
  return request({
    url: '/health/vets',
    method: 'get',
    params
  })
}

export function getVetInstitution(vetUserId) {
  return request({
    url: `/health/vets/${vetUserId}/institution`,
    method: 'get'
  })
}

export function getVaccineTypes() {
  return request({
    url: '/health/vaccine-types',
    method: 'get'
  })
}

// ---- 新增：兽医工作台相关接口 ----
export function getDailyStats(params = {}) {
  return request({
    url: '/health/stats/daily',
    method: 'get',
    params
  })
}

export function getOverviewStats(params = {}) {
  return request({
    url: '/health/stats/overview',
    method: 'get',
    params
  })
}

export function getPatientDetail(petId) {
  return request({
    url: `/health/patients/${petId}`,
    method: 'get'
  })
}

export function getPatientAppointments(petId, params = {}) {
  return request({
    url: `/health/patients/${petId}/appointments`,
    method: 'get',
    params
  })
}

export function getPatientMedicalRecords(petId, params = {}) {
  return request({
    url: `/health/patients/${petId}/medical-records`,
    method: 'get',
    params
  })
}

export function getPatientReminders(petId, params = {}) {
  return request({
    url: `/health/patients/${petId}/reminders`,
    method: 'get',
    params
  })
}

export function createMedication(data) {
  return request({
    url: '/health/medications',
    method: 'post',
    data
  })
}

export function updateMedicationRecord(id, data) {
  return request({
    url: `/health/medications/${id}`,
    method: 'put',
    data
  })
}

export function deleteMedicationRecord(id) {
  return request({
    url: `/health/medications/${id}`,
    method: 'delete'
  })
}

export function getTemplates(params = {}) {
  return request({
    url: '/health/templates',
    method: 'get',
    params
  })
}

export function createTemplate(data) {
  return request({
    url: '/health/templates',
    method: 'post',
    data
  })
}

export function updateTemplate(id, data) {
  return request({
    url: `/health/templates/${id}`,
    method: 'put',
    data
  })
}

export function deleteTemplate(id) {
  return request({
    url: `/health/templates/${id}`,
    method: 'delete'
  })
}

export function getTasks(params = {}) {
  return request({
    url: '/health/tasks',
    method: 'get',
    params
  })
}

export function updateTask(id, data) {
  return request({
    url: `/health/tasks/${id}`,
    method: 'put',
    data
  })
}
