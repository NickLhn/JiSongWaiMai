import request from '@/utils/request'

// 上传图片
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('directory', 'logo')
  return request({
    url: '/v1/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传文件
export function uploadFile(file, directory = 'files') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('directory', directory)
  return request({
    url: '/v1/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
