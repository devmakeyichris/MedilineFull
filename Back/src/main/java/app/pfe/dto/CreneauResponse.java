package app.pfe.dto;



import java.time.LocalDate;
import java.time.LocalTime;

public class CreneauResponse {
    private int idCreneau;
    private LocalDate dateCreneau;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean bloque;
    private int docteurId;

    public CreneauResponse(int idCreneau, LocalDate dateCreneau, LocalTime heureDebut,
                           LocalTime heureFin, boolean bloque, int docteurId) {
        this.idCreneau = idCreneau;
        this.dateCreneau = dateCreneau;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.bloque = bloque;
        this.docteurId = docteurId;
    }

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

    public int getDocteurId() {
        return docteurId;
    }

    public void setDocteurId(int docteurId) {
        this.docteurId = docteurId;
    }

    // getters et setters
}