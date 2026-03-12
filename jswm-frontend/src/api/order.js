import request from '@/utils/request'

export const getOrderList = (params) => {
  return request({
    url: '/v1/orders',
    method: 'get',
    params
  })
}

export const getOrderDetail = (id) => {
  return request({
    url: `/v1/orders/${id}`,
    method: 'get'
  })
}

export const createOrder = (data) => {
  return request({
    url: '/v1/orders',
    method: 'post',
    data
  })
}

export const payOrder = (id) => {
  return request({
    url: `/v1/orders/${id}/pay`,
    method: 'post'
  })
}

export const cancelOrder = (id, reason) => {
  return request({
    url: `/v1/orders/${id}/cancel`,
    method: 'post',
    data: { reason }
  })
}

export const acceptOrder = (id) => {
  return request({
    url: `/v1/orders/${id}/accept`,
    method: 'post'
  })
}

export const deliverOrder = (id) => {
  return request({
    url: `/v1/orders/${id}/deliver`,
    method: 'post'
  })
}

export const completeOrder = (id) => {
  return request({
    url: `/v1/orders/${id}/complete`,
    method: 'post'
  })
}
