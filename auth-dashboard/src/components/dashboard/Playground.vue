<template>
  <div class="playground">
    <div class="header">
      <h1 class="text-gradient">Auth Playground</h1>
      <p class="subtext">Demo the registration and login flow for your applications.</p>
    </div>

    <div class="playground-layout">
      <!-- Controls -->
      <div class="controls-panel glass-card">
        <div class="section">
          <label>1. Select Application</label>
          <select v-model="selectedAppId" class="glass-input">
            <option disabled value="">Choose an app...</option>
            <option v-for="app in appStore.applications" :key="app.id" :value="app.id">
              {{ app.name }}
            </option>
          </select>
        </div>

        <div class="section-tabs">
          <button 
            v-for="tab in ['Register', 'Login']" 
            :key="tab"
            :class="{ active: activeTab === tab }"
            @click="activeTab = tab"
          >
            {{ tab }}
          </button>
        </div>

        <!-- Register Form -->
        <div v-if="activeTab === 'Register'" class="form">
          <div class="form-group">
            <label>Email</label>
            <input v-model="regForm.email" type="email" placeholder="user@example.com" class="glass-input" />
          </div>
          <div class="form-group">
            <label>Password</label>
            <input v-model="regForm.password" type="password" placeholder="••••••••" class="glass-input" />
          </div>
          <button class="btn-primary w-full" @click="handleRegister" :disabled="loading">
            Register User
          </button>
        </div>

        <!-- Login Form -->
        <div v-if="activeTab === 'Login'" class="form">
          <div v-if="!mfaRequired" class="login-step">
            <div class="form-group">
              <label>Email</label>
              <input v-model="loginForm.email" type="email" placeholder="user@example.com" class="glass-input" />
            </div>
            <div class="form-group">
              <label>Password</label>
              <input v-model="loginForm.password" type="password" placeholder="••••••••" class="glass-input" />
            </div>
            <button class="btn-primary w-full" @click="handleLogin" :disabled="loading">
              Login
            </button>
          </div>

          <!-- MFA Step -->
          <div v-else class="mfa-step">
            <p class="mfa-notice">MFA Required! Enter your 6-digit code.</p>
            <div class="form-group">
              <label>Verification Code</label>
              <input v-model="mfaCode" type="text" maxlength="6" placeholder="000000" class="glass-input text-center text-2xl tracking-widest" />
            </div>
            <button class="btn-primary w-full" @click="handleVerifyMfa" :disabled="loading">
              Verify & Complete Login
            </button>
            <button class="btn-text w-full mt-2" @click="mfaRequired = false">Back to Login</button>
          </div>
        </div>
      </div>

      <!-- Results / Logs -->
      <div class="results-panel glass-card">
        <div class="card-header">
          <h3>Response & Logs</h3>
          <button class="btn-text" @click="clearLogs">Clear</button>
        </div>
        <div class="logs-container" ref="logsRef">
          <div v-for="(log, i) in logs" :key="i" :class="['log-item', log.type]">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-msg">{{ log.message }}</span>
            <pre v-if="log.payload" class="log-payload">{{ JSON.stringify(log.payload, null, 2) }}</pre>
          </div>
          <div v-if="logs.length === 0" class="empty-logs">
             No activity yet. Try registering or logging in.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAppStore } from '../../stores/app'
import axios from 'axios'

const appStore = useAppStore()
const selectedAppId = ref('')
const activeTab = ref('Register')
const loading = ref(false)
const logs = ref<any[]>([])
const logsRef = ref<HTMLElement | null>(null)

const regForm = reactive({ email: '', password: '' })
const loginForm = reactive({ email: '', password: '' })
const mfaRequired = ref(false)
const mfaCode = ref('')

const addLog = (message: string, type: 'info' | 'success' | 'error' = 'info', payload?: any) => {
  logs.value.push({
    time: new Date().toLocaleTimeString(),
    message,
    type,
    payload
  })
  setTimeout(() => {
    if (logsRef.value) logsRef.value.scrollTop = logsRef.value.scrollHeight
  }, 100)
}

const clearLogs = () => logs.value = []

const handleRegister = async () => {
  if (!selectedAppId.value) return addLog('Please select an application first', 'error')
  loading.value = true
  addLog(`Requesting registration for ${regForm.email}...`)
  try {
    const res = await axios.post('http://localhost:8080/api/v1/auth/register', {
      applicationId: selectedAppId.value,
      email: regForm.email,
      password: regForm.password
    })
    addLog('Registration successful!', 'success', res.data)
  } catch (err: any) {
    addLog(`Registration failed: ${err.response?.data?.message || err.message}`, 'error', err.response?.data)
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  if (!selectedAppId.value) return addLog('Please select an application first', 'error')
  loading.value = true
  addLog(`Attempting login for ${loginForm.email}...`)
  try {
    const res = await axios.post('http://localhost:8080/api/v1/auth/login', {
      email: loginForm.email,
      password: loginForm.password,
      applicationId: selectedAppId.value
    })
    
    if (res.data.mfaRequired) {
      mfaRequired.value = true
      addLog('Login challenge: MFA is required for this account.', 'info', res.data)
    } else {
      addLog('Login successful!', 'success', res.data)
      if (res.data.redirectUrl) {
        addLog(`Redirecting to application: ${res.data.redirectUrl}`, 'info')
        setTimeout(() => {
          window.location.href = res.data.redirectUrl
        }, 1500)
      }
    }
  } catch (err: any) {
    addLog(`Login failed: ${err.response?.data?.message || err.message}`, 'error', err.response?.data)
  } finally {
    loading.value = false
  }
}

const handleVerifyMfa = async () => {
  loading.value = true
  addLog(`Verifying MFA code ${mfaCode.value}...`)
  try {
    const res = await axios.post('http://localhost:8080/api/v1/mfa/verify-login', {
      email: loginForm.email,
      code: mfaCode.value
    })
    addLog('MFA verification successful!', 'success', res.data)
    mfaRequired.value = false
  } catch (err: any) {
    addLog(`MFA verification failed: ${err.response?.data?.message || err.message}`, 'error', err.response?.data)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.playground {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.playground-layout {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 24px;
  align-items: start;
}

.controls-panel {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.section-tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 4px;
}

.section-tabs button {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-muted);
  padding: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.section-tabs button.active {
  background: var(--primary);
  color: white;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-muted);
}

.glass-input {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 10px 14px;
  color: white;
  font-size: 14px;
  width: 100%;
}

.w-full { width: 100%; }
.mt-2 { margin-top: 8px; }

.results-panel {
  padding: 24px;
  height: 600px;
  display: flex;
  flex-direction: column;
}

.logs-container {
  margin-top: 16px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
}

.log-item {
  margin-bottom: 12px;
  border-left: 2px solid #6366f1;
  padding-left: 12px;
}

.log-item.success { border-left-color: #10b981; }
.log-item.error { border-left-color: #ef4444; }

.log-time {
  color: var(--text-muted);
  margin-right: 8px;
}

.log-msg {
  font-weight: 600;
}

.log-payload {
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.05);
  padding: 8px;
  border-radius: 4px;
  white-space: pre-wrap;
  color: #818cf8;
}

.empty-logs {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: var(--text-muted);
  font-style: italic;
}

.mfa-notice {
  font-size: 14px;
  color: #fbbf24;
  margin-bottom: 8px;
  text-align: center;
}

.text-center { text-align: center; }
.text-2xl { font-size: 1.5rem; }
.tracking-widest { letter-spacing: 0.5em; }
</style>
