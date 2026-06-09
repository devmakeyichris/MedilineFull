package app.pfe.dto;

import app.pfe.state.DocteurState;
import app.pfe.state.SexeState;

public class DocteurResponse {

    private Integer idDocteur;
    private String nomDocteur;
    private String prenomDocteur;
    private String emailDocteur;
    private String telephoneDocteur;
    private String adresseDocteur;
    private String villeDocteur;
    private String specialiteDocteur;
    private String descDocteur;
    private SexeState sexeDocteur;
    private DocteurState valider;
    private String photoProfil;

    public DocteurResponse() {
    }

    public DocteurResponse(
            Integer idDocteur,
            String nomDocteur,
            String prenomDocteur,
            String emailDocteur,
            String telephoneDocteur,
            String adresseDocteur,
            String villeDocteur,
            String specialiteDocteur,
            String descDocteur,
            SexeState sexeDocteur,
            DocteurState valider,
            String photoProfil) {

        this.idDocteur = idDocteur;
        this.nomDocteur = nomDocteur;
        this.prenomDocteur = prenomDocteur;
        this.emailDocteur = emailDocteur;
        this.telephoneDocteur = telephoneDocteur;
        this.adresseDocteur = adresseDocteur;
        this.villeDocteur = villeDocteur;
        this.specialiteDocteur = specialiteDocteur;
        this.descDocteur = descDocteur;
        this.sexeDocteur = sexeDocteur;
        this.valider = valider;
        this.photoProfil = photoProfil;
    }

    public Integer getIdDocteur() { return idDocteur; }
    public void setIdDocteur(Integer idDocteur) { this.idDocteur = idDocteur; }

    public String getNomDocteur() { return nomDocteur; }
    public void setNomDocteur(String nomDocteur) { this.nomDocteur = nomDocteur; }

    public String getPrenomDocteur() { return prenomDocteur; }
    public void setPrenomDocteur(String prenomDocteur) { this.prenomDocteur = prenomDocteur; }

    public String getEmailDocteur() { return emailDocteur; }
    public void setEmailDocteur(String emailDocteur) { this.emailDocteur = emailDocteur; }

    public String getTelephoneDocteur() { return telephoneDocteur; }
    public void setTelephoneDocteur(String telephoneDocteur) { this.telephoneDocteur = telephoneDocteur; }

    public String getAdresseDocteur() { return adresseDocteur; }
    public void setAdresseDocteur(String adresseDocteur) { this.adresseDocteur = adresseDocteur; }

    public String getVilleDocteur() { return villeDocteur; }
    public void setVilleDocteur(String villeDocteur) { this.villeDocteur = villeDocteur; }

    public String getSpecialiteDocteur() { return specialiteDocteur; }
    public void setSpecialiteDocteur(String specialiteDocteur) { this.specialiteDocteur = specialiteDocteur; }

    public String getDescDocteur() { return descDocteur; }
    public void setDescDocteur(String descDocteur) { this.descDocteur = descDocteur; }

    public SexeState getSexeDocteur() { return sexeDocteur; }
    public void setSexeDocteur(SexeState sexeDocteur) { this.sexeDocteur = sexeDocteur; }

    public DocteurState getValider() { return valider; }
    public void setValider(DocteurState valider) { this.valider = valider; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }
}