<script lang="ts">
  import { onMount } from 'svelte';

  type Creneau = {
    id: number;
    date: string;
    heure: string;
    heureFin: string;
    disponible: boolean;
  };

  let calendrier = $state<Creneau[]>([]);
  let newDate = $state('');
  let newHour = $state('');
  let newHourFin = $state('');
  let erreur = $state('');
  let succes = $state('');
  let idDocteur = $state<number | null>(null);
  let chargement = $state(true);

  onMount(async () => {
    // Récupérer l'id du docteur connecté depuis localStorage
    const id = localStorage.getItem('userId');
    if (!id) { window.location.href = '/login-page'; return; }
    idDocteur = parseInt(id);

    // Charger les créneaux
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8086/docteurs/${idDocteur}/creneaux`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        calendrier = data.map((c: any) => ({
          id: c.idCreneau,
          date: c.dateCreneau,
          heure: c.heureDebut,
          heureFin: c.heureFin,
          disponible: !c.bloque
        }));
      }
    } catch (e) {
      erreur = "Impossible de charger les créneaux.";
    } finally {
      chargement = false;
    }
  });

  async function ajouterCreneau() {
    erreur = '';
    if (!newDate) { erreur = 'La date est obligatoire.'; return; }
    if (!newHour) { erreur = "L'heure de début est obligatoire."; return; }
    if (!newHourFin) { erreur = "L'heure de fin est obligatoire."; return; }
    if (newHourFin <= newHour) { erreur = "L'heure de fin doit être après l'heure de début."; return; }

    // Vérifier si créneau existe déjà
    const existe = calendrier.find(c => c.date === newDate && c.heure === newHour);
    if (existe) { erreur = 'Ce créneau existe déjà.'; return; }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8086/docteurs/${idDocteur}/creneaux`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          dateCreneau: newDate,
          heureDebut: newHour,
          heureFin: newHourFin,
          bloque: false
        })
      });

      if (response.ok) {
        const c = await response.json();
        calendrier.push({
          id: c.idCreneau,
          date: c.dateCreneau,
          heure: c.heureDebut,
          heureFin: c.heureFin,
          disponible: !c.bloque
        });
        newDate = '';
        newHour = '';
        newHourFin = '';
        succes = 'Créneau ajouté avec succès !';
        setTimeout(() => succes = '', 3000);
      } else {
        erreur = "Erreur lors de l'ajout du créneau.";
      }
    } catch (e) {
      erreur = "Impossible de contacter le serveur.";
    }
  }

  async function bloquerCreneau(index: number) {
    try {
      const token = localStorage.getItem('token');
      const idCreneau = calendrier[index].id;
      const response = await fetch(`http://localhost:8086/docteurs/${idDocteur}/creneaux/${idCreneau}/bloquer`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        calendrier[index].disponible = false;
      }
    } catch (e) {
      erreur = "Erreur lors du blocage.";
    }
  }

  async function supprimerCreneau(index: number) {
    if (!confirm('Voulez-vous vraiment supprimer ce créneau ?')) return;
    try {
      const token = localStorage.getItem('token');
      const idCreneau = calendrier[index].id;
      const response = await fetch(`http://localhost:8086/docteurs/${idDocteur}/creneaux/${idCreneau}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        calendrier.splice(index, 1);
      }
    } catch (e) {
      erreur = "Erreur lors de la suppression.";
    }
  }
</script>

<svelte:head>
  <title>Agenda — MediLine</title>
</svelte:head>

<div class="page">

  <div class="entete">
    <div class="entete-icone">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <rect x="3" y="4" width="18" height="18" rx="2" stroke="white" stroke-width="2"/>
        <path d="M16 2v4M8 2v4M3 10h18" stroke="white" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
    <div>
      <h1>Agenda du médecin</h1>
      <p>Ajoutez, bloquez ou supprimez vos créneaux de consultation</p>
    </div>
  </div>

  <!-- MESSAGES -->
  {#if succes}
    <div class="msg-succes">✅ {succes}</div>
  {/if}
  {#if erreur}
    <div class="msg-erreur-box">⚠️ {erreur}</div>
  {/if}

  <!-- FORMULAIRE AJOUT -->
  <div class="carte">
    <h2>Ajouter un créneau</h2>
    <div class="form-ajout">
      <div class="filtre-group">
        <label for="date">Date</label>
        <input type="date" bind:value={newDate} />
      </div>
      <div class="filtre-group">
        <label for="heure-debut">Heure début</label>
        <input type="time" bind:value={newHour} />
      </div>
      <div class="filtre-group">
        <label for="heure-fin">Heure fin</label>
        <input type="time" bind:value={newHourFin} />
      </div>
      <button class="btn-ajouter" onclick={ajouterCreneau}>
        + Ajouter
      </button>
    </div>
  </div>

  <!-- TABLEAU CRÉNEAUX -->
  <div class="carte-tableau">
    {#if chargement}
      <div class="vide">Chargement des créneaux...</div>
    {:else if calendrier.length === 0}
      <div class="vide">Aucun créneau — ajoutez-en un ci-dessus.</div>
    {:else}
      <table>
        <thead>
          <tr class="titre">
            <th>Date</th>
            <th>Heure début</th>
            <th>Heure fin</th>
            <th>Disponibilité</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {#each calendrier as creneau, index}
            <tr>
              <td>{creneau.date}</td>
              <td>{creneau.heure}</td>
              <td>{creneau.heureFin}</td>
              <td>
                <span class="badge {creneau.disponible ? 'badge-dispo' : 'badge-occupe'}">
                  {creneau.disponible ? 'Disponible' : 'Bloqué'}
                </span>
              </td>
              <td class="actions-td">
                {#if creneau.disponible}
                  <button class="btn-bloquer" onclick={() => bloquerCreneau(index)}>
                    🔒 Bloquer
                  </button>
                {/if}
                <button class="btn-supprimer" onclick={() => supprimerCreneau(index)}>
                  🗑 Supprimer
                </button>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    {/if}
  </div>

</div>

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

  .msg-succes {
    background: #d4edda; border: 1px solid #c3e6cb; color: #155724;
    border-radius: 10px; padding: 0.9rem 1.2rem; margin-bottom: 1.5rem; font-size: 0.95rem;
  }

  .msg-erreur-box {
    background: #fff0f3; border: 1px solid #f8d7da; color: #c21852;
    border-radius: 10px; padding: 0.9rem 1.2rem; margin-bottom: 1.5rem; font-size: 0.95rem;
  }

  .carte {
    background: white; border-radius: 16px; padding: 1.8rem;
    border: 1px solid rgba(0,0,0,0.07); box-shadow: 0 2px 20px rgba(0,0,0,0.05);
    margin-bottom: 1.5rem;
  }

  .carte h2 {
    font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700;
    color: #0d1b2a; margin-bottom: 1.2rem;
    padding-bottom: 0.7rem; border-bottom: 1.5px solid #f0f0f0;
  }

  .form-ajout { display: flex; align-items: flex-end; gap: 1rem; flex-wrap: wrap; }

  .filtre-group { display: flex; flex-direction: column; gap: 0.3rem; }

  .filtre-group label {
    font-size: 0.8rem; font-weight: 600; color: #4a5568;
    text-transform: uppercase; letter-spacing: 0.05em;
  }

  .filtre-group input {
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    padding: 0.45rem 0.8rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem; color: #1a2332; background: white; outline: none;
    transition: border-color 0.2s;
  }

  .filtre-group input:focus { border-color: #e91e8c; }

  .btn-ajouter {
    padding: 0.5rem 1.4rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    color: white; border: none; border-radius: 8px;
    font-family: 'DM Sans', sans-serif; font-size: 0.9rem; font-weight: 500;
    cursor: pointer; transition: opacity 0.2s;
  }

  .btn-ajouter:hover { opacity: 0.9; }

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

  .actions-td { display: flex; gap: 0.5rem; justify-content: center; align-items: center; }

  .badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 100px; font-size: 0.8rem; font-weight: 500; }
  .badge-dispo  { background: #d4edda; color: #155724; }
  .badge-occupe { background: #f8d7da; color: #721c24; }

  .btn-bloquer {
    background: linear-gradient(135deg, #e91e8c, #c21852); color: white; border: none;
    padding: 0.35rem 0.8rem; border-radius: 8px;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem; font-weight: 500;
    cursor: pointer; transition: opacity 0.2s;
  }

  .btn-bloquer:hover { opacity: 0.9; }

  .btn-supprimer {
    background: transparent; border: 1px solid #f8d7da; color: #dc3545;
    border-radius: 8px; padding: 0.35rem 0.8rem;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem;
    cursor: pointer; transition: all 0.2s;
  }

  .btn-supprimer:hover { background: #fff0f3; }

  .vide { padding: 3rem; text-align: center; color: #a0aec0; font-size: 0.95rem; }

  @media (max-width: 768px) {
    .form-ajout { flex-direction: column; align-items: flex-start; }
    .actions-td { flex-direction: column; }
  }
</style>