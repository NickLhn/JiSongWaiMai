import request from '@/utils/request'

/**
 * 上传文件
 * @param {File} file - 文件对象
 * @param {string} directory - 上传目录，默认 'images'
 * @returns {Promise} 上传结果，包含文件URL
 */
export function uploadFile(file, directory = 'images') {
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

/**
 * 删除文件
 * @param {string} fileUrl - 文件URL
 * @returns {Promise}
 */
export function deleteFile(fileUrl) {
  return request({
    url: '/v1/upload',
    method: 'delete',
    params: { url: fileUrl }
  })
}
