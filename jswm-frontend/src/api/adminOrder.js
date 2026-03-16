import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/v1/admin/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/v1/admin/orders/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/v1/admin/orders/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 取消订单
export function cancelOrder(id, reason) {
  return request({
    url: `/v1/admin/orders/${id}/cancel`,
    method: 'put',
    params: { reason }
  })
}

// 删除订单
export function deleteOrder(id) {
  return request({
    url: `/v1/admin/orders/${id}`,
    method: 'delete'
  })
}
