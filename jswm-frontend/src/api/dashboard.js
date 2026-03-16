import request from '@/utils/request'

export function getMerchantDashboard() {
  return request({
    url: '/v1/merchant/dashboard',
    method: 'get'
  })
}
