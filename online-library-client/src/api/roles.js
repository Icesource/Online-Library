import request from '@/utils/request'

export function rolesList(query) {
  return request({
    url: '/api/role/list',
    method: 'get',
    params: query
  })
}

export function roleAdd(data) {
  return request({
    url: '/api/role/add',
    method: 'post',
    data
  })
}

export function roleDelete(data) {
  return request({
    url: '/api/role/delete',
    method: 'post',
    data
  })
}

export function roleUpdate(data) {
  return request({
    url: '/api/role/update',
    method: 'post',
    data
  })
}
