import Cookies from 'js-cookie'

// const TokenKey = 'Admin-Token'

export function getToken(cname) {
  var name = cname + '='
  var ca = document.cookie.split(';')
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i]
    while (c.charAt(0) === ' ') c = c.substring(1)
    if (c.indexOf(name) !== -1) return c.substring(name.length, c.length)
  }
  return ''
}

export function setToken(cvalue) {
  var d = new Date()
  console.log('cvalue=' + cvalue)
  d.setTime(d.getTime() + (1 * 24 * 60 * 60 * 1000))
  var expires = 'expires=' + d.toUTCString()
  console.info('username' + '=' + cvalue + '; ' + expires)
  document.cookie = 'username' + '=' + cvalue + '; ' + expires
  console.info(document.cookie)
}

export function removeToken(name) {
  return Cookies.remove(name)
}

export function clearCookie(cname) {
  var d = new Date()
  console.log('cvalue=' + '')
  d.setTime(d.getTime() + (-1 * 24 * 60 * 60 * 1000))
  var expires = 'expires=' + d.toUTCString()
  console.info('username' + '=' + '' + '; ' + expires)
  document.cookie = 'username' + '=' + '' + '; ' + expires
}
