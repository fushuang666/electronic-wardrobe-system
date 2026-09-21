<template>
  <div class="page-container">
    <div class="toolbar">
      <span style="font-weight:600;">场景提醒</span>
      <el-button type="primary" @click="generate"><el-icon><refresh/></el-icon>生成提醒</el-button>
    </div>

    <el-empty v-if="list.length === 0" description="暂无提醒，点击「生成提醒」分析衣柜" />

    <el-card v-for="r in list" :key="r.id" style="margin-bottom:12px;">
      <div style="display:flex;align-items:center;justify-content:space-between;">
        <div>
          <el-tag :type="typeColor(r.type)" style="margin-right:8px;">{{ typeText(r.type) }}</el-tag>
          <span style="font-weight:600;">{{ r.title }}</span>
          <el-tag v-if="r.status === 'UNREAD'" size="small" type="danger" style="margin-left:8px;">未读</el-tag>
        </div>
        <div style="color:#909399;font-size:12px;">{{ formatTime(r.createTime) }}</div>
      </div>
      <div style="margin-top:8px;color:#606266;">{{ r.content }}</div>
      <div style="margin-top:8px;" v-if="r.status === 'UNREAD'">
        <el-button size="small" @click="read(r)">标记已读</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { reminderApi } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const list = ref([])

function wid() { return userStore.currentWardrobeId }

function typeText(t) {
  return { IDLE: '闲置', WEATHER: '天气', OUTFIT: '搭配', IMBALANCE: '不均衡' }[t] || t
}
function typeColor(t) {
  return { IDLE: 'warning', WEATHER: 'info', OUTFIT: 'success', IMBALANCE: 'danger' }[t] || ''
}
function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 19) : ''
}

async function load() {
  if (!wid()) return
  const data = await reminderApi.list(wid(), { page: 0, size: 100 })
  list.value = data.content
}

async function generate() {
  await reminderApi.generate(wid())
  ElMessage.success('已生成提醒')
  load()
}

async function read(r) {
  await reminderApi.read(wid(), r.id)
  ElMessage.success('已标记已读')
  load()
}

onMounted(() => { if (userStore.currentWardrobeId) load() })
</script>
