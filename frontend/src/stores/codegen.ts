import { defineStore } from 'pinia'
import type { GeneratedCodeFiles } from '@/api/types'

interface ProjectInfo {
  projectName: string
  outputDir: string
}

export const useCodeGenStore = defineStore('codegen', {
  state: () => ({
    generatedFiles: {} as GeneratedCodeFiles,
    projectInfo: {
      projectName: '',
      outputDir: ''
    } as ProjectInfo
  }),

  actions: {
    setGeneratedFiles(files: GeneratedCodeFiles) {
      this.generatedFiles = files
    },

    setProjectInfo(info: ProjectInfo) {
      this.projectInfo = info
    },

    clear() {
      this.generatedFiles = {}
      this.projectInfo = {
        projectName: '',
        outputDir: ''
      }
    }
  }
})

