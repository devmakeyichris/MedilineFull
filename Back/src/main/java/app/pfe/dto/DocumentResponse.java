package app.pfe.dto;

public class DocumentResponse {

    private int idDocument;
    private String nameDocument;
    private String typeDocument;
    private String urlDocument;
    private Integer idDocteur;

    public DocumentResponse() {
    }

    public DocumentResponse(int idDocument, String nameDocument, String typeDocument, String urlDocument, Integer idDocteur) {
        this.idDocument = idDocument;
        this.nameDocument = nameDocument;
        this.typeDocument = typeDocument;
        this.urlDocument = urlDocument;
        this.idDocteur = idDocteur;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getNameDocument() {
        return nameDocument;
    }

    public void setNameDocument(String nameDocument) {
        this.nameDocument = nameDocument;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getUrlDocument() {
        return urlDocument;
    }

    public void setUrlDocument(String urlDocument) {
        this.urlDocument = urlDocument;
    }

    public Integer getIdDocteur() {
        return idDocteur;
    }

    public void setIdDocteur(Integer idDocteur) {
        this.idDocteur = idDocteur;
    }
}