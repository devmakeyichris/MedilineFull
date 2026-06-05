package app.pfe.dto;

import app.pfe.state.SexeState;

public class PatientResponse {

    private int idPatient;
    private String nomPatient;
    private String prenomPatient;
    private String emailPatient;
    private String telPatient;
    private String adressePatient;
    private String villePatient;
    private String dateNaissance;
    private SexeState sexePatient;

    public PatientResponse() {
    }

    public PatientResponse(int idPatient, String nomPatient, String prenomPatient,
                          String emailPatient, String telPatient,
                          String adressePatient, String villePatient,
                          String dateNaissance, SexeState sexePatient) {

        this.idPatient= idPatient;
        this.nomPatient = nomPatient;
        this.prenomPatient = prenomPatient;
        this.emailPatient = emailPatient;
        this.telPatient = telPatient;
        this.adressePatient = adressePatient;
        this.villePatient = villePatient;
        this.dateNaissance = dateNaissance;
        this.sexePatient = sexePatient;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public SexeState getSexePatient() {
        return sexePatient;
    }

    public void setSexePatient(SexeState
     sexePatient) {
        this.sexePatient = sexePatient;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public String getNomPatient() {
        return nomPatient;
    }

    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }

    public String getPrenomPatient() {
        return prenomPatient;
    }

    public void setPrenomPatient(String prenomPatient) {
        this.prenomPatient = prenomPatient;
    }

    public String getEmailPatient() {
        return emailPatient;
    }

    public void setEmailPatient(String emailPatient) {
        this.emailPatient = emailPatient;
    }

    public String getTelPatient() {
        return telPatient;
    }

    public void setTelPatient(String telPatient) {
        this.telPatient = telPatient;
    }

    public String getAdressePatient() {
        return adressePatient;
    }

    public void setAdressePatient(String adressePatient) {
        this.adressePatient = adressePatient;
    }

    public String getVillePatient() {
        return villePatient;
    }

    public void setVillePatient(String villePatient) {
        this.villePatient = villePatient;
    }
}