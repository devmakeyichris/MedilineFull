package app.pfe.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.pfe.entity.Ordonnance;
import app.pfe.repository.OrdonnanceRepository;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
    }

    public Ordonnance addOrdonnance(Ordonnance ordonnance) {
        return ordonnanceRepository.save(ordonnance);
    }

    public List<Ordonnance> getOrdonnancesByPatientId(int idPatient) {
        return ordonnanceRepository.findByPatient_IdPatient(idPatient);
    }

    public List<Ordonnance> getOrdonnancesByDocteurId(int idDocteur) {
        return ordonnanceRepository.findByDocteur_IdDocteur(idDocteur);
    }

    public List<Ordonnance> getAllOrdonnances() {
        return ordonnanceRepository.findAll();
    }
}