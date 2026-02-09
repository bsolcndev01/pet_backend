import request from './request'

export const feedbackApi = {
  submitFeedback(data) {
    return request({ url: '/admin/feedback', method: 'post', data })
  },
  listMyFeedback(userId) {
    const params = userId ? { userId } : undefined
    return request({ url: '/feedback/my', method: 'get', params })
  }
}
