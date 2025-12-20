<template>
  <div class="code-gen-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><DocumentAdd /></el-icon>
            代码生成配置
          </span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        label-position="left"
      >
        <!-- SQL Input -->
        <el-form-item label="SQL 语句" prop="sql">
          <el-input
            v-model="form.sql"
            type="textarea"
            :rows="12"
            placeholder="请输入 CREATE TABLE SQL 语句，支持多表（用分号分隔）&#10;&#10;示例：&#10;CREATE TABLE `user` (&#10;  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,&#10;  `username` VARCHAR(50) NOT NULL COMMENT '用户名',&#10;  `email` VARCHAR(100) COMMENT '邮箱'&#10;) COMMENT='用户表';"
            class="sql-input"
          />
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>支持 MySQL 和 PostgreSQL 语法</span>
          </div>
        </el-form-item>

        <!-- Project Info -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input
                v-model="form.projectName"
                placeholder="例如: order-service"
              />
              <div class="form-tip">支持连字符，会自动转换为类名</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="包名" prop="packageName">
              <el-input
                v-model="form.packageName"
                placeholder="例如: com.example.order"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="输出目录" prop="outputDir">
          <el-input
            v-model="form.outputDir"
            placeholder="例如: D:/S2S_Output"
          />
          <div class="form-tip">服务器端输出目录路径</div>
        </el-form-item>

        <!-- Database Type -->
        <el-form-item label="数据库类型" prop="dbType">
          <el-radio-group v-model="form.dbType">
            <el-radio label="mysql">MySQL</el-radio>
            <el-radio label="postgresql">PostgreSQL</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- Tech Stack Config -->
        <el-divider content-position="left">
          <span style="color: #409eff; font-weight: 600">技术栈配置（可选）</span>
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ORM 框架">
              <el-select v-model="form.techStack.ormFramework" placeholder="选择 ORM 框架">
                <el-option label="MyBatis" value="mybatis" />
                <el-option label="MyBatis-Plus" value="mybatis-plus" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="构建工具">
              <el-select v-model="form.techStack.buildTool" placeholder="选择构建工具">
                <el-option label="Maven" value="maven" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Java 版本">
              <el-select v-model="form.techStack.javaVersion" placeholder="选择 Java 版本">
                <el-option label="Java 8" value="8" />
                <el-option label="Java 11" value="11" />
                <el-option label="Java 17" value="17" />
                <el-option label="Java 21" value="21" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Spring Boot 版本">
              <el-input
                v-model="form.techStack.springBootVersion"
                placeholder="例如: 3.1.5"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="功能选项">
          <el-checkbox v-model="form.techStack.useLombok">使用 Lombok</el-checkbox>
          <el-checkbox v-model="form.techStack.useSwagger">使用 Swagger</el-checkbox>
          <el-checkbox v-model="form.techStack.useValidation">使用 Bean Validation</el-checkbox>
        </el-form-item>

        <!-- Actions -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleGenerate"
            size="large"
          >
            <el-icon><MagicStick /></el-icon>
            生成代码
          </el-button>
          <el-button
            v-if="generatedFiles && Object.keys(generatedFiles).length > 0"
            type="success"
            @click="handlePack"
            :loading="packing"
            size="large"
          >
            <el-icon><Download /></el-icon>
            打包下载
          </el-button>
          <el-button @click="handleReset" size="large">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Code Preview -->
    <el-card
      v-if="generatedFiles && Object.keys(generatedFiles).length > 0"
      class="preview-card"
      shadow="hover"
    >
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Document /></el-icon>
            生成的代码预览
          </span>
          <el-button
            type="primary"
            size="small"
            @click="handlePack"
            :loading="packing"
          >
            <el-icon><Download /></el-icon>
            打包下载
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeFile" type="border-card" class="code-tabs">
        <el-tab-pane
          v-for="(content, filename) in generatedFiles"
          :key="filename"
          :label="filename"
          :name="filename"
        >
          <div class="code-content">
            <el-scrollbar height="500px">
              <pre><code>{{ content }}</code></pre>
            </el-scrollbar>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  DocumentAdd,
  Document,
  InfoFilled,
  MagicStick,
  Download,
  Refresh
} from '@element-plus/icons-vue'
import { generateCode, packProject, downloadFile } from '@/api/codegen'
import type { GenRequest, TechStackConfig, GeneratedCodeFiles } from '@/api/types'
import { useCodeGenStore } from '@/stores/codegen'

const formRef = ref<FormInstance>()
const loading = ref(false)
const packing = ref(false)
const activeFile = ref('')
const generatedFiles = ref<GeneratedCodeFiles>({})
const codeGenStore = useCodeGenStore()

// Form data
const form = reactive<GenRequest & { techStack: TechStackConfig }>({
  sql: '',
  projectName: '',
  packageName: '',
  outputDir: 'D:/S2S_Output',
  dbType: 'mysql',
  techStack: {
    ormFramework: 'mybatis',
    buildTool: 'maven',
    useLombok: true,
    useSwagger: false,
    useValidation: true,
    javaVersion: '17',
    springBootVersion: '3.1.5',
    databaseType: 'mysql'
  }
})

// Form validation rules
const rules: FormRules = {
  sql: [
    { required: true, message: '请输入 SQL 语句', trigger: 'blur' }
  ],
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  packageName: [
    { required: true, message: '请输入包名', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_.]*[a-z0-9]$/, message: '包名格式不正确', trigger: 'blur' }
  ],
  outputDir: [
    { required: true, message: '请输入输出目录', trigger: 'blur' }
  ]
}

// Generate code
const handleGenerate = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // Sync dbType to techStack
      form.techStack.databaseType = form.dbType

      const response = await generateCode(form)

      if (response.code === 200 && response.data) {
        generatedFiles.value = response.data
        activeFile.value = Object.keys(response.data)[0] || ''

        // Save to store
        codeGenStore.setGeneratedFiles(response.data)
        codeGenStore.setProjectInfo({
          projectName: form.projectName,
          outputDir: form.outputDir
        })

        ElMessage.success(`代码生成成功！共生成 ${Object.keys(response.data).length} 个文件`)
      }
    } catch (error: any) {
      ElMessage.error(error.message || '代码生成失败')
    } finally {
      loading.value = false
    }
  })
}

// Pack and download
const handlePack = async () => {
  if (!form.projectName || !form.outputDir) {
    ElMessage.warning('请先完成代码生成')
    return
  }

  packing.value = true
  try {
    const blob = await packProject({
      projectName: form.projectName,
      outputDir: form.outputDir
    })

    if (!blob || !(blob instanceof Blob)) {
      throw new Error('获取文件数据失败')
    }

    const filename = `${form.projectName}.zip`
    downloadFile(blob, filename)

    ElMessage.success('项目打包下载成功！')
  } catch (error: any) {
    console.error('Pack error:', error)
    ElMessage.error(error.message || '打包失败')
  } finally {
    packing.value = false
  }
}

// Handle database type change
const handleDbTypeChange = (value: string) => {
  form.techStack.databaseType = value
}

// Reset form
const handleReset = () => {
  formRef.value?.resetFields()
  generatedFiles.value = {}
  activeFile.value = ''
  codeGenStore.clear()
}
</script>

<style scoped>
.code-gen-container {
  max-width: 1200px;
  margin: 0 auto;
}

.main-card {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sql-input {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.preview-card {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
}

.code-tabs {
  border-radius: 8px;
}

.code-content {
  background: #f5f7fa;
  border-radius: 4px;
  padding: 16px;
}

.code-content pre {
  margin: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.code-content code {
  font-family: inherit;
}
</style>

