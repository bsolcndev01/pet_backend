import request from './request'

export function getPlans(petId) {
  return request({
    url: '/plans',
    method: 'get',
    params: { petId }
  })
}

export function addPlan(data) {
  return request({
    url: '/plans',
    method: 'post',
    data
  })
}

export function updatePlan(id, data) {
  return request({
    url: `/plans/${id}`,
    method: 'put',
    data
  })
}

export function deletePlan(id) {
  return request({
    url: `/plans/${id}`,
    method: 'delete'
  })
}
