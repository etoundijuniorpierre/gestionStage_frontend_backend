import { create } from 'zustand';
import type { RegistrationFormData } from '../types/registration';

interface RegistrationState {
  step: number;
  formData: RegistrationFormData;
  showSuccess: boolean;
  setStep: (step: number) => void;
  setFormData: (data: Partial<RegistrationFormData> | ((prev: RegistrationFormData) => RegistrationFormData)) => void;
  setShowSuccess: (show: boolean) => void;
  reset: () => void;
}

export const useRegistrationStore = create<RegistrationState>((set) => ({
  step: 1,
  formData: {},
  showSuccess: false,
  setStep: (step) => set({ step }),
  setFormData: (data) => set((state) => ({
    formData: typeof data === 'function' ? data(state.formData) : { ...state.formData, ...data }
  })),
  setShowSuccess: (show) => set({ showSuccess: show }),
  reset: () => set({ step: 1, formData: {}, showSuccess: false }),
}));
