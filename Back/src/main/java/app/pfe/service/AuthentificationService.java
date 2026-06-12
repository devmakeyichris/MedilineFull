package app.pfe.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.pfe.component.JwtUtil;
import app.pfe.entity.Admin;
import app.pfe.entity.Docteur;
import app.pfe.entity.Patient;
import app.pfe.repository.AdminRepository;
import app.pfe.repository.DocteurRepository;
import app.pfe.repository.PatientRepository;
import app.pfe.state.DocteurState;

@Service
public class AuthentificationService {
    
    private final PatientRepository patientRepository;
    private final DocteurRepository docteurRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    
    public AuthentificationService(PatientRepository patientRepository,
    DocteurRepository docteurRepository,JwtUtil jwtUtil,PasswordEncoder passwordEncoder,AdminRepository adminRepository,EmailService emailService) {
        this.patientRepository = patientRepository;
        this.docteurRepository = docteurRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.emailService = emailService;
    }
    
    public String login(String email, String password) {
        
        Patient patient = patientRepository.findByEmailPatient(email).orElse(null);
        
        if (patient != null && passwordEncoder.matches(password, patient.getMotDePassePatient())) {
            return jwtUtil.generateToken(
            patient.getEmailPatient(),
            "PATIENT",
            patient.getIdPatient()
            );
        }
        
        Docteur docteur = docteurRepository.findByEmailDocteur(email).orElse(null);
        
        if (docteur != null && passwordEncoder.matches(password, docteur.getMotDePasseDocteur())) {
            
            if (docteur.getValider() != DocteurState.VALIDE) {
                throw new IllegalArgumentException("Votre compte docteur n'est pas encore validé");
            }
            
            return jwtUtil.generateToken(
            docteur.getEmailDocteur(),
            "DOCTEUR",
            docteur.getIdDocteur()
            );
        }
        
        Admin admin = adminRepository.findByEmailAdmin(email).orElse(null);
        
        if (admin != null && passwordEncoder.matches(password, admin.getMotDePasseAdmin())) {
            return jwtUtil.generateToken(
            admin.getEmailAdmin(),
            "ADMIN",
            admin.getIdAdmin()
            );
        }
        
        throw new IllegalArgumentException("Email ou mot de passe incorrect");
    }
    
    
    
    
    
}





