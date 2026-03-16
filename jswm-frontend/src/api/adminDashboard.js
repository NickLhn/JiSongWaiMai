import request from '@/utils/request'

export function getAdminDashboard() {
  return request({
    url: '/v1/admin/dashboard',
    method: 'get'
  })
}
