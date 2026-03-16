import request from '@/utils/request'

// 获取订单评价
export function getReviewByOrderId(orderId) {
  return request({
    url: `/v1/reviews/order/${orderId}`,
    method: 'get'
  })
}

// 获取商家评价列表
export function getMerchantReviews(merchantId, params) {
  return request({
    url: `/v1/reviews/merchant/${merchantId}`,
    method: 'get',
    params
  })
}

// 获取商家评价统计
export function getMerchantReviewStats(merchantId) {
  return request({
    url: `/v1/reviews/merchant/${merchantId}/stats`,
    method: 'get'
  })
}

// 获取我的评价列表
export function getMyReviews(params) {
  return request({
    url: '/v1/reviews/my',
    method: 'get',
    params
  })
}

// 提交评价
export function submitReview(data) {
  return request({
    url: '/v1/reviews',
    method: 'post',
    data
  })
}

// 删除评价
export function deleteReview(id) {
  return request({
    url: `/v1/reviews/${id}`,
    method: 'delete'
  })
}
