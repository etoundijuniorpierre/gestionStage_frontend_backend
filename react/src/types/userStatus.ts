export interface UserStatusResponseDto {
  email: string;
  status: 'INACTIF' | 'ACTIF';
  message: string;
}
