import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()

  const hasPermission = (code: string): boolean => {
    if (authStore.permissions.includes('*:*')) return true
    return authStore.permissions.includes(code)
  }

  return { hasPermission }
}
