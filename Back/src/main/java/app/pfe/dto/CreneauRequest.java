package app.pfe.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public class CreneauRequest {
    private LocalDate dateCreneau;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean bloque;
    private int docteurId;
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
    public int getDocteurId() {
        return docteurId;
    }
    public void setDocteurId(int docteurId) {
        this.docteurId = docteurId;
    }
}