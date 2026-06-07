<script lang="ts">
  import { onMount } from 'svelte';

  // ── ROLE ──
  let role = $state('');
  let userId = $state('');

  // ── VARIABLES MÉDECIN ──
  let patientChoisi = $state('');        // id du patient sélectionné
  let nomPatient = $state('');
  let dateNaissance = $state('');
  let contenu = $state('');
  let nomMedecin = $state('');
  let specialite = $state('');
  let erreur = $state<Record<string, string>>({});
  let succes = $state(false);

  // Liste des patients (pour le menu déroulant)
  let listePatients = $state<any[]>([]);

  const todayISO = new Date().toISOString().split('T')[0];
  let dateOrdonnance = $state(todayISO);
  const today = new Date().toLocaleDateString('fr-FR');

  // ── VUE PATIENT ──
  let ordonnances = $state<any[]>([]);
  let ordoSelectionnee = $state<any>(null);
  let ordoApercu = $state<any>(null);   // ordonnance à imprimer en PDF (patient)

  onMount(async () => {
    role = localStorage.getItem('role') || '';
    if (!role) { window.location.href = '/login'; return; }

    userId = localStorage.getItem('userId') || '';
    const token = localStorage.getItem('token');

    if (role === 'DOCTEUR') {
      // Pré-remplir nom + spécialité du docteur connecté (optionnel)
      nomMedecin = localStorage.getItem('nom') || '';

      // Charger la liste des patients pour le menu déroulant
      try {
        const res = await fetch('http://localhost:8086/patients/all', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
          listePatients = await res.json();
        }
      } catch (e) {
        console.error('Erreur chargement patients:', e);
      }
    }

    if (role === 'PATIENT') {
      // Charger les ordonnances du patient connecté
      try {
        const res = await fetch(`http://localhost:8086/ordonnances/patient/${userId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
          const data = await res.json();
          ordonnances = data.map((o: any) => ({
            id: o.idOrdonnance,
            nomMedecin: o.nomDocteur,
            specialite: o.specialiteDocteur,
            nomPatient: o.nomPatient,
            dateNaissance: o.dNaissPatient,
            date: o.dateOrdonnance ? new Date(o.dateOrdonnance).toLocaleDateString('fr-FR') : '',
            contenu: o.contenu
          }));
        }
      } catch (e) {
        console.error('Erreur chargement ordonnances:', e);
      }
    }
  });

  // Quand le médecin choisit un patient → remplir nom + date de naissance
  function choisirPatient(e: Event) {
    const id = (e.target as HTMLSelectElement).value;
    patientChoisi = id;
    const p = listePatients.find((pat: any) => String(pat.idPatient) === String(id));
    if (p) {
      nomPatient = `${p.nomPatient} ${p.prenomPatient}`;
      dateNaissance = p.dNaissPatient || '';
    }
  }

  function valider() {
    erreur = {};
    if (!patientChoisi) erreur.patient = "Veuillez sélectionner un patient.";
    if (!contenu.trim()) erreur.contenu = "L'ordonnance ne peut pas être vide.";
  }

  // ── DOCTEUR : enregistrer + générer le PDF ──
  async function genererPDF() {
    valider();
    if (Object.keys(erreur).length > 0) return;

    // 1. Enregistrer dans le backend
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8086/ordonnances/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          patientId: parseInt(patientChoisi),
          docteurId: parseInt(userId),
          dateOrdonnance: dateOrdonnance,
          contenu: contenu
        })
      });

      if (!response.ok) {
        erreur.general = "Erreur lors de l'enregistrement de l'ordonnance.";
        return;
      }
    } catch (e) {
      erreur.general = "Impossible de contacter le serveur.";
      return;
    }

    // 2. Générer le PDF depuis l'aperçu (élément réel rendu par Svelte)
    const { default: html2pdf } = await import('html2pdf.js');
    const element = document.getElementById('apercu');
    if (!element) return;

    element.style.position = 'static';
    element.style.left = '0';

    await html2pdf().set({
      margin: 0,
      filename: `ordonnance_${nomPatient.replace(/\s+/g, '_')}_${today.replace(/\//g, '-')}.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    }).from(element).save();

    element.style.position = 'absolute';
    element.style.left = '-9999px';

    succes = true;
    setTimeout(() => succes = false, 4000);
  }

  function reinitialiser() {
    patientChoisi = '';
    nomPatient = '';
    dateNaissance = '';
    contenu = '';
    erreur = {};
    succes = false;
  }

  // ── PATIENT : télécharger une ordonnance reçue ──
  // On affiche l'ordo dans un aperçu réel (#apercu-dl) puis on génère le PDF
  async function telechargerOrdo(ordo: any) {
    ordoApercu = ordo;

    // Attendre que Svelte rende l'aperçu
    await new Promise(resolve => {
      requestAnimationFrame(() => requestAnimationFrame(resolve));
    });

    const { default: html2pdf } = await import('html2pdf.js');
    const element = document.getElementById('apercu-dl');
    if (!element) return;

    element.style.position = 'static';
    element.style.left = '0';

    await html2pdf().set({
      margin: 0,
      filename: `ordonnance_${ordo.nomPatient.replace(/\s+/g, '_')}_${ordo.date.replace(/\//g, '-')}.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    }).from(element).save();

    element.style.position = 'absolute';
    element.style.left = '-9999px';

    ordoApercu = null;
  }
</script>

<svelte:head>
  <title>Ordonnances — MediLine</title>
</svelte:head>

{#if role === 'DOCTEUR'}

  <div class="page">
    <div class="entete">
      <div class="entete-icone">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <polyline points="14 2 14 8 20 8" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <line x1="16" y1="13" x2="8" y2="13" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <line x1="16" y1="17" x2="8" y2="17" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h1>Rédiger une ordonnance</h1>
        <p>Remplissez le formulaire puis générez le PDF</p>
      </div>
    </div>

    {#if succes}
      <div class="msg-succes">✅ Ordonnance enregistrée et téléchargée avec succès !</div>
    {/if}
    {#if erreur.general}
      <div class="msg-erreur-box">⚠️ {erreur.general}</div>
    {/if}

    <div class="grille">
      <div class="carte">
        <h2>Informations médecin</h2>
        <div class="champ">
          <label for="nom-med">Nom du médecin</label>
          <input type="text" id="nomMedecin" bind:value={nomMedecin} placeholder="Ex: Dr. Benali" />
        </div>
        <div class="champ">
          <label for="specialite">Spécialité</label>
          <input type="text" id="specialite" bind:value={specialite} placeholder="Ex: Cardiologue" />
        </div>
        <div class="champ">
          <label for="date-ordo">Date de l'ordonnance</label>
          <input type="date" id="dateOrdonnance" bind:value={dateOrdonnance} />
        </div>
      </div>

      <div class="carte">
        <h2>Ordonnance</h2>

        <div class="champ">
          <label for="select-patient">Patient <span class="requis">*</span></label>
          <select id="select-patient" onchange={choisirPatient} class:erreur-champ={erreur.patient}>
            <option value="">-- Sélectionnez un patient --</option>
            {#each listePatients as p}
              <option value={p.idPatient}>{p.nomPatient} {p.prenomPatient}</option>
            {/each}
          </select>
          {#if erreur.patient}<span class="msg-erreur">{erreur.patient}</span>{/if}
        </div>

        <div class="champ">
          <label for="contenu">Contenu de l'ordonnance <span class="requis">*</span></label>
          <textarea bind:value={contenu} rows="9"
            placeholder="Ex: Paracétamol 1g — 3x/jour pendant 5 jours..."
            class:erreur-champ={erreur.contenu}></textarea>
          {#if erreur.contenu}<span class="msg-erreur">{erreur.contenu}</span>{/if}
        </div>

        <!-- APERCU PDF (invisible) -->
        <div id="apercu" class="apercu">
          <div class="apercu-entete">
            <div>
              <div class="apercu-logo">MediLine</div>
              <div class="apercu-soustitre">Plateforme médicale sécurisée</div>
            </div>
            <div class="apercu-date">
              Date : {dateOrdonnance ? new Date(dateOrdonnance).toLocaleDateString('fr-FR') : today}
            </div>
          </div>
          <div class="apercu-medecin">
            <strong>{nomMedecin || 'Nom du médecin'}</strong>
            <span>{specialite || 'Spécialité'}</span>
          </div>
          <hr class="ligne-rose" />
          <div class="apercu-patient">
            <div class="sous-titre">Informations patient</div>
            <p>Nom : <strong>{nomPatient || '—'}</strong></p>
            <p>Date de naissance : <strong>{dateNaissance ? new Date(dateNaissance).toLocaleDateString('fr-FR') : '—'}</strong></p>
          </div>
          <hr class="ligne-grise" />
          <div class="apercu-contenu">
            <div class="apercu-titre">ORDONNANCE MÉDICALE</div>
            <div class="apercu-barre"></div>
            <div class="apercu-texte">{contenu || '—'}</div>
          </div>
          <div class="apercu-signature">
            <div class="signature-ligne"></div>
            <div class="signature-label">Signature du médecin</div>
            <div class="signature-nom">{nomMedecin || '—'}</div>
          </div>
          <div class="apercu-footer">
            Document généré par MediLine · Conforme RGPD
          </div>
        </div>

        <div class="boutons">
          <button class="btn-reset" onclick={reinitialiser}>Réinitialiser</button>
          <button class="btn-generer" onclick={genererPDF}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            Enregistrer & générer le PDF
          </button>
        </div>
      </div>
    </div>
  </div>

{:else if role === 'PATIENT'}

  <div class="page">
    <div class="entete">
      <div class="entete-icone">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <polyline points="14 2 14 8 20 8" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <line x1="16" y1="13" x2="8" y2="13" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <line x1="16" y1="17" x2="8" y2="17" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h1>Mes ordonnances</h1>
        <p>Consultez et téléchargez vos ordonnances reçues</p>
      </div>
    </div>

    {#if ordonnances.length === 0}
      <div class="vide">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#cbd5e0" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
        </svg>
        <p>Aucune ordonnance reçue pour le moment.</p>
      </div>
    {:else}
      <div class="liste-ordonnances">
        {#each ordonnances as ordo}
          <div class="carte-ordo">
            <div class="carte-ordo-gauche">
              <div class="ordo-icone">📄</div>
              <div class="ordo-info">
                <strong>{ordo.nomMedecin}</strong>
                <span>{ordo.specialite}</span>
                <span class="ordo-date-badge">📅 {ordo.date}</span>
              </div>
            </div>
            <div class="carte-ordo-droite">
              <div class="ordo-pat-info">Patient : <strong>{ordo.nomPatient}</strong></div>
              <button class="btn-voir-ordo" onclick={() => ordoSelectionnee = ordo}>👁 Voir</button>
              <button class="btn-telecharger" onclick={() => telechargerOrdo(ordo)}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
                Télécharger PDF
              </button>
            </div>
          </div>
        {/each}
      </div>

      <!-- MODAL APERCU -->
      {#if ordoSelectionnee}
        <div class="modal-fond" role="button" tabindex="0"
          onclick={() => ordoSelectionnee = null}
          onkeydown={(e) => e.key === 'Escape' && (ordoSelectionnee = null)}>
          <div class="modal" role="dialog" aria-modal="true" tabindex="-1"
            onclick={(e) => e.stopPropagation()}
            onkeydown={(e) => e.stopPropagation()}>
            <div class="modal-entete">
              <h3>Aperçu de l'ordonnance</h3>
              <button class="modal-fermer" onclick={() => ordoSelectionnee = null}>✕</button>
            </div>
            <div class="modal-corps">
              <div class="modal-ligne"><span>Médecin</span><strong>{ordoSelectionnee.nomMedecin}</strong></div>
              <div class="modal-ligne"><span>Spécialité</span><strong>{ordoSelectionnee.specialite}</strong></div>
              <div class="modal-ligne"><span>Date</span><strong>{ordoSelectionnee.date}</strong></div>
              <div class="modal-ligne"><span>Patient</span><strong>{ordoSelectionnee.nomPatient}</strong></div>
              <hr style="border: none; border-top: 1px solid #f0f0f0; margin: 1rem 0;" />
              <div class="modal-titre">ORDONNANCE MÉDICALE</div>
              <div class="modal-contenu">{ordoSelectionnee.contenu}</div>
            </div>
            <div class="modal-actions">
              <button class="btn-reset" onclick={() => ordoSelectionnee = null}>Fermer</button>
              <button class="btn-telecharger" onclick={() => { telechargerOrdo(ordoSelectionnee); ordoSelectionnee = null; }}>
                Télécharger PDF
              </button>
            </div>
          </div>
        </div>
      {/if}

      <!-- APERCU PDF PATIENT (invisible, rendu par Svelte = capture fiable) -->
      {#if ordoApercu}
        <div id="apercu-dl" class="apercu">
          <div class="apercu-entete">
            <div>
              <div class="apercu-logo">MediLine</div>
              <div class="apercu-soustitre">Plateforme médicale sécurisée</div>
            </div>
            <div class="apercu-date">Date : {ordoApercu.date}</div>
          </div>
          <div class="apercu-medecin">
            <strong>{ordoApercu.nomMedecin}</strong>
            <span>{ordoApercu.specialite}</span>
          </div>
          <hr class="ligne-rose" />
          <div class="apercu-patient">
            <div class="sous-titre">Informations patient</div>
            <p>Nom : <strong>{ordoApercu.nomPatient}</strong></p>
            <p>Date de naissance : <strong>{ordoApercu.dateNaissance ? new Date(ordoApercu.dateNaissance).toLocaleDateString('fr-FR') : '—'}</strong></p>
          </div>
          <hr class="ligne-grise" />
          <div class="apercu-contenu">
            <div class="apercu-titre">ORDONNANCE MÉDICALE</div>
            <div class="apercu-barre"></div>
            <div class="apercu-texte">{ordoApercu.contenu}</div>
          </div>
          <div class="apercu-signature">
            <div class="signature-ligne"></div>
            <div class="signature-label">Signature du médecin</div>
            <div class="signature-nom">{ordoApercu.nomMedecin}</div>
          </div>
          <div class="apercu-footer">
            Document généré par MediLine · Conforme RGPD
          </div>
        </div>
      {/if}
    {/if}
  </div>

{:else}
  <div style="display:flex; align-items:center; justify-content:center; height:50vh; color:#718096;">
    Chargement...
  </div>
{/if}

<style>
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700&family=DM+Sans:wght@300;400;500&display=swap');

  .page { padding: 2rem 5%; max-width: 1100px; margin: 0 auto; font-family: 'DM Sans', sans-serif; }

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

  .msg-erreur { font-size: 0.8rem; color: #e53e3e; }

  .grille { display: grid; grid-template-columns: 1fr 2fr; gap: 1.5rem; align-items: start; }

  .carte {
    background: white; border-radius: 16px; padding: 1.8rem;
    border: 1px solid rgba(0,0,0,0.07); box-shadow: 0 2px 20px rgba(0,0,0,0.05);
  }

  .carte h2 {
    font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700;
    color: #0d1b2a; margin-bottom: 1.2rem;
    padding-bottom: 0.7rem; border-bottom: 1.5px solid #f0f0f0;
  }

  .champ { display: flex; flex-direction: column; gap: 0.4rem; margin-bottom: 1rem; }

  label { font-size: 0.85rem; font-weight: 500; color: #4a5568; }
  .requis { color: #e91e8c; }

  input, textarea, select {
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 10px;
    padding: 0.7rem 1rem; font-family: 'DM Sans', sans-serif;
    font-size: 0.95rem; color: #1a2332; background: white;
    outline: none; transition: border-color 0.2s; resize: vertical;
  }

  input:focus, textarea:focus, select:focus { border-color: #e91e8c; }
  .erreur-champ { border-color: #e53e3e !important; }

  .apercu { position: absolute; left: -9999px; top: 0; background: white; width: 794px; }

  .apercu-entete {
    background: #e91e8c; color: white; padding: 1rem 1.5rem;
    display: flex; justify-content: space-between; align-items: center;
  }

  .apercu-logo { font-family: 'Syne', sans-serif; font-size: 1.3rem; font-weight: 700; }
  .apercu-soustitre { font-size: 0.78rem; opacity: 0.85; }
  .apercu-date { font-size: 0.85rem; }

  .apercu-medecin { padding: 1rem 1.5rem; display: flex; flex-direction: column; gap: 0.2rem; }
  .apercu-medecin strong { font-size: 1rem; color: #0d1b2a; }
  .apercu-medecin span   { font-size: 0.85rem; color: #718096; }

  .ligne-rose { border: none; border-top: 1.5px solid #e91e8c; margin: 0 1.5rem; }
  .ligne-grise { border: none; border-top: 1px solid #e2e8f0; margin: 0 1.5rem; }

  .apercu-patient { padding: 1rem 1.5rem; }

  .sous-titre {
    font-size: 0.8rem; font-weight: 600; text-transform: uppercase;
    letter-spacing: 0.08em; color: #718096; margin-bottom: 0.6rem;
  }

  .apercu-patient p { font-size: 0.9rem; color: #4a5568; margin-bottom: 0.3rem; }
  .apercu-contenu { padding: 1.2rem 1.5rem; min-height: 100px; }

  .apercu-titre {
    font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700;
    color: #e91e8c; text-align: center; margin-bottom: 0.4rem;
  }

  .apercu-barre { width: 80px; height: 2px; background: #e91e8c; margin: 0 auto 1rem; border-radius: 2px; }
  .apercu-texte { font-size: 0.95rem; color: #1a2332; line-height: 1.8; white-space: pre-wrap; }

  .apercu-signature { padding: 1rem 1.5rem; display: flex; flex-direction: column; align-items: flex-end; gap: 0.3rem; }
  .signature-ligne { width: 160px; height: 1px; background: #e2e8f0; }
  .signature-label { font-size: 0.78rem; color: #718096; }
  .signature-nom { font-size: 0.9rem; font-weight: 600; color: #0d1b2a; }

  .apercu-footer { background: #f8f9fa; padding: 0.7rem 1.5rem; font-size: 0.75rem; color: #a0aec0; text-align: center; }

  .boutons { display: flex; justify-content: flex-end; gap: 1rem; }

  .btn-reset {
    padding: 0.75rem 1.5rem; background: transparent;
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 10px;
    font-family: 'DM Sans', sans-serif; font-size: 0.95rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }

  .btn-reset:hover { border-color: #e91e8c; color: #e91e8c; }

  .btn-generer {
    display: flex; align-items: center; gap: 0.5rem; padding: 0.75rem 1.8rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    border: none; border-radius: 10px; font-family: 'DM Sans', sans-serif;
    font-size: 0.95rem; font-weight: 500; color: white; cursor: pointer; transition: opacity 0.2s;
  }

  .btn-generer:hover { opacity: 0.9; }

  .liste-ordonnances { display: flex; flex-direction: column; gap: 1rem; }

  .carte-ordo {
    background: white; border-radius: 14px; padding: 1.5rem;
    border: 1px solid rgba(0,0,0,0.07); box-shadow: 0 2px 15px rgba(0,0,0,0.04);
    display: flex; align-items: center; justify-content: space-between; gap: 1rem;
  }

  .carte-ordo-gauche { display: flex; align-items: center; gap: 1rem; }
  .ordo-icone { font-size: 2rem; flex-shrink: 0; }

  .ordo-info { display: flex; flex-direction: column; gap: 0.2rem; }
  .ordo-info strong { font-size: 1rem; color: #0d1b2a; }
  .ordo-info span   { font-size: 0.85rem; color: #718096; }
  .ordo-date-badge  { font-size: 0.8rem !important; color: #e91e8c !important; font-weight: 500; }

  .carte-ordo-droite { display: flex; flex-direction: column; align-items: flex-end; gap: 0.8rem; }
  .ordo-pat-info { font-size: 0.85rem; color: #718096; }

  .btn-telecharger {
    display: flex; align-items: center; gap: 0.5rem; padding: 0.6rem 1.2rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    border: none; border-radius: 8px; font-family: 'DM Sans', sans-serif;
    font-size: 0.85rem; font-weight: 500; color: white; cursor: pointer; transition: opacity 0.2s;
  }

  .btn-telecharger:hover { opacity: 0.9; }

  .vide { display: flex; flex-direction: column; align-items: center; gap: 1rem; padding: 4rem; color: #a0aec0; font-size: 0.95rem; }

  .modal-fond {
    position: fixed; inset: 0; background: rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center; z-index: 200; padding: 1rem;
  }

  .modal { background: white; border-radius: 16px; width: 100%; max-width: 500px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }

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
    padding: 0.5rem 0; font-size: 0.88rem; border-bottom: 1px solid #f8f8f8;
  }

  .modal-ligne span   { color: #718096; }
  .modal-ligne strong { color: #0d1b2a; }

  .modal-titre { font-size: 0.9rem; font-weight: 700; color: #e91e8c; text-align: center; margin-bottom: 0.8rem; }

  .modal-contenu {
    font-size: 0.9rem; color: #1a2332; line-height: 1.8;
    white-space: pre-wrap; background: #fdf2f8; border-radius: 8px; padding: 1rem;
  }

  .modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; padding: 1rem 1.5rem; border-top: 1px solid #f0f0f0; }

  .btn-voir-ordo {
    padding: 0.5rem 1rem; background: transparent;
    border: 1.5px solid rgba(0,0,0,0.12); border-radius: 8px;
    font-family: 'DM Sans', sans-serif; font-size: 0.82rem;
    cursor: pointer; color: #4a5568; transition: all 0.2s;
  }

  .btn-voir-ordo:hover { border-color: #e91e8c; color: #e91e8c; }

  @media (max-width: 768px) {
    .grille { grid-template-columns: 1fr; }
    .carte-ordo { flex-direction: column; align-items: flex-start; }
    .carte-ordo-droite { align-items: flex-start; }
  }
</style>