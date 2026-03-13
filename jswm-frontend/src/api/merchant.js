import request from '@/utils/request'

export const getMerchantList = (params) => {
  return request({
    url: '/v1/merchants',
    method: 'get',
    params
  })
}

export const getMerchantDetail = (id) => {
  return request({
    url: `/v1/merchants/${id}`,
    method: 'get'
  })
}

export const addMerchant = (data) => {
  return request({
    url: '/v1/merchants',
    method: 'post',
    data
  })
}

export const updateMerchant = (id, data) => {
  return request({
    url: `/v1/merchants/${id}`,
    method: 'put',
    data
  })
}

export const deleteMerchant = (id) => {
  return request({
    url: `/v1/merchants/${id}`,
    method: 'delete'
  })
}

// 获取当前商家的店铺信息
export const getMyMerchantInfo = () => {
  return request({
    url: '/v1/merchants/my',
    method: 'get'
  })
}

// 更新当前商家的店铺信息
export const updateMyMerchantInfo = (data) => {
  return request({
    url: '/v1/merchants/my',
    method: 'put',
    data
  })
}
