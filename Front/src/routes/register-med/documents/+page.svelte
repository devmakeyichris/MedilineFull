<script lang="ts">
  let identite: File[] = [];
  let diplomes: File[] = [];
  let autresDocs: File[] = [];
  let erreur: Record<string, string> = {};

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

    const formData = new FormData();
    identite.forEach(file => formData.append("identite", file));
    diplomes.forEach(file => formData.append("diplomes", file));
    autresDocs.forEach(file => formData.append("autresDocs", file));
    formData.append("idDocteur", docteurId);

    try {
      const response = await fetch("http://localhost:8086/documents/add", {
        method: "POST",
        body: formData
      });

      if (response.ok) {
        alert("Documents envoyés avec succès ! Votre compte est en attente de validation par l’admin.");
        window.location.href = "/login-med/confirmation";
      } else {
        erreur.general = "Erreur lors de l'envoi des documents.";
      }
    } catch (e) {
      erreur.general = "Impossible de contacter le serveur.";
    }
  }
</script>

<svelte:head>
  <title>Documents Médecin</title>
</svelte:head>

<div class="container">
  <div class="card">

    <div class="card-head">
      <div class="head-icon">
        📄
      </div>
      <div>
        <h1>Documents requis</h1>
        <p>Ajoutez vos pièces d’identité et diplômes pour valider votre inscription</p>
      </div>
    </div>

    <form on:submit|preventDefault={continuer}>
      <div class="section">Pièce d'identité</div>
      <div class="field">
        <label for="piece">Ajouter vos pièces d'identité <span class="req">*</span></label>
        <input type="file" multiple on:change={(e) => handleFiles(e, "identite")} />
        {#if erreur.identite}<span class="err-msg">{erreur.identite}</span>{/if}
        {#if identite.length > 0}
        <ul>
          {#each identite as f, i}
             <li>{f.name} <button type="button" class="btn-delete" on:click={() => supprimerFichier("identite", i)}>❌</button></li>
         {/each}
        </ul>
        {/if}
      </div>

      <div class="section">Diplômes</div>
      <div class="field">
        <label for="diplome">Ajouter vos diplômes <span class="req">*</span></label>
        <input type="file" multiple on:change={(e) => handleFiles(e, "diplomes")} />
        {#if erreur.diplomes}<span class="err-msg">{erreur.diplomes}</span>{/if}
        {#if diplomes.length > 0}
        <ul>
          {#each diplomes as f, i}
             <li>{f.name} <button type="button" class="btn-delete" on:click={() => supprimerFichier("diplomes", i)}>❌</button></li>
         {/each}
        </ul>
        {/if}
      </div>

      <div class="section">Autres documents</div>
      <div class="field">
        <label for="autres">Ajouter d'autres documents (optionnel)</label>
        <input type="file" multiple on:change={(e) => handleFiles(e, "autres")} />
        {#if autresDocs.length > 0}
          <ul>
            {#each autresDocs as f, i}
              <li>{f.name} <button type="button" class="btn-delete" on:click={() => supprimerFichier("autres", i)}>❌</button></li>
            {/each}
          </ul>
        {/if}
      </div>
       

      <p class="note"><span class="req">*</span> Champs obligatoires</p>
      <button type="submit" class="btn-submit">Envoyer les documents</button>
    </form>
  </div>
</div>

<style>
  .container {
    display: flex;
    justify-content: center;
    padding: 40px 24px;
  }

  .card {
    width: 100%;
    max-width: 580px;
    background: white;
    border: 0.5px solid #f8bbd0;
    border-radius: 16px;
    overflow: hidden;
  }

  .card-head {
    background: linear-gradient(135deg, #e91e8c 0%, #c2185b 100%);
    padding: 20px 24px;
    display: flex;
    align-items: center;
    gap: 12px;
    color: white;
  }

  .head-icon {
    font-size: 24px;
    flex-shrink: 0;
  }

  .card-head h1 {
    font-size: 16px;
    font-weight: 600;
  }

  .card-head p {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.72);
    margin-top: 2px;
  }

  form {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .section {
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 1.2px;
    color: #c2185b;
    border-bottom: 1.5px solid #fce4ec;
    padding-bottom: 6px;
    margin-top: 10px;
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  label {
    font-size: 13px;
    font-weight: 500;
    color: #555;
  }

  .req { color: #e91e8c; }

  input[type="file"] {
    border: 1px solid #f8bbd0;
    border-radius: 8px;
    background: #fff9fb;
    padding: 6px;
    font-size: 13px;
    color: #333;
    cursor: pointer;
  }

  input[type="file"]:focus {
    border-color: #e91e8c;
    background: white;
    box-shadow: 0 0 0 3px rgba(233, 30, 140, 0.08);
  }

  .err-msg {
    font-size: 11px;
    color: #e53935;
  }

  ul {
    margin-top: 5px;
    font-size: 13px;
    color: #555;
  }

  .note {
    font-size: 11px;
    color: #aaa;
  }

  .btn-submit {
    height: 42px;
    background: linear-gradient(135deg, #e91e8c, #c2185b);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .btn-submit:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(194, 24, 91, 0.35);
  }

  @media (max-width: 560px) {
    form { padding: 16px; }
    .card-head { padding: 16px; }
  }
</style>
