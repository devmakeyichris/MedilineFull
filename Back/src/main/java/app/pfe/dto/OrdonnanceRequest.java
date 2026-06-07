package app.pfe.dto;

import java.time.LocalDate;

public class OrdonnanceRequest {
    private String contenu;
    private LocalDate dateOrdonnance;
    private int patientId;
    private int docteurId;
    
    public String getContenu() {
        return contenu;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    public LocalDate getDateOrdonnance() {
        return dateOrdonnance;
    }
    public void setDateOrdonnance(LocalDate dateOrdonnance) {
        this.dateOrdonnance = dateOrdonnance;
    }
    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    public int getDocteurId() {
        return docteurId;
    }
    public void setDocteurId(int docteurId) {
        this.docteurId = docteurId;
    }

   
}