<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="icon-main"><Document /></el-icon>
          <div class="header-text">
            <h1 class="title">S2S Engine</h1>
            <p class="subtitle">SQL to SpringBoot · Strategy Architecture V3.0</p>
          </div>
        </div>
      </div>
    </el-header>

    <el-main class="app-main">
      <!-- 创建芯片连接效果的布局结构 -->
      <div class="chip-container">
        <!-- 左侧连接线 -->
        <div class="connection-left">
          <div class="circuit-line"></div>
          <div class="circuit-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>

        <!-- 中间芯片核心 -->
        <div class="chip-core">
          <div class="chip-pins chip-pins-left">
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
          </div>

          <div class="content-card">
            <router-view />
          </div>

          <div class="chip-pins chip-pins-right">
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
            <span class="pin"></span>
          </div>
        </div>

        <!-- 右侧连接线 -->
        <div class="connection-right">
          <div class="circuit-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
          <div class="circuit-line"></div>
        </div>
      </div>
    </el-main>

    <el-footer class="app-footer">
      <div class="footer-divider"></div>
      <p class="footer-text">© 2025 S2S Code Generator · Strategy Architecture by <span class="highlight">MING</span></p>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { Document } from '@element-plus/icons-vue'
</script>

<style scoped>
/* 主容器 - 优化的渐变背景 */
.app-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 40%, #48c6ef 75%, #2af598 100%);
  background-size: 400% 400%;
  animation: gradientFlow 20s ease infinite;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  position: relative;
}

@keyframes gradientFlow {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* Header - 极简专业风格 */
.app-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-bottom: 1px solid rgba(30, 60, 114, 0.08);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  padding: 0;
  height: auto !important;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 18px 40px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* 标题样式 - 清晰专业 */
.title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #1e3c72;
  letter-spacing: -0.3px;
  line-height: 1.2;
}

.subtitle {
  margin: 0;
  font-size: 11px;
  color: #6b7785;
  letter-spacing: 0.8px;
  font-weight: 500;
  line-height: 1.2;
}

/* 图标样式 - 精致动效 */
.icon-main {
  color: #2af598;
  font-size: 32px;
  animation: iconPulse 4s ease-in-out infinite;
  filter: drop-shadow(0 2px 6px rgba(42, 245, 152, 0.3));
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 2px 6px rgba(42, 245, 152, 0.3));
  }
  50% {
    transform: scale(1.05);
    filter: drop-shadow(0 4px 12px rgba(42, 245, 152, 0.5));
  }
}

/* 主内容区 */
.app-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 40px;
  position: relative;
  z-index: 10;
}

/* 芯片连接容器布局 */
.chip-container {
  display: flex;
  align-items: center;
  gap: 0;
  position: relative;
}

/* 左右连接线效果 */
.connection-left,
.connection-right {
  flex: 1;
  display: flex;
  align-items: center;
  position: relative;
  min-width: 80px;
}

.connection-left {
  justify-content: flex-end;
  padding-right: 0;
}

.connection-right {
  justify-content: flex-start;
  padding-left: 0;
}

.circuit-line {
  height: 3px;
  flex: 1;
  background: linear-gradient(90deg,
  rgba(42, 245, 152, 0.3),
  rgba(42, 245, 152, 0.6),
  rgba(42, 245, 152, 0.3)
  );
  position: relative;
  box-shadow: 0 0 10px rgba(42, 245, 152, 0.5);
  animation: dataFlow 2s linear infinite;
}

@keyframes dataFlow {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.circuit-dots {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2af598, #48c6ef);
  box-shadow: 0 0 12px rgba(42, 245, 152, 0.8);
  animation: dotPulse 2s ease-in-out infinite;
}

.dot:nth-child(2) {
  animation-delay: 0.3s;
}

.dot:nth-child(3) {
  animation-delay: 0.6s;
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 12px rgba(42, 245, 152, 0.8);
  }
  50% {
    transform: scale(1.3);
    box-shadow: 0 0 20px rgba(42, 245, 152, 1);
  }
}

/* 芯片核心容器 */
.chip-core {
  display: flex;
  align-items: center;
  position: relative;
  flex-shrink: 0;
}

/* 芯片引脚效果 */
.chip-pins {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 4px;
  position: relative;
  z-index: 5;
}

.chip-pins-left {
  margin-right: -1px;
}

.chip-pins-right {
  margin-left: -1px;
}

.pin {
  width: 20px;
  height: 4px;
  background: linear-gradient(90deg, #1e3c72, #2af598, #1e3c72);
  background-size: 200% 100%;
  box-shadow: 0 0 8px rgba(42, 245, 152, 0.6);
  border-radius: 1px;
  animation: pinFlow 3s linear infinite;
}

.chip-pins-right .pin {
  animation-direction: reverse;
}

.pin:nth-child(2) { animation-delay: 0.3s; }
.pin:nth-child(3) { animation-delay: 0.6s; }
.pin:nth-child(4) { animation-delay: 0.9s; }
.pin:nth-child(5) { animation-delay: 1.2s; }

@keyframes pinFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

/* 内容卡片 - 芯片主体 */
.content-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  border: 2px solid rgba(42, 245, 152, 0.3);
  box-shadow:
      0 8px 32px rgba(30, 60, 114, 0.15),
      0 2px 8px rgba(0, 0, 0, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.8),
      0 0 0 1px rgba(42, 245, 152, 0.2);
  padding: 48px;
  min-height: 65vh;
  min-width: 800px;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 10;
}

.content-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(42, 245, 152, 0.6), transparent);
}

.content-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(42, 245, 152, 0.6), transparent);
}

.content-card:hover {
  transform: translateY(-2px);
  border-color: rgba(42, 245, 152, 0.5);
  box-shadow:
      0 12px 40px rgba(30, 60, 114, 0.2),
      0 4px 12px rgba(0, 0, 0, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 1),
      0 0 0 1px rgba(42, 245, 152, 0.4),
      0 0 30px rgba(42, 245, 152, 0.3);
}

/* Footer - 简洁优雅 */
.app-footer {
  height: auto !important;
  padding: 32px 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.05), transparent);
  text-align: center;
  position: relative;
  z-index: 10;
}

.footer-divider {
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(42, 245, 152, 0.8), transparent);
  margin: 0 auto 16px;
  border-radius: 2px;
  box-shadow: 0 0 16px rgba(42, 245, 152, 0.4);
}

.footer-text {
  margin: 0;
  color: rgba(255, 255, 255, 0.95);
  font-size: 13px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.2px;
}

.highlight {
  color: #2af598;
  font-weight: 700;
  text-shadow: 0 0 12px rgba(42, 245, 152, 0.5);
  letter-spacing: 0.5px;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(42, 165, 152, 0.6), rgba(42, 245, 152, 0.8));
  border-radius: 4px;
  transition: background 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(42, 245, 152, 0.9), rgba(42, 165, 152, 0.9));
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-card {
    min-width: 600px;
  }

  .connection-left,
  .connection-right {
    min-width: 60px;
  }

  .chip-pins {
    gap: 12px;
  }

  .pin {
    width: 16px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 14px 24px;
  }

  .header-left {
    gap: 10px;
  }

  .icon-main {
    font-size: 26px;
  }

  .title {
    font-size: 18px;
  }

  .subtitle {
    font-size: 10px;
    letter-spacing: 0.6px;
  }

  .chip-container {
    flex-direction: column;
    gap: 20px;
  }

  .connection-left,
  .connection-right {
    width: 100%;
    min-width: unset;
  }

  .connection-left {
    flex-direction: column;
    padding-right: 0;
  }

  .connection-right {
    flex-direction: column;
    padding-left: 0;
  }

  .circuit-line {
    width: 3px;
    height: 40px;
  }

  .circuit-dots {
    flex-direction: row;
    padding: 8px 0;
  }

  .chip-core {
    flex-direction: column;
  }

  .chip-pins {
    flex-direction: row;
    gap: 12px;
  }

  .chip-pins-left {
    margin-right: 0;
    margin-bottom: -1px;
  }

  .chip-pins-right {
    margin-left: 0;
    margin-top: -1px;
  }

  .pin {
    width: 4px;
    height: 16px;
  }

  .content-card {
    min-width: unset;
    width: 100%;
    padding: 28px;
    border-radius: 12px;
  }

  .app-main {
    padding: 32px 24px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 12px 16px;
  }

  .content-card {
    padding: 20px;
  }
}
</style>
