package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.pfe.dto.DocteurResponse;
import app.pfe.dto.DocumentResponse;
import app.pfe.dto.RegisterDocteurRequest;
import app.pfe.entity.Docteur;
import app.pfe.entity.Document;
import app.pfe.entity.Rdv;
import app.pfe.service.DocteurService;
import app.pfe.service.DocumentService;
import app.pfe.service.RdvService;




@RestController
@RequestMapping("/docteurs")
public class DocteurController {
    
    private final RdvService rdvService;
    private final DocteurService docteurService;
    private final DocumentService documentService;
    
    
    public DocteurController(RdvService rdvService, DocteurService docteurService, DocumentService documentService) { 
        
        this.rdvService = rdvService; 
        this.docteurService = docteurService;
        this.documentService = documentService;
    } 
    
    @PostMapping("/add")
    public ResponseEntity<DocteurResponse> ajouterDocteur(@RequestBody RegisterDocteurRequest dto) {
        
        Docteur docteur = new Docteur();
        docteur.setNomDocteur(dto.getNom());
        docteur.setPrenomDocteur(dto.getPrenom());
        docteur.setEmailDocteur(dto.getEmail());
        docteur.setMotDePasseDocteur(dto.getMotDePasse());
        docteur.setTelephoneDocteur(dto.getTelephone());
        docteur.setSexeDocteur(dto.getSexe());
        docteur.setAdresseDocteur(dto.getAdresse());
        docteur.setVilleDocteur(dto.getVille());
        docteur.setSpecialiteDocteur(dto.getSpecialite());
        docteur.setDescDocteur(dto.getDescription());
        
        
        Docteur saved = docteurService.addDocteur(docteur);
        
        DocteurResponse response = new DocteurResponse(
        saved.getIdDocteur(),
        saved.getNomDocteur(),
        saved.getPrenomDocteur(),
        saved.getEmailDocteur(),
        saved.getTelephoneDocteur(),
        saved.getAdresseDocteur(),
        saved.getVilleDocteur(),
        saved.getSpecialiteDocteur(),
        saved.getDescDocteur(),
        saved.getSexeDocteur(),
        saved.getValider()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/valider")
    public ResponseEntity<List<DocteurResponse>> getDocteursValides() {
        List<DocteurResponse> responses = docteurService.findDocteursValider()
        .stream()
        .map(docteur -> new DocteurResponse(
        docteur.getIdDocteur(),
        docteur.getNomDocteur(),
        docteur.getPrenomDocteur(),
        docteur.getEmailDocteur(),
        docteur.getTelephoneDocteur(),
        docteur.getAdresseDocteur(),
        docteur.getVilleDocteur(),
        docteur.getSpecialiteDocteur(),
        docteur.getDescDocteur(),
        docteur.getSexeDocteur(),
        docteur.getValider()
        ))
        .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteDocteurById(@PathVariable int id) {
        return ResponseEntity.ok(docteurService.deleteDocteurById(id));
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Docteur> updateDocteur(@PathVariable int id, @RequestBody RegisterDocteurRequest dto) {
        Docteur docteur = new Docteur();
        docteur.setNomDocteur(dto.getNom());
        docteur.setPrenomDocteur(dto.getPrenom());
        docteur.setEmailDocteur(dto.getEmail());
        docteur.setMotDePasseDocteur(dto.getMotDePasse());
        docteur.setTelephoneDocteur(dto.getTelephone());
        docteur.setAdresseDocteur(dto.getAdresse());
        docteur.setVilleDocteur(dto.getVille());
        docteur.setSpecialiteDocteur(dto.getSpecialite());
        docteur.setDescDocteur(dto.getDescription());
        docteur.setSexeDocteur(dto.getSexe());
        
        return ResponseEntity.ok(docteurService.updateDocteur(id, docteur));
    }
    
    
    @GetMapping("/{id}/rdvs")
    public ResponseEntity<List<Rdv>> getRdvsByDocteur(@PathVariable int id) {
        return ResponseEntity.ok(rdvService.getRdvsByDocteurId(id));
    }
    
    @GetMapping("/{id}/document")
    public ResponseEntity<List<DocumentResponse>> getDocumentByDocteur(@PathVariable int id){
        List<DocumentResponse> responses = documentService.findDocumentByIdDocteur(id)
        .stream()
        .map(document -> new DocumentResponse(
        document.getIdDocument(),
        document.getNameDocument(),
        document.getTypeDocument(),
        document.getUrlDocument(),
        document.getDocteur().getIdDocteur()
        ))
        .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    
    
}
