import request from './request'

/**
 * 获取即将到来的提醒列表
 */
export function getUpcomingReminders(params = {}) {
  return request({
    url: '/reminder/upcoming',
    method: 'get',
    params
  })
}

/**
 * 获取提醒列表
 */
export function getAllReminders(params = {}) {
  return request({
    url: '/reminder/list',
    method: 'get',
    params
  })
}

/**
 * 新增提醒
 */
export function addReminder(data = {}) {
  return request({
    url: '/reminder/add',
    method: 'post',
    data
  })
}
