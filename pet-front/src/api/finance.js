import request from './request'

export function getFinanceList(params = {}) {
  return request({
    url: '/finance/list',
    method: 'get',
    params
  })
}

export function addFinanceRecord(data) {
  return request({
    url: '/finance/add',
    method: 'post',
    data
  })
}
