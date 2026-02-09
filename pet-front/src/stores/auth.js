import { defineStore } from 'pinia'
import { login as apiLogin, logout as apiLogout, getCurrentUser } from '@/api/auth'

const SESSION_TTL_MS = 24 * 60 * 60 * 1000 // 24小时

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // 登录状态
    token: localStorage.getItem('token') || '',
    userInfo: null,
    username: localStorage.getItem('username') || '',
    userRole: localStorage.getItem('userRole') || '宠物主人',
    loginTimestamp: Number(localStorage.getItem('loginTimestamp')) || 0,
    isAuthenticated: !!localStorage.getItem('token')
  }),
  actions: {
    /**
     * 登录
     * @param {string} username 用户名
     * @param {string} password 密码
     */
    async login(username, password) {
      try {
        const res = await apiLogin({ username, password })
        this.token = res.data?.token || ''
        this.userRole = res.data?.roleName || '宠物主人' // 与路由守卫角色一致
        this.username = username || res.data?.username || ''
        this.loginTimestamp = Date.now()
        localStorage.setItem('token', this.token)
        localStorage.setItem('userRole', this.userRole)
        localStorage.setItem('loginTimestamp', String(this.loginTimestamp))
        if (this.username) {
          localStorage.setItem('username', this.username)
        }
        await this.fetchUserInfo()
        return true
      } catch (error) {
        console.error('登录失败:', error)
        return false
      }
    },

    /**
     * 获取当前用户信息
     */
    async fetchUserInfo() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        this.userRole = res.data?.roleName || localStorage.getItem('userRole') || '宠物主人'
        // 如果登录时已经有用户名，优先使用现有用户名，避免后端返回的默认账号覆盖前端显示
        const fetchedUsername = res.data?.username || res.data?.nickname || ''
        if (!this.username && fetchedUsername) {
          this.username = fetchedUsername
        }
        if (this.username) localStorage.setItem('username', this.username)
        this.isAuthenticated = true
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.logout()
      }
    },

    /**
     * 退出登录
     */
    async logout() {
      try {
        await apiLogout()
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        // 清空状态
        this.token = ''
        this.userInfo = null
        this.username = ''
        this.userRole = ''
        this.loginTimestamp = 0
        this.isAuthenticated = false
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
        localStorage.removeItem('username')
        localStorage.removeItem('loginTimestamp')
      }
    },

    /**
     * 刷新/重载时恢复会话
     */
    async restoreSession() {
      const savedToken = localStorage.getItem('token')
      const savedLoginTimestamp = Number(localStorage.getItem('loginTimestamp')) || 0
      if (!savedToken) {
        this.logout()
        return
      }
      if (this.isSessionExpired(savedLoginTimestamp)) {
        this.logout()
        return
      }
      this.token = savedToken
      this.loginTimestamp = savedLoginTimestamp
      this.userRole = localStorage.getItem('userRole') || '宠物主人'
       this.username = localStorage.getItem('username') || ''
      this.isAuthenticated = true
      if (!this.userInfo) {
        try {
          await this.fetchUserInfo()
        } catch (e) {
          console.error('恢复会话失败', e)
          this.logout()
        }
      }
    },

    /**
     * 会话是否过期
     * @param {number} loginTimestamp
     */
    isSessionExpired(loginTimestamp = this.loginTimestamp) {
      if (!loginTimestamp) return true
      return Date.now() - loginTimestamp >= SESSION_TTL_MS
    }
  }
})
