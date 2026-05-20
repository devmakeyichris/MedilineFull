<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import { onMount } from 'svelte';


	let { children } = $props();
	let searchOpen =$state (false);
    let searchQuery = $state ('');


</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<!-- NAVBAR -->
  <nav class="navbar">
    <div class="logo">
    <a href="/" class="logo-link">
      <img src="/images/LOGO.png" alt="Logo MediLine" class="logo-img">
      <span class="logo-text">Medi<strong>Line</strong></span>
    </a>
    </div>
    <div class="nav-links">

      <div class="search-wrapper">
        {#if searchOpen}
          <input 
          type="text" placeholder="Rechercher un médecin..." 
          class="search-input" 
          bind:value={searchQuery} 
          />
        {/if}
         <button class="search-btn" aria-label="Rechercher" onclick={() => searchOpen = !searchOpen}>
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
      </button>
      </div>
      <a href="#features">Fonctionnalités</a>
      <a href="/login-page" class="btn-nav">Se connecter</a>
    </div>
  </nav>

  <main>
	{@render children()}
  </main>

  <!-- FOOTER -->
  <footer>
    <div class="footer-logo">
      <span class="logo-icon"><img src="/images/LOGO.png" alt="Logo MediLine" class="logo-img"></span>
      <span class="logo-text">Medi<strong>Line</strong></span>
    </div>
    <p>© 2025 MediLine · Tous droits réservés · Conforme RGPD</p>
  </footer>

  <style>
   /* NAVBAR */
  .navbar {
    position: fixed;
    top: 0; left: 0; right: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1rem 5%;
    background: rgba(248, 250, 254, 0.842);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(0,0,0,0.06);
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Syne', sans-serif;
    font-size: 1.4rem;
  }
   .logo-img{
    height: 50px;
    width: auto;
    object-fit: contain;
   }
  .logo-icon {
    background: #1565c0;
    color: white;
    width: 32px; height: 32px;
    border-radius: 8px;
    display: flex; align-items: center; justify-content: center;
    font-size: 0.9rem;
  }

  .logo-text { color: #1a2332; }
  .logo-text strong { color: #c21852; }

  a.logo-link {
    display: flex;
    align-items: center;
    gap: 8px;
    text-decoration: none;
  }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 2rem;
  }

  .nav-links a {
    text-decoration: none;
    color: #4a5568;
    font-size: 0.95rem;
    transition: color 0.2s;
  }

  .nav-links a:hover { color: #1565c0; }

  .btn-nav {
    background: linear-gradient(135deg, #e91e8c 0%, #0d47a1 100%) !important;
    color: white !important;
    padding: 0.5rem 1.2rem;
    border-radius: 8px;
    font-weight: 500;
  }


  .btn-nav:hover { background: linear-gradient(135deg, #e91e8c 0%, #0d47a1 100%) !important; }

 .search-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;}

  .search-input {
  border: 1.5px solid rgba(21, 101, 192, 0.3);
  border-radius: 8px;
  padding: 0.4rem 0.8rem;
  font-family: 'DM Sans', sans-serif;
  font-size: 0.9rem;
  outline: none;
  width: 200px;
  transition: all 0.3s ease;
  color: #1a2332;
  background: white;
}

.search-input:focus {
  border-color: #e91e8c;
}

.search-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #4a5568;
  display: flex;
  align-items: center;
  padding: 0.3rem;
  transition: color 0.2s;
}

.search-btn:hover { color: #e91e8c; }

/* FOOTER */
  footer {
    padding: 2.5rem 5%;
    border-top: 1px solid rgba(0,0,0,0.06);
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 1rem;
  }

  footer p {
    font-size: 0.85rem;
    color: #718096;
  }

  .footer-logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Syne', sans-serif;
    font-size: 1.4rem;
  }
   .logo-img{
    height: 50px;
    width: auto;
    object-fit: contain;
   }

  .logo-icon {
    background: #1565c0;
    color: white;
    width: 32px; height: 32px;
    border-radius: 8px;
    display: flex; align-items: center; justify-content: center;
    font-size: 0.9rem;
  }

  .logo-text { color: #1a2332; }
  .logo-text strong { color: #c21852; }

  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
  }

  @media (max-width: 768px) {
    footer { justify-content: center; text-align: center; }
    .nav-links a:not(.btn-nav) { display: none; }
  }

  :global(body) {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

main {
  flex: 1;
  padding-top: 80px;
}
</style>

