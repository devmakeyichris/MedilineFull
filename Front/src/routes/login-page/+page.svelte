<script>
  let email = $state("");
  let password = $state("");
  let erreur = $state({});
  let charger = $state(false);

  function valider() {
    erreur = {};
    if (!email.trim()) erreur.email = "L'email est obligatoire.";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) erreur.email = "Email invalide.";
    if (!password.trim()) erreur.password = "Le mot de passe est obligatoire.";
  }

  // Décode le token jwt pour extraire le rôle et l'email
  function decodeToken(token) {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  }

  async function connecter() {
    valider();
    if (Object.keys(erreur).length > 0) return;
    charger = true;

    try {
      const response = await fetch("http://localhost:8086/auth/login", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (!response.ok) {
        erreur.general = "Email ou mot de passe incorrect.";
        return;
      }

      const token = await response.text();   // ← token brut JWT
      const payload = decodeToken(token);    // ← { sub: email, role: "PATIENT"/"DOCTEUR"/"ADMIN" }

      // Sauvegarde dans localStorage
      localStorage.setItem("token", token);
      localStorage.setItem("role", payload.role);
      localStorage.setItem("email", payload.sub);

      // Redirection selon le rôle
      if (payload.role === "PATIENT")      window.location.href = "/medecin";
      else if (payload.role === "DOCTEUR") window.location.href = "/rendez-Vous";
      else if (payload.role === "ADMIN")   window.location.href = "/admin";

    } catch (e) {
      erreur.general = "Une erreur est survenue. Veuillez réessayer.";
    } finally {
      charger = false;
    }
  }
</script>

<svelte:head>
  <title>Connexion — MediLine</title>
</svelte:head>

<div class="container">
  <div class="card">

    <div class="card-head">
      <div class="head-icon">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="white" stroke-width="2" stroke-linecap="round"/>
          <circle cx="12" cy="7" r="4" stroke="white" stroke-width="2"/>
        </svg>
      </div>
      <div>
        <h1>Connexion</h1>
        <p>Accédez à votre compte</p>
      </div>
    </div>

    {#if erreur.general}
      <div class="alert-error">{erreur.general}</div>
    {/if}

    <form onsubmit={(e) => { e.preventDefault(); connecter(); }}>

      <div class="field">
        <label for="email">Email <span class="req">*</span></label>
        <input
          id="email"
          type="email"
          bind:value={email}
          placeholder="Ex: exemple@email.com"
          class:error={erreur.email}
        />
        {#if erreur.email}<span class="err-msg">{erreur.email}</span>{/if}
      </div>

      <div class="field">
        <label for="password">Mot de passe <span class="req">*</span></label>
        <input
          id="password"
          type="password"
          bind:value={password}
          placeholder="Votre mot de passe"
          class:error={erreur.password}
        />
        {#if erreur.password}<span class="err-msg">{erreur.password}</span>{/if}
      </div>

      <div class="oublie">
        <a href="/mot-de-passe-oublie">Mot de passe oublié ?</a>
      </div>

      <button type="submit" class="btn-submit" disabled={charger}>
        {#if charger}
          <span class="spinner"></span> Connexion...
        {:else}
          Se connecter
        {/if}
      </button>

    </form>

    <p class="switch">
      Pas encore de compte ?
      <a href="/register-pat">Créer un compte</a>
    </p>

  </div>
</div>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700&family=DM+Sans:wght@300;400;500&display=swap');

  .container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafe;
    padding: 2rem;
  }

  .card {
    background: white;
    border-radius: 20px;
    padding: 2.5rem;
    width: 100%;
    max-width: 440px;
    box-shadow: 0 4px 40px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0,0,0,0.06);
  }

  .card-head {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .head-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .card-head h1 {
    font-family: 'normal', sans-serif;
    font-size: 1.4rem;
    font-weight: 700;
    color: #0d1b2a;
    margin: 0;
  }

  .card-head p {
    font-size: 0.85rem;
    color: #718096;
    margin: 0;
  }

  .alert-error {
    background: #fff0f3;
    border: 1px solid #f8d7da;
    color: #c21852;
    border-radius: 10px;
    padding: 0.8rem 1rem;
    font-size: 0.9rem;
    margin-bottom: 1.5rem;
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
    margin-bottom: 1.2rem;
  }

  label {
    font-size: 0.85rem;
    font-weight: 500;
    color: #4a5568;
  }

  .req { color: #e91e8c; }

  input {
    border: 1.5px solid rgba(0,0,0,0.12);
    border-radius: 10px;
    padding: 0.7rem 1rem;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.95rem;
    color: #1a2332;
    background: white;
    outline: none;
    transition: border-color 0.2s;
  }

  input:focus { border-color: #e91e8c; }
  input.error { border-color: #e53e3e; }

  .err-msg {
    font-size: 0.8rem;
    color: #e53e3e;
  }

  .oublie {
    text-align: right;
    margin-bottom: 1.5rem;
    margin-top: -0.5rem;
  }

  .oublie a {
    font-size: 0.82rem;
    color: #e91e8c;
    text-decoration: none;
  }

  .oublie a:hover { text-decoration: underline; }

  .btn-submit {
    width: 100%;
    padding: 0.85rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    color: white;
    border: none;
    border-radius: 10px;
    font-family: 'DM Sans', sans-serif;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    transition: opacity 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .btn-submit:hover:not(:disabled) { opacity: 0.9; }
  .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

  .spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255,255,255,0.4);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  .switch {
    text-align: center;
    font-size: 0.88rem;
    color: #718096;
    margin-top: 1.5rem;
  }

  .switch a {
    color: #e91e8c;
    text-decoration: none;
    font-weight: 500;
  }

  .switch a:hover { text-decoration: underline; }
</style>