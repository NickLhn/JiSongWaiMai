import request from '@/utils/request'

// 登录相关
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/v1/auth/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/v1/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/v1/user/info',
    method: 'put',
    data
  })
}

export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/v1/user/password',
    method: 'put',
    data: { oldPassword, newPassword }
  })
}

export function updateAvatar(avatar) {
  return request({
    url: '/v1/user/avatar',
    method: 'put',
    data: { avatar }
  })
}

// 收藏相关
export function getFavorites() {
  return request({
    url: '/v1/user/favorites',
    method: 'get'
  })
}

export function addFavorite(merchantId) {
  return request({
    url: '/v1/user/favorites',
    method: 'post',
    data: { merchantId }
  })
}

export function removeFavorite(merchantId) {
  return request({
    url: `/v1/user/favorites/${merchantId}`,
    method: 'delete'
  })
}

export function checkFavorite(merchantId) {
  return request({
    url: '/v1/user/favorites/check',
    method: 'get',
    params: { merchantId }
  })
}

// 优惠券相关
export function getCoupons(status) {
  return request({
    url: '/v1/user/coupons',
    method: 'get',
    params: { status }
  })
}

export function getAvailableCouponCount() {
  return request({
    url: '/v1/user/coupons/count',
    method: 'get'
  })
}

// 地址相关
export function getAddresses() {
  return request({
    url: '/v1/user/addresses',
    method: 'get'
  })
}

export function getAddressById(id) {
  return request({
    url: `/v1/user/addresses/${id}`,
    method: 'get'
  })
}

export function getDefaultAddress() {
  return request({
    url: '/v1/user/addresses/default',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/v1/user/addresses',
    method: 'post',
    data
  })
}

export function updateAddress(id, data) {
  return request({
    url: `/v1/user/addresses/${id}`,
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/v1/user/addresses/${id}`,
    method: 'delete'
  })
}

export function setDefaultAddress(id) {
  return request({
    url: `/v1/user/addresses/${id}/default`,
    method: 'put'
  })
}
