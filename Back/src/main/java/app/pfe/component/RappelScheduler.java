package app.pfe.component;

import app.pfe.entity.Rdv;
import app.pfe.repository.RdvRepository;
import app.pfe.service.EmailService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RappelScheduler {

    private final RdvRepository rdvRepository;
    private final EmailService emailService;

    public RappelScheduler(RdvRepository rdvRepository, EmailService emailService) {
        this.rdvRepository = rdvRepository;
        this.emailService = emailService;
    }

    // Tourne toutes les heures
    @Scheduled(fixedRate = 3600000)
    public void envoyerRappels() {
        LocalDateTime dans5h = LocalDateTime.now().plusHours(5);
        LocalDateTime dans6h = LocalDateTime.now().plusHours(6);

        // Récupérer les RDV dans les 5-6h à venir
        List<Rdv> rdvs = rdvRepository.findByDateRdvBetween(dans5h, dans6h);

        for (Rdv rdv : rdvs) {
            emailService.envoyerEmail(
                rdv.getPatient().getEmailPatient(),
                "Rappel — Votre rendez-vous dans 5h",
                "Bonjour " + rdv.getPatient().getPrenomPatient() + ",\n\n" +
                "Rappel : vous avez un rendez-vous dans 5 heures.\n" +
                "Médecin : Dr. " + rdv.getDocteur().getNomDocteur() + "\n" +
                "Date    : " + rdv.getDateRdv() + "\n" +
                "Heure   : " + rdv.getHeureRdv() + "\n\n" +
                "L'équipe MediLine"
            );
        }
    }
}
