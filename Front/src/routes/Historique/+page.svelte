<script lang="ts">
  import { onMount } from 'svelte';

  let role = $state('');
  let idUser = $state('');
  let onglet = $state('rdv');
  let chargement = $state(true);

  // VUE PATIENT
  let rdvPatient = $state<any[]>([]);
  let ordonnancesPatient = $state<any[]>([]);

  // VUE DOCTEUR
  let rdvMedecin = $state<any[]>([]);
  let ordonnancesMedecin = $state<any[]>([]);

  onMount(async () => {
    role = localStorage.getItem('role') || '';
    idUser = localStorage.getItem('userId') || '';
    if (!role || !idUser) { window.location.href = '/login-page'; return; }

    const token = localStorage.getItem('token');

    try {
      if (role === 'PATIENT') {
        // RDV du patient
        const rdvRes = await fetch(`http://localhost:8086/patients/${idUser}/rdvs`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (rdvRes.ok) {
          const data = await rdvRes.json();
          rdvPatient = data.map((r: any) => ({
            id: r.idRdv,
            medecin: `Dr. ${r.docteur?.nomDocteur || ''} ${r.docteur?.prenomDocteur || ''}`,
            specialite: r.docteur?.specialiteDocteur || '',
            date: r.dateRdv ? r.dateRdv.split('T')[0] : '',
            heure: r.heureRdv,
            statut: r.statusRdv
          }));
        }

        // Ordonnances du patient
        const ordoRes = await fetch(`http://localhost:8086/ordonnances/patient/${idUser}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (ordoRes.ok) {
          const data = await ordoRes.json();
          ordonnancesPatient = data.map((o: any) => ({
            id: o.idOrdonnance,
            medecin: o.nomMedecin,
            specialite: o.specialite,
            date: o.dateOrdonnance ? new Date(o.dateOrdonnance).toLocaleDateString('fr-FR') : '',
            contenu: o.contenu
          }));
        }
      }

      if (role === 'DOCTEUR') {
        // RDV du docteur
        const rdvRes = await fetch(`http://localhost:8086/docteurs/${idUser}/rdvs`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (rdvRes.ok) {
          const data = await rdvRes.json();
          rdvMedecin = data.map((r: any) => ({
            id: r.idRdv,
            patient: `${r.patient?.nomPatient || ''} ${r.patient?.prenomPatient || ''}`,
            date: r.dateRdv ? r.dateRdv.split('T')[0] : '',
            heure: r.heureRdv,
            statut: r.statusRdv
          }));
        }

        // Ordonnances du docteur
        const ordoRes = await fetch(`http://localhost:8086/ordonnances/docteur/${idUser}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (ordoRes.ok) {
          const data = await ordoRes.json();
          ordonnancesMedecin = data.map((o: any) => ({
            id: o.idOrdonnance,
            patient: o.nomPatient,
            date: o.dateOrdonnance ? new Date(o.dateOrdonnance).toLocaleDateString('fr-FR') : '',
            contenu: o.contenu
          }));
        }
      }
    } catch (e) {
      console.error('Erreur chargement historique:', e);
    } finally {
      chargement = false;
    }
  });
</script>

<svelte:head>
  <title>Historique médical — MediLine</title>
</svelte:head>

{#if role === 'PATIENT'}

  <div class="page">

    <div class="entete">
      <div class="entete-icone">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M12 20h9" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h1>Mon historique médical</h1>
        <p>Consultez vos rendez-vous passés et ordonnances reçues</p>
      </div>
    </div>

    <!-- ONGLETS -->
    <div class="onglets">
      <button class="onglet" class:actif={onglet === 'rdv'} onclick={() => onglet = 'rdv'}>
        📅 Rendez-vous
      </button>
      <button class="onglet" class:actif={onglet === 'ordonnances'} onclick={() => onglet = 'ordonnances'}>
        📄 Ordonnances
      </button>
    </div>

    <!-- RENDEZ-VOUS -->
    {#if onglet === 'rdv'}
      {#if rdvPatient.length === 0}
        <div class="vide">
          <p>Aucun rendez-vous dans votre historique.</p>
        </div>
      {:else}
        <div class="liste">
          {#each rdvPatient as rdv}
            <div class="carte-item">
              <div class="item-gauche">
                <div class="item-icone">📅</div>
                <div class="item-info">
                  <strong>{rdv.medecin}</strong>
                  <span>{rdv.specialite}</span>
                  <span class="item-date">🕐 {rdv.date} à {rdv.heure}</span>
                </div>
              </div>
              <div class="item-droite">
                <span class="statut {rdv.statut === 'Terminé' ? 'statut-termine' : 'statut-annule'}">
                  {rdv.statut}
                </span>
              </div>
            </div>
          {/each}
        </div>
      {/if}

    <!-- ORDONNANCES -->
    {:else if onglet === 'ordonnances'}
      {#if ordonnancesPatient.length === 0}
        <div class="vide">
          <p>Aucune ordonnance dans votre historique.</p>
        </div>
      {:else}
        <div class="liste">
          {#each ordonnancesPatient as ordo}
            <div class="carte-item">
              <div class="item-gauche">
                <div class="item-icone">📄</div>
                <div class="item-info">
                  <strong>{ordo.medecin}</strong>
                  <span>{ordo.specialite}</span>
                  <span class="item-date">📅 {ordo.date}</span>
                  <span class="item-contenu">{ordo.contenu}</span>
                </div>
              </div>
              <div class="item-droite">
                <a href="/ordonnance" class="btn-voir">Voir l'ordonnance</a>
              </div>
            </div>
          {/each}
        </div>
      {/if}
    {/if}

  </div>

{:else if role === 'DOCTEUR'}

  <div class="page">

    <div class="entete">
      <div class="entete-icone">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M12 20h9" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h1>Historique des consultations</h1>
        <p>Retrouvez toutes vos consultations passées</p>
      </div>
    </div>

    <!-- STATS RAPIDES -->
    <div class="stats">
      <div class="stat-carte">
        <div class="stat-num">{rdvMedecin.length}</div>
        <div class="stat-label">Consultations totales</div>
      </div>
      <div class="stat-carte">
        <div class="stat-num">{rdvMedecin.filter(r => r.statut === 'Terminé').length}</div>
        <div class="stat-label">Terminées</div>
      </div>
      <div class="stat-carte">
        <div class="stat-num">{ordonnancesMedecin.length}</div>
        <div class="stat-label">Ordonnances émises</div>
      </div>
      <div class="stat-carte">
        <div class="stat-num">{rdvMedecin.filter(r => r.statut === 'Annulé').length}</div>
        <div class="stat-label">Annulées</div>
      </div>
    </div>

    <!-- TABLEAU -->
    {#if rdvMedecin.length === 0}
      <div class="vide">
        <p>Aucune consultation dans votre historique.</p>
      </div>
    {:else}
      <div class="carte-tableau">
        <table>
          <thead>
            <tr>
              <th>Patient</th>
              <th>Date</th>
              <th>Heure</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            {#each rdvMedecin as rdv}
              <tr>
                <td><strong>{rdv.patient}</strong></td>
                <td>{rdv.date}</td>
                <td>{rdv.heure}</td>
                <td>
                  <span class="statut {rdv.statut === 'Terminé' ? 'statut-termine' : 'statut-annule'}">
                    {rdv.statut}
                  </span>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}

  </div>

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

  /* ENTETE */
  .entete {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .entete-icone {
    width: 50px;
    height: 50px;
    border-radius: 14px;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .entete h1 {
    font-family: 'Syne', sans-serif;
    font-size: 1.6rem;
    font-weight: 700;
    color: #0d1b2a;
    margin: 0;
  }

  .entete p { font-size: 0.88rem; color: #718096; margin: 0; }

  /* ONGLETS */
  .onglets {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
    border-bottom: 2px solid #f0f0f0;
    padding-bottom: 0;
  }

  .onglet {
    padding: 0.7rem 1.5rem;
    background: transparent;
    border: none;
    border-bottom: 2px solid transparent;
    margin-bottom: -2px;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.95rem;
    color: #718096;
    cursor: pointer;
    transition: all 0.2s;
    border-radius: 8px 8px 0 0;
  }

  .onglet:hover { color: #e91e8c; }

  .onglet.actif {
    color: #e91e8c;
    border-bottom-color: #e91e8c;
    font-weight: 500;
  }

  /* LISTE */
  .liste {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .carte-item {
    background: white;
    border-radius: 14px;
    padding: 1.2rem 1.5rem;
    border: 1px solid rgba(0,0,0,0.07);
    box-shadow: 0 2px 15px rgba(0,0,0,0.04);
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }

  .item-gauche {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .item-icone { font-size: 1.8rem; flex-shrink: 0; }

  .item-info {
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  .item-info strong { font-size: 0.95rem; color: #0d1b2a; }
  .item-info span { font-size: 0.82rem; color: #718096; }
  .item-date { color: #e91e8c !important; font-weight: 500; }
  .item-contenu { color: #4a5568 !important; font-style: italic; }

  .item-droite {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 0.5rem;
    flex-shrink: 0;
  }

  /* STATUTS */
  .statut {
    display: inline-block;
    padding: 0.25rem 0.8rem;
    border-radius: 100px;
    font-size: 0.8rem;
    font-weight: 500;
  }

  .statut-termine { background: #d4edda; color: #155724; }
  .statut-annule { background: #f8d7da; color: #721c24; }

  /* BOUTON VOIR */
  .btn-voir {
    padding: 0.4rem 1rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    color: white;
    border-radius: 8px;
    font-size: 0.82rem;
    font-weight: 500;
    text-decoration: none;
    transition: opacity 0.2s;
  }

  .btn-voir:hover { opacity: 0.9; }

  /* STATS MÉDECIN */
  .stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
    margin-bottom: 1.5rem;
  }

  .stat-carte {
    background: white;
    border-radius: 14px;
    padding: 1.2rem 1.5rem;
    border: 1px solid rgba(0,0,0,0.07);
    box-shadow: 0 2px 15px rgba(0,0,0,0.04);
    text-align: center;
  }

  .stat-num {
    font-family: 'Syne', sans-serif;
    font-size: 2rem;
    font-weight: 700;
    color: #e91e8c;
  }

  .stat-label { font-size: 0.82rem; color: #718096; margin-top: 0.2rem; }

  /* TABLEAU MÉDECIN */
  .carte-tableau {
    background: white;
    border-radius: 16px;
    border: 1px solid rgba(0,0,0,0.07);
    box-shadow: 0 2px 20px rgba(0,0,0,0.05);
    overflow: hidden;
  }

  table { width: 100%; border-collapse: collapse; }

  :global(thead tr) {
    background: #e91e8c !important;
    color: white;
  }

  th {
    padding: 0.9rem 1.2rem;
    font-size: 0.85rem;
    font-weight: 600;
    text-align: left;
  }

  td {
    padding: 0.9rem 1.2rem;
    font-size: 0.9rem;
    color: #4a5568;
    border-bottom: 1px solid #f0f0f0;
  }

  tbody tr:hover { background: #fdf2f8; }
  tbody tr:last-child td { border-bottom: none; }

  /* VIDE */
  .vide {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 4rem;
    color: #a0aec0;
    font-size: 0.95rem;
  }

  @media (max-width: 768px) {
    .stats { grid-template-columns: repeat(2, 1fr); }
    .carte-item { flex-direction: column; align-items: flex-start; }
    .item-droite { align-items: flex-start; }
  }
</style>