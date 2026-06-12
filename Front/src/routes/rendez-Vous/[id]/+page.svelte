<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  
  type Creneau = {
    id: number;
    date: string;
    heure: string;
    disponible: boolean;
  };
  
  type RdvMedecin = {
    id: number;
    patient: string;
    date: string;
    heure: string;
    statusRdv: string;
    motif: string;
  };
  
  let role = $state('');
  let medecinId = $state('');
  let chargement = $state(true);
  let erreurApi = $state('');
  
  // ═══ VUE PATIENT ═══
  let creneaux = $state<Creneau[]>([]);
  let filtreDate = $state('');
  let filtreDisponible = $state('tous');
  let confirmation = $state('');
  let creneauChoisi = $state<Creneau | null>(null);
    
    // 👉 Déclaration du motif côté patient
    let motif = $state('');
    
    let creneauxFiltres = $derived(
    creneaux.filter(c => {
      const matchDate = filtreDate ? c.date === filtreDate : true;
      const matchDispo =
      filtreDisponible === 'tous' ? true :
      filtreDisponible === 'disponible' ? c.disponible :
      !c.disponible;
      return matchDate && matchDispo;
    })
    );
    
    function ouvrirReservation(creneau: Creneau) {
      if (!creneau.disponible) return;
      creneauChoisi = creneau;
    }
    
    async function confirmerReservation() {
      if (!creneauChoisi) return;
      if (!motif.trim()) {
        erreurApi = "Veuillez préciser le motif du rendez-vous.";
        return;
      }
      
      try {
        const token = localStorage.getItem('token');
        const patientId = localStorage.getItem('userId');
        
        const response = await fetch('http://localhost:8086/rdv/add', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            date: creneauChoisi.date,
            heure: creneauChoisi.heure,
            patientId: parseInt(patientId!),
            docteurId: parseInt(medecinId),
            status: 'Attente de confirmation'
          })
        });
        
        if (response.ok) {
          creneauChoisi.disponible = false;
          confirmation = `✅ Rendez-vous enregistré le ${creneauChoisi.date} à ${creneauChoisi.heure}. Motif : ${motif}. En attente de confirmation du médecin.`;
          creneauChoisi = null;
          motif = '';
          setTimeout(() => confirmation = '', 5000);
        } else {
          erreurApi = "Erreur lors de la réservation. Réessayez.";
        }
      } catch (e) {
        erreurApi = "Impossible de contacter le serveur.";
      }
    }
    
    // ═══ VUE DOCTEUR ═══
    let rdvMedecin = $state<RdvMedecin[]>([]);
    let filtreDateMed = $state('');
    let filtreStatut = $state('tous');
    let rdvSelectionne = $state<RdvMedecin | null>(null);
      
      let rdvFiltres = $derived(
      rdvMedecin.filter(r => {
        const matchDate = filtreDateMed ? r.date === filtreDateMed : true;
        const matchStatut = filtreStatut === 'tous' ? true : r.statusRdv === filtreStatut;
        return matchDate && matchStatut;
      })
      );
      
      function peutAnnuler(date: string, heure: string): boolean {
        const rdvDate = new Date(`${date}T${heure}:00`);
        const maintenant = new Date();
        const diffHeures = (rdvDate.getTime() - maintenant.getTime()) / (1000 * 60 * 60);
        return diffHeures > 3;
      }
      
      async function annulerRdv(rdv: RdvMedecin) {
        try {
          const token = localStorage.getItem('token');
          const response = await fetch(`http://localhost:8086/rdv/cancel/${rdv.id}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
          });
          
          if (response.ok) {
            rdv.statusRdv = 'ANNULE';
            rdvSelectionne = null;
          } else {
            erreurApi = "Erreur lors de l'annulation.";
          }
        } catch (e) {
          erreurApi = "Impossible de contacter le serveur.";
        }
      }
      
      // ═══ CHARGEMENT DES DONNÉES ═══
      onMount(async () => {
        role = localStorage.getItem('role') || '';
        if (!role) { window.location.href = '/login-page'; return; }
        
        medecinId = $page.params.id ?? '';
        const token = localStorage.getItem('token');
        
        try {
          if (role === 'PATIENT') {
            const res = await fetch(`http://localhost:8086/docteurs/${medecinId}/creneaux`, {
              headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (res.ok) {
              const data = await res.json();
              
              creneaux = data.map((c: any) => ({
                id: c.idCreneau,
                date: c.dateCreneau,
                heure: c.heureDebut,
                disponible: !c.bloque
              }));
            } else {
              erreurApi = "Impossible de charger les créneaux du médecin.";
            }
          }
          else if (role === 'DOCTEUR') {
            const docteurId = localStorage.getItem('userId');
            const res = await fetch(`http://localhost:8086/docteurs/${docteurId}/rdvs`, {
              headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
              const data = await res.json();
              rdvMedecin = data.map((r: any) => ({
                id: r.idRdv,
                patient: `${r.patient?.nomPatient || ''} ${r.patient?.prenomPatient || ''}`,
                date: r.dateRdv ? r.dateRdv.split('T')[0] : '',
                heure: r.heureRdv,
                statusRdv: r.statusRdv,
                motif: r.motif || ''
              }));
            }
          }
        } catch (e) {
          erreurApi = "Impossible de charger les données.";
        } finally {
          chargement = false;
        }
      });
    </script>
    
    
    <svelte:head>
    <title>Rendez-vous — MediLine</title>
  </svelte:head>
  
  
  <!-- VUE PATIENT  -->
  
  {#if role === 'PATIENT'}
  <div class="page">
    
    <div class="entete">
      <div class="entete-icone">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <rect x="3" y="4" width="18" height="18" rx="2" stroke="white" stroke-width="2"/>
          <path d="M16 2v4M8 2v4M3 10h18" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h1>Prendre rendez-vous</h1>
        <p>Consultez les créneaux disponibles et réservez</p>
      </div>
    </div>
    
    {#if confirmation}
    <div class="confirm-banner">{confirmation}</div>
    {/if}
    
    <div class="filtres">
      <div class="filtre-group">
        <label for="date-pat">Date</label>
        <input type="date" id="date-pat" bind:value={filtreDate} />
      </div>
      <div class="filtre-group">
        <label for="dispo">Disponibilité</label>
        <select id="dispo" bind:value={filtreDisponible}>
          <option value="tous">Tous</option>
          <option value="disponible">Disponible</option>
          <option value="occupe">Occupé</option>
        </select>
      </div>
      {#if filtreDate || filtreDisponible !== 'tous'}
      <button class="btn-reset" onclick={() => { filtreDate = ''; filtreDisponible = 'tous'; }}>
        Réinitialiser
      </button>
      {/if}
    </div>
    
    <div class="carte-tableau">
      <table>
        <thead>
          <tr class="titre">
            <th>Date</th>
            <th>Heure</th>
            <th>Disponibilité</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {#each creneauxFiltres as c}
          <tr class={c.disponible ? '' : 'occupe'}>
            <td>{c.date}</td>
            <td>{c.heure}</td>
            <td>
              <span class="badge {c.disponible ? 'badge-dispo' : 'badge-occupe'}">
                {c.disponible ? 'Disponible' : 'Occupé'}
              </span>
            </td>
            <td>
              <button class="btn-reserver" disabled={!c.disponible} onclick={() => ouvrirReservation(c)}>
                Réserver
              </button>
            </td>
          </tr>
          {/each}
          {#if creneauxFiltres.length === 0}
          <tr>
            <td colspan="4" class="vide-tableau">Aucun créneau trouvé.</td>
          </tr>
          {/if}
        </tbody>
      </table>
    </div>
    
    <!-- MODAL MOTIF PATIENT -->
    {#if creneauChoisi}
    <div class="modal-fond" role="button" tabindex="0"
    onclick={() => creneauChoisi = null}
    onkeydown={(e) => e.key === 'Escape' && (creneauChoisi = null)}>
    <div class="modal" role="dialog" aria-modal="true" tabindex="-1"
    onclick={(e) => e.stopPropagation()}
    onkeydown={(e) => e.stopPropagation()}>
    
    <div class="modal-entete">
      <h3>Confirmer le rendez-vous</h3>
      <button class="modal-fermer" onclick={() => creneauChoisi = null}>✕</button>
    </div>
    
    <div class="modal-corps">
      <div class="modal-ligne"><span>Date</span><strong>{creneauChoisi.date}</strong></div>
      <div class="modal-ligne"><span>Heure</span><strong>{creneauChoisi.heure}</strong></div>
      <div class="champ-motif">
        <label for="motif">Motif de la consultation <span class="requis">*</span></label>
        <textarea id="motif" bind:value={motif} rows="3"
        placeholder="Ex: Douleurs abdominales, renouvellement ordonnance...">
      </textarea>
    </div>
  </div>
  
  <div class="modal-actions">
    <button class="btn-reset" onclick={() => creneauChoisi = null}>Annuler</button>
    <button class="btn-confirmer" disabled={!motif.trim()} onclick={confirmerReservation}>
      ✅ Confirmer
    </button>
  </div>
  
</div>
</div>
{/if}

</div>


<!-- VUE DOCTEUR  -->

{:else if role === 'DOCTEUR'}
<div class="page">
  
  <div class="entete">
    <div class="entete-icone">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <rect x="3" y="4" width="18" height="18" rx="2" stroke="white" stroke-width="2"/>
        <path d="M16 2v4M8 2v4M3 10h18" stroke="white" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
    <div>
      <h1>Mes rendez-vous</h1>
      <p>Gérez vos rendez-vous et consultations à venir</p>
    </div>
  </div>
  
  <!-- STATS -->
  <div class="stats">
    <div class="stat-carte">
      <div class="stat-num">{rdvMedecin.filter(r => r.statusRdv === 'confirmé').length}</div>
      <div class="stat-label">Confirmés</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{rdvMedecin.filter(r => r.statusRdv === 'annulé').length}</div>
      <div class="stat-label">Annulés</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{rdvMedecin.length}</div>
      <div class="stat-label">Total</div>
    </div>
  </div>
  
  <!-- FILTRES -->
  <div class="filtres">
    <div class="filtre-group">
      <label for="date-med">Date</label>
      <input type="date" id="date-med" bind:value={filtreDateMed} />
    </div>
    <div class="filtre-group">
      <label for="statut">Statut</label>
      <select id="statut" bind:value={filtreStatut}>
        <option value="tous">Tous</option>
        <option value="confirmé">Confirmé</option>
        <option value="annulé">Annulé</option>
      </select>
    </div>
    {#if filtreDateMed || filtreStatut !== 'tous'}
    <button class="btn-reset" onclick={() => { filtreDateMed = ''; filtreStatut = 'tous'; }}>
      Réinitialiser
    </button>
    {/if}
  </div>
  
  <!-- TABLEAU RENDEZ-VOUS -->
  <div class="carte-tableau">
    <table>
      <thead>
        <tr class="titre">
          <th>Patient</th>
          <th>Date</th>
          <th>Heure</th>
          <th>Statut</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {#each rdvFiltres as rdv}
        <tr>
          <td><strong>{rdv.patient}</strong></td>
          <td>{rdv.date}</td>
          <td>{rdv.heure}</td>
          <td>
            <span class="badge {rdv.statusRdv === 'confirmé' ? 'badge-dispo' : 'badge-occupe'}">
              {rdv.statusRdv}
            </span>
          </td>
          <td>
            <button class="btn-detail" onclick={() => rdvSelectionne = rdv}>
              Détails
            </button>
          </td>
        </tr>
        {/each}
        {#if rdvFiltres.length === 0}
        <tr>
          <td colspan="5" class="vide-tableau">Aucun rendez-vous trouvé.</td>
        </tr>
        {/if}
      </tbody>
    </table>
  </div>
  
  <!-- MODAL DÉTAILS DOCTEUR -->
  {#if rdvSelectionne}
  <div class="modal-fond" role="button" tabindex="0"
  onclick={() => rdvSelectionne = null}
  onkeydown={(e) => e.key === 'Escape' && (rdvSelectionne = null)}>
  <div class="modal" role="dialog" aria-modal="true" tabindex="-1"
  onclick={(e) => e.stopPropagation()}
  onkeydown={(e) => e.stopPropagation()}>
  
  <div class="modal-entete">
    <h3>Détails du rendez-vous</h3>
    <button class="modal-fermer" onclick={() => rdvSelectionne = null}>✕</button>
  </div>
  
  <div class="modal-corps">
    <div class="modal-ligne"><span>Patient</span><strong>{rdvSelectionne.patient}</strong></div>
    <div class="modal-ligne"><span>Date</span><strong>{rdvSelectionne.date}</strong></div>
    <div class="modal-ligne"><span>Heure</span><strong>{rdvSelectionne.heure}</strong></div>
    <div class="modal-ligne"><span>Motif</span><strong>{rdvSelectionne.motif}</strong></div>
    <div class="modal-ligne">
      <span>Statut</span>
      <span class="badge {rdvSelectionne.statusRdv === 'confirmé' ? 'badge-dispo' : 'badge-occupe'}">
        {rdvSelectionne.statusRdv}
      </span>
    </div>
    {#if rdvSelectionne.statusRdv !== 'annulé' && !peutAnnuler(rdvSelectionne.date, rdvSelectionne.heure)}
    <div class="msg-delai">
      ⚠️ Annulation impossible — moins de 3h avant le rendez-vous.
    </div>
    {/if}
  </div>
  
  <div class="modal-actions">
    <button class="btn-reset" onclick={() => rdvSelectionne = null}>Fermer</button>
    {#if rdvSelectionne.statusRdv !== 'annulé'}
    <button class="btn-annuler"
    disabled={!peutAnnuler(rdvSelectionne.date, rdvSelectionne.heure)}
    onclick={() => annulerRdv(rdvSelectionne!)}>
    ✕ Annuler
  </button>
  {/if}
</div>

</div>
</div>
{/if}

</div>


<!-- CHARGEMENT                            -->

{:else}
<div style="display:flex; align-items:center; justify-content:center; height:50vh; color:#718096;">
  Chargement...
</div>
{/if}

<style>
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700&family=DM+Sans:wght@300;400;500&display=swap');
  
  .page {
    padding: 2rem 5%;
    max-width: 1100px;
    margin: 0 auto;
    font-family: 'DM Sans', sans-serif;
  }
  
  .entete { display: flex; align-items: center; gap: 1rem; margin-bottom: 2rem; }
  
  .entete-icone {
    width: 50px; height: 50px; border-radius: 14px;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  }
  
  .entete h1 { font-family: 'Syne', sans-serif; font-size: 1.6rem; font-weight: 700; color: #0d1b2a; margin: 0; }
  .entete p  { font-size: 0.88rem; color: #718096; margin: 0; }
  
  .confirm-banner {
    background: #d4edda; color: #155724; border: 1px solid #c3e6cb;
    border-radius: 10px; padding: 1rem 1.5rem; margin-bottom: 1.5rem; font-size: 0.95rem;
  }
  
  .stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1.5rem; }
  
  .stat-carte {
    background: white; border-radius: 14px; padding: 1.2rem 1.5rem;
    border: 1px solid rgba(0,0,0,0.07); box-shadow: 0 2px 15px rgba(0,0,0,0.04); text-align: center;
  }
  
  .stat-num   { font-family: 'Syne', sans-serif; font-size: 2rem; font-weight: 700; color: #e91e8c; }
  .stat-label { font-size: 0.82rem; color: #718096; margin-top: 0.2rem; }
  
  .filtres { display: flex; align-items: flex-end; gap: 1.5rem; margin-bottom: 1.5rem; flex-wrap: wrap; }
  .filtre-group { display: flex; flex-direction: column; gap: 0.3rem; }
  
  .filtre-group label {
    font-size: 0.8rem; font-weight: 600; color: #4a5568;
    text-transform: uppercase; letter-spacing: 0.05em;
  }
  
  .filtre-group input, .filtre-group select {
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    padding: 0.45rem 0.8rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem; color: #1a2332; background: white; outline: none;
  }
  
  .filtre-group input:focus, .filtre-group select:focus { border-color: #e91e8c; }
  
  .btn-reset {
    background: transparent; border: 1.5px solid rgba(0,0,0,0.12);
    border-radius: 8px; padding: 0.45rem 1rem;
    font-family: 'DM Sans', sans-serif; font-size: 0.85rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }
  
  .btn-reset:hover { border-color: #e91e8c; color: #e91e8c; }
  
  .carte-tableau {
    background: white; border-radius: 16px;
    border: 1px solid rgba(0,0,0,0.07);
    box-shadow: 0 2px 20px rgba(0,0,0,0.05); overflow: hidden;
  }
  
  table { width: 100%; border-collapse: collapse; }
  :global(tr.titre) { background: #e91e8c !important; color: white; }
  
  th, td { border-bottom: 1px solid #f0f0f0; padding: 0.85rem 1rem; text-align: center; font-size: 0.9rem; }
  th { font-weight: 600; font-size: 0.85rem; }
  
  tbody tr:hover { background: #fdf2f8; }
  tbody tr:last-child td { border-bottom: none; }
  tr.occupe { opacity: 0.6; }
  .vide-tableau { color: #718096; padding: 2rem !important; }
  
  .badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 100px; font-size: 0.8rem; font-weight: 500; }
  .badge-dispo  { background: #d4edda; color: #155724; }
  .badge-occupe { background: #f8d7da; color: #721c24; }
  
  .btn-reserver {
    background: #e91e8c; color: white; border: none;
    padding: 0.4rem 1rem; border-radius: 8px; cursor: pointer;
    font-family: 'DM Sans', sans-serif; font-size: 0.85rem; font-weight: 500; transition: background 0.2s;
  }
  
  .btn-reserver:hover:not(:disabled) { background: #c21852; }
  .btn-reserver:disabled { background: #e2e8f0; color: #a0aec0; cursor: not-allowed; }
  
  .btn-detail {
    background: transparent; border: 1.5px solid rgba(0,0,0,0.12);
    border-radius: 8px; padding: 0.4rem 0.9rem;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }
  
  .btn-detail:hover { border-color: #e91e8c; color: #e91e8c; }
  
  .modal-fond {
    position: fixed; inset: 0; background: rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center; z-index: 200; padding: 1rem;
  }
  
  .modal {
    background: white; border-radius: 16px;
    width: 100%; max-width: 460px; box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  }
  
  .modal-entete {
    display: flex; align-items: center; justify-content: space-between;
    padding: 1.2rem 1.5rem; border-bottom: 1px solid #f0f0f0;
  }
  
  .modal-entete h3 { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700; color: #0d1b2a; margin: 0; }
  
  .modal-fermer {
    background: none; border: none; font-size: 1rem; cursor: pointer;
    color: #718096; padding: 0.2rem 0.5rem; border-radius: 6px; transition: background 0.2s;
  }
  
  .modal-fermer:hover { background: #f0f0f0; }
  
  .modal-corps { padding: 1.2rem 1.5rem; }
  
  .modal-ligne {
    display: flex; justify-content: space-between; align-items: center;
    padding: 0.6rem 0; font-size: 0.88rem; border-bottom: 1px solid #f8f8f8;
  }
  
  .modal-ligne span   { color: #718096; }
  .modal-ligne strong { color: #0d1b2a; }
  
  .msg-delai {
    background: #fff3cd; border: 1px solid #fde68a; color: #92400e;
    border-radius: 8px; padding: 0.7rem 1rem; font-size: 0.85rem; margin-top: 1rem;
  }
  
  .modal-actions {
    display: flex; justify-content: flex-end; gap: 0.8rem;
    padding: 1rem 1.5rem; border-top: 1px solid #f0f0f0;
  }
  
  .champ-motif { display: flex; flex-direction: column; gap: 0.4rem; margin-top: 1rem; }
  .champ-motif label { font-size: 0.85rem; font-weight: 500; color: #4a5568; }
  .requis { color: #e91e8c; }
  
  .champ-motif textarea {
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 10px;
    padding: 0.7rem 1rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem; color: #1a2332; outline: none; resize: vertical; transition: border-color 0.2s;
  }
  
  .champ-motif textarea:focus { border-color: #e91e8c; }
  
  .btn-confirmer {
    padding: 0.6rem 1.2rem; background: #28a745; color: white; border: none;
    border-radius: 8px; font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem; font-weight: 500; cursor: pointer; transition: opacity 0.2s;
  }
  
  .btn-confirmer:hover:not(:disabled) { opacity: 0.9; }
  .btn-confirmer:disabled { opacity: 0.5; cursor: not-allowed; }
  
  .btn-annuler {
    padding: 0.6rem 1.2rem; background: #dc3545; color: white; border: none;
    border-radius: 8px; font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem; font-weight: 500; cursor: pointer; transition: opacity 0.2s;
  }
  
  .btn-annuler:hover:not(:disabled) { opacity: 0.9; }
  .btn-annuler:disabled { opacity: 0.5; cursor: not-allowed; }
  
  @media (max-width: 768px) {
    .stats { grid-template-columns: repeat(2, 1fr); }
    .filtres { flex-direction: column; align-items: flex-start; }
  }
</style>