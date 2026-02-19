// =====================================================================
// Constantes centralisées pour les enums / valeurs de sélection
// Utiliser ces constantes partout pour éviter les chaînes codées en dur
// =====================================================================

// --- Statut d'une offre ---
export const OfferStatus = {
  ALL: 'ALL',
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
} as const;
export type OfferStatusType = (typeof OfferStatus)[keyof typeof OfferStatus];
export type OfferStatusFilter = OfferStatusType;

export const OFFER_STATUS_LABELS: Record<OfferStatusType, string> = {
  [OfferStatus.ALL]: 'Toutes',
  [OfferStatus.PENDING]: 'En attente',
  [OfferStatus.APPROVED]: 'Approuvées',
  [OfferStatus.REJECTED]: 'Refusées',
};

/** Statuts d'offre disponibles sans le filtre "ALL" */
export const OFFER_STATUSES = [
  OfferStatus.PENDING,
  OfferStatus.APPROVED,
  OfferStatus.REJECTED,
] as const;

// --- Type de stage ---
export const InternshipType = {
  INITIATION: 'Initiation',
  PERFECTIONNEMENT: 'Perfectionnement',
  PRE_EMPLOI: 'Pré-emploi',
} as const;
export type InternshipTypeValue = (typeof InternshipType)[keyof typeof InternshipType];

export const INTERNSHIP_TYPES: InternshipTypeValue[] = [
  InternshipType.INITIATION,
  InternshipType.PERFECTIONNEMENT,
  InternshipType.PRE_EMPLOI,
];

// --- Filtre de localisation ---
export const LocationFilter = {
  ALL: 'ALL',
  REMOTE: 'REMOTE',
  ONSITE: 'ONSITE',
} as const;
export type LocationFilterType = (typeof LocationFilter)[keyof typeof LocationFilter];

export const LOCATION_FILTER_OPTIONS: { key: LocationFilterType; label: string }[] = [
  { key: LocationFilter.ALL, label: 'Toutes' },
  { key: LocationFilter.REMOTE, label: 'En remote' },
  { key: LocationFilter.ONSITE, label: 'Sur site' },
];

// --- Filtre de paiement ---
export const PayingFilter = {
  ALL: 'ALL',
  PAYING: 'PAYING',
  NON_PAYING: 'NON_PAYING',
} as const;
export type PayingFilterType = (typeof PayingFilter)[keyof typeof PayingFilter];

export const PAYING_FILTER_OPTIONS: { key: PayingFilterType; label: string }[] = [
  { key: PayingFilter.ALL, label: 'Toutes' },
  { key: PayingFilter.NON_PAYING, label: 'Non payant' },
  { key: PayingFilter.PAYING, label: 'Payant' },
];

// --- Filtre type de stage (avec ALL) ---
export const InternshipTypeFilter = {
  ALL: 'ALL',
  INITIATION: InternshipType.INITIATION,
  PERFECTIONNEMENT: InternshipType.PERFECTIONNEMENT,
  PRE_EMPLOI: InternshipType.PRE_EMPLOI,
} as const;
export type InternshipTypeFilterType = (typeof InternshipTypeFilter)[keyof typeof InternshipTypeFilter];

export const INTERNSHIP_TYPE_FILTER_OPTIONS: { key: InternshipTypeFilterType; label: string }[] = [
  { key: InternshipTypeFilter.ALL, label: 'Tous' },
  { key: InternshipTypeFilter.INITIATION, label: 'Initiation' },
  { key: InternshipTypeFilter.PERFECTIONNEMENT, label: 'Perfectionnement' },
  { key: InternshipTypeFilter.PRE_EMPLOI, label: 'Pré-emploi' },
];

// --- Statut de candidature ---
export const ApplicationState = {
  PENDING: 'PENDING',
  ACCEPTED: 'ACCEPTED',
  REJECTED: 'REJECTED',
  APPROVED: 'APPROVED',
} as const;
export type ApplicationStateType = (typeof ApplicationState)[keyof typeof ApplicationState];

export const APPLICATION_STATE_LABELS: Record<string, string> = {
  [ApplicationState.PENDING]: 'En attente',
  [ApplicationState.ACCEPTED]: 'Acceptée',
  [ApplicationState.APPROVED]: 'Approuvée',
  [ApplicationState.REJECTED]: 'Refusée',
};

// --- Statut de convention ---
export const ConventionState = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
} as const;
export type ConventionStateType = (typeof ConventionState)[keyof typeof ConventionState];

// --- Domaines de stage ---
export const INTERNSHIP_DOMAINS = [
  'Informatique',
  'Génie logiciel',
  'Intelligence artificielle',
  'Cybersécurité',
  'Réseaux et télécommunications',
  'Finance',
  'Comptabilité',
  'Marketing',
  'Ressources humaines',
  'Droit',
  'Médecine',
  'Pharmacie',
  'Architecture',
  'Design',
  'Journalisme',
  'Éducation',
  'Sciences politiques',
  'Sciences environnementales',
] as const;
export type InternshipDomain = (typeof INTERNSHIP_DOMAINS)[number];
