import request from '@/utils/request'

export function functionList(query) {
  return request({
    url: '/api/function/list',
    method: 'get',
    params: query
  })
}

export function functionAdd(data) {
  return request({
    url: '/api/function/add',
    method: 'post',
    data
  })
}

export function functionDelete(data) {
  return request({
    url: '/api/function/delete',
    method: 'post',
    data
  })
}

export function functionUpdate(data) {
  return request({
    url: '/api/function/update',
    method: 'post',
    data
  })
}
