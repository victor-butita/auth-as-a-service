<template>
  <div class="integration">
    <div class="header">
      <h1 class="text-gradient">Integration</h1>
      <p class="subtext">Quickly integrate AuthService into your application.</p>
    </div>

    <div class="playground-grid">
      <div class="snippets glass-card">
        <div class="snippet-header">
          <div class="tabs">
            <button 
              v-for="lang in ['Javascript', 'Curl', 'Python']" 
              :key="lang"
              :class="{ active: selectedLang === lang }"
              @click="selectedLang = lang"
            >
              {{ lang }}
            </button>
          </div>
          <button class="btn-copy">Copy Code</button>
        </div>
        <div class="code-area">
          <pre><code>{{ selectedSnippet }}</code></pre>
        </div>
      </div>

      <div class="guide glass-card">
        <h3>Quick Start Guide</h3>
        <ol class="steps">
          <li>
            <div class="step-num">1</div>
            <div class="step-content">
              <strong>Create an Application</strong>
              <p>Go to the Applications tab and create a new client to get your Client ID and Secret.</p>
            </div>
          </li>
          <li>
            <div class="step-num">2</div>
            <div class="step-content">
              <strong>Install the SDK</strong>
              <p>Use our official Npm or Maven packages to get started in seconds.</p>
            </div>
          </li>
          <li>
            <div class="step-num">3</div>
            <div class="step-content">
              <strong>Register Users</strong>
              <p>Call the registration endpoint to start onboarding your users.</p>
            </div>
          </li>
        </ol>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const selectedLang = ref('Javascript')

const snippets: Record<string, string> = {
  Javascript: `// Install: npm install @authservice/sdk

import { AuthService } from '@` + `authservice/sdk'

const auth = new AuthService({
  clientId: 'YOUR_CLIENT_ID',
  clientSecret: 'YOUR_CLIENT_SECRET'
})

// Register a user
const user = await auth.register({
  email: 'user@example.com',
  password: 'secure-password'
})`,
  Curl: `curl -X POST http://localhost:8080/api/v1/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "applicationId": "YOUR_APP_ID",
    "email": "user@example.com",
    "password": "secure-password"
  }'`,
  Python: `# Install: pip install authservice-sdk

from authservice import Client

client = Client(
    client_id="YOUR_CLIENT_ID",
    client_secret="YOUR_CLIENT_SECRET"
)

# Register user
user = client.register(
    email="user@example.com",
    password="secure-password"
)`
}

const selectedSnippet = computed(() => snippets[selectedLang.value])
</script>

<style scoped>
.integration {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.header .subtext {
  color: var(--text-muted);
}

.playground-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
}

.snippets {
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.snippet-header {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.tabs {
  display: flex;
  gap: 8px;
}

.tabs button {
  background: transparent;
  border: none;
  color: var(--text-muted);
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.tabs button.active {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
}

.btn-copy {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-muted);
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.code-area {
  padding: 24px;
  background: rgba(0, 0, 0, 0.2);
  flex: 1;
}

.code-area pre {
  margin: 0;
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #d1d5db;
}

.guide {
  padding: 24px;
}

.guide h3 {
  margin-bottom: 24px;
}

.steps {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.steps li {
  display: flex;
  gap: 16px;
}

.step-num {
  width: 28px;
  height: 28px;
  background: var(--primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.step-content strong {
  display: block;
  font-size: 14px;
  margin-bottom: 4px;
}

.step-content p {
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.5;
}
</style>
