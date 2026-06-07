package app.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.pfe.entity.Ordonnance;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Integer> {
     List<Ordonnance> findByPatient_IdPatient(int idPatient);

    List<Ordonnance> findByDocteur_IdDocteur(int idDocteur);
    
}
