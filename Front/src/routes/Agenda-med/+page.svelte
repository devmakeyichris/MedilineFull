<script lang="ts">
    type Creneau = {
        date: string;
        heure: string;
        disponible: boolean;
    };

    let calendrier: Creneau[] = [
        {date: "2026-02-18" , heure: "11:00", disponible: true},
        {date: "2026-02-18", heure: "14:10", disponible: true},
    ];

    let newDate = "";
    let newHour = "";

    function ajouterCreneau(){
        if(newDate && newHour){
            calendrier.push({
                date: newDate,
                heure: newHour,
                disponible: true
            });
           newDate = "";
           newHour = ""; 
        }
    }

    function bloquerCreneau(index: number){
        calendrier[index].disponible = false;
    } 

    function supprimerCreneau(index: number){
        calendrier.splice(index, 1);
    }
</script>

<div class="container">
  <div class="card">
    <div class="card-head">
      <h1>Agenda du Médecin</h1>
      <p>Ajoutez, bloquez ou supprimez vos créneaux de consultation</p>
    </div>

    <form on:submit|preventDefault={ajouterCreneau}>                 
        <label for="date">Date:</label>
        <input type="date" bind:value={newDate} required/>
        <label for="time">Heure:</label>
        <input type="time" bind:value={newHour} required/>
        <button type="submit" class="btn-submit">Ajouter</button>
    </form>

    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Heure</th>
                <th>Disponibilité</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            {#each calendrier as creneau, index}
            <tr>
                <td>{creneau.date}</td>
                <td>{creneau.heure}</td>
                <td>{creneau.disponible ? "Disponible" : "Bloqué"}</td>
                <td>
                    {#if creneau.disponible}
                    <button type="button" class="btn-action" on:click={() => bloquerCreneau(index)}>Bloquer</button>
                    {/if}
                    <button type="button" class="btn-delete" on:click={() => supprimerCreneau(index)}>❌ Supprimer</button>
                </td>
            </tr>
            {/each}
        </tbody>
    </table>
  </div>
</div>

<style>
  .container {
    display: flex;
    justify-content: center;
    padding: 95px 84px;
  }

  .card {
    width: 100%;
    max-width: 900px;
    background: white;
    border: 0.5px solid #f8bbd0;
    border-radius: 16px;
    overflow: hidden;
  }

  .card-head {
    background: linear-gradient(135deg, #e91e8c 0%, #c2185b 100%);
    padding: 20px 24px;
    color: white;
    border-radius: 16px 16px 0 0;
  }

  .card-head h1 {
    font-size: 18px;
    font-weight: 600;
  }

  .card-head p {
    font-size: 12px;
    color: rgba(255,255,255,0.8);
  }

  form {
    padding: 20px;
    display: flex;
    gap: 12px;
    align-items: center;
    flex-wrap: wrap;
  }

  label {
    font-size: 13px;
    font-weight: 500;
    color: #555;
  }

  input {
    border: 1px solid #f8bbd0;
    border-radius: 8px;
    padding: 6px 10px;
    font-size: 13px;
    background: #fff9fb;
    outline: none;
  }

  input:focus {
    border-color: #e91e8c;
    background: white;
    box-shadow: 0 0 0 3px rgba(233, 30, 140, 0.08);
  }

  .btn-submit, .btn-action {
    background: linear-gradient(135deg, #e91e8c, #c2185b);
    color: white;
    border: none;
    border-radius: 8px;
    padding: 8px 14px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }

  .btn-submit:hover, .btn-action:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(194, 24, 91, 0.35);
  }

  .btn-delete {
    background: none;
    border: none;
    color: #e53935;
    cursor: pointer;
    font-size: 13px;
    margin-left: 6px;
  }

  .btn-delete:hover {
    color: #b71c1c;
  }

  table {
    width: 100%;
    border-collapse: collapse;
    margin: 20px;
    font-size: 13px;
  }

  th, td {
    border: 1px solid #f8bbd0;
    padding: 8px;
    text-align: center;
  }

  th {
    background: #fce4ec;
    color: #c2185b;
    font-weight: 600;
  }

  td {
    background: #fff9fb;
  }
</style>
