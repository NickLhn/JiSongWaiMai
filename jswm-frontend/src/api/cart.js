import request from '@/utils/request'

export const getCartList = () => {
  return request({
    url: '/v1/cart',
    method: 'get'
  })
}

export const addToCart = (params) => {
  return request({
    url: '/v1/cart',
    method: 'post',
    params
  })
}

export const updateCartItem = (id, quantity) => {
  return request({
    url: `/v1/cart/${id}`,
    method: 'put',
    params: { quantity }
  })
}

export const deleteCartItem = (id) => {
  return request({
    url: `/v1/cart/${id}`,
    method: 'delete'
  })
}

export const clearCart = () => {
  return request({
    url: '/v1/cart',
    method: 'delete'
  })
}
