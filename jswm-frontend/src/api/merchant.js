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
