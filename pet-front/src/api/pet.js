import request from './request'

/**
 * 获取宠物列表
 * @param {Object} params - { keyword, page, size }
 */
export function getPetList(params = {}) {
  return request({
    url: '/pets',
    method: 'get',
    params
  })
}

/**
 * 获取宠物详情
 * @param {number} petId 宠物ID
 */
export function getPetDetail(petId) {
  return request({
    url: `/pets/${petId}`,
    method: 'get'
  })
}

export function getPetPhotos(petId) {
  return request({
    url: `/pets/${petId}/photos`,
    method: 'get'
  })
}

export function uploadPetGalleryPhoto(petId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/pets/${petId}/photos`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deletePetPhoto(photoId, petId) {
  return request({
    url: `/pets/photos/${photoId}`,
    method: 'delete',
    params: { petId }
  })
}

export function updatePetPhotoPosition(photoId, petId, posX, posY) {
  return request({
    url: `/pets/photos/${photoId}/position`,
    method: 'patch',
    params: { petId },
    data: { posX, posY }
  })
}

/**
 * 添加宠物
 * @param {Object} data 宠物信息
 */
export function addPet(data) {
  return request({
    url: '/pets',
    method: 'post',
    data
  })
}

/**
 * 更新宠物信息
 * @param {number} petId 宠物ID
 * @param {Object} data 宠物信息
 */
export function updatePet(petId, data) {
  return request({
    url: `/pets/${petId}`,
    method: 'put',
    data
  })
}

/**
 * 上传宠物照片（文件会存入后端数据库）
 * @param {number} petId
 * @param {File} file
 */
export function uploadPetPhoto(petId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/pets/${petId}/photo`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除宠物
 * @param {number} petId 宠物ID
 */
export function deletePet(petId) {
  return request({
    url: `/pets/${petId}`,
    method: 'delete'
  })
}

/**
 * 获取宠物健康摘要
 */
export function getHealthSummary() {
  return request({
    url: '/pets/health-summary',
    method: 'get'
  })
}

/**
 * 获取宠物指定指标的趋势数据
 * @param {number} petId
 * @param {'weight'|'temperature'|'activity'|'intake'} type
 * @param {number} limit 返回条数，默认30
 */
export function getPetMetrics(petId, type, limit = 30) {
  return request({
    url: `/pets/${petId}/metrics`,
    method: 'get',
    params: { type, limit }
  })
}

/**
 * 添加宠物指标记录
 * @param {number} petId
 * @param {{type:string,value:number,recordDate:string,notes?:string}} data
 */
export function addPetMetric(petId, data) {
  return request({
    url: `/pets/${petId}/metrics`,
    method: 'post',
    data
  })
}
