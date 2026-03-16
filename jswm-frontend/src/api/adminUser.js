import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/v1/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/v1/admin/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteUser(id) {
  return request({
    url: `/v1/admin/users/${id}`,
    method: 'delete'
  })
}

export function updateUser(id, data) {
  return request({
    url: `/v1/admin/users/${id}`,
    method: 'put',
    data
  })
}
