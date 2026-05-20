<script>
  import { onMount } from 'svelte';

  let visible = false;
  let statsVisible = false;
  let searchOpen = false;
  let searchQuery = '';

  onMount(() => {
    setTimeout(() => visible = true, 100);

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(e => { if (e.isIntersecting) statsVisible = true; });
    }, { threshold: 0.3 });

    const statsEl = document.querySelector('.stats-section');
    if (statsEl) observer.observe(statsEl);
  });

  const features = [
    {
      icon: '🔍',
      title: 'Trouvez un spécialiste',
      desc: 'Recherchez par spécialité, ville ou nom. Accédez à des profils vérifiés et fiables.'
    },
    {
      icon: '📅',
      title: 'Réservez en ligne',
      desc: 'Consultez les disponibilités en temps réel et réservez votre créneau en quelques clics.'
    },
    {
      icon: '📋',
      title: 'Gérez vos soins',
      desc: 'Ordonnances électroniques, historique médical et messagerie sécurisée au même endroit.'
    }
  ];
</script>

<div class="page">
  <!-- HERO -->
  <section class="hero">
    <div class="hero-bg">
      <div class="overlay"></div>
      <div class="blob blob1"></div>
      <div class="blob blob2"></div>
      <div class="grid-overlay"></div>
    </div>

    <div class="hero-cont" class:visible>
      <h1>Votre santé,<br/><em>simplifiée</em></h1>
      <p class="hero-desc">
        Connectez-vous avec des professionnels de santé vérifiés. Prenez rendez-vous, 
        gérez vos ordonnances et communiquez en toute sécurité.
      </p>
      <div class="hero-action">
        <a href="/register-pat" class="btn-primary">
          Je suis patient
          <span class="fleche">→</span>
        </a>
        <a href="/register-med" class="btn-secondary">
          Je suis médecin
        </a>
      </div>
    </div>

    <div class="hero-visual" class:visible>   
      <div class="card-floating card-notif">
        <span>🔔</span>
        <div>
          <strong>Rappel de rendez-vous</strong>
          <span>Demain à 10h30 — Dr. Benali</span>
        </div>
      </div>

      <div class="card-floating card-ordo">
        <span>📄</span>
        <div>
          <strong>Ordonnance disponible</strong>
          <span>Télécharger le PDF</span>
        </div>
      </div>
    </div>
  </section>
</div>
<!-- FEATURES -->
  <section class="features-section" id="features">
    <div class="section-label">Pourquoi MediLine ?</div>
    <h2>Tout ce dont vous avez besoin,<br/>au même endroit</h2>
    <div class="features-grid">
      {#each features as f, i}
        <div class="feature-card">
          <div class="feature-icon">{f.icon}</div>
          <h3>{f.title}</h3>
          <p>{f.desc}</p>
        </div>
      {/each}
    </div>
  </section>

  <!-- CTA -->

  <section class="cta-section">
    <div class="cta-box">
      <h2>Prêt à prendre soin de vous ?</h2>
      <p>Rejoignez des milliers de patients qui font confiance à MediLine.</p>
      <div class="cta-buttons">
        <a href="/register-pat" class="btn-primary">Créer un compte patient</a>
        <a href="/register-med" class="btn-ghost">Inscrire mon cabinet</a>
      </div>
    </div>  </section>


<style>
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:ital,wght@0,300;0,400;0,500;1,300&display=swap');

  :global(*, *::before, *::after) { box-sizing: border-box; margin: 0; padding: 0; }
  :global(body) { background: #f8fafe; }

  .page {
    font-family: 'DM Sans', sans-serif;
    color: #1a2332;
    overflow-x: hidden;
  }

  /* HERO */
  .hero {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 7rem 5% 4rem;
    position: relative;
    gap: 3rem;
    background-image: url('/images/accueil.jpeg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
  }

  .hero-bg {
    position: absolute;
    inset: 0;
    overflow: hidden;
    z-index: 0;
  }

  .overlay{
    position: absolute;
    inset: 0;
    background: rgba(255, 255, 255, 0);
  }
  .blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.12;
  }

  .blob1 {
    width: 600px; height: 600px;
    background: #f06ca3;
    top: -100px; right: -100px;
  }

  .blob2 {
    width: 400px; height: 400px;
    background: #ee70c4;
    bottom: 0; left: 10%;
  }

  .grid-overlay {
    position: absolute;
    inset: 0;
    background-image: 
      linear-gradient(rgba(21, 101, 192, 0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(21, 101, 192, 0.04) 1px, transparent 1px);
    background-size: 50px 50px;
  }

  .hero-cont {
    flex: 1;
    max-width: 560px;
    position: relative;
    z-index: 1;
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .hero-cont.visible {
    opacity: 1;
    transform: translateY(0);
  }

  h1 {
    font-family: 'arial', sans-serif;
    font-size: clamp(4.8rem, 7vw, 6.2rem);
    font-weight: 800;
    line-height: 1.1;
    margin-bottom: 1.2rem;
    color: #000000cd;
  }

  h1 em {
    font-style: 'arial', sans-serif;
    color: rgba(2, 2, 2, 0.612) ;
    position: relative;
  }

  h1 em::after {
    content: '';
    position: absolute;
    bottom: 2px; left: 0; right: 0;
    height: 4px;
    background: rgba(0, 0, 0, 0.582);
    border-radius: 2px;
    opacity: 0.4;
  }

  .hero-desc {
    font-size: 1.1rem;
    color: #111318;
    line-height: 1.7;
    margin-bottom: 2rem;
    font-weight: 300;
  }

  .hero-action {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
    margin-bottom: 1.2rem;
  }

  .btn-primary {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: linear-gradient(135deg, #e91e8c 0%, #0d47a1 100%);
    color: white;
    padding: 0.85rem 1.8rem;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 500;
    font-size: 1rem;
    transition: all 0.2s;
    box-shadow: 0 4px 20px rgba(21, 101, 192, 0.3);
  }

  .btn-primary:hover {
    background:#e91e8c;
    transform: translateY(-2px);
    box-shadow: 0 8px 30px rgba(21, 101, 192, 0.4);
  }

  .fleche{ transition: transform 0.2s; }
  .btn-primary:hover .fleche { transform: translateX(4px); }

  .btn-secondary {
    display: inline-flex;
    align-items: center;
    background: white;
    color: #1565c0;
    padding: 0.85rem 1.8rem;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 500;
    font-size: 1rem;
    border: 1.5px solid rgba(21, 101, 192, 0.3);
    transition: all 0.2s;
  }

  .btn-secondary:hover {
    border-color: #1565c0;
    background: rgba(21, 101, 192, 0.04);
  }

  /* HERO VISUAL */
  .hero-visual {
    flex: 1;
    max-width: 440px;
    position: relative;
    z-index: 1;
    min-height: 400px;
    opacity: 0;
    transform: translateX(30px);
    transition: all 1s cubic-bezier(0.16, 1, 0.3, 1) 0.3s;
  }

  .hero-visual.visible {
    opacity: 1;
    transform: translateX(0);
  }

  .card-floating {
    background: white;
    border-radius: 16px;
    padding: 1.2rem 1.4rem;
    box-shadow: 0 8px 40px rgba(0,0,0,0.08);
    position: absolute;
    border: 1px solid rgba(0,0,0,0.05);
  }
  
  .card-notif {
    display: flex;
    align-items: center;
    gap: 0.8rem;
    font-size: 0.85rem;
    bottom: 80px; right: -30px;
    width: 240px;
    animation: float 4s ease-in-out infinite 1s;
  }

  .card-notif div, .card-ordo div {
    display: flex;
    flex-direction: column;
  }

  .card-notif strong, .card-ordo strong { font-size: 0.85rem; color: #1a2332; }
  .card-notif span, .card-ordo span { font-size: 0.75rem; color: #718096; }

  .card-ordo {
    display: flex;
    align-items: center;
    gap: 0.8rem;
    font-size: 0.85rem;
    bottom: 0; left: 20px;
    width: 210px;
    animation: float 4s ease-in-out infinite 2s;
  }

  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-8px); }
  }

  /* FEATURES */
  .features-section {
    padding: 6rem 5%;
    text-align: center;
  }

  .section-label {
    font-size: 0.85rem;
    font-weight: 600;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: #1565c0;
    margin-bottom: 1rem;
  }

  .features-section h2 {
    font-family: 'Syne', sans-serif;
    font-size: clamp(1.8rem, 3vw, 2.6rem);
    font-weight: 700;
    margin-bottom: 3rem;
    color: #0d1b2a;
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    max-width: 1000px;
    margin: 0 auto;
  }

  .feature-card {
    background: white;
    border-radius: 16px;
    padding: 2rem;
    border: 1px solid rgba(0,0,0,0.06);
    text-align: left;
    transition: all 0.3s;
    opacity: 1;
  }

  .feature-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 40px rgba(21, 101, 192, 0.1);
    border-color: rgba(21, 101, 192, 0.15);
  }

  .feature-icon {
    font-size: 2rem;
    margin-bottom: 1rem;
  }

  .feature-card h3 {
    font-family: 'Syne', sans-serif;
    font-size: 1.15rem;
    font-weight: 700;
    margin-bottom: 0.6rem;
    color: #0d1b2a;
  }

  .feature-card p {
    font-size: 0.95rem;
    color: #718096;
    line-height: 1.6;
  }

  /* CTA */
  .cta-section {
    padding: 6rem 5%;
  }

  .cta-box {
    background: linear-gradient(135deg, #e91e8c 0%, #0d47a1 100%);
    border-radius: 24px;
    padding: 4rem;
    text-align: center;
    max-width: 800px;
    margin: 0 auto;
    position: relative;
    overflow: hidden;
  }

  .cta-box::before {
    content: '';
    position: absolute;
    top: -50%; right: -10%;
    width: 400px; height: 400px;
    background: rgba(255,255,255,0.05);
    border-radius: 50%;
  }

  .cta-box h2 {
    font-family: 'Syne', sans-serif;
    font-size: 2rem;
    font-weight: 700;
    color: white;
    margin-bottom: 0.8rem;
  }

  .cta-box p {
    color: rgba(255,255,255,0.75);
    margin-bottom: 2rem;
    font-size: 1.05rem;
  }

  .cta-buttons {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }

  .cta-buttons .btn-primary {
    background: white;
    color: #1565c0;
    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  }

  .cta-buttons .btn-primary:hover {
    background: #f0f4ff;
    box-shadow: 0 8px 30px rgba(0,0,0,0.2);
  }

  .btn-ghost {
    display: inline-flex;
    align-items: center;
    background: transparent;
    color: white;
    padding: 0.85rem 1.8rem;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 500;
    font-size: 1rem;
    border: 1.5px solid rgba(255,255,255,0.4);
    transition: all 0.2s;
  }

  .btn-ghost:hover { background: rgba(255,255,255,0.1); }

  
  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
  }

  @media (max-width: 768px) {
    .hero {
      flex-direction: column;
      padding-top: 6rem;
      text-align: center;
    }
    .hero-action { justify-content: center; }
    .hero-visual { display: none; }
    .cta-box { padding: 2.5rem 1.5rem; }
  }

</style>
