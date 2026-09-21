<template>
  <div class="page-container">
    <el-card style="max-width:480px;">
      <div style="font-weight:600;margin-bottom:16px;">个人资料</div>
      <el-form label-width="80px">
        <el-form-item label="用户名"><el-input :model-value="userInfo.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="头像">
          <img v-if="form.avatar" :src="form.avatar" style="width:64px;height:64px;border-radius:50%;object-fit:cover;" />
          <el-button size="small" style="margin-left:12px;" @click="pickAvatar">上传头像</el-button>
        </el-form-item>
        <el-form-item label="注册时间"><el-input :model-value="formatTime(userInfo.createTime)" disabled /></el-form-item>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </el-form>
    </el-card>
    <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatar" />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { authApi, fileApi } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})
const saving = ref(false)
const avatarInput = ref()
const form = reactive({ nickname: '', avatar: '' })

if (userStore.userInfo) {
  form.nickname = userStore.userInfo.nickname || ''
  form.avatar = userStore.userInfo.avatar || ''
}

function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '' }

function pickAvatar() { avatarInput.value.click() }
async function onAvatar(e) {
  const file = e.target.files[0]
  if (!file) return
  e.target.value = ''
  const res = await fileApi.upload(file)
  form.avatar = res.url
}

async function save() {
  saving.value = true
  try {
    await authApi.profile(form.nickname, form.avatar)
    await userStore.fetchUserInfo()
    ElMessage.success('已保存')
  } catch (e) {}
  finally { saving.value = false }
}
</script>
