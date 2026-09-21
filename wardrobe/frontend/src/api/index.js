import request from '@/utils/request'

export const authApi = {
  login: (username, password) => request.post('/auth/login', null, { params: { username, password } }),
  register: (username, password, nickname) => request.post('/auth/register', null, { params: { username, password, nickname } }),
  info: () => request.get('/auth/info'),
  profile: (nickname, avatar) => request.put('/auth/profile', null, { params: { nickname, avatar } })
}

export const userApi = {
  get: (id) => request.get('/users/' + id)
}

export const wardrobeApi = {
  list: () => request.get('/wardrobes'),
  create: (name, description, type) => request.post('/wardrobes', null, { params: { name, description, type } }),
  update: (id, name, description) => request.put('/wardrobes/' + id, null, { params: { name, description } }),
  remove: (id) => request.delete('/wardrobes/' + id),
  addMember: (id, username, role) => request.post('/wardrobes/' + id + '/members', null, { params: { username, role } }),
  removeMember: (id, targetUserId) => request.delete('/wardrobes/' + id + '/members/' + targetUserId),
  members: (id) => request.get('/wardrobes/' + id + '/members')
}

export const clothingApi = {
  page: (wid, params) => request.get('/wardrobes/' + wid + '/clothings', { params }),
  get: (wid, id) => request.get('/wardrobes/' + wid + '/clothings/' + id),
  create: (wid, data) => request.post('/wardrobes/' + wid + '/clothings', data),
  update: (wid, id, data) => request.put('/wardrobes/' + wid + '/clothings/' + id, data),
  remove: (wid, id) => request.delete('/wardrobes/' + wid + '/clothings/' + id),
  wear: (wid, id) => request.post('/wardrobes/' + wid + '/clothings/' + id + '/wear')
}

export const outfitApi = {
  page: (wid, params) => request.get('/wardrobes/' + wid + '/outfits', { params }),
  detail: (wid, id) => request.get('/wardrobes/' + wid + '/outfits/' + id),
  create: (wid, data, clothingIds) => request.post('/wardrobes/' + wid + '/outfits', data, { params: { clothingIds: joinIds(clothingIds) } }),
  update: (wid, id, data, clothingIds) => request.put('/wardrobes/' + wid + '/outfits/' + id, data, { params: { clothingIds: joinIds(clothingIds) } }),
  remove: (wid, id) => request.delete('/wardrobes/' + wid + '/outfits/' + id),
  apply: (wid, id) => request.post('/wardrobes/' + wid + '/outfits/' + id + '/apply')
}

function joinIds(ids) {
  if (!ids || ids.length === 0) return ''
  return ids.join(',')
}

export const weatherApi = {
  get: (city) => request.get('/weather', { params: { city } }),
  preference: () => request.get('/weather/preference'),
  savePreference: (city, cityCode) => request.post('/weather/preference', null, { params: { city, cityCode } })
}

export const analyticsApi = {
  stats: (wid) => request.get('/wardrobes/' + wid + '/analytics')
}

export const reminderApi = {
  list: (wid, params) => request.get('/wardrobes/' + wid + '/reminders', { params }),
  generate: (wid) => request.post('/wardrobes/' + wid + '/reminders/generate'),
  read: (wid, id) => request.put('/wardrobes/' + wid + '/reminders/' + id + '/read')
}

export const fileApi = {
  upload: (file) => {
    const form = new FormData()
    form.append('file', file)
    return request.post('/files/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
