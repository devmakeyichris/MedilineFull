package app.pfe.dto;

import java.time.LocalDate;

public class OrdonnanceResponse {
    private int idOrdonnance;
    private String nomMedecin;
    private String specialite;
    private String nomPatient;
    private String dateNaissance;
    private LocalDate dateOrdonnance;
    private String contenu;
    public OrdonnanceResponse(int idOrdonnance, String nomMedecin, String specialite, String nomPatient,
            String dateNaissance, LocalDate dateOrdonnance, String contenu) {
        this.idOrdonnance = idOrdonnance;
        this.nomMedecin = nomMedecin;
        this.specialite = specialite;
        this.nomPatient = nomPatient;
        this.dateNaissance = dateNaissance;
        this.dateOrdonnance = dateOrdonnance;
        this.contenu = contenu;
    }
    public int getIdOrdonnance() {
        return idOrdonnance;
    }
    public void setIdOrdonnance(int idOrdonnance) {
        this.idOrdonnance = idOrdonnance;
    }
    public String getNomMedecin() {
        return nomMedecin;
    }
    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }
    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
    public String getNomPatient() {
        return nomPatient;
    }
    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }
    public String getDateNaissance() {
        return dateNaissance;
    }
    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
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

    // constructeur + getters / setters
}
