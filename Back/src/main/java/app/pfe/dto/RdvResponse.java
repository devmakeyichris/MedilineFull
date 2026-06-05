package app.pfe.dto;

import java.time.LocalDateTime;

public class RdvResponse {

    private int idRdv;
    private LocalDateTime dateRdv;
    private String heureRdv;
    private String statusRdv;
    private int patientId;
    private int docteurId;

    public RdvResponse() {}

    public RdvResponse(int idRdv, LocalDateTime dateRdv, String heureRdv,
                       String statusRdv, int patientId, int docteurId) {
        this.idRdv = idRdv;
        this.dateRdv = dateRdv;
        this.heureRdv = heureRdv;
        this.statusRdv = statusRdv;
        this.patientId = patientId;
        this.docteurId = docteurId;
    }

    public int getIdRdv() {
        return idRdv;
    }

    public void setIdRdv(int idRdv) {
        this.idRdv = idRdv;
    }

    public LocalDateTime getDateRdv() {
        return dateRdv;
    }

    public void setDateRdv(LocalDateTime dateRdv) {
        this.dateRdv = dateRdv;
    }

    public String getHeureRdv() {
        return heureRdv;
    }

    public void setHeureRdv(String heureRdv) {
        this.heureRdv = heureRdv;
    }

    public String getStatusRdv() {
        return statusRdv;
    }

    public void setStatusRdv(String statusRdv) {
        this.statusRdv = statusRdv;
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