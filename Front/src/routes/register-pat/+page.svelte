<script lang="ts">
  let nom = $state('');
  let prenom = $state('');
  let email = $state('');
  let telephone = $state('');
  let adresse = $state('');
  let ville = $state('');
  let password = $state('');
  let confirmerMotdepasse = $state('');
  let erreur = $state<Record<string, string>>({});
  let succes = $state(false);
  let sexe = $state('');
  let dateNaissance = $state('');


  function valider() {
    erreur = {};
    if (!nom.trim()) erreur.nom = "Le nom est obligatoire.";
    if (!prenom.trim()) erreur.prenom = "Le prénom est obligatoire.";
    if (!email.trim()) erreur.email = "L'email est obligatoire.";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) erreur.email = "Email invalide.";
    if (!telephone.trim()) erreur.telephone = "Le téléphone est obligatoire.";
    else if (!/^[0-9+\s]{8,15}$/.test(telephone)) erreur.telephone = "Numéro invalide.";
    if (!ville.trim()) erreur.ville = "La ville est obligatoire.";
    if (!dateNaissance) erreur.dateNaissance = "La date de naissance est obligatoire.";
    if (!password) erreur.password = "Le mot de passe est obligatoire.";
    else if (password.length < 8) erreur.password = "Le mot de passe doit contenir au moins 8 caractères.";
    if (confirmerMotdepasse !== password) erreur.confirmerMotdepasse = "Les mots de passe ne correspondent pas.";
    if (!sexe) erreur.sexe = "Le sexe est obligatoire.";
    return Object.keys(erreur).length === 0;
  }

  async function inscrire() {
    if (!valider()) return;

    try {
      const response = await fetch("http://localhost:8086/patients/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nom, prenom, email, telephone, adresse, ville, sexe, dateNaissance, password })
      });

      if (response.ok) {
        succes = true;
      } else {
        // Récupérer le message d'erreur du backend
      const messageErreur = await response.text();
      erreur.general = messageErreur || "Erreur lors de l'inscription ou Email déjà utilisé.";
      }
    } catch (e) {
      erreur.general = "Impossible de contacter le serveur.";
    }
  }
 
</script>

<svelte:head>
  <title>Inscription Patient — MediLine</title>
</svelte:head>

<div class="container">
  <div class="card">

    {#if succes}
      <div class="succes-box">
        <div class="check-icon">✓</div>
        <h2>Compte créé avec succès !</h2>
        <p>Bienvenue <strong>{prenom} {nom}</strong>. Un email de confirmation a été envoyé à <strong>{email}</strong>.</p>
        <a href="/login-page" class="btn-submit">Se connecter</a>
      </div>

    {:else}

      <div class="card-head">
        <div class="head-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <circle cx="12" cy="7" r="4" stroke="white" stroke-width="2"/>
          </svg>
        </div>
        <div>
          <h1>Créer un compte patient</h1>
          <p>Rejoignez MediLine et accédez à vos soins en ligne</p>
        </div>
      </div>

      {#if erreur.general}
        <div class="alert-error">{erreur.general}</div>
      {/if}

      <form onsubmit={(e) => { e.preventDefault(); inscrire(); }}>

        <div class="section">Informations personnelles</div>

        <div class="row2">
          <div class="field">
            <label for="nom">Nom <span class="req">*</span></label>
            <input id="nom" type="text" bind:value={nom}
              placeholder="Ex: Benali" class:error={erreur.nom} />
            {#if erreur.nom}<span class="err-msg">{erreur.nom}</span>{/if}
          </div>
          <div class="field">
            <label for="prenom">Prénom <span class="req">*</span></label>
            <input id="prenom" type="text" bind:value={prenom}
              placeholder="Ex: Amina" class:error={erreur.prenom} />
            {#if erreur.prenom}<span class="err-msg">{erreur.prenom}</span>{/if}
          </div>
        </div>

        <div class="row2">
          <div class="field">
            <label for="ville">Ville <span class="req">*</span></label>
            <input id="ville" type="text" bind:value={ville}
              placeholder="Ex: Casablanca" class:error={erreur.ville} />
            {#if erreur.ville}<span class="err-msg">{erreur.ville}</span>{/if}
          </div>
          <div class="field">
            <label for="adresse">Adresse <span class="opt">(optionnel)</span></label>
            <input id="adresse" type="text" bind:value={adresse}
              placeholder="Ex: 12 rue des Fleurs" />
          </div>
        </div>

        <div class="field">
          <label for="sexe">Sexe <span class="req">*</span></label>
          <select bind:value={sexe} class:error={erreur.sexe}>
            <option value="">-- Sélectionnez --</option>
            <option value="HOMME">Masculin</option>
            <option value="FEMME">Féminin</option>
          </select>
          {#if erreur.sexe}<span class="err-msg">{erreur.sexe}</span>{/if}
        </div>
        <div class="field">
        <label for="dateNaissance">Date de Naissance <span class="req">*</span></label>
        <input id="dateNaissance" type="date" bind:value={dateNaissance}
          class:error={erreur.dateNaissance} />
        {#if erreur.dateNaissance}<span class="err-msg">{erreur.dateNaissance}</span>{/if}
       </div>

        <div class="section">Coordonnées & sécurité</div>

        <div class="field">
          <label for="telephone">Téléphone <span class="req">*</span></label>
          <input id="telephone" type="tel" bind:value={telephone}
            placeholder="Ex: +212 6 12 34 56 78" class:error={erreur.telephone} />
          {#if erreur.telephone}<span class="err-msg">{erreur.telephone}</span>{/if}
        </div>

        <div class="field">
          <label for="email">Adresse email <span class="req">*</span></label>
          <input id="email" type="email" bind:value={email}
            placeholder="Ex: patient@email.com" class:error={erreur.email} />
          {#if erreur.email}<span class="err-msg">{erreur.email}</span>{/if}
        </div>

        <div class="row2">
          <div class="field">
            <label for="password">Mot de passe <span class="req">*</span></label>
            <input id="password" type="password" bind:value={password}
              placeholder="Min. 8 caractères" class:error={erreur.password} />
            {#if erreur.password}<span class="err-msg">{erreur.password}</span>{/if}
          </div>
          <div class="field">
            <label for="confirmer">Confirmer <span class="req">*</span></label>
            <input id="confirmer" type="password" bind:value={confirmerMotdepasse}
              placeholder="Répéter" class:error={erreur.confirmerMotdepasse} />
            {#if erreur.confirmerMotdepasse}<span class="err-msg">{erreur.confirmerMotdepasse}</span>{/if}
          </div>
        </div>

        <p class="note"><span class="req">*</span> Champs obligatoires</p>

        <button type="submit" class="btn-submit">Créer mon compte</button>

        <p class="login-link">Déjà inscrit ? <a href="/login-page">Se connecter</a></p>
        <p class="login-link">Vous êtes médecin ? <a href="/register-med">Inscription médecin</a></p>

      </form>
    {/if}

  </div>
</div>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700&family=DM+Sans:wght@300;400;500&display=swap');

  .container {
    display: flex;
    justify-content: center;
    padding: 40px 24px;
    font-family: 'DM Sans', sans-serif;
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
  }

  .head-icon {
    width: 42px; height: 42px;
    background: rgba(255,255,255,0.18);
    border-radius: 10px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }

  .card-head h1 { font-size: 16px; font-weight: 600; color: white; margin: 0; }
  .card-head p  { font-size: 12px; color: rgba(255,255,255,0.72); margin: 2px 0 0; }

  .alert-error {
    background: #fff0f3; border: 1px solid #f8d7da;
    color: #c21852; border-radius: 10px;
    padding: 0.8rem 1rem; font-size: 0.9rem;
    margin: 1rem 1.5rem 0;
  }

  form {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .section {
    font-size: 14px; font-weight: 600;
    text-transform: uppercase; letter-spacing: 1.2px;
    color: #c2185b;
    border-bottom: 1.5px solid #fce4ec;
    padding-bottom: 6px;
  }

  .row2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

  .field { display: flex; flex-direction: column; gap: 5px; }

  label { font-size: 13px; font-weight: 500; color: #555; }
  .req  { color: #e91e8c; }
  .opt  { font-weight: 400; color: #aaa; font-size: 11px; }

  input, select {
    height: 38px;
    padding: 0 12px;
    border: 1px solid #f8bbd0;
    border-radius: 8px;
    background: #fff9fb;
    font-size: 13px;
    color: #333;
    outline: none;
    transition: all 0.2s;
    font-family: inherit;
  }

  input:focus, select:focus {
    border-color: #e91e8c;
    background: white;
    box-shadow: 0 0 0 3px rgba(233,30,140,0.08);
  }

  input.error, select.error {
    border-color: #e53935;
    background: #fff5f5;
  }

  .err-msg { font-size: 11px; color: #e53935; }
  .note    { font-size: 11px; color: #aaa; }

  .btn-submit {
    height: 42px;
    background: linear-gradient(135deg, #e91e8c, #c2185b);
    color: white; border: none; border-radius: 8px;
    font-size: 14px; font-weight: 600; cursor: pointer;
    transition: all 0.2s; font-family: inherit;
    width: 100%;
    display: flex; align-items: center; justify-content: center;
    text-decoration: none;
  }

  .btn-submit:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(194,24,91,0.35);
  }

  .login-link { text-align: center; font-size: 13px; color: #888; }
  .login-link a { color: #c2185b; font-weight: 600; text-decoration: none; }
  .login-link a:hover { text-decoration: underline; }

  .succes-box {
    padding: 48px 32px; text-align: center;
    display: flex; flex-direction: column;
    align-items: center; gap: 14px;
  }

  .check-icon {
    width: 60px; height: 60px;
    background: linear-gradient(135deg, #e91e8c, #c2185b);
    border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    font-size: 1.6rem; color: white;
  }

  .succes-box h2 { font-size: 1.3rem; color: #c2185b; }
  .succes-box p  { color: #777; max-width: 340px; line-height: 1.6; font-size: 14px; }

  @media (max-width: 560px) {
    .row2 { grid-template-columns: 1fr; }
    form { padding: 16px; }
    .card-head { padding: 16px; }
  }
</style>