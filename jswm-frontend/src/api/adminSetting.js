import request from '@/utils/request'

// 获取所有系统设置
export function getSettings() {
  return request({
    url: '/v1/admin/settings',
    method: 'get'
  })
}

// 更新系统设置
export function updateSettings(data) {
  return request({
    url: '/v1/admin/settings',
    method: 'put',
    data
  })
}

// 获取单个设置
export function getSettingByKey(key) {
  return request({
    url: `/v1/admin/settings/${key}`,
    method: 'get'
  })
}

// 更新单个设置
export function updateSettingByKey(key, value) {
  return request({
    url: `/v1/admin/settings/${key}`,
    method: 'put',
    params: { value }
  })
}
