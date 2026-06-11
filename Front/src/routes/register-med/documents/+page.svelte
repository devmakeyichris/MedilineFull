<script lang="ts">
  let photoProfil = $state<File | null>(null);
  let previewPhoto = $state('');
  let identite = $state<File[]>([]);
  let diplomes = $state<File[]>([]);
  let autresDocs = $state<File[]>([]);
  let erreur = $state<Record<string, string>>({});
  let chargement = $state(false);

  function handlePhoto(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      photoProfil = file;
      previewPhoto = URL.createObjectURL(file);
    }
  }

  function supprimerPhoto() {
    photoProfil = null;
    previewPhoto = '';
  }

  function handleFiles(event: Event, type: string) {
    const files: File[] = Array.from((event.target as HTMLInputElement).files || []);
    if (type === "identite") identite = [...identite, ...files];
    if (type === "diplomes") diplomes = [...diplomes, ...files];
    if (type === "autres") autresDocs = [...autresDocs, ...files];
  }

  function supprimerFichier(type: string, index: number) {
    if (type === "identite") identite = identite.filter((_, i) => i !== index);
    if (type === "diplomes") diplomes = diplomes.filter((_, i) => i !== index);
    if (type === "autres") autresDocs = autresDocs.filter((_, i) => i !== index);
  }

  function validerDocuments() {
    erreur = {};
    if (!photoProfil) erreur.photo = "Veuillez ajouter une photo de profil.";
    if (identite.length === 0) erreur.identite = "Veuillez ajouter au moins une pièce d'identité.";
    if (diplomes.length === 0) erreur.diplomes = "Veuillez ajouter au moins un diplôme.";
    return Object.keys(erreur).length === 0;
  }

  async function continuer() {
    if (!validerDocuments()) return;

    const docteurId = localStorage.getItem("docteurId");
    if (!docteurId) {
      alert("Docteur non identifié. Veuillez recommencer l'inscription.");
      return;
    }

    chargement = true;

    // 1. Upload de la photo de profil
    try {
      const photoForm = new FormData();
      photoForm.append("file", photoProfil!);

      const responsePhoto = await fetch(`http://localhost:8086/docteurs/${docteurId}/photo`, {
        method: "POST",
        body: photoForm
      });

      if (!responsePhoto.ok) {
        erreur.general = "Erreur lors de l'envoi de la photo.";
        chargement = false;
        return;
      }
    } catch (e) {
      erreur.general = "Impossible de contacter le serveur (photo).";
      chargement = false;
      return;
    }

    // 2. Upload des documents (identité, diplômes, autres)
    try {
      const formData = new FormData();
      identite.forEach(file => formData.append("identite", file));
      diplomes.forEach(file => formData.append("diplomes", file));
      autresDocs.forEach(file => formData.append("autresDocs", file));
      formData.append("idDocteur", docteurId);


      const response = await fetch("http://localhost:8086/documents/upload", {
        method: "POST",
        body: formData
      });

      if (response.ok) {
        alert("Documents envoyés avec succès ! Votre compte est en attente de validation par l'admin.");
        window.location.href = "/login-page";
      } else {
        erreur.general = "Erreur lors de l'envoi des documents.";
      }
    } catch (e) {
      erreur.general = "Impossible de contacter le serveur (documents).";
    } finally {
      chargement = false;
    }
  }
</script>

<svelte:head>
  <title>Documents Médecin — MediLine</title>
</svelte:head>

<div class="container">
  <div class="card">

    <div class="card-head">
      <div class="head-icon">📄</div>
      <div>
        <h1>Documents requis</h1>
        <p>Ajoutez votre photo, pièces d'identité et diplômes</p>
      </div>
    </div>

    {#if erreur.general}
      <div class="alert-error">{erreur.general}</div>
    {/if}

    <form onsubmit={(e) => { e.preventDefault(); continuer(); }}>

      <!-- PHOTO DE PROFIL -->
      <div class="section">Photo de profil</div>
      <div class="field">
        <label for="photo">Photo <span class="req">*</span></label>

        <div class="photo-zone">
          {#if previewPhoto}
            <img src={previewPhoto} alt="Aperçu" class="photo-preview" />
            <button type="button" class="btn-supprimer-photo" onclick={supprimerPhoto}>
              🗑 Supprimer
            </button>
          {:else}
            <label class="photo-upload-label">
              <span class="upload-icon">📷</span>
              <span class="upload-text">Cliquez pour ajouter votre photo</span>
              <input type="file" accept="image/*" onchange={handlePhoto} hidden />
            </label>
          {/if}
        </div>
        {#if erreur.photo}<span class="err-msg">{erreur.photo}</span>{/if}
      </div>

      <!-- PIÈCE D'IDENTITÉ -->
      <div class="section">Pièce d'identité</div>
      <div class="field">
        <label for="identite">Ajouter vos pièces d'identité <span class="req">*</span></label>
        <input type="file" multiple onchange={(e) => handleFiles(e, "identite")} />
        {#if erreur.identite}<span class="err-msg">{erreur.identite}</span>{/if}
        {#if identite.length > 0}
          <ul>
            {#each identite as f, i}
              <li>{f.name} <button type="button" class="btn-delete" onclick={() => supprimerFichier("identite", i)}>❌</button></li>
            {/each}
          </ul>
        {/if}
      </div>

      <!-- DIPLÔMES -->
      <div class="section">Diplômes</div>
      <div class="field">
        <label for="diplomes">Ajouter vos diplômes <span class="req">*</span></label>
        <input type="file" multiple onchange={(e) => handleFiles(e, "diplomes")} />
        {#if erreur.diplomes}<span class="err-msg">{erreur.diplomes}</span>{/if}
        {#if diplomes.length > 0}
          <ul>
            {#each diplomes as f, i}
              <li>{f.name} <button type="button" class="btn-delete" onclick={() => supprimerFichier("diplomes", i)}>❌</button></li>
            {/each}
          </ul>
        {/if}
      </div>

      <!-- AUTRES -->
      <div class="section">Autres documents</div>
      <div class="field">
        <label for="autres">Ajouter d'autres documents (optionnel)</label>
        <input type="file" multiple onchange={(e) => handleFiles(e, "autres")} />
        {#if autresDocs.length > 0}
          <ul>
            {#each autresDocs as f, i}
              <li>{f.name} <button type="button" class="btn-delete" onclick={() => supprimerFichier("autres", i)}>❌</button></li>
            {/each}
          </ul>
        {/if}
      </div>
       

      <p class="note"><span class="req">*</span> Champs obligatoires</p>
      <button type="submit" class="btn-submit" disabled={chargement}>
        {#if chargement}
          Envoi en cours...
        {:else}
          Envoyer les documents
        {/if}
      </button>
    </form>
  </div>
</div>

<style>
  .container { display: flex; justify-content: center; padding: 40px 24px; }

  .card {
    width: 100%; max-width: 580px;
    background: white; border: 0.5px solid #f8bbd0;
    border-radius: 16px; overflow: hidden;
  }

  .card-head {
    background: linear-gradient(135deg, #e91e8c 0%, #c2185b 100%);
    padding: 20px 24px;
    display: flex; align-items: center; gap: 12px;
    color: white;
  }

  .head-icon { font-size: 24px; flex-shrink: 0; }
  .card-head h1 { font-size: 16px; font-weight: 600; margin: 0; }
  .card-head p  { font-size: 12px; color: rgba(255,255,255,0.72); margin: 2px 0 0; }

  .alert-error {
    background: #fff0f3; border: 1px solid #f8d7da;
    color: #c21852; border-radius: 10px;
    padding: 0.8rem 1rem; font-size: 0.9rem;
    margin: 1rem 1.5rem 0;
  }

  form { padding: 24px; display: flex; flex-direction: column; gap: 14px; }

  .section {
    font-size: 14px; font-weight: 600;
    text-transform: uppercase; letter-spacing: 1.2px;
    color: #c2185b;
    border-bottom: 1.5px solid #fce4ec;
    padding-bottom: 6px; margin-top: 10px;
  }

  .field { display: flex; flex-direction: column; gap: 5px; }
  label  { font-size: 13px; font-weight: 500; color: #555; }
  .req   { color: #e91e8c; }

  /* PHOTO */
  .photo-zone {
    display: flex; align-items: center; gap: 1rem;
    padding: 1rem; background: #fff9fb;
    border: 1.5px dashed #f8bbd0; border-radius: 10px;
  }

  .photo-preview {
    width: 100px; height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid white;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }

  .photo-upload-label {
    width: 100%;
    display: flex; flex-direction: column;
    align-items: center; gap: 0.5rem;
    padding: 1.5rem; cursor: pointer;
    transition: opacity 0.2s;
  }

  .photo-upload-label:hover { opacity: 0.7; }

  .upload-icon { font-size: 2rem; }
  .upload-text { font-size: 0.85rem; color: #c2185b; font-weight: 500; }

  .btn-supprimer-photo {
    background: transparent; border: 1px solid #f8d7da;
    color: #dc3545; border-radius: 6px;
    padding: 0.4rem 0.8rem; font-size: 0.78rem;
    cursor: pointer; transition: all 0.2s;
    font-family: inherit;
  }

  .btn-supprimer-photo:hover { background: #fff0f3; }

  input[type="file"] {
    border: 1px solid #f8bbd0; border-radius: 8px;
    background: #fff9fb; padding: 6px;
    font-size: 13px; color: #333; cursor: pointer;
  }

  input[type="file"]:focus {
    border-color: #e91e8c;
    background: white;
    box-shadow: 0 0 0 3px rgba(233,30,140,0.08);
  }

  .err-msg { font-size: 11px; color: #e53935; }
  ul       { margin-top: 5px; font-size: 13px; color: #555; padding-left: 1rem; }
  li       { margin-bottom: 4px; }
  .note    { font-size: 11px; color: #aaa; }

  .btn-delete {
    background: none; border: none;
    color: #e53935; cursor: pointer;
    font-size: 13px; margin-left: 6px;
  }

  .btn-submit {
    height: 42px;
    background: linear-gradient(135deg, #e91e8c, #c2185b);
    color: white; border: none; border-radius: 8px;
    font-size: 14px; font-weight: 600;
    cursor: pointer; transition: all 0.2s;
    width: 100%;
    display: flex; align-items: center; justify-content: center;
  }

  .btn-submit:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(194,24,91,0.35);
  }

  .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

  @media (max-width: 560px) {
    form { padding: 16px; }
    .card-head { padding: 16px; }
  }
</style>
