<template>
  <el-container style="height:100%">
    <el-aside width="220px" style="background:#001529;">
      <div style="color:#fff;font-size:18px;font-weight:600;text-align:center;padding:18px 0;border-bottom:1px solid #1f2d3d;">
        电子衣橱
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        style="border-right:none;">
        <el-menu-item index="/clothing"><el-icon><tshirt/></el-icon>衣物管理</el-menu-item>
        <el-menu-item index="/outfit"><el-icon><connection/></el-icon>手动搭配</el-menu-item>
        <el-menu-item index="/weather"><el-icon><sunny/></el-icon>天气联动</el-menu-item>
        <el-menu-item index="/analytics"><el-icon><data-line/></el-icon>数据分析</el-menu-item>
        <el-menu-item index="/reminder"><el-icon><bell/></el-icon>场景提醒</el-menu-item>
        <el-menu-item index="/wardrobe-manage"><el-icon><box/></el-icon>衣柜管理</el-menu-item>
        <el-menu-item index="/profile"><el-icon><user/></el-icon>个人资料</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;background:#fff;border-bottom:1px solid #ebeef5;">
        <div>
          <span style="color:#909399;font-size:13px;margin-right:8px;">当前衣柜</span>
          <el-select v-model="currentId" placeholder="请选择衣柜" style="width:220px" @change="onSwitch">
            <el-option
              v-for="w in wardrobes"
              :key="w.wardrobe.id"
              :label="w.wardrobe.name + '（' + roleText(w.role) + '）'"
              :value="String(w.wardrobe.id)" />
          </el-select>
        </div>
        <el-dropdown @command="onCommand">
          <span style="cursor:pointer;">
            <el-icon><user/></el-icon> {{ nickname }} <el-icon><arrow-down/></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main style="background:#f5f7fa;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const wardrobes = computed(() => userStore.wardrobes)
const currentId = computed({
  get: () => userStore.currentWardrobeId,
  set: (v) => userStore.setCurrentWardrobe(v)
})
const nickname = computed(() => (userStore.userInfo ? userStore.userInfo.nickname : ''))
const activeMenu = computed(() => route.path)

function roleText(role) {
  return { OWNER: '所有者', ADMIN: '管理员', MEMBER: '成员' }[role] || role
}

function onSwitch(val) {
  userStore.setCurrentWardrobe(val)
  ElMessage.success('已切换衣柜')
}

function onCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    router.replace('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
  if (userStore.token && userStore.wardrobes.length === 0) {
    await userStore.loadWardrobes()
  }
})
</script>
