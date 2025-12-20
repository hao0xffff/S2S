import request from './request'
import type { ApiResult, GenRequest, PackRequest, GeneratedCodeFiles } from './types'

/**
 * Generate SpringBoot project from SQL
 */
export function generateCode(data: GenRequest): Promise<ApiResult<GeneratedCodeFiles>> {
  return request.post<ApiResult<GeneratedCodeFiles>>('/s2s/generate', data)
}

/**
 * Pack project to ZIP file (POST)
 */
export async function packProject(data: PackRequest): Promise<Blob> {
  const response = await request.post<Blob>('/s2s/pack', data, {
    responseType: 'blob'
  })
  // Ensure we return a proper Blob
  if (response instanceof Blob) {
    return response
  }
  throw new Error('Invalid response type')
}

/**
 * Pack project to ZIP file (GET)
 */
export async function packProjectGet(projectName: string, outputDir: string): Promise<Blob> {
  const response = await request.get<Blob>('/s2s/pack', {
    params: { projectName, outputDir },
    responseType: 'blob'
  })
  // Ensure we return a proper Blob
  if (response instanceof Blob) {
    return response
  }
  throw new Error('Invalid response type')
}

/**
 * Download file helper
 */
export function downloadFile(blob: Blob | null | undefined, filename: string): void {
  // Validate blob
  if (!blob) {
    console.error('Invalid blob: blob is null or undefined')
    throw new Error('文件数据为空')
  }
  
  // Type guard: ensure it's a Blob instance
  if (!(blob instanceof Blob)) {
    console.error('Invalid blob type:', typeof blob, blob)
    throw new Error('无效的文件数据类型')
  }
  
  try {
    // Create object URL
    const url: string = URL.createObjectURL(blob)
    
    // Create download link
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.style.display = 'none'
    
    // Append to body and trigger download
    document.body.appendChild(link)
    link.click()
    
    // Clean up after a short delay
    setTimeout(() => {
      if (document.body.contains(link)) {
        document.body.removeChild(link)
      }
      URL.revokeObjectURL(url)
    }, 100)
  } catch (error) {
    console.error('Download error:', error)
    throw new Error(`文件下载失败: ${error instanceof Error ? error.message : '未知错误'}`)
  }
}

