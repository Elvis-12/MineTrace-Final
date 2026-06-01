import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface User {
  id: string;
  fullName: string;
  email: string;
  role: string;
  organizationName: string;
}

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  hasHydrated: boolean;
  setHasHydrated: (v: boolean) => void;
  login: (token: string, user: User) => void;
  logout: () => void;
  hydrate: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAuthenticated: false,
      hasHydrated: false,
      setHasHydrated: (v) => set({ hasHydrated: v }),
      login: (token, user) => set({ token, user, isAuthenticated: true }),
      logout: () => {
        set({ token: null, user: null, isAuthenticated: false });
        localStorage.removeItem('auth-storage');
      },
      hydrate: () => {
        const state = useAuthStore.getState();
        if (state.token) {
          try {
            const payload = JSON.parse(atob(state.token.split('.')[1]));
            if (payload.exp && payload.exp * 1000 < Date.now()) {
              state.logout();
            }
          } catch (e) {
            // ignore
          }
        }
      },
    }),
    {
      name: 'auth-storage',
      onRehydrateStorage: () => (state) => {
        state?.setHasHydrated(true);
      },
    }
  )
);
