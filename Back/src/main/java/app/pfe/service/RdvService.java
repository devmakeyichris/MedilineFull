package app.pfe.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import app.pfe.entity.Docteur;
import app.pfe.entity.Patient;
import app.pfe.entity.Rdv;
import app.pfe.repository.DocteurRepository;
import app.pfe.repository.PatientRepository;
import app.pfe.repository.RdvRepository;

@Service
public class RdvService {
    
    private final RdvRepository rdvRepository;
    private final PatientRepository patientRepository;
    private final DocteurRepository docteurRepository;
    private final EmailService emailService;
    
    public RdvService(RdvRepository rdvRepository, PatientRepository patientRepository, DocteurRepository docteurRepository, EmailService emailService) {
        this.rdvRepository = rdvRepository;
        this.patientRepository = patientRepository;
        this.docteurRepository = docteurRepository;
        this.emailService = emailService;
    }
    
    
    public Rdv addRdv(Rdv rdv, int idPatient, int idDocteur) {
        
        Docteur docteur = docteurRepository.findById(idDocteur)
        .orElseThrow(() -> new IllegalArgumentException("Docteur introuvable"));
        
        Patient patient = patientRepository.findById(idPatient)
        .orElseThrow(() -> new IllegalArgumentException("Patient introuvable"));
        
        rdv.setPatient(patient);
        rdv.setDocteur(docteur);
        
        boolean dejaPris = checkDisponibilitesRdv(rdv);
        
        if (dejaPris) {
            throw new IllegalArgumentException("Ce créneau est déjà pris !");
        }
        
        rdv.setStatusRdv("Attente de confirmation");
        
        return rdvRepository.save(rdv);
    }
    //Lorsque le patient sera connecte et aura pris le rendez-vous,le front envera en http l'id du docteur,du patient et les infos du rdv au back pour que le back puisse faire le lien entre les trois et creer le rdv dan s la base de donnee
    
    
    
    
    
    
    public String checkRdv(Rdv rdv) {
        LocalDateTime maintenant = LocalDateTime.now();
        
        if (rdv.getDateRdv().isBefore(maintenant.plusHours(24))) {//si le rendez‑vous est prévu dans moins de 24h.
            return "Confirmation requise avant ce rendez-vous.";
        }
        return "Rendez-vous toujours en attente de confirmation.";
    }
    
    
    
    
    
    
    public Rdv confirmRdv(Rdv rdv) {
        rdv.setStatusRdv("CONFIRMÉ");
        Rdv saved = rdvRepository.save(rdv);
        
        // Email au patient
        emailService.envoyerEmail(
        saved.getPatient().getEmailPatient(),
        "Confirmation de votre rendez-vous — MediLine",
        "Bonjour " + saved.getPatient().getPrenomPatient() + ",\n\n" +
        "Votre rendez-vous a été confirmé :\n" +
        "Médecin : Dr. " + saved.getDocteur().getNomDocteur() + "\n" +
        "Date    : " + saved.getDateRdv() + "\n" +
        "Heure   : " + saved.getHeureRdv() + "\n\n" +
        "L'équipe MediLine"
        );
        
        // Email au médecin
        emailService.envoyerEmail(
        saved.getDocteur().getEmailDocteur(),
        "Nouveau rendez-vous — MediLine",
        "Bonjour Dr. " + saved.getDocteur().getNomDocteur() + ",\n\n" +
        "Un nouveau rendez-vous a été pris :\n" +
        "Patient : " + saved.getPatient().getPrenomPatient() + " " + saved.getPatient().getNomPatient() + "\n" +
        "Date    : " + saved.getDateRdv() + "\n" +
        "Heure   : " + saved.getHeureRdv() + "\n\n" +
        "L'équipe MediLine"
        );
        
        return saved;
    }
    
    //Dans le front , il y auura un bouton comfirmer rdv
    
    
    
    public boolean checkDisponibilitesRdv(Rdv rdv){
        
        LocalDateTime dateRdv = rdv.getDateRdv();
        
        String heureRdv = rdv.getHeureRdv();
        
        int idDocteur = rdv.getDocteur().getIdDocteur();
        
        return rdvRepository.existsByDateRdvAndHeureRdvAndDocteur_idDocteur(dateRdv,heureRdv,idDocteur);
    }
    
    
    
    
    
    public Rdv updateRdv(Rdv rdv){
        return rdvRepository.save(rdv);
    }
    
    
    public Rdv cancelRdv(Rdv rdv) {
        rdv.setStatusRdv("ANNULE");
        Rdv saved = rdvRepository.save(rdv);
        
        // Email au patient
        emailService.envoyerEmail(
        saved.getPatient().getEmailPatient(),
        "Annulation de votre rendez-vous — MediLine",
        "Bonjour " + saved.getPatient().getPrenomPatient() + ",\n\n" +
        "Votre rendez-vous prévu avec Dr. " + saved.getDocteur().getNomDocteur() +
        " le " + saved.getDateRdv() + " à " + saved.getHeureRdv() +
        " a été annulé.\n\n" +
        "Merci de reprendre un nouveau rendez-vous si nécessaire.\n\n" +
        "L'équipe MediLine"
        );
        
        // Email au médecin
        emailService.envoyerEmail(
        saved.getDocteur().getEmailDocteur(),
        "Annulation de rendez-vous — MediLine",
        "Bonjour Dr. " + saved.getDocteur().getNomDocteur() + ",\n\n" +
        "Le rendez-vous avec le patient " + saved.getPatient().getPrenomPatient() +
        " " + saved.getPatient().getNomPatient() + " prévu le " +
        saved.getDateRdv() + " à " + saved.getHeureRdv() + " a été annulé.\n\n" +
        "L'équipe MediLine"
        );
        
        return saved;
    }
    
    
    
    public Rdv findById(int idRdv) {
        Rdv rdv = rdvRepository.findById(idRdv)
        .orElseThrow(() -> new IllegalArgumentException("Aucun rendez vous avec cet Id"));
        return rdv;
    }
    
    public List<Rdv> getAllRdv(){
        return rdvRepository.findAll();
    }
    
    
    public List<Rdv> getRdvsByPatientId(int idPatient) { 
        
        return rdvRepository.findByPatient_IdPatient(idPatient); 
    } 
    
    public List<Rdv> getRdvsByDocteurId(int idDocteur) { 
        
        return rdvRepository.findByDocteur_IdDocteur(idDocteur);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
