import request from '@/utils/request'

export const getDishList = (params) => {
  return request({
    url: '/v1/dishes',
    method: 'get',
    params
  })
}

export const getDishDetail = (id) => {
  return request({
    url: `/v1/dishes/${id}`,
    method: 'get'
  })
}
