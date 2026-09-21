import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, wardrobeApi } from '@/api'

const TOKEN_KEY = 'wardrobe_token'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref(null)
  const wardrobes = ref([])
  const currentWardrobeId = ref(localStorage.getItem('wardrobe_current') || '')

  function setToken(t) {
    token.value = t
    if (t) localStorage.setItem(TOKEN_KEY, t)
    else localStorage.removeItem(TOKEN_KEY)
  }

  async function login(username, password) {
    const res = await authApi.login(username, password)
    setToken(res.token)
    userInfo.value = res.user
    await loadWardrobes()
  }

  async function register(username, password, nickname) {
    const res = await authApi.register(username, password, nickname)
    setToken(res.token)
    userInfo.value = res.user
    await loadWardrobes()
  }

  async function fetchUserInfo() {
    if (!token.value) return
    userInfo.value = await authApi.info()
  }

  async function loadWardrobes() {
    const list = await wardrobeApi.list()
    wardrobes.value = list
    if (!currentWardrobeId.value && list.length) {
      currentWardrobeId.value = String(list[0].wardrobe.id)
      localStorage.setItem('wardrobe_current', currentWardrobeId.value)
    }
  }

  function setCurrentWardrobe(id) {
    currentWardrobeId.value = String(id)
    localStorage.setItem('wardrobe_current', currentWardrobeId.value)
  }

  function currentWardrobe() {
    return wardrobes.value.find(w => String(w.wardrobe.id) === String(currentWardrobeId.value))
  }

  function logout() {
    setToken('')
    userInfo.value = null
    wardrobes.value = []
    currentWardrobeId.value = ''
    localStorage.removeItem('wardrobe_current')
  }

  return {
    token, userInfo, wardrobes, currentWardrobeId,
    setToken, login, register, fetchUserInfo, loadWardrobes, setCurrentWardrobe, currentWardrobe, logout
  }
})
