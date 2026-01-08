<template>
  <div class="toast-container">
    <TransitionGroup name="toast">
      <div 
        v-for="toast in toastStore.toasts" 
        :key="toast.id" 
        class="toast-item glass-card"
        :class="toast.type"
      >
        <div class="toast-icon">
          <CheckCircle v-if="toast.type === 'success'" :size="20" />
          <AlertCircle v-if="toast.type === 'error'" :size="20" />
          <Info v-if="toast.type === 'info'" :size="20" />
        </div>
        <div class="toast-content">
          <h4 v-if="toast.title">{{ toast.title }}</h4>
          <p>{{ toast.message }}</p>
        </div>
        <button class="toast-close" @click="toastStore.remove(toast.id)">
          <X :size="16" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import { useToastStore } from '../../stores/toast'
import { CheckCircle, AlertCircle, Info, X } from 'lucide-vue-next'

const toastStore = useToastStore()
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: none; /* Allow clicking through container */
}

.toast-item {
  pointer-events: auto; /* Re-enable clicks on toasts */
  width: 320px;
  padding: 16px;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  transition: all 0.3s ease;
}

.toast-item.success {
  background: rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.3);
}

.toast-item.error {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
}

.toast-item.info {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
}

.toast-icon {
  margin-top: 2px;
}

.toast-item.success .toast-icon { color: #34d399; }
.toast-item.error .toast-icon { color: #f87171; }
.toast-item.info .toast-icon { color: #60a5fa; }

.toast-content {
  flex: 1;
}

.toast-content h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.toast-content p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  line-height: 1.4;
}

.toast-close {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  padding: 0;
  margin-top: 2px;
  transition: color 0.2s;
}

.toast-close:hover {
  color: white;
}

/* Animations */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(50px) scale(0.9);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(50px);
}
</style>
