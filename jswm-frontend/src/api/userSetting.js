import request from '@/utils/request'

export function getUserSetting() {
  return request({
    url: '/v1/user/setting',
    method: 'get'
  })
}

export function updateUserSetting(data) {
  return request({
    url: '/v1/user/setting',
    method: 'put',
    data
  })
}
