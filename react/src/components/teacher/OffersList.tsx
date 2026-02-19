import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import TeacherHeader from './TeacherHeader';
import { getOffersToReviewByDepartment } from '../../api/teacherApi';
import TeacherOfferCard from './TeacherOfferCard';
import type { OfferResponseDto } from '../../types/offer';
import {
  OfferStatus,
  LocationFilter,
  PayingFilter,
  InternshipTypeFilter,
  LOCATION_FILTER_OPTIONS,
  PAYING_FILTER_OPTIONS,
  INTERNSHIP_TYPE_FILTER_OPTIONS,
  type OfferStatusFilter,
  type LocationFilterType,
  type PayingFilterType,
  type InternshipTypeFilterType,
} from '../../constants/offerConstants';

export default function OffersList() {
  const navigate = useNavigate();
  const [offers, setOffers] = useState<OfferResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter] = useState<OfferStatusFilter>(OfferStatus.ALL);
  const [locationFilter, setLocationFilter] = useState<LocationFilterType>(LocationFilter.ALL);
  const [payingFilter, setPayingFilter] = useState<PayingFilterType>(PayingFilter.ALL);
  const [typeFilter, setTypeFilter] = useState<InternshipTypeFilterType>(InternshipTypeFilter.ALL);

  useEffect(() => {
    const fetchOffers = async () => {
      try {
        setLoading(true);
        const response = await getOffersToReviewByDepartment();
        console.log('=== OFFERS RESPONSE ===');
        console.log('Response:', response);
        console.log('Data:', response.data);
        setOffers(response.data || []);
      } catch (error) {
        console.error('Erreur lors du chargement des offres:', error);
        setOffers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchOffers();

    // Recharger les offres quand on revient sur la page
    const handleFocus = () => fetchOffers();
    window.addEventListener('focus', handleFocus);

    return () => window.removeEventListener('focus', handleFocus);
  }, []);

  const filteredOffers = offers.filter(offer => {
    const matchesSearch = offer.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (offer.enterprise?.name || '').toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === OfferStatus.ALL || offer.status === statusFilter;
    const matchesLocation = locationFilter === LocationFilter.ALL ||
      (locationFilter === LocationFilter.REMOTE && offer.remote) ||
      (locationFilter === LocationFilter.ONSITE && !offer.remote);
    const matchesPaying = payingFilter === PayingFilter.ALL ||
      (payingFilter === PayingFilter.PAYING && offer.paying) ||
      (payingFilter === PayingFilter.NON_PAYING && !offer.paying);
    const matchesType = typeFilter === InternshipTypeFilter.ALL || offer.typeOfInternship === typeFilter;
    return matchesSearch && matchesStatus && matchesLocation && matchesPaying && matchesType;
  });


  return (
    <div className="min-h-screen bg-login-gradient flex flex-col gap-5">
      <TeacherHeader />
      <main className="flex flex-row items-start justify-center flex-1 px-4 pb-12 gap-8">
        {/* Sidebar de filtres */}
        <aside className="hidden md:flex flex-col items-start min-w-[210px] max-w-[260px] mr-4 rounded-xl shadow-lg px-7 py-8 gap-6">
          <div className="flex items-center gap-2 mb-4">
            <span className="text-[var(--color-neutre9)] text-base">Filter</span>
          </div>

          {/* Filtre par statut */}
          {/* <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Statut</div>
            <div className="flex flex-col gap-1">
              {(Object.values(OfferStatus) as OfferStatusFilter[]).map((key) => (
                <label key={key} className="flex items-center gap-2 text-xs text-[var(--color-neutre9)]">
                  <input
                    type="radio"
                    name="status"
                    checked={statusFilter === key}
                    onChange={() => setStatusFilter(key)}
                    className="accent-[#b79056]"
                  />
                  {OFFER_STATUS_LABELS[key]}
                </label>
              ))}
            </div>
          </div> */}

          <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Location</div>
            <div className="flex flex-col gap-1">
              {LOCATION_FILTER_OPTIONS.map(({ key, label }) => (
                <label key={key} className="flex items-center gap-2 text-xs text-[var(--color-neutre9)]">
                  <input
                    type="radio"
                    name="location"
                    checked={locationFilter === key}
                    onChange={() => setLocationFilter(key)}
                    className="accent-[#b79056]"
                  />
                  {label}
                </label>
              ))}
            </div>
          </div>

          <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Payant</div>
            <div className="flex flex-col gap-1">
              {PAYING_FILTER_OPTIONS.map(({ key, label }) => (
                <label key={key} className="flex items-center gap-2 text-xs text-[var(--color-neutre9)]">
                  <input
                    type="radio"
                    name="paying"
                    checked={payingFilter === key}
                    onChange={() => setPayingFilter(key)}
                    className="accent-[#b79056]"
                  />
                  {label}
                </label>
              ))}
            </div>
          </div>

          <div>
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Type de stage</div>
            <div className="flex flex-col gap-1">
              {INTERNSHIP_TYPE_FILTER_OPTIONS.map(({ key, label }) => (
                <label key={key} className="flex items-center gap-2 text-xs text-[var(--color-neutre9)]">
                  <input
                    type="radio"
                    name="internshipType"
                    checked={typeFilter === key}
                    onChange={() => setTypeFilter(key)}
                    className="accent-[#b79056]"
                  />
                  {label}
                </label>
              ))}
            </div>
          </div>
        </aside>

        {/* Section recherche + offres */}
        <section className="flex-1 w-full max-w-[800px] mt-8">
          <input
            type="text"
            placeholder="Saisir ici pour rechercher une offre"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            className="w-full mb-5 px-2 py-2 border-none bg-[var(--color-neutre95)] text-[var(--color-neutre2-paragraphe)] text-base text-center shadow focus:outline-none focus:ring-2 focus:ring-[#b79056] placeholder-[var(--color-neutre2-paragraphe)] rounded-lg"
            style={{ fontFamily: 'inherit', letterSpacing: '0.01em' }}
          />
          <div className="space-y-4">
            {loading ? (
              <div className="py-16 text-center text-[var(--color-jaune)] text-lg">Chargement des offres...</div>
            ) : filteredOffers.length === 0 ? (
              <div className="py-16 text-center text-[var(--color-jaune)] text-lg">Aucune offre trouvée.</div>
            ) : (
              filteredOffers.map((offer) => (
                <TeacherOfferCard
                  key={offer.id}
                  offer={offer}
                  onClick={() => navigate(`/enseignant/offres/${offer.id}`)}
                />
              ))
            )}
          </div>
        </section>
      </main>
    </div>
  );
}
