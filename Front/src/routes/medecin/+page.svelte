<script lang="ts">
 import { onMount } from 'svelte';
  import { goto } from '$app/navigation';

  type Medecin = {
    id: number;
    nom: string;
    prenom: string;
    specialite: string;
    photo: string;
    adresse: string;
  };

  let medecins: Medecin[] = [
    { id: 1, nom: "Dr. Martin", prenom: "Jean", specialite: "Cardiologue", photo: "/images/martin.jpg", adresse: "123 Rue de la Santé, Casablanca" },
    { id: 2, nom: "Dr. Amina", prenom: "Fatima", specialite: "Pédiatre", photo: "/images/amina.jpg", adresse: "Chahdia, Meknès" }
  ];
   
  onMount(async () => {
  const response = await fetch("http://localhost:8086/docteurs/valider");
  if (response.ok) {
    const data = await response.json();
    medecins = data.map((d: any) => ({
      id: d.idDocteur,
      nom: d.nomDocteur,
      prenom: d.prenomDocteur,
      specialite: d.specialiteDocteur,
      photo: d.photoDocteur || "/images/default.jpg", // récupéré du backend
      adresse: d.villeDocteur
    }));
  } else {
    alert("Erreur lors du chargement des médecins validés.");
  }
});



  function prendreRendezVous(medecin: Medecin) {
    goto(`/rendez-Vous/${medecin.id}`);
  }
</script>

<svelte:head>
  <title>Choisissez votre médecin — MediLine</title>
</svelte:head>

<div class="page">

  <div class="entete">
    <div class="entete-icone">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="white" stroke-width="2" stroke-linecap="round"/>
        <circle cx="12" cy="7" r="4" stroke="white" stroke-width="2"/>
      </svg>
    </div>
    <div>
      <h1>Choisissez votre médecin</h1>
      <p>Sélectionnez un professionnel de santé et prenez rendez-vous</p>
    </div>
  </div>

  <div class="grille">
    {#each medecins as m}
      <div class="card">
        <div class="card-photo">
          <img src={m.photo} alt="Photo de {m.nom}" />
        </div>
        <div class="card-corps">
          <h2>{m.nom} {m.prenom}</h2>
          <span class="badge-specialite">{m.specialite}</span>
          <p class="adresse">📍 {m.adresse}</p>
        </div>
        <button class="btn-rdv" onclick={() => prendreRendezVous(m)}>
          Prendre rendez-vous →
        </button>
      </div>
    {/each}
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

  .entete {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .entete-icone {
    width: 50px; height: 50px;
    border-radius: 14px;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }

  .entete h1 {
    font-family: 'Syne', sans-serif;
    font-size: 1.6rem; font-weight: 700;
    color: #0d1b2a; margin: 0;
  }

  .entete p { font-size: 0.88rem; color: #718096; margin: 0; }

  .grille {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 1.5rem;
  }

  .card {
    background: white;
    border-radius: 16px;
    border: 1px solid rgba(0,0,0,0.07);
    box-shadow: 0 2px 20px rgba(0,0,0,0.05);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    transition: transform 0.2s, box-shadow 0.2s;
  }

  .card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(233,30,140,0.1);
  }

  .card-photo {
    background: #fdf2f8;
    display: flex;
    justify-content: center;
    padding: 1.5rem;
  }

  .card-photo img {
    width: 90px; height: 90px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid white;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }

  .card-corps {
    padding: 1rem 1.2rem;
    flex: 1;
    text-align: center;
  }

  .card-corps h2 {
    font-family: 'Syne', sans-serif;
    font-size: 1rem; font-weight: 700;
    color: #0d1b2a; margin: 0 0 0.5rem;
  }

  .badge-specialite {
    display: inline-block;
    color: #e91e8c;
    font-size: 0.78rem;
    font-weight: 600;
    padding: 0.25rem 0.8rem;
    margin-bottom: 0.8rem;
  }

  .adresse {
    font-size: 0.82rem;
    color: #718096;
    margin: 0;
  }

  .btn-rdv {
    margin: 0 1.2rem 1.2rem;
    padding: 0.75rem;
    background: linear-gradient(135deg, #e91e8c, #c21852);
    color: white;
    border: none;
    border-radius: 10px;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: opacity 0.2s;
    width: calc(100% - 2.4rem);
  }

  .btn-rdv:hover { opacity: 0.9; }

  @media (max-width: 600px) {
    .grille { grid-template-columns: 1fr; }
  }
</style>