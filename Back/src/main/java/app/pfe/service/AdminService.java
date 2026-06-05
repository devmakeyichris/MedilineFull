package app.pfe.service;

import java.util.NoSuchElementException;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.pfe.entity.Admin;
import app.pfe.entity.Docteur;
import app.pfe.repository.AdminRepository;
import app.pfe.repository.DocteurRepository;
import app.pfe.state.DocteurState;

@Service
public class AdminService {
    
    
    private final AdminRepository adminRepository;
    private final DocteurRepository docteurRepository;
     private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public AdminService(AdminRepository adminRepository, DocteurRepository docteurRepository, EmailService emailService,PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.docteurRepository = docteurRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Admin saveAdmin(Admin admin) {
          admin.setMotDePasseAdmin(passwordEncoder.encode(admin.getMotDePasseAdmin()));

        return adminRepository.save(admin);
    }
    
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmailAdmin(email);
    }
    
    public void validerDocteur(int idDocteur) {
        
        Docteur docteur = docteurRepository.findById(idDocteur)
        .orElseThrow(() -> new NoSuchElementException("Docteur introuvable"));
        
        docteur.setValider(DocteurState.VALIDE);
        docteurRepository.save(docteur);

          // Envoi email de validation
        emailService.envoyerEmail(
            docteur.getEmailDocteur(),
            "Compte MediLine validé !",
            "Bonjour Dr. " + docteur.getNomDocteur() + ",\n\n" +
            "Félicitations ! Votre compte médecin a été validé.\n" +
            "Vous pouvez maintenant vous connecter et gérer vos consultations.\n\n" +
            "L'équipe MediLine"
        );
    }//bouton de valider
    
    
    
    public void refuserDocteur(int idDocteur) {
        Docteur docteur = docteurRepository.findById(idDocteur)
        .orElseThrow(() -> new NoSuchElementException("Docteur introuvable"));
        docteur.setValider(DocteurState.REFUSE);
        docteurRepository.save(docteur);

        // Envoi email de refus
        emailService.envoyerEmail(
            docteur.getEmailDocteur(),
            "Compte MediLine refusé",
            "Bonjour Dr. " + docteur.getNomDocteur() + ",\n\n" +
            "Après vérification de vos informations, nous sommes désolés de vous informer que votre compte médecin n’a pas été validé.\n" +
            "Vous pouvez nous recontacter pour plus de détails ou soumettre une nouvelle demande.\n\n" +
            "L'équipe MediLine"
        );
    }  //bouton de refus
    
    
    
    
}
