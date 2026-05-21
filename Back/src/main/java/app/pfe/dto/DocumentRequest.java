package app.pfe.dto;


public class DocumentRequest {
    private String name;
    private String type;
    private String url;
    private Integer docteurId; // pour lier au docteur


    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Integer getDocteurId() {
        return docteurId;
    }
    public void setDocteurId(Integer docteurId) {
        this.docteurId = docteurId;
    }

    // Getters & setters
}

