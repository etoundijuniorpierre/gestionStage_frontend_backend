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
