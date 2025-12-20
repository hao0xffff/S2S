<template>
  <div class="preview-container">
    <el-card v-if="files && Object.keys(files).length > 0" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Document /></el-icon>
            代码预览
          </span>
          <el-button type="primary" @click="handleDownload">
            <el-icon><Download /></el-icon>
            打包下载
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeFile" type="border-card">
        <el-tab-pane
          v-for="(content, filename) in files"
          :key="filename"
          :label="filename"
          :name="filename"
        >
          <el-scrollbar height="600px">
            <pre><code>{{ content }}</code></pre>
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-empty v-else description="暂无生成的代码，请先进行代码生成" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useCodeGenStore } from '@/stores/codegen'
import { packProject, downloadFile } from '@/api/codegen'
import { ElMessage } from 'element-plus'
import { Document, Download } from '@element-plus/icons-vue'

const codeGenStore = useCodeGenStore()
const activeFile = ref('')

const files = computed(() => codeGenStore.generatedFiles)
const projectInfo = computed(() => codeGenStore.projectInfo)

const handleDownload = async () => {
  if (!projectInfo.value.projectName || !projectInfo.value.outputDir) {
    ElMessage.warning('项目信息不完整')
    return
  }

  try {
    const blob = await packProject({
      projectName: projectInfo.value.projectName,
      outputDir: projectInfo.value.outputDir
    })

    if (!blob || !(blob instanceof Blob)) {
      throw new Error('获取文件数据失败')
    }

    downloadFile(blob, `${projectInfo.value.projectName}.zip`)
    ElMessage.success('下载成功！')
  } catch (error: any) {
    console.error('Download error:', error)
    ElMessage.error(error.message || '下载失败')
  }
}

// Set first file as active
if (files.value && Object.keys(files.value).length > 0) {
  activeFile.value = Object.keys(files.value)[0]
}
</script>

<style scoped>
.preview-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

pre {
  margin: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-wrap: break-word;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>

