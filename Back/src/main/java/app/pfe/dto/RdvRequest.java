package app.pfe.dto;

import java.time.LocalDateTime;



public class RdvRequest {
    private Integer idRdv;
    private LocalDateTime date;
    private String heure;
    private String status;
    private Integer patientId;
    private Integer docteurId;
    
    public Integer getIdRdv() {
        return idRdv;
    }
    
    public void setIdRdv(Integer idRdv) {
        this.idRdv = idRdv;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDocteurId() {
        return docteurId;
    }

    public void setDocteurId(Integer docteurId) {
        this.docteurId = docteurId;
    }
}