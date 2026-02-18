import { create } from 'zustand';

export interface RegistrationFormData {
  type?: 'student' | 'enterprise' | 'teacher';
  email?: string;
  password?: string;
  confirmPassword?: string;
  name?: string;
  firstName?: string;
  sector?: string;
  languages?: string[];
  githubLink?: string;
  linkedinLink?: string;
  enterpriseName?: string;
  contact?: string;
  location?: string;
  country?: string;
  city?: string;
  sectorOfActivity?: string;
  remote?: boolean;
  paying?: boolean;
  logo?: File;
  matriculation?: string;
  department?: string;
}

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
