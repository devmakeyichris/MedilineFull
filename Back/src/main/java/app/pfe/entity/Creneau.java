package app.pfe.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCreneau;

    private LocalDate dateCreneau;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    private boolean bloque;

    @ManyToOne
    @JoinColumn(name = "id_docteur")
    private Docteur docteur;

    public Creneau() {}

    public int getIdCreneau() {
        return idCreneau;
    }

    public void setIdCreneau(int idCreneau) {
        this.idCreneau = idCreneau;
    }

    public LocalDate getDateCreneau() {
        return dateCreneau;
    }

    public void setDateCreneau(LocalDate dateCreneau) {
        this.dateCreneau = dateCreneau;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public boolean isBloque() {
        return bloque;
    }

    public void setBloque(boolean bloque) {
        this.bloque = bloque;
    }

    public Docteur getDocteur() {
        return docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }

    
}