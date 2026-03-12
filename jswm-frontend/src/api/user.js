import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/v1/auth/register',
    method: 'post',
    data
  })
}

export const getUserInfo = () => {
  return request({
    url: '/v1/user/info',
    method: 'get'
  })
}

export const updateUserInfo = (data) => {
  return request({
    url: '/v1/user/info',
    method: 'put',
    data
  })
}
