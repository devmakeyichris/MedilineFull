package app.pfe.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.pfe.entity.Creneau;
import app.pfe.repository.CreneauRepository;

@Service
public class CreneauService {

    private final CreneauRepository creneauRepository;

    public CreneauService(CreneauRepository creneauRepository) {
        this.creneauRepository = creneauRepository;
    }

    public Creneau addCreneau(Creneau creneau) {
        return creneauRepository.save(creneau);
    }

    public List<Creneau> getCreneauxByDocteur(int idDocteur) {
        return creneauRepository.findByDocteur_IdDocteur(idDocteur);
    }

    public Creneau findById(int id) {
        return creneauRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Créneau introuvable"));
    }

    public Creneau bloquerCreneau(int id) {
        Creneau creneau = findById(id);
        creneau.setBloque(true);
        return creneauRepository.save(creneau);
    }

    public void supprimerCreneau(int id) {
        creneauRepository.deleteById(id);
    }
}