package app.pfe.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import app.pfe.entity.Patient;
import app.pfe.repository.PatientRepository;


@Service
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    
    
    
    
    public Patient addPatient(Patient patient) {
        if(patientRepository.existsByEmailPatient(patient.getEmailPatient())){
            throw new IllegalArgumentException("Un patient avec cet email existe déjà");
        }
        patient.setMotDePassePatient(passwordEncoder.encode(patient.getMotDePassePatient()));
        Patient saved = patientRepository.save(patient);
        
        // Envoi email de bienvenue
        emailService.envoyerEmail(
        saved.getEmailPatient(),
        "Inscription MediLine — Bienvenue",
        "Bonjour " + saved.getNomPatient() + ",\n\n" +
        "Votre compte patient a été créé avec succès.\n" +
        "Vous pouvez dès maintenant vous connecter et prendre vos rendez-vous.\n\n" +
        "L'équipe MediLine"
        );
        
        return saved;
    }
    
    
    public Patient updatePatient(int idPatient, Patient newInfoPatient) {
        
        Patient oldInfoPatient = patientRepository.findById(idPatient)
        .orElseThrow(() -> new IllegalArgumentException("Patient introuvable"));
        
        BeanUtils.copyProperties(newInfoPatient, oldInfoPatient, "idPatient");
        
        if (newInfoPatient.getMotDePassePatient() != null 
        && !newInfoPatient.getMotDePassePatient().isBlank()) {
            
            oldInfoPatient.setMotDePassePatient(
            passwordEncoder.encode(newInfoPatient.getMotDePassePatient())
            );
        }
        
        return patientRepository.save(oldInfoPatient);
    }
    
    
    
    public boolean deletePatientById(int idPatient){
        if(patientRepository.existsById(idPatient)){
            patientRepository.deleteById(idPatient);
            return true;
        }
        return false;
        
    }
    
    public Boolean deletePatientByEmail(String email){
        
        Patient patient = patientRepository.findByEmailPatient(email)
        .orElseThrow(() -> new IllegalArgumentException("Aucun patient ne possede cet email"));
        patientRepository.delete(patient);
        return true;
    }
    
    public Patient findPatientById(int idPAtient){
        Patient patient = patientRepository.findById(idPAtient)
        .orElseThrow(() -> new IllegalArgumentException("le patient avec l'id " + idPAtient + " est introuvable"));
        return patient;
    }
    
    
    public List<Patient>  getAllPatient(){
        return patientRepository.findAll();
    }
    
    
    
    
    public Patient getPatientById(int patientId) {
        return patientRepository.findById(patientId)
        .orElseThrow(() -> new IllegalArgumentException("Le patient avec l'id " + patientId + " n'existe pas !"));
    }
    
    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmailPatient(email)
        .orElseThrow(() -> new IllegalArgumentException("Patient introuvable"));
    }
    
  
    
    
}
