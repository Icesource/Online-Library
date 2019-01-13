import request from '@/utils/request'

export function resourceListByType(query, typeId) {
  return request({
    url: '/api/resource/list/' + typeId,
    method: 'get',
    params: query
  })
}

export function resourceListAll(query) {
  return request({
    url: '/api/resource/list',
    method: 'get',
    params: query
  })
}

export function resourceAdd(data) {
  return request({
    url: '/api/resource/upload',
    method: 'post',
    data
  })
}

export function resourceDelete(query) {
  return request({
    url: '/api/resource/delete',
    method: 'get',
    params: query
  })
}

export function resourceDownload(query, typeId) {
  return request({
    url: '/api/resource/download/' + typeId,
    method: 'get',
    params: query
  })
}
