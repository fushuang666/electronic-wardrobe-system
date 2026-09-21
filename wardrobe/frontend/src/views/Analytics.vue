<template>
  <div class="page-container">
    <el-row :gutter="16" style="margin-bottom:16px;">
      <el-col :span="6"><el-card><div style="color:#909399;">衣物总数</div><div style="font-size:28px;font-weight:600;">{{ stats.total || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card><div style="color:#909399;">闲置衣物</div><div style="font-size:28px;font-weight:600;color:#e6a23c;">{{ stats.idleCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card><div style="color:#909399;">闲置率</div><div style="font-size:28px;font-weight:600;color:#e6a23c;">{{ stats.idleRate || 0 }}%</div></el-card></el-col>
      <el-col :span="6"><el-card><div style="color:#909399;">品类数</div><div style="font-size:28px;font-weight:600;">{{ Object.keys(stats.byCategory || {}).length }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12"><el-card><div style="font-weight:600;margin-bottom:8px;">品类分布</div><div ref="catChart" style="height:300px;"></div></el-card></el-col>
      <el-col :span="12"><el-card><div style="font-weight:600;margin-bottom:8px;">季节分布</div><div ref="seasonChart" style="height:300px;"></div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px;">
      <el-col :span="12"><el-card><div style="font-weight:600;margin-bottom:8px;">颜色分布</div><div ref="colorChart" style="height:300px;"></div></el-card></el-col>
      <el-col :span="12"><el-card><div style="font-weight:600;margin-bottom:8px;">最常穿着 Top5</div><div ref="topChart" style="height:300px;"></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/store/user'
import { analyticsApi } from '@/api'

const userStore = useUserStore()
const stats = ref({})
const catChart = ref()
const seasonChart = ref()
const colorChart = ref()
const topChart = ref()
let charts = []

function wid() { return userStore.currentWardrobeId }

function toPieData(map) {
  return Object.keys(map || {}).map(k => ({ name: k, value: map[k] }))
}

function renderCharts() {
  charts.forEach(c => c && c.dispose())
  charts = []

  const c1 = echarts.init(catChart.value)
  c1.setOption({ tooltip: {}, legend: { bottom: 0 }, series: [{ type: 'pie', radius: '55%', data: toPieData(stats.value.byCategory) }] })
  charts.push(c1)

  const c2 = echarts.init(seasonChart.value)
  c2.setOption({ tooltip: {}, xAxis: { type: 'category', data: Object.keys(stats.value.bySeason || {}) }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: Object.values(stats.value.bySeason || {}) }] })
  charts.push(c2)

  const c3 = echarts.init(colorChart.value)
  c3.setOption({ tooltip: {}, legend: { bottom: 0 }, series: [{ type: 'pie', radius: '55%', data: toPieData(stats.value.byColor) }] })
  charts.push(c3)

  const top = stats.value.topWorn || []
  const c4 = echarts.init(topChart.value)
  c4.setOption({ tooltip: {}, xAxis: { type: 'category', data: top.map(t => t.name), axisLabel: { interval: 0, rotate: 20 } }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: top.map(t => t.wearCount) }] })
  charts.push(c4)
}

async function load() {
  if (!wid()) return
  stats.value = await analyticsApi.stats(wid())
  renderCharts()
}

watch(() => userStore.currentWardrobeId, () => load())

onMounted(() => {
  if (userStore.currentWardrobeId) load()
  window.addEventListener('resize', () => charts.forEach(c => c && c.resize()))
})
onBeforeUnmount(() => charts.forEach(c => c && c.dispose()))
</script>
