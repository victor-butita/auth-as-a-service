<template>
  <div class="settings-view">
    <div class="view-header">
      <div>
        <h1>Settings</h1>
        <p class="subtext">Manage your dashboard preferences and configuration.</p>
      </div>
    </div>

    <div class="settings-grid">
      <!-- General Settings -->
      <div class="glass-card settings-section">
        <div class="section-header">
          <Settings :size="20" />
          <h3>General</h3>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Dashboard Theme</label>
            <p>Choose your preferred color scheme</p>
          </div>
          <select class="glass-input-compact" v-model="settings.theme" @change="saveSettings">
            <option value="dark">Dark</option>
            <option value="light">Light</option>
            <option value="auto">Auto</option>
          </select>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Language</label>
            <p>Select your preferred language</p>
          </div>
          <select class="glass-input-compact" v-model="settings.language" @change="saveSettings">
            <option value="en">English</option>
            <option value="es">Spanish</option>
            <option value="fr">French</option>
          </select>
        </div>
      </div>

      <!-- Security Settings -->
      <div class="glass-card settings-section">
        <div class="section-header">
          <Shield :size="20" />
          <h3>Security</h3>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Two-Factor Authentication</label>
            <p>{{ settings.mfaEnabled ? 'Enabled' : 'Not configured' }}</p>
          </div>
          <button class="btn-secondary" @click="toggleMFA">
            {{ settings.mfaEnabled ? 'Disable' : 'Enable' }}
          </button>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Session Timeout</label>
            <p>Automatically log out after inactivity</p>
          </div>
          <select class="glass-input-compact" v-model="settings.sessionTimeout" @change="saveSettings">
            <option value="15">15 minutes</option>
            <option value="30">30 minutes</option>
            <option value="60">1 hour</option>
            <option value="0">Never</option>
          </select>
        </div>
      </div>

      <!-- Notifications -->
      <div class="glass-card settings-section">
        <div class="section-header">
          <Bell :size="20" />
          <h3>Notifications</h3>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Email Notifications</label>
            <p>Receive updates about your applications</p>
          </div>
          <label class="toggle">
            <input type="checkbox" v-model="settings.emailNotifications" @change="saveSettings" />
            <span class="slider"></span>
          </label>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Security Alerts</label>
            <p>Get notified about suspicious activity</p>
          </div>
          <label class="toggle">
            <input type="checkbox" v-model="settings.securityAlerts" @change="saveSettings" />
            <span class="slider"></span>
          </label>
        </div>
      </div>

      <!-- API Configuration -->
      <div class="glass-card settings-section">
        <div class="section-header">
          <Code :size="20" />
          <h3>API Configuration</h3>
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>API Base URL</label>
            <p>Default endpoint for API requests</p>
          </div>
          <input 
            type="text" 
            class="glass-input-compact" 
            v-model="settings.apiBaseUrl" 
            @blur="saveSettings"
          />
        </div>
        <div class="setting-item">
          <div class="setting-info">
            <label>Rate Limiting</label>
            <p>Maximum requests per minute</p>
          </div>
          <input 
            type="number" 
            class="glass-input-compact" 
            v-model.number="settings.rateLimit" 
            @blur="saveSettings"
            min="1"
            max="1000"
          />
        </div>
      </div>
    </div>

    <!-- Save Indicator -->
    <div v-if="showSaveIndicator" class="save-indicator">
      <Check :size="16" />
      Settings saved
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Settings, Shield, Bell, Code, Check } from 'lucide-vue-next'
import { useToastStore } from '../../stores/toast'

const toastStore = useToastStore()
const showSaveIndicator = ref(false)

interface SettingsState {
  theme: string
  language: string
  mfaEnabled: boolean
  sessionTimeout: string
  emailNotifications: boolean
  securityAlerts: boolean
  apiBaseUrl: string
  rateLimit: number
}

const settings = reactive<SettingsState>({
  theme: 'dark',
  language: 'en',
  mfaEnabled: false,
  sessionTimeout: '30',
  emailNotifications: true,
  securityAlerts: true,
  apiBaseUrl: 'http://localhost:8080/api/v1',
  rateLimit: 100
})

const loadSettings = () => {
  const saved = localStorage.getItem('dashboard-settings')
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      Object.assign(settings, parsed)
    } catch (e) {
      console.error('Failed to load settings:', e)
    }
  }
}

const saveSettings = () => {
  localStorage.setItem('dashboard-settings', JSON.stringify(settings))
  showSaveIndicator.value = true
  setTimeout(() => {
    showSaveIndicator.value = false
  }, 2000)
  
  // Apply theme if changed
  if (settings.theme === 'light') {
    document.documentElement.style.setProperty('--bg-primary', '#ffffff')
    document.documentElement.style.setProperty('--text-main', '#000000')
  } else {
    document.documentElement.style.setProperty('--bg-primary', '#0a0a0f')
    document.documentElement.style.setProperty('--text-main', '#ffffff')
  }
}

const toggleMFA = () => {
  settings.mfaEnabled = !settings.mfaEnabled
  saveSettings()
  
  if (settings.mfaEnabled) {
    toastStore.success('Two-Factor Authentication enabled')
  } else {
    toastStore.info('Two-Factor Authentication disabled')
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings-view {
  animation: fadeIn 0.5s ease-out;
}

.view-header {
  margin-bottom: 32px;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #fff 0%, #a5b4fc 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtext {
  color: var(--text-muted);
  font-size: 16px;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.settings-section {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #818cf8;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.setting-info {
  flex: 1;
}

.setting-info label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: white;
  margin-bottom: 4px;
}

.setting-info p {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.glass-input-compact {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  min-width: 150px;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

/* Toggle Switch */
.toggle {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.1);
  transition: 0.3s;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--primary);
  border-color: var(--primary);
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.save-indicator {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #34d399;
  padding: 12px 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  animation: slideIn 0.3s ease-out;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(12px);
  z-index: 1000;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
