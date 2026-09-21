<template>
  <div class="page-container">
    <div class="toolbar">
      <el-select v-model="filters.category" placeholder="品类" clearable style="width:120px" @change="load">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-select v-model="filters.season" placeholder="季节" clearable style="width:110px" @change="load">
        <el-option v-for="s in seasons" :key="s" :label="s" :value="s" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="搜索名称/品牌/备注" clearable style="width:200px" @keyup.enter="load" @clear="load" />
      <el-button type="primary" @click="load">查询</el-button>

      <el-button type="success" @click="pickPhoto"><el-icon><camera/></el-icon>拍照添加</el-button>
      <el-button type="warning" @click="pickAlbum"><el-icon><picture/></el-icon>相册添加</el-button>
      <el-button type="primary" @click="openCreate"><el-icon><plus/></el-icon>新增衣物</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="6" v-for="item in list" :key="item.id" class="clothing-card">
        <el-card :body-style="{ padding: '10px' }">
          <img v-if="item.imageUrl" :src="item.imageUrl" class="clothing-img" />
          <div v-else class="clothing-img" style="display:flex;align-items:center;justify-content:center;color:#999;">无图</div>
          <div style="margin-top:8px;">
            <div style="font-weight:600;">{{ item.name }}</div>
            <el-tag size="small" v-if="item.category">{{ item.category }}</el-tag>
            <el-tag size="small" type="success" v-if="item.season">{{ item.season }}</el-tag>
            <el-tag size="small" type="info" v-if="item.color">{{ item.color }}</el-tag>
          </div>
          <div style="color:#909399;font-size:12px;margin:6px 0;">穿着 {{ item.wearCount || 0 }} 次</div>
          <div style="display:flex;justify-content:space-between;">
            <el-button size="small" @click="wear(item)">标记穿着</el-button>
            <el-button size="small" type="primary" @click="openEdit(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(item)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-if="total > 0"
      style="margin-top:16px;justify-content:center;display:flex;"
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="page + 1"
      @current-change="onPage" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editing ? '编辑衣物' : '新增衣物'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="衣物名称" />
        </el-form-item>
        <el-form-item label="图片">
          <img v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:120px;object-fit:cover;border-radius:6px;" />
          <br/>
          <el-button size="small" @click="pickInDialog">选择图片</el-button>
        </el-form-item>
        <el-form-item label="品类">
          <el-select v-model="form.category" placeholder="选择品类" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="季节">
          <el-select v-model="form.season" placeholder="选择季节" style="width:100%">
            <el-option v-for="s in seasons" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色"><el-input v-model="form.color" /></el-form-item>
        <el-form-item label="风格"><el-input v-model="form.style" placeholder="休闲/正式/运动..." /></el-form-item>
        <el-form-item label="场合"><el-input v-model="form.occasion" /></el-form-item>
        <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
        <el-form-item label="价格"><el-input v-model="form.price" type="number" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.note" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <input ref="photoInput" type="file" accept="image/*" capture="environment" style="display:none" @change="onFilePicked" />
    <input ref="albumInput" type="file" accept="image/*" style="display:none" @change="onFilePicked" />
    <input ref="dialogInput" type="file" accept="image/*" style="display:none" @change="onDialogFile" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { clothingApi, fileApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const categories = ['上衣', '下装', '外套', '鞋帽', '配饰', '连衣裙']
const seasons = ['春', '夏', '秋', '冬', '四季']

const list = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(12)
const filters = reactive({ category: '', season: '', keyword: '' })

const dialogVisible = ref(false)
const editing = ref(false)
const saving = ref(false)
const form = reactive({ id: null, name: '', imageUrl: '', category: '', season: '', color: '', style: '', occasion: '', brand: '', price: '', note: '' })
const photoInput = ref()
const albumInput = ref()
const dialogInput = ref()

function wid() {
  return userStore.currentWardrobeId
}

async function load() {
  if (!wid()) return
  const data = await clothingApi.page(wid(), { ...filters, page: page.value, size: size.value })
  list.value = data.content
  total.value = data.totalElements
}

function onPage(p) {
  page.value = p - 1
  load()
}

function pickPhoto() { photoInput.value.click() }
function pickAlbum() { albumInput.value.click() }
function pickInDialog() { dialogInput.value.click() }

async function onFilePicked(e) {
  const file = e.target.files[0]
  if (!file) return
  e.target.value = ''
  const res = await fileApi.upload(file)
  openCreateWithImage(res.url)
}

async function onDialogFile(e) {
  const file = e.target.files[0]
  if (!file) return
  e.target.value = ''
  const res = await fileApi.upload(file)
  form.imageUrl = res.url
}

function openCreate() {
  editing.value = false
  Object.assign(form, { id: null, name: '', imageUrl: '', category: '', season: '', color: '', style: '', occasion: '', brand: '', price: '', note: '' })
  dialogVisible.value = true
}

function openCreateWithImage(url) {
  openCreate()
  form.imageUrl = url
  ElMessage.success('图片已上传，请补充衣物信息')
}

function openEdit(item) {
  editing.value = true
  Object.assign(form, {
    id: item.id, name: item.name, imageUrl: item.imageUrl, category: item.category,
    season: item.season, color: item.color, style: item.style, occasion: item.occasion,
    brand: item.brand, price: item.price, note: item.note
  })
  dialogVisible.value = true
}

async function save() {
  if (!form.name) {
    ElMessage.warning('请填写名称')
    return
  }
  saving.value = true
  const payload = { ...form }
  if (payload.price === '' ) payload.price = null
  try {
    if (editing.value) {
      await clothingApi.update(wid(), form.id, payload)
    } else {
      await clothingApi.create(wid(), payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {}
  finally { saving.value = false }
}

async function wear(item) {
  await clothingApi.wear(wid(), item.id)
  ElMessage.success('已记录穿着')
  load()
}

async function remove(item) {
  await ElMessageBox.confirm('确认删除该衣物？', '提示', { type: 'warning' })
  await clothingApi.remove(wid(), item.id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => {
  if (userStore.currentWardrobeId) load()
})
</script>
