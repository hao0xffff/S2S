/**
 * API Response wrapper
 */
export interface ApiResult<T = any> {
  code: number
  message: string
  data: T
}

/**
 * Tech Stack Configuration
 */
export interface TechStackConfig {
  ormFramework?: 'mybatis' | 'mybatis-plus' | 'jpa'
  buildTool?: 'maven' | 'gradle'
  useLombok?: boolean
  useSwagger?: boolean
  useValidation?: boolean
  javaVersion?: '8' | '11' | '17' | '21'
  springBootVersion?: string
  databaseType?: 'mysql' | 'postgresql' | 'postgres'
  usePagination?: boolean
  useRedis?: boolean
}

/**
 * Code Generation Request
 */
export interface GenRequest {
  sql: string
  projectName: string
  packageName: string
  outputDir: string
  dbType?: string
  techStack?: TechStackConfig
}

/**
 * Pack Request
 */
export interface PackRequest {
  projectName: string
  outputDir: string
}

/**
 * Generated Code Files
 */
export type GeneratedCodeFiles = Record<string, string>

