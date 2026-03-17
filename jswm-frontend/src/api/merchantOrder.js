import request from '@/utils/request'

// 获取商家订单列表
export function getMerchantOrders(status) {
  return request({
    url: '/v1/merchant/orders',
    method: 'get',
    params: { status }
  })
}

// 接单
export function acceptOrder(id) {
  return request({
    url: `/v1/merchant/orders/${id}/accept`,
    method: 'post'
  })
}

// 开始配送
export function deliverOrder(id) {
  return request({
    url: `/v1/merchant/orders/${id}/deliver`,
    method: 'post'
  })
}

// 完成订单
export function completeOrder(id) {
  return request({
    url: `/v1/merchant/orders/${id}/complete`,
    method: 'post'
  })
}
