<template>
  <div class="stat-card glass-card">
    <div class="header">
      <span class="label">{{ label }}</span>
      <div class="icon-wrapper">
        <component :is="icon" :size="16" />
      </div>
    </div>
    <div class="value-row">
      <span class="value">{{ value }}</span>
      <span v-if="trend" :class="['trend', trend > 0 ? 'up' : 'down']">
        {{ trend > 0 ? '+' : '' }}{{ trend }}%
      </span>
    </div>
    <div class="footer">
      <span class="subtext">Last 30 days</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

defineProps<{
  label: string
  value: string | number
  trend?: number
  icon: Component
}>()
</script>

<style scoped>
.stat-card {
  padding: 24px;
  flex: 1;
  min-width: 240px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.label {
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 500;
}

.icon-wrapper {
  background: rgba(255, 255, 255, 0.05);
  padding: 8px;
  border-radius: 8px;
  color: var(--text-muted);
}

.value-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.value {
  font-size: 28px;
  font-weight: 700;
}

.trend {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 6px;
}

.trend.up {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.trend.down {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.footer {
  margin-top: 16px;
  font-size: 12px;
  color: var(--text-muted);
}
</style>
