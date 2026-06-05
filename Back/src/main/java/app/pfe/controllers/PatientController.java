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

import app.pfe.dto.PatientResponse;
import app.pfe.dto.RegisterPatientRequest;
import app.pfe.entity.Patient;
import app.pfe.entity.Rdv;
import app.pfe.service.PatientService;
import app.pfe.service.RdvService;

@RestController
@RequestMapping("/patients")
public class PatientController {
    
    private final PatientService patientService;
    private final RdvService rdvService;
    
    public PatientController(PatientService patientService, RdvService rdvService){
        this.patientService = patientService;
        this.rdvService = rdvService;
    }
    
        
    @PostMapping("/add")
    public ResponseEntity<PatientResponse> ajouterPatient(
    @RequestBody RegisterPatientRequest dto) {
        
        Patient patient = new Patient();
        
        patient.setNomPatient(dto.getNom());
        patient.setPrenomPatient(dto.getPrenom());
        patient.setEmailPatient(dto.getEmail());
        patient.setTelPatient(dto.getTelephone());
        patient.setAdressePatient(dto.getAdresse());
        patient.setVillePatient(dto.getVille());
        patient.setdNaissPatient(dto.getDateNaissance());
        patient.setSexePatient(dto.getSexe());
        patient.setMotDePassePatient(dto.getPassword());
        
        Patient saved = patientService.addPatient(patient);
        
        PatientResponse response = new PatientResponse(
        saved.getIdPatient(),
        saved.getNomPatient(),
        saved.getPrenomPatient(),
        saved.getEmailPatient(),
        saved.getTelPatient(),
        saved.getAdressePatient(),
        saved.getVillePatient(),
        saved.getdNaissPatient(),
        saved.getSexePatient()

        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
    }



    // ✅ Mise à jour avec DTO
    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody RegisterPatientRequest dto) {
        try {
            Patient patient = new Patient();
            patient.setNomPatient(dto.getNom());
            patient.setPrenomPatient(dto.getPrenom());
            patient.setEmailPatient(dto.getEmail());
            patient.setTelPatient(dto.getTelephone());
            patient.setAdressePatient(dto.getAdresse());
            patient.setVillePatient(dto.getVille());
            patient.setMotDePassePatient(dto.getPassword());
            
            return ResponseEntity.ok(patientService.updatePatient(id, patient));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur : " + e.getMessage());
        }
    }
    
    // ✅ Les autres endpoints restent inchangés
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Boolean> deletePatient(@PathVariable int id) {
        return ResponseEntity.ok(patientService.deletePatientById(id));
    }
    
    @DeleteMapping("/delete/email/{email}")
    public ResponseEntity<Boolean> deletePatientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(patientService.deletePatientByEmail(email));
    }
    
    @GetMapping("/getAll")
    public List<Patient> getAllPatients() {
        return patientService.getAllPatient();
    }
    
    @GetMapping("/{id}/rdvs")
    public List<Rdv> getRdvsByPatient(@PathVariable int id) {
        return rdvService.getRdvsByPatientId(id);
    }
}
