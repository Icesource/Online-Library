import request from '@/utils/request'

export function typeList(query) {
  return request({
    url: '/api/type/list',
    method: 'get',
    params: query
  })
}

export function typeAdd(data) {
  return request({
    url: '/api/type/add',
    method: 'post',
    data
  })
}

export function typeDelete(data) {
  return request({
    url: '/api/type/delete',
    method: 'post',
    data
  })
}

export function typeUpdate(data) {
  return request({
    url: '/api/type/update',
    method: 'post',
    data
  })
}
