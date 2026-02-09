import request from './request'

/**
 * 获取即将到来的预约列表
 * @param {Object} params - { page, size }
 */
export function getUpcomingAppointments(params = {}) {
  return request({
    url: '/appointment/upcoming',
    method: 'get',
    params
  })
}

/**
 * 获取预约列表
 * @param {Object} params - { page, size }
 */
export function getAppointmentList(params = {}) {
  return request({
    url: '/appointment/list',
    method: 'get',
    params
  })
}

/**
 * 新增预约
 * @param {Object} data - 预约信息
 */
export function addAppointment(data = {}) {
  return request({
    url: '/appointment/add',
    method: 'post',
    data
  })
}

/**
 * 更新预约
 */
export function editAppointment(id, data = {}) {
  return request({
    url: `/appointment/edit/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除预约
 */
export function deleteAppointment(id) {
  return request({
    url: `/appointment/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 确认预约
 */
export function confirmAppointment(id) {
  return request({
    url: `/appointment/confirm/${id}`,
    method: 'put'
  })
}
