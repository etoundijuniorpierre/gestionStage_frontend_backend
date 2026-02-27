import type { EnterpriseResponseDto } from './enterprise';

// TypeScript interface based on backend OfferResponseDto, EnterpriseOfferResponseDto, and OfferRequestDto

export interface OfferRequestDto {
  title: string;
  description: string;
  domain: string;
  typeOfInternship: string;
  job: string;
  requirements: string;
  startDate: string; // ISO string - sera converti en LocalDate par le backend
  endDate: string;   // ISO string - sera converti en LocalDate par le backend
  numberOfPlaces: number; // ✅ Corrigé: number au lieu de string
  paying: boolean;
  remote: boolean;
}

export interface ConventionResponseDto {
  state: string;
  hasFile: boolean;
}

export interface OfferResponseDto {
  id: number;
  title: string;
  description: string;
  domain: string;
  typeOfInternship: string;
  job: string;
  requirements: string;
  numberOfPlaces: string;
  durationOfInternship: number;
  startDate: string; // ISO string from backend
  endDate: string;   // ISO string from backend
  status: string;
  paying: boolean;
  remote: boolean;
  enterprise: EnterpriseResponseDto;
  convention?: ConventionResponseDto;
}
