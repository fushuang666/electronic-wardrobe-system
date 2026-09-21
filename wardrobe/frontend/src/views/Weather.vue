<template>
  <div class="page-container">
    <div class="toolbar">
      <el-input v-model="city" placeholder="输入城市，如 Beijing / 北京" style="width:240px" @keyup.enter="query" />
      <el-button type="primary" @click="query">查询天气</el-button>
      <el-button @click="savePref">设为我的默认城市</el-button>
    </div>

    <el-card v-if="weather" style="max-width:480px;">
      <div style="display:flex;align-items:center;justify-content:space-between;">
        <div>
          <div style="font-size:14px;color:#909399;">{{ weather.city }}</div>
          <div style="font-size:42px;font-weight:600;line-height:1.2;">{{ Math.round(weather.temp) }}℃</div>
          <div style="color:#606266;">{{ weather.condition }}</div>
        </div>
        <div style="text-align:right;">
          <div style="font-size:13px;color:#909399;">体感 {{ Math.round(weather.feelsLike) }}℃</div>
          <el-tag size="small" type="info" style="margin-top:8px;">来源：{{ sourceText }}</el-tag>
        </div>
      </div>
      <el-alert style="margin-top:16px;" :title="'穿衣建议'" type="success" :description="weather.suggestion" show-icon :closable="false" />
    </el-card>

    <el-alert v-else title="请输入城市查询天气" type="info" :closable="false" style="max-width:480px;" />

    <p style="color:#909399;font-size:12px;margin-top:12px;max-width:480px;">
      说明：未配置天气 API Key 时系统自动使用本地模拟数据（来源标注 mock）；在后端 application.yml 的
      wardrobe.weather.api-key 填入 OpenWeatherMap 免费 Key 后可获取真实天气。
    </p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { weatherApi } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const city = ref('')
const weather = ref(null)

const sourceText = computed(() => {
  if (!weather.value) return ''
  return weather.value.source === 'api' ? '真实 API' : '本地模拟'
})

async function query() {
  if (!city.value) { ElMessage.warning('请输入城市'); return }
  weather.value = await weatherApi.get(city.value)
}

async function savePref() {
  if (!city.value) { ElMessage.warning('请输入城市'); return }
  await weatherApi.savePreference(city.value)
  ElMessage.success('已保存默认城市')
}

onMounted(async () => {
  const pref = await weatherApi.preference()
  if (pref && pref.city) {
    city.value = pref.city
    weather.value = await weatherApi.get(pref.city)
  }
})
</script>
