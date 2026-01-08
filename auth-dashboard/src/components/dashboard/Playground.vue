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
          <label>1. Select Tenant</label>
          <select v-model="selectedTenantId" class="glass-input">
            <option disabled value="">Choose a tenant...</option>
            <option v-for="tenant in appStore.tenants" :key="tenant.id" :value="tenant.id">
              {{ tenant.name }}
            </option>
          </select>
        </div>

        <div class="section" v-if="selectedTenantId">
          <label>2. Select Application</label>
          <select v-model="appStore.selectedApplicationId" class="glass-input" :disabled="!filteredApplications.length">
            <option disabled value="">Choose an app...</option>
            <option v-for="app in filteredApplications" :key="app.id" :value="app.id">
              {{ app.name }}
            </option>
          </select>
          <p v-if="filteredApplications.length === 0" class="help-text">No applications found for this tenant.</p>
        </div>

        <div class="section">
          <label>2. Redirect URI (Must match IDP config)</label>
          <input v-model="customRedirectUri" type="text" class="glass-input" placeholder="http://localhost:5173/" />
          
          <div v-if="suggestedUris.length > 0" class="suggestions-list">
            <span class="suggestion-label">Suggestions from App Config:</span>
            <button 
              v-for="uri in suggestedUris" 
              :key="uri" 
              class="uri-suggestion-chip"
              @click="customRedirectUri = uri"
              type="button"
            >
              {{ uri }}
            </button>
          </div>
          
          <p class="help-text">Constructed: <code>{{ customRedirectUri }}</code></p>
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
          <div class="form-group">
            <label>Roles (comma separated or select below)</label>
            <input v-model="regForm.roles" type="text" placeholder="USER, ADMIN" class="glass-input" />
            <div v-if="selectedApp && selectedApp.roles && selectedApp.roles.length > 0" class="role-chips">
              <span 
                v-for="role in selectedApp.roles" 
                :key="role" 
                @click="toggleRole(role)"
                :class="{ active: regForm.roles.includes(role) }"
                class="role-chip"
              >
                {{ role }}
              </span>
            </div>
          </div>
          <div v-if="selectedApp && selectedApp.registrationFields && selectedApp.registrationFields.length > 0" class="dynamic-fields">
            <div v-for="field in selectedApp.registrationFields" :key="field" class="form-group">
              <label>{{ field.replace(/_/g, ' ') }}</label>
              <input v-model="regForm.metadata[field]" type="text" :placeholder="'Enter ' + field.replace(/_/g, ' ').toLowerCase()" class="glass-input" />
            </div>
          </div>
          <div class="auth-buttons">
            <button class="btn-primary" @click="handleRegister" :disabled="loading">
              <Key :size="18" />
              Register w/ APA Connect
            </button>
            <button 
              v-if="availableProviders.includes('google')"
              class="btn-social btn-google" 
              @click="triggerSSO('google')" 
              :disabled="loading"
            >
              <img src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" alt="Google" width="18" />
              Sign up with Google
            </button>
            <button 
              v-if="availableProviders.includes('zoho')"
              class="btn-social btn-zoho" 
              @click="triggerSSO('zoho')" 
              :disabled="loading"
            >
              <img src="https://www.vectorlogo.zone/logos/zoho/zoho-icon.svg" alt="Zoho" width="18" />
              Sign up with Zoho
            </button>
          </div>
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
            <div class="auth-buttons">
              <button class="btn-primary" @click="handleLogin" :disabled="loading">
                <Key :size="18" />
                {{ loading ? 'Logging in...' : 'Login w/ APA Connect' }}
              </button>
              <button 
                v-if="availableProviders.includes('google')"
                class="btn-social btn-google" 
                @click="triggerSSO('google')" 
                :disabled="loading"
              >
                <img src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" alt="Google" width="18" />
                Sign in with Google
              </button>
              <button 
                v-if="availableProviders.includes('zoho')"
                class="btn-social btn-zoho" 
                @click="triggerSSO('zoho')" 
                :disabled="loading"
              >
                <img src="https://www.vectorlogo.zone/logos/zoho/zoho-icon.svg" alt="Zoho" width="18" />
                Sign in with Zoho
              </button>
            </div>
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
          <div class="header-actions">
            <button class="btn-text-small" @click="fetchSystemLogs" :disabled="!appStore.currentUserId">
              <RefreshCw :size="14" :class="{ 'spin': loadingLogs }" /> Sync System Logs
            </button>
            <button class="btn-text-small" @click="clearLogs">Clear</button>
          </div>
        </div>
        <div class="logs-container" ref="logsRef">
          <div v-for="(log, i) in appStore.playgroundLogs" :key="i" :class="['log-item', log.type]">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-msg">{{ log.message }}</span>
            <pre v-if="log.payload" class="log-payload">{{ JSON.stringify(log.payload, null, 2) }}</pre>
          </div>
          <div v-if="appStore.playgroundLogs.length === 0" class="empty-logs">
             No activity yet. Try registering or logging in.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useAppStore } from '../../stores/app'
import { useToastStore } from '../../stores/toast'
import { RefreshCw } from 'lucide-vue-next'
import axios from 'axios'

declare global {
  interface Window {
    google: any;
  }
}

const appStore = useAppStore()
const toastStore = useToastStore()
const activeTab = ref('Register')
const loading = ref(false)
const loadingLogs = ref(false)
const logsRef = ref<HTMLElement | null>(null)

const availableProviders = computed(() => {
  return selectedApp.value?.identityProviders || []
})

const regForm = reactive({ email: '', password: '', roles: '', metadata: {} as Record<string, string> })
const loginForm = reactive({ email: '', password: '' })
const customRedirectUri = ref(window.location.origin + window.location.pathname)

const selectedApp = computed(() => {
  return appStore.applications.find(a => a.id === appStore.selectedApplicationId)
})

const selectedTenantId = ref('')

// Watch for tenant changes to fetch applications if needed or filter them
watch(selectedTenantId, async (newId) => {
  if (newId) {
    appStore.selectedApplicationId = null
    await appStore.fetchApplications(newId)
  }
})

const filteredApplications = computed(() => {
  if (!selectedTenantId.value) return []
  return appStore.applications.filter(app => app.tenantId === selectedTenantId.value)
})

const suggestedUris = computed(() => {
  return selectedApp.value?.redirectUris || []
})

// Auto-select first URI if available when app changes
watch(() => appStore.selectedApplicationId, (newId) => {
  if (newId && suggestedUris.value.length > 0) {
    customRedirectUri.value = Array.from(suggestedUris.value)[0] as string
  }
})

const toggleRole = (role: string) => {
  const roles = regForm.roles.split(',').map(r => r.trim()).filter(r => r !== '')
  if (roles.includes(role)) {
    regForm.roles = roles.filter(r => r !== role).join(', ')
  } else {
    roles.push(role)
    regForm.roles = roles.join(', ')
  }
}
const mfaRequired = ref(false)
const mfaCode = ref('')

onMounted(async () => {
  // Load Google GSI SDK
  const script = document.createElement('script')
  script.src = 'https://accounts.google.com/gsi/client'
  script.async = true
  script.defer = true
  document.head.appendChild(script)

  // Check for Zoho Callback in URL
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  const state = urlParams.get('state')
  if (code && state === 'zoho_playground') {
    handleZohoCallback(code)
    // Clean URL
    window.history.replaceState({}, document.title, window.location.pathname)
  }
})

const addLog = (message: string, type: 'info' | 'success' | 'error' | 'system' = 'info', payload?: any) => {
  if (type === 'success') toastStore.success(message)
  if (type === 'error') toastStore.error(message)
  
  appStore.playgroundLogs.push({
    time: new Date().toLocaleTimeString(),
    message,
    type,
    payload
  })
  setTimeout(() => {
    if (logsRef.value) logsRef.value.scrollTop = logsRef.value.scrollHeight
  }, 100)
}

const clearLogs = () => appStore.playgroundLogs = []

const handleRegister = async () => {
  if (!appStore.selectedApplicationId) return addLog('Please select an application first', 'error')
  loading.value = true
  addLog(`Requesting registration for ${regForm.email}...`)
  try {
    const rolesArray = regForm.roles
      .split(',')
      .map(r => r.trim())
      .filter(r => r !== '')

    const res = await axios.post('http://localhost:8080/api/v1/auth/register', {
      applicationId: appStore.selectedApplicationId,
      email: regForm.email,
      password: regForm.password,
      roles: rolesArray.length > 0 ? rolesArray : null,
      metadata: regForm.metadata
    })
    addLog('Registration successful!', 'success', res.data)
    appStore.currentUserId = res.data.id
    setTimeout(fetchSystemLogs, 1000)
  } catch (err: any) {
    addLog(`Registration failed: ${err.response?.data?.message || err.message}`, 'error', err.response?.data)
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  if (!appStore.selectedApplicationId) return addLog('Please select an application first', 'error')
  loading.value = true
  addLog(`Attempting login for \${loginForm.email}...`)
  try {
    const res = await axios.post('http://localhost:8080/api/v1/auth/login', {
      email: loginForm.email,
      password: loginForm.password,
      applicationId: appStore.selectedApplicationId
    })
    
    if (res.data.mfaRequired) {
      mfaRequired.value = true
      addLog('Login challenge: MFA is required for this account.', 'info', res.data)
    } else {
      addLog('Login successful!', 'success', res.data)
      appStore.currentUserId = res.data.user?.id
      setTimeout(fetchSystemLogs, 1000)
    }
  } catch (err: any) {
    addLog(`Login failed: \${err.response?.data?.message || err.message}`, 'error')
  } finally {
    loading.value = false
  }
}

const triggerSSO = (provider: string) => {
  if (provider === 'google') {
    triggerGoogleSSO()
  } else if (provider === 'zoho') {
    triggerZohoSSO()
  }
}

const triggerGoogleSSO = () => {
  const providerConfig = selectedApp.value?.providerConfigs?.find((p: any) => p.providerName === 'google')
  const clientId = providerConfig?.clientId
  
  if (!clientId || clientId.startsWith('CLIENT_ID_FOR_')) {
    addLog('Google Client ID not configured. Please edit the application and provide a real Client ID.', 'error')
    return
  }

  addLog('Initializing Google Sign-In...', 'info')
  
  try {
    window.google.accounts.id.initialize({
      client_id: clientId,
      callback: handleGoogleCallback
    })
    window.google.accounts.id.prompt()
  } catch (err) {
    addLog('Failed to initialize Google SDK. Ensure you are on a valid domain.', 'error')
  }
}

const handleGoogleCallback = async (response: any) => {
  addLog('Received Credential from Google', 'success')
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/v1/auth/sso/callback', {
      applicationId: appStore.selectedApplicationId,
      provider: 'google',
      idToken: response.credential
    })
    addLog('Google SSO successful!', 'success', res.data)
    appStore.currentUserId = res.data.user?.id
    setTimeout(fetchSystemLogs, 1000)
  } catch (err: any) {
    addLog(`Google SSO Failed: ${err.response?.data?.message || err.message}`, 'error')
  } finally {
    loading.value = false
  }
}

const triggerZohoSSO = () => {
  const providerConfig = selectedApp.value?.providerConfigs?.find((p: any) => p.providerName === 'zoho')
  const clientId = providerConfig?.clientId
  
  if (!clientId || clientId.startsWith('CLIENT_ID_FOR_')) {
    addLog('Zoho Client ID not configured. Please edit the application and provide a real Client ID.', 'error')
    return
  }

  addLog('Redirecting to Zoho for authentication...', 'info')
  
  const redirectUri = customRedirectUri.value || (window.location.origin + window.location.pathname)
  const zohoUrl = `https://accounts.zoho.com/oauth/v2/auth?response_type=code&client_id=${clientId}&scope=aaaserver.profile.READ&redirect_uri=${encodeURIComponent(redirectUri)}&state=zoho_playground`
  
  window.location.href = zohoUrl
}

const handleZohoCallback = async (code: string) => {
  addLog('Received Auth Code from Zoho', 'success')
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/v1/auth/sso/callback', {
      applicationId: appStore.selectedApplicationId,
      provider: 'zoho',
      idToken: code, // Zoho uses code exchange on backend
      redirectUri: customRedirectUri.value || (window.location.origin + window.location.pathname)
    })
    addLog('Zoho SSO successful!', 'success', res.data)
    appStore.currentUserId = res.data.user?.id
    setTimeout(fetchSystemLogs, 1000)
  } catch (err: any) {
    addLog(`Zoho SSO Failed: ${err.response?.data?.message || err.message}`, 'error')
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
    appStore.currentUserId = res.data.user?.id
    setTimeout(fetchSystemLogs, 1000)
  } catch (err: any) {
    addLog(`MFA verification failed: ${err.response?.data?.message || err.message}`, 'error', err.response?.data)
  } finally {
    loading.value = false
  }
}

const fetchSystemLogs = async () => {
  if (!appStore.currentUserId) return
  loadingLogs.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/v1/analytics/events/user/${appStore.currentUserId}`)
    const systemEvents = res.data
    
    // Merge only new events to avoid duplicates
    systemEvents.forEach((event: any) => {
      const exists = appStore.playgroundLogs.find((l: any) => l.payload?.id === event.id)
      if (!exists) {
        addLog(`[SYSTEM EVENT] ${event.type}`, 'system', event)
      }
    })
  } catch (err: any) {
    addLog('Failed to fetch system logs', 'error')
  } finally {
    loadingLogs.value = false
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

.glass-input {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 10px 14px;
  color: white;
  font-size: 14px;
  width: 100%;
}

.role-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.role-chip {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  color: var(--text-muted);
  transition: all 0.2s;
}

.role-chip:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.role-chip.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
  font-weight: 700;
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

.log-item.system {
  border-left-color: #f59e0b;
  background: rgba(245, 158, 11, 0.05);
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-text-small {
  background: transparent;
  border: none;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-text-small:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.help-text {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 4px;
}

.help-text code {
  color: #818cf8;
  background: rgba(129, 140, 248, 0.1);
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.suggestion-label {
  width: 100%;
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 600;
  margin-bottom: 2px;
}

.uri-suggestion-chip {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-muted);
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.uri-suggestion-chip:hover {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
  border-color: #6366f1;
}

.social-login {
  margin-top: 16px;
}

.btn-google {
  width: 100%;
  padding: 10px;
  background: white;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-google:hover:not(:disabled) {
  background: #f8f8f8;
}

.btn-google:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.auth-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.btn-primary {
  padding: 10px;
  border-radius: 8px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-social {
  width: 100%;
  padding: 10px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-social:hover:not(:disabled) {
  background: #f8f8f8;
  border-color: #ccc;
  transform: translateY(-1px);
}

.btn-google { color: #333; }
.btn-zoho { color: #000; border-left: 4px solid #f44336; }

.btn-social:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 20px 0;
  color: var(--text-muted);
  font-size: 11px;
  letter-spacing: 1px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.divider:not(:empty)::before { margin-right: 1.5em; }
.divider:not(:empty)::after { margin-left: 1.5em; }

.text-center { text-align: center; }
.text-2xl { font-size: 1.5rem; }
.tracking-widest { letter-spacing: 0.5em; }
</style>
