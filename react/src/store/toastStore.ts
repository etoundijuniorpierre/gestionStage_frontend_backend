import { create } from 'zustand';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  id: string;
  type: ToastType;
  message: string;
  duration?: number;
}

interface ToastStore {
  toasts: Toast[];
  addToast: (toast: Omit<Toast, 'id'>) => void;
  removeToast: (id: string) => void;
  success: (message: string, duration?: number) => void;
  error: (message: string, duration?: number) => void;
  warning: (message: string, duration?: number) => void;
  info: (message: string, duration?: number) => void;
}

export const useToastStore = create<ToastStore>((set) => ({
  toasts: [],
  
  addToast: (toast) => {
    const id = Math.random().toString(36).substring(7);
    const newToast = { ...toast, id };
    
    set((state) => ({
      toasts: [...state.toasts, newToast],
    }));

    // Auto remove after duration
    setTimeout(() => {
      set((state) => ({
        toasts: state.toasts.filter((t) => t.id !== id),
      }));
    }, toast.duration || 3000);
  },

  removeToast: (id) =>
    set((state) => ({
      toasts: state.toasts.filter((toast) => toast.id !== id),
    })),

  success: (message, duration = 3000) =>
    set((state) => {
      const id = Math.random().toString(36).substring(7);
      const newToast = { id, type: 'success' as ToastType, message, duration };
      setTimeout(() => {
        set((s) => ({ toasts: s.toasts.filter((t) => t.id !== id) }));
      }, duration);
      return { toasts: [...state.toasts, newToast] };
    }),

  error: (message, duration = 4000) =>
    set((state) => {
      const id = Math.random().toString(36).substring(7);
      const newToast = { id, type: 'error' as ToastType, message, duration };
      setTimeout(() => {
        set((s) => ({ toasts: s.toasts.filter((t) => t.id !== id) }));
      }, duration);
      return { toasts: [...state.toasts, newToast] };
    }),

  warning: (message, duration = 3500) =>
    set((state) => {
      const id = Math.random().toString(36).substring(7);
      const newToast = { id, type: 'warning' as ToastType, message, duration };
      setTimeout(() => {
        set((s) => ({ toasts: s.toasts.filter((t) => t.id !== id) }));
      }, duration);
      return { toasts: [...state.toasts, newToast] };
    }),

  info: (message, duration = 3000) =>
    set((state) => {
      const id = Math.random().toString(36).substring(7);
      const newToast = { id, type: 'info' as ToastType, message, duration };
      setTimeout(() => {
        set((s) => ({ toasts: s.toasts.filter((t) => t.id !== id) }));
      }, duration);
      return { toasts: [...state.toasts, newToast] };
    }),
}));
