<template>
  <div class="page-container">
    <div class="toolbar">
      <span style="font-weight:600;">我的衣柜</span>
      <el-button type="primary" @click="openCreate"><el-icon><plus/></el-icon>新建衣柜</el-button>
    </div>

    <el-table :data="wardrobes" border>
      <el-table-column prop="wardrobe.name" label="名称" />
      <el-table-column label="类型">
        <template #default="{ row }">{{ row.wardrobe.type === 'SHARED' ? '共享' : '个人' }}</template>
      </el-table-column>
      <el-table-column label="我的角色">
        <template #default="{ row }">{{ roleText(row.role) }}</template>
      </el-table-column>
      <el-table-column prop="wardrobe.description" label="描述" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="switchTo(row)">切换</el-button>
          <el-button size="small" v-if="canManage(row.role)" @click="manage(row)">成员</el-button>
          <el-button size="small" type="danger" v-if="row.role === 'OWNER'" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="createVisible" title="新建衣柜" width="440px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="个人" value="PERSONAL" />
            <el-option label="共享" value="SHARED" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="create">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="memberVisible" title="成员管理" width="520px">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户名"><el-input v-model="newMember.username" placeholder="被邀请用户名" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="newMember.role" style="width:120px">
            <el-option label="成员" value="MEMBER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="addMember">添加</el-button>
      </el-form>
      <el-divider />
      <el-table :data="memberRows" border>
        <el-table-column prop="username" label="用户" />
        <el-table-column label="角色">
          <template #default="{ row }">{{ roleText(row.role) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="danger" :disabled="row.role === 'OWNER'" @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { wardrobeApi, userApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const wardrobes = computed(() => userStore.wardrobes)

const createVisible = ref(false)
const saving = ref(false)
const form = reactive({ name: '', description: '', type: 'PERSONAL' })

const memberVisible = ref(false)
const memberRows = ref([])
const newMember = reactive({ username: '', role: 'MEMBER' })
let currentWid = null

function roleText(role) {
  return { OWNER: '所有者', ADMIN: '管理员', MEMBER: '成员' }[role] || role
}
function canManage(role) {
  return role === 'OWNER' || role === 'ADMIN'
}

function openCreate() {
  Object.assign(form, { name: '', description: '', type: 'PERSONAL' })
  createVisible.value = true
}

async function create() {
  if (!form.name) { ElMessage.warning('请填写名称'); return }
  saving.value = true
  try {
    await wardrobeApi.create(form.name, form.description, form.type)
    ElMessage.success('创建成功')
    createVisible.value = false
    await userStore.loadWardrobes()
  } catch (e) {}
  finally { saving.value = false }
}

function switchTo(row) {
  userStore.setCurrentWardrobe(row.wardrobe.id)
  ElMessage.success('已切换至「' + row.wardrobe.name + '」')
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除该衣柜？其下衣物/搭配将一并删除', '提示', { type: 'warning' })
  await wardrobeApi.remove(row.wardrobe.id)
  ElMessage.success('已删除')
  await userStore.loadWardrobes()
}

async function manage(row) {
  currentWid = row.wardrobe.id
  newMember.username = ''
  newMember.role = 'MEMBER'
  await loadMembers()
  memberVisible.value = true
}

async function loadMembers() {
  const list = await wardrobeApi.members(currentWid)
  memberRows.value = []
  for (const m of list) {
    let username = '用户#' + m.userId
    try {
      const u = await userApi.get(m.userId)
      username = u.nickname || u.username
    } catch (e) {}
    memberRows.value.push({ ...m, username })
  }
}

async function addMember() {
  if (!newMember.username) { ElMessage.warning('请输入用户名'); return }
  await wardrobeApi.addMember(currentWid, newMember.username, newMember.role)
  ElMessage.success('已添加成员')
  await loadMembers()
}

async function removeMember(row) {
  await wardrobeApi.removeMember(currentWid, row.userId)
  ElMessage.success('已移除')
  await loadMembers()
}
</script>
