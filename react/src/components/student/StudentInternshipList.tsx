import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import EtudiantHeader from './StudentHeader';

import { getApprovedOffers, filterOffers } from '../../api/studentApi';
import type { OfferResponseDto } from '../../types/offer';



export default function ListStagesEtudiant() {
  const [offers, setOffers] = useState<OfferResponseDto[]>([]);
  const [filteredOffers, setFilteredOffers] = useState<OfferResponseDto[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [filterLoading, setFilterLoading] = useState(false);
  
  // États des filtres
  const [filters, setFilters] = useState({
    remote: false,
    onSite: false,
    paying: null as boolean | null,
    typeOfInternship: [] as string[],
    status: [] as string[]
  });
  
  const navigate = useNavigate();

  // Fonction pour appliquer les filtres
  const applyFilters = async () => {
    setFilterLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.remote) params.append('remote', 'true');
      if (filters.onSite) params.append('onSite', 'true');
      if (filters.paying !== null) params.append('paying', filters.paying.toString());
      
      const response = await filterOffers(filters.paying || undefined, filters.remote);
      const filteredData = response?.data as OfferResponseDto[] || [];
      
      // Filtrer par type de stage si sélectionné
      let finalFiltered = filteredData;
      if (filters.typeOfInternship.length > 0) {
        finalFiltered = finalFiltered.filter(offer => 
          filters.typeOfInternship.includes(offer.typeOfInternship || '')
        );
      }
      
      // Filtrer par statut si sélectionné
      if (filters.status.length > 0) {
        finalFiltered = finalFiltered.filter(offer => 
          filters.status.includes(offer.status || '')
        );
      }
      
      setFilteredOffers(finalFiltered);
    } catch (error) {
      console.error('Erreur lors du filtrage:', error);
      setFilteredOffers([]);
    } finally {
      setFilterLoading(false);
    }
  };

  // Fonction pour réinitialiser les filtres
  const resetFilters = () => {
    setFilters({
      remote: false,
      onSite: false,
      paying: null,
      typeOfInternship: [],
      status: []
    });
    setFilteredOffers(offers);
  };

  // Fonction pour gérer les changements de filtres
  const handleFilterChange = (filterType: string, value: boolean | string | null) => {
    setFilters(prev => {
      const newFilters = { ...prev };
      
      if (filterType === 'remote' || filterType === 'onSite') {
        newFilters[filterType] = value as boolean;
      } else if (filterType === 'paying') {
        newFilters.paying = value as boolean | null;
      } else if (filterType === 'typeOfInternship') {
        const strValue = value as string;
        if (newFilters.typeOfInternship.includes(strValue)) {
          newFilters.typeOfInternship = newFilters.typeOfInternship.filter(t => t !== strValue);
        } else {
          newFilters.typeOfInternship = [...newFilters.typeOfInternship, strValue];
        }
      } else if (filterType === 'status') {
        const strValue = value as string;
        if (newFilters.status.includes(strValue)) {
          newFilters.status = newFilters.status.filter(s => s !== strValue);
        } else {
          newFilters.status = [...newFilters.status, strValue];
        }
      }
      
      return newFilters;
    });
  };

  useEffect(() => {
    getApprovedOffers()
      .then(res => {
        const apiOffers = res?.data as OfferResponseDto[] | undefined;
        const offersData = apiOffers || [];
        setOffers(offersData);
        setFilteredOffers(offersData); // Initialiser les offres filtrées
        setLoading(false);
      }).catch((res: { response?: { data?: { message?: string } } }) => {
        console.error(res);
        setLoading(false);
      });
  }, []);

  // Appliquer les filtres quand ils changent
  useEffect(() => {
    if (offers.length > 0) {
      applyFilters();
    }
  }, [filters, offers]);

  // Recherche sur le titre ou l'entreprise
  const searchFilteredOffers = filteredOffers.filter(offer =>
    offer.title.toLowerCase().includes(search.toLowerCase()) ||
    offer.enterprise.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-login-gradient flex flex-col">
      <EtudiantHeader />
      <main className="flex flex-row items-start justify-center flex-1 px-4 pb-12 gap-8">
        {/* Sidebar de filtres */}
        <aside className="hidden md:flex flex-col items-start min-w-[210px] max-w-[260px] mt-12 mr-4 rounded-xl shadow-lg px-7 py-8 gap-6">
          <div className="flex items-center gap-2 mb-4">
            <span className="text-[var(--color-neutre9)] text-base">Filtres</span>
            <button
              onClick={resetFilters}
              className="text-xs text-[#b79056] hover:underline cursor-pointer"
              disabled={filterLoading}
            >
              Réinitialiser
            </button>
          </div>
          
          <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Location</div>
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.remote}
                  onChange={(e) => handleFilterChange('remote', e.target.checked)}
                  disabled={filterLoading}
                />
                En remote
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.onSite}
                  onChange={(e) => handleFilterChange('onSite', e.target.checked)}
                  disabled={filterLoading}
                />
                Sur site
              </label>
            </div>
          </div>
          
          <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Payant</div>
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="radio" 
                  name="payant" 
                  className="accent-[#b79056]" 
                  checked={filters.paying === false}
                  onChange={() => handleFilterChange('paying', false)}
                  disabled={filterLoading}
                />
                Non
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="radio" 
                  name="payant" 
                  className="accent-[#b79056]" 
                  checked={filters.paying === true}
                  onChange={() => handleFilterChange('paying', true)}
                  disabled={filterLoading}
                />
                Oui
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="radio" 
                  name="payant" 
                  className="accent-[#b79056]" 
                  checked={filters.paying === null}
                  onChange={() => handleFilterChange('paying', null)}
                  disabled={filterLoading}
                />
                Tous
              </label>
            </div>
          </div>
          
          <div className="mb-4">
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Type de stage</div>
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.typeOfInternship.includes('Initiation')}
                  onChange={() => handleFilterChange('typeOfInternship', 'Initiation')}
                  disabled={filterLoading}
                />
                Initiation
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.typeOfInternship.includes('Perfectionnement')}
                  onChange={() => handleFilterChange('typeOfInternship', 'Perfectionnement')}
                  disabled={filterLoading}
                />
                Perfectionnement
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.typeOfInternship.includes('Pré-emploi')}
                  onChange={() => handleFilterChange('typeOfInternship', 'Pré-emploi')}
                  disabled={filterLoading}
                />
                Pré-emploi
              </label>
            </div>
          </div>
          
          <div>
            <div className="text-xs text-[var(--color-neutre9)] font-semibold mb-2">Statut du stage</div>
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.status.includes('APPROVED')}
                  onChange={() => handleFilterChange('status', 'APPROVED')}
                  disabled={filterLoading}
                />
                Approuvé
              </label>
              <label className="flex items-center gap-2 text-xs text-[var(--color-neutre9)] cursor-pointer">
                <input 
                  type="checkbox" 
                  className="accent-[#b79056]" 
                  checked={filters.status.includes('REJECTED')}
                  onChange={() => handleFilterChange('status', 'REJECTED')}
                  disabled={filterLoading}
                />
                Rejeté
              </label>
            </div>
          </div>
          
          {filterLoading && (
            <div className="text-xs text-[var(--color-jaune)] text-center">
              Application des filtres...
            </div>
          )}
        </aside>
        {/* Section recherche + offres */}
        <section className="flex-1 w-full max-w-[800px] mt-8">
          <motion.div
            className="w-full"
            initial={{ opacity: 0, y: 40 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <div className="flex flex-col gap-3 mb-5">
              <input
                type="text"
                placeholder="Saisir ici pour rechercher un stage"
                value={search}
                onChange={e => setSearch(e.target.value)}
                className="w-full px-4 py-2 border-none bg-[var(--color-neutre95)] text-[var(--color-neutre2-paragraphe)] text-base text-center shadow focus:outline-none focus:ring-2 focus:ring-[#b79056] placeholder-[var(--color-neutre2-paragraphe)]"
                style={{ fontFamily: 'inherit', letterSpacing: '0.01em' }}
              />
                             {!loading && (
                 <div className="text-xs text-[var(--color-neutre9)] text-center">
                   {searchFilteredOffers.length} offre{searchFilteredOffers.length !== 1 ? 's' : ''} trouvée{searchFilteredOffers.length !== 1 ? 's' : ''}
                   {filters.remote || filters.onSite || filters.paying !== null || filters.typeOfInternship.length > 0 || filters.status.length > 0 ? ' avec les filtres appliqués' : ''}
                 </div>
               )}
            </div>
            <div className="flex flex-col gap-7">
              {loading ? (
                <div className="py-16 text-center text-[var(--color-jaune)] text-lg">Chargement des offres...</div>
              ) : filteredOffers.length === 0 ? (
                searchFilteredOffers.length === 0 && search ? (
                  <div className="py-16 text-center text-[var(--color-jaune)] text-lg">Aucune offre trouvée pour votre recherche.</div>
                ) : (
                  <div className="py-16 text-center text-[var(--color-jaune)] text-lg">
                    {offers.length === 0 ? 'Aucune offre de stage disponible' : 'Aucune offre ne correspond à vos critères de filtrage'}
                  </div>
                )
              ) : (
                searchFilteredOffers.map((offer) => (
                  <motion.div
                    key={offer.id}
                    className="flex flex-row items-stretch bg-[var(--color-light)] rounded-xl shadow-lg border border-[#e1d3c1] overflow-hidden hover:bg-[var(--color-light)] transition-colors cursor-pointer"
                    whileHover={{ scale: 1.01 }}
                    onClick={() => navigate(`/stage/${offer.id}`)}
                  >
                  {/* Colonne gauche : logo, entreprise, pays, ville, secteur */}
                  <div className="flex flex-col items-center justify-center w-32 min-w-[175px] bg-[var(--color-light)] border-l-[var(--color-emraude)] p-3">
                    <div className="h-12 w-12 rounded-full flex items-center justify-center bg-[var(--color-vert)] text-white font-bold text-lg mb-2 border border-[#e1d3c1]">{offer.enterprise.name.charAt(0)}</div>
                    <div className="text-xs text-[var(--color-dark)] font-semibold text-center">{offer.enterprise.name}</div>
                    <div className="text-[10px] text-[var(--color-dark)] mt-1">{offer.enterprise.country || 'Nigeria'} · {offer.enterprise.city || 'Lagos'}</div>
                    <div className="text-[10px] text-[var(--color-dark)] mt-1">{offer.enterprise.sectorOfActivity}</div>
                  </div>
                  {/* Centre : titre, deadline, type, période, badges */}
                  <div className="flex-1 flex flex-col justify-between py-4">
                    <div className="flex flex-col pb-2">
                      <div className="font-semibold text-[var(--color-dark)] text-lg md:text-lg">{offer.title}</div>
                      <span className="ml-2 text-xs text-[var(--color-dark)]">Délai de candidature <b>2 mars 2025</b></span>
                    </div>
                    <div className="flex flex-col mt-2 mb-2 flex-wrap">
                      <div className="text-xs text-[var(--color-dark)]">Type de stage : <b>{offer.typeOfInternship || 'Perfectionnement'}</b></div>
                      <div className="text-xs text-[var(--color-dark)]">Stage payant : <b>{offer.paying ? 'OUI' : 'NON'}</b></div>
                      <div className="text-xs text-[var(--color-dark)]">Période du stage : <b>{offer.startDate} - {offer.endDate}</b></div>
                    </div>
                    <div className="flex flex-row flex-wrap gap-2 mt-1 ">
                      {/* Badges (mockés, car pas dans le backend) */}
                      <span className="px-2 py-1 rounded-full text-xs font-medium bg-[#e1d3c1] text-[var(--color-vert)] border border-[var(--color-vert)]">. En remote</span>
                      <span className="px-2 py-1 rounded-full text-xs font-medium bg-[#e1d3c1] text-[var(--color-vert)] border border-[var(--color-vert)]">.Après interview</span>
                    </div>
                  </div>
                  {/* Colonne droite : places, postulants, domaine, tags */}
                  <div className="flex flex-col justify-between items-end max-w-[243px] bg-[var(--color-light)] p-4 border-l border-dashed border-[var(--color-neutre6-placeholder)]">
                    <div className="mb-2">
                      <div className="text-xs text-[var(--color-dark)]">Nombre de place <b>2</b></div>
                      <div className="text-xs text-[var(--color-dark)]">Nombre de postulants <b>5</b></div>
                      <div className="text-xs text-[var(--color-dark)]">Domaine <b>{offer.domain}</b></div>
                    </div>
                  </div>
                </motion.div>
                ))
              )}
            </div>
          </motion.div>
        </section>
      </main>
    </div>
  );
}

