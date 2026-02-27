import type { StudentApplicationDto } from './student';

export interface ApplicationResponseDto {
  id: number;
  state: string;
  hasFiles: HasFilesDto;
  student: StudentApplicationDto;
  offer: ApplicationOfferDto;
  enterprise: ApplicationEnterpriseDto;
}

export interface ApplicationEnterpriseDto {
  id: number;
  name: string;
}

export interface ApplicationOfferDto {
  id: number;
  title: string;
  domain: string;
  description: string;
}

export interface HasFilesDto {
  hasCV: boolean;
  hasCoverLetter: boolean;
}