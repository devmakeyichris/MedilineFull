package app.pfe.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Ordonnance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOrdonnance;
    
    private LocalDate dateOrdonnance;
    
    @Column(columnDefinition = "TEXT")
    private String contenu;
    
    @ManyToOne
    @JoinColumn(name = "id_patient")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "id_docteur")
    private Docteur docteur;
    
    public Ordonnance() {}

    public int getIdOrdonnance() {
        return idOrdonnance;
    }

    public void setIdOrdonnance(int idOrdonnance) {
        this.idOrdonnance = idOrdonnance;
    }

    public LocalDate getDateOrdonnance() {
        return dateOrdonnance;
    }

    public void setDateOrdonnance(LocalDate dateOrdonnance) {
        this.dateOrdonnance = dateOrdonnance;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Docteur getDocteur() {
        return docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }
    
    // getters / setters
}
