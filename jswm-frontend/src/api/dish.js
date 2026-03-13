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

export const addDish = (data) => {
  return request({
    url: '/v1/dishes',
    method: 'post',
    data
  })
}

export const updateDish = (id, data) => {
  return request({
    url: `/v1/dishes/${id}`,
    method: 'put',
    data
  })
}

export const deleteDish = (id) => {
  return request({
    url: `/v1/dishes/${id}`,
    method: 'delete'
  })
}
