import { defineStore } from 'pinia'

export interface Toast {
    id: string
    title?: string
    message: string
    type: 'success' | 'error' | 'info' | 'warning'
    duration?: number
}

export const useToastStore = defineStore('toast', {
    state: () => ({
        toasts: [] as Toast[]
    }),
    actions: {
        show(toast: Omit<Toast, 'id'>) {
            const id = Math.random().toString(36).substring(2, 9)
            const newToast = { ...toast, id, duration: toast.duration || 5000 }
            this.toasts.push(newToast)

            if (newToast.duration > 0) {
                setTimeout(() => {
                    this.remove(id)
                }, newToast.duration)
            }
        },
        success(message: string, title?: string) {
            this.show({ type: 'success', message, title })
        },
        error(message: string, title?: string) {
            this.show({ type: 'error', message, title })
        },
        info(message: string, title?: string) {
            this.show({ type: 'info', message, title })
        },
        remove(id: string) {
            this.toasts = this.toasts.filter(t => t.id !== id)
        }
    }
})
