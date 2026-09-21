<template>
  <div class="page-container">
    <div class="toolbar">
      <span style="font-weight:600;">我的搭配</span>
      <el-button type="primary" @click="openCreate"><el-icon><plus/></el-icon>新增搭配</el-button>
    </div>

    <el-empty v-if="list.length === 0" description="还没有搭配，去创建一套吧" />

    <el-row :gutter="16">
      <el-col :span="8" v-for="o in list" :key="o.id" style="margin-bottom:16px;">
        <el-card>
          <div style="font-weight:600;margin-bottom:6px;">{{ o.name }}</div>
          <div style="color:#909399;font-size:12px;margin-bottom:8px;min-height:18px;">{{ o.description || '暂无描述' }}</div>
          <div style="display:flex;gap:6px;flex-wrap:wrap;min-height:80px;">
            <img v-for="c in (detailMap[o.id] ? detailMap[o.id].items : [])" :key="c.id"
                 :src="c.imageUrl" style="width:70px;height:70px;object-fit:cover;border-radius:6px;" />
          </div>
          <div style="margin-top:10px;display:flex;justify-content:space-between;">
            <el-button size="small" type="success" @click="apply(o)">应用</el-button>
            <el-button size="small" @click="openEdit(o)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(o)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑搭配' : '新增搭配'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="包含衣物">
          <el-select v-model="form.clothingIds" multiple filterable placeholder="选择衣物" style="width:100%">
            <el-option v-for="c in clothingOptions" :key="c.id" :label="c.name + (c.category ? '（' + c.category + '）' : '')" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { outfitApi, clothingApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const list = ref([])
const detailMap = ref({})
const clothingOptions = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const saving = ref(false)
const form = reactive({ id: null, name: '', description: '', clothingIds: [] })

function wid() { return userStore.currentWardrobeId }

async function load() {
  if (!wid()) return
  const data = await outfitApi.page(wid(), { page: 0, size: 100 })
  list.value = data.content
  for (const o of list.value) {
    const d = await outfitApi.detail(wid(), o.id)
    detailMap.value[o.id] = d
  }
}

async function loadClothingOptions() {
  const data = await clothingApi.page(wid(), { page: 0, size: 500 })
  clothingOptions.value = data.content
}

function openCreate() {
  editing.value = false
  Object.assign(form, { id: null, name: '', description: '', clothingIds: [] })
  loadClothingOptions()
  dialogVisible.value = true
}

function openEdit(o) {
  editing.value = true
  const d = detailMap.value[o.id]
  form.id = o.id
  form.name = o.name
  form.description = o.description
  form.clothingIds = d ? d.items.map(i => i.id) : []
  loadClothingOptions()
  dialogVisible.value = true
}

async function save() {
  if (!form.name) { ElMessage.warning('请填写名称'); return }
  saving.value = true
  try {
    if (editing.value) {
      await outfitApi.update(wid(), form.id, { name: form.name, description: form.description }, form.clothingIds)
    } else {
      await outfitApi.create(wid(), { name: form.name, description: form.description }, form.clothingIds)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await load()
  } catch (e) {}
  finally { saving.value = false }
}

async function apply(o) {
  await outfitApi.apply(wid(), o.id)
  ElMessage.success('已应用，相关衣物穿着次数 +1')
  load()
}

async function remove(o) {
  await ElMessageBox.confirm('确认删除该搭配？', '提示', { type: 'warning' })
  await outfitApi.remove(wid(), o.id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => { if (userStore.currentWardrobeId) load() })
</script>
