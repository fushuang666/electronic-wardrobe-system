<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2 style="text-align:center;margin-bottom:24px;">电子衣橱 · 注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（至少3位）" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称（可选）" :prefix-icon="Avatar" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="submit">注册并登录</el-button>
      </el-form>
      <div style="margin-top:12px;text-align:right;">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Avatar } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const formRef = ref()
const form = reactive({ username: '', nickname: '', password: '' })
const rules = {
  username: [{ required: true, min: 3, message: '用户名至少3位', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }]
}
const loading = ref(false)
const userStore = useUserStore()
const router = useRouter()

async function submit() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.register(form.username, form.password, form.nickname)
      ElMessage.success('注册成功')
      router.replace('/')
    } catch (e) {}
    finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-wrap {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f2fe, #f5f7fa);
}
.auth-card {
  width: 360px;
}
</style>
