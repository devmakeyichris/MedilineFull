package app.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.pfe.entity.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Integer> {
    List<Creneau> findByDocteur_IdDocteur(int idDocteur);
}