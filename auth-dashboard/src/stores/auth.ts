import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        tenant: JSON.parse(localStorage.getItem('tenant') || 'null'),
        loading: false,
        error: null as string | null,
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
    },
    actions: {
        async login(email: string, tenantId: string) {
            this.loading = true
            this.error = null
            try {
                // In a real app, this would be a full auth flow.
                // For the demo dashboard, we'll use a simplified 'Tenant Login' simulation
                // or just store the tenant ID to manage applications.
                this.token = 'demo-token-' + Math.random().toString(36).substr(2)
                this.tenant = { id: tenantId, email }

                localStorage.setItem('token', this.token)
                localStorage.setItem('tenant', JSON.stringify(this.tenant))
            } catch (err: any) {
                this.error = err.message
            } finally {
                this.loading = false
            }
        },
        logout() {
            this.token = null
            this.tenant = null
            localStorage.removeItem('token')
            localStorage.removeItem('tenant')
        }
    }
})
