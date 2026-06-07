<script lang="ts">
  import { onMount } from 'svelte';

  let role = $state('');
  let onglet = $state('medecins');

  type Medecin = {
    id: number;
    nom: string;
    prenom: string;
    specialite: string;
    ville: string;
    email: string;
    telephone: string;
    adresse: string;
    statut: string;
  };

  type Patient = {
    id: number;
    nom: string;
    prenom: string;
    email: string;
    ville: string;
    telephone: string;
  };

  type Document = {
    id: number;
    nom: string;
    type: string;
    url: string;
  };

  let medecins = $state<Medecin[]>([]);
  let patients = $state<Patient[]>([]);
  let medecinSelectionne = $state<Medecin | null>(null);
  let documentsMedecin = $state<Document[]>([]);
  let chargementDocs = $state(false);
  let filtreStatut = $state('tous');
  let recherche = $state('');
  let chargement = $state(true);

  let medecinsFiltres = $derived(
    medecins.filter(m => {
      const matchStatut = filtreStatut === 'tous' ? true : m.statut === filtreStatut;
      const matchRecherche = recherche
        ? `${m.nom} ${m.prenom} ${m.specialite}`.toLowerCase().includes(recherche.toLowerCase())
        : true;
      return matchStatut && matchRecherche;
    })
  );

  let patientsFiltres = $derived(
    patients.filter(p =>
      recherche
        ? `${p.nom} ${p.prenom} ${p.email}`.toLowerCase().includes(recherche.toLowerCase())
        : true
    )
  );

  // Ouvre le modal médecin et charge ses documents
  async function ouvrirMedecin(m: Medecin) {
    medecinSelectionne = m;
    documentsMedecin = [];
    chargementDocs = true;
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`http://localhost:8086/docteurs/${m.id}/document`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        documentsMedecin = data.map((d: any) => ({
          id: d.idDocument,
          nom: d.nameDocument,
          type: d.typeDocument,
          url: d.urlDocument
        }));
      }
    } catch (e) {
      console.error('Erreur chargement documents:', e);
    } finally {
      chargementDocs = false;
    }
  }

  async function validerMedecin(m: Medecin) {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8086/admin/docteurs/${m.id}/valider`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        m.statut = 'validé';
        medecinSelectionne = null;
      } else {
        alert("Erreur lors de la validation.");
      }
    } catch (e) {
      alert("Impossible de contacter le serveur.");
    }
  }

  async function rejeterMedecin(m: Medecin) {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8086/admin/docteurs/${m.id}/refuser`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        m.statut = 'rejeté';
        medecinSelectionne = null;
      } else {
        alert("Erreur lors du refus.");
      }
    } catch (e) {
      alert("Impossible de contacter le serveur.");
    }
  }

  async function supprimerPatient(p: Patient) {
    if (!confirm(`Voulez-vous vraiment supprimer ${p.prenom} ${p.nom} ?`)) return;
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8086/patients/delete/id/${p.id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        patients = patients.filter(pat => pat.id !== p.id);
      } else {
        alert("Erreur lors de la suppression.");
      }
    } catch (e) {
      alert("Impossible de contacter le serveur.");
    }
  }

  onMount(async () => {
    localStorage.setItem('role', 'ADMIN'); // ← temporaire pour tester
    role = localStorage.getItem('role') || '';
    if (role !== 'ADMIN') { window.location.href = '/login'; return; }

    const token = localStorage.getItem('token');

    // Charger les médecins
    try {
      const res = await fetch('http://localhost:8086/admin/docteurs', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        medecins = data.map((d: any) => ({
          id: d.idDocteur,
          nom: d.nomDocteur,
          prenom: d.prenomDocteur,
          specialite: d.specialiteDocteur,
          ville: d.villeDocteur,
          email: d.emailDocteur,
          telephone: d.telephoneDocteur,
          adresse: d.adresseDocteur,
          statut: d.valider === 'VALIDE' ? 'validé' :
                  d.valider === 'REFUSE' ? 'rejeté' : 'en_attente'
        }));
      }
    } catch (e) {
      console.error('Erreur chargement médecins:', e);
    }

    // Charger les patients
    try {
      const res = await fetch('http://localhost:8086/patients/getAll', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        patients = data.map((p: any) => ({
          id: p.idPatient,
          nom: p.nomPatient,
          prenom: p.prenomPatient,
          email: p.emailPatient,
          ville: p.villePatient,
          telephone: p.telPatient
        }));
      }
    } catch (e) {
      console.error('Erreur chargement patients:', e);
    }

    chargement = false;
  });
</script>

<svelte:head>
  <title>Administration — MediLine</title>
</svelte:head>

{#if role === 'ADMIN'}
<div class="page">

  <!-- ENTETE -->
  <div class="entete">
    <div class="entete-icone">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <path d="M12 15c3.314 0 6-1.343 6-3V6c0-1.657-2.686-3-6-3S6 4.343 6 6v6c0 1.657 2.686 3 6 3z" stroke="white" stroke-width="2"/>
        <path d="M6 12c0 1.657 2.686 3 6 3s6-1.343 6-3" stroke="white" stroke-width="2"/>
        <path d="M6 15c0 1.657 2.686 3 6 3s6-1.343 6-3" stroke="white" stroke-width="2"/>
      </svg>
    </div>
    <div>
      <h1>Tableau de bord Admin</h1>
      <p>Gérez les médecins, patients et validations</p>
    </div>
  </div>

  <!-- STATS -->
  <div class="stats">
    <div class="stat-carte">
      <div class="stat-num">{medecins.length}</div>
      <div class="stat-label">Médecins total</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{medecins.filter(m => m.statut === 'en_attente').length}</div>
      <div class="stat-label stat-attente">En attente</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{medecins.filter(m => m.statut === 'validé').length}</div>
      <div class="stat-label stat-valide">Validés</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{medecins.filter(m => m.statut === 'rejeté').length}</div>
      <div class="stat-label stat-rejete">Rejetés</div>
    </div>
    <div class="stat-carte">
      <div class="stat-num">{patients.length}</div>
      <div class="stat-label">Patients</div>
    </div>
  </div>

  <!-- ONGLETS -->
  <div class="onglets">
    <button class="onglet" class:actif={onglet === 'medecins'}
      onclick={() => { onglet = 'medecins'; recherche = ''; }}>
      👨‍⚕️ Médecins
    </button>
    <button class="onglet" class:actif={onglet === 'patients'}
      onclick={() => { onglet = 'patients'; recherche = ''; }}>
      👤 Patients
    </button>
  </div>

  <!-- BARRE RECHERCHE + FILTRE -->
  <div class="barre-outils">
    <input type="text" class="recherche" bind:value={recherche}
      placeholder={onglet === 'medecins' ? 'Rechercher un médecin...' : 'Rechercher un patient...'} />
    {#if onglet === 'medecins'}
      <select class="filtre-select" bind:value={filtreStatut}>
        <option value="tous">Tous les statuts</option>
        <option value="en_attente">En attente</option>
        <option value="validé">Validés</option>
        <option value="rejeté">Rejetés</option>
      </select>
    {/if}
  </div>

  <!-- TABLEAU MÉDECINS -->
  {#if onglet === 'medecins'}
    <div class="carte-tableau">
      <table>
        <thead>
          <tr class="titre">
            <th>Nom</th>
            <th>Spécialité</th>
            <th>Ville</th>
            <th>Email</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {#each medecinsFiltres as m}
            <tr>
              <td><strong>Dr. {m.prenom} {m.nom}</strong></td>
              <td>{m.specialite}</td>
              <td>{m.ville}</td>
              <td>{m.email}</td>
              <td>
                <span class="badge
                  {m.statut === 'validé' ? 'badge-valide' :
                   m.statut === 'en_attente' ? 'badge-attente' :
                   'badge-rejete'}">
                  {m.statut === 'en_attente' ? 'En attente' :
                   m.statut === 'validé' ? 'Validé' : 'Rejeté'}
                </span>
              </td>
              <td>
                <button class="btn-detail" onclick={() => ouvrirMedecin(m)}>
                  Détails
                </button>
              </td>
            </tr>
          {/each}
          {#if medecinsFiltres.length === 0}
            <tr>
              <td colspan="6" class="vide-tableau">Aucun médecin trouvé.</td>
            </tr>
          {/if}
        </tbody>
      </table>
    </div>

  <!-- TABLEAU PATIENTS -->
  {:else if onglet === 'patients'}
    <div class="carte-tableau">
      <table>
        <thead>
          <tr class="titre">
            <th>Nom</th>
            <th>Email</th>
            <th>Ville</th>
            <th>Téléphone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {#each patientsFiltres as p}
            <tr>
              <td><strong>{p.prenom} {p.nom}</strong></td>
              <td>{p.email}</td>
              <td>{p.ville}</td>
              <td>{p.telephone}</td>
              <td>
                <button class="btn-supprimer" onclick={() => supprimerPatient(p)}>
                  🗑 Supprimer
                </button>
              </td>
            </tr>
          {/each}
          {#if patientsFiltres.length === 0}
            <tr>
              <td colspan="5" class="vide-tableau">Aucun patient trouvé.</td>
            </tr>
          {/if}
        </tbody>
      </table>
    </div>
  {/if}

  <!-- MODAL DÉTAILS MÉDECIN -->
  {#if medecinSelectionne}
    <div class="modal-fond" role="button" tabindex="0"
      onclick={() => medecinSelectionne = null}
      onkeydown={(e) => e.key === 'Escape' && (medecinSelectionne = null)}>
      <div class="modal" role="dialog" aria-modal="true" tabindex="-1"
        onclick={(e) => e.stopPropagation()}
        onkeydown={(e) => e.stopPropagation()}>

        <div class="modal-entete">
          <h3>Dossier médecin</h3>
          <button class="modal-fermer" onclick={() => medecinSelectionne = null}>✕</button>
        </div>

        <div class="modal-corps">
          <div class="modal-ligne"><span>Nom</span><strong>Dr. {medecinSelectionne.prenom} {medecinSelectionne.nom}</strong></div>
          <div class="modal-ligne"><span>Spécialité</span><strong>{medecinSelectionne.specialite}</strong></div>
          <div class="modal-ligne"><span>Ville</span><strong>{medecinSelectionne.ville}</strong></div>
          <div class="modal-ligne"><span>Adresse</span><strong>{medecinSelectionne.adresse}</strong></div>
          <div class="modal-ligne"><span>Téléphone</span><strong>{medecinSelectionne.telephone}</strong></div>
          <div class="modal-ligne"><span>Email</span><strong>{medecinSelectionne.email}</strong></div>
          <div class="modal-ligne">
            <span>Statut</span>
            <span class="badge
              {medecinSelectionne.statut === 'validé' ? 'badge-valide' :
               medecinSelectionne.statut === 'en_attente' ? 'badge-attente' :
               'badge-rejete'}">
              {medecinSelectionne.statut === 'en_attente' ? 'En attente' :
               medecinSelectionne.statut === 'validé' ? 'Validé' : 'Rejeté'}
            </span>
          </div>

          <!-- DOCUMENTS -->
          <div class="docs-section">
            <div class="docs-titre">📎 Documents soumis</div>
            {#if chargementDocs}
              <div class="docs-vide">Chargement des documents...</div>
            {:else if documentsMedecin.length === 0}
              <div class="docs-vide">Aucun document soumis.</div>
            {:else}
              {#each documentsMedecin as doc}
                <a class="doc-item" href={doc.url} target="_blank" rel="noopener noreferrer">
                  <span class="doc-icone">📄</span>
                  <span class="doc-nom">{doc.nom}</span>
                  <span class="doc-type">{doc.type}</span>
                  <span class="doc-voir">Ouvrir ↗</span>
                </a>
              {/each}
            {/if}
          </div>

          {#if medecinSelectionne.statut === 'en_attente'}
            <div class="msg-info">
              📋 Vérifiez les documents soumis avant de valider ou rejeter ce médecin.
            </div>
          {/if}
        </div>

        <div class="modal-actions">
          <button class="btn-reset" onclick={() => medecinSelectionne = null}>Fermer</button>
          {#if medecinSelectionne.statut === 'en_attente'}
            <button class="btn-rejeter" onclick={() => rejeterMedecin(medecinSelectionne!)}>
              ✕ Rejeter
            </button>
            <button class="btn-valider" onclick={() => validerMedecin(medecinSelectionne!)}>
              ✅ Valider
            </button>
          {/if}
        </div>

      </div>
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
    max-width: 1200px;
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

  .stats { display: grid; grid-template-columns: repeat(5, 1fr); gap: 1rem; margin-bottom: 1.5rem; }

  .stat-carte {
    background: white; border-radius: 14px; padding: 1.2rem 1.5rem;
    border: 1px solid rgba(0,0,0,0.07); box-shadow: 0 2px 15px rgba(0,0,0,0.04); text-align: center;
  }

  .stat-num   { font-family: 'Syne', sans-serif; font-size: 2rem; font-weight: 700; color: #e91e8c; }
  .stat-label { font-size: 0.82rem; color: #718096; margin-top: 0.2rem; }
  .stat-attente { color: #856404 !important; }
  .stat-valide  { color: #155724 !important; }
  .stat-rejete  { color: #721c24 !important; }

  /* ONGLETS */
  .onglets {
    display: flex; gap: 0.5rem; margin-bottom: 1.5rem;
    border-bottom: 2px solid #f0f0f0; padding-bottom: 0;
  }

  .onglet {
    padding: 0.7rem 1.5rem; background: transparent; border: none;
    border-bottom: 2px solid transparent; margin-bottom: -2px;
    font-family: 'DM Sans', sans-serif; font-size: 0.95rem;
    color: #718096; cursor: pointer; transition: all 0.2s;
    border-radius: 8px 8px 0 0;
  }

  .onglet:hover { color: #e91e8c; }
  .onglet.actif { color: #e91e8c; border-bottom-color: #e91e8c; font-weight: 500; }

  /* BARRÉS D'OUTILS */
  .barre-outils { display: flex; gap: 1rem; margin-bottom: 1.5rem; flex-wrap: wrap; }

  .recherche {
    flex: 1; min-width: 200px;
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    padding: 0.5rem 1rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem; color: #1a2332; outline: none; transition: border-color 0.2s;
  }

  .recherche:focus { border-color: #e91e8c; }

  .filtre-select {
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    padding: 0.5rem 1rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem; color: #1a2332; background: white; outline: none;
  }

  /* TABLEAU */
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
  .vide-tableau { color: #718096; padding: 2rem !important; }

  /* BADGES */
  .badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 100px; font-size: 0.8rem; font-weight: 500; }
  .badge-valide  { background: #d4edda; color: #155724; }
  .badge-attente { background: #fff3cd; color: #856404; }
  .badge-rejete  { background: #f8d7da; color: #721c24; }

  /* BOUTONS */
  .btn-detail {
    background: transparent; border: 1.5px solid rgba(0,0,0,0.12);
    border-radius: 8px; padding: 0.4rem 0.9rem;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }

  .btn-detail:hover { border-color: #e91e8c; color: #e91e8c; }

  .btn-supprimer {
    background: transparent; border: 1px solid #f8d7da;
    color: #dc3545; border-radius: 8px; padding: 0.4rem 0.9rem;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem;
    cursor: pointer; transition: all 0.2s;
  }

  .btn-supprimer:hover { background: #fff0f3; }

  .modal-fond {
    position: fixed; inset: 0; background: rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center; z-index: 200; padding: 1rem;
  }

  .modal {
    background: white; border-radius: 16px;
    width: 100%; max-width: 460px; box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    max-height: 90vh; overflow-y: auto;
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

  /* DOCUMENTS */
  .docs-section { margin-top: 1rem; }

  .docs-titre {
    font-size: 0.85rem; font-weight: 600; color: #4a5568;
    margin-bottom: 0.6rem;
  }

  .docs-vide { font-size: 0.82rem; color: #a0aec0; font-style: italic; padding: 0.5rem 0; }

  .doc-item {
    display: flex; align-items: center; gap: 0.6rem;
    padding: 0.6rem 0.8rem; margin-bottom: 0.5rem;
    background: #f8fafe; border: 1px solid rgba(0,0,0,0.05);
    border-radius: 8px; text-decoration: none; transition: all 0.2s;
  }

  .doc-item:hover { background: #fdf2f8; border-color: #e91e8c; }

  .doc-icone { font-size: 1.1rem; }
  .doc-nom { font-size: 0.85rem; color: #0d1b2a; font-weight: 500; flex: 1; }
  .doc-type { font-size: 0.75rem; color: #718096; text-transform: uppercase; }
  .doc-voir { font-size: 0.78rem; color: #e91e8c; font-weight: 500; }

  .msg-info {
    background: #e8f4fd; border: 1px solid #bee3f8; color: #2c5282;
    border-radius: 8px; padding: 0.7rem 1rem; font-size: 0.85rem; margin-top: 1rem;
  }

  .modal-actions {
    display: flex; justify-content: flex-end; gap: 0.8rem;
    padding: 1rem 1.5rem; border-top: 1px solid #f0f0f0;
  }

  .btn-reset {
    padding: 0.6rem 1.2rem; background: transparent;
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    font-family: 'DM Sans', sans-serif; font-size: 0.88rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }

  .btn-reset:hover { border-color: #e91e8c; color: #e91e8c; }

  .btn-valider {
    padding: 0.6rem 1.2rem; background: #28a745; color: white; border: none;
    border-radius: 8px; font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem; font-weight: 500; cursor: pointer; transition: opacity 0.2s;
  }

  .btn-valider:hover { opacity: 0.9; }

  .btn-rejeter {
    padding: 0.6rem 1.2rem; background: #dc3545; color: white; border: none;
    border-radius: 8px; font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem; font-weight: 500; cursor: pointer; transition: opacity 0.2s;
  }

  .btn-rejeter:hover { opacity: 0.9; }

  @media (max-width: 768px) {
    .stats { grid-template-columns: repeat(2, 1fr); }
    .barre-outils { flex-direction: column; }
  }
</style>