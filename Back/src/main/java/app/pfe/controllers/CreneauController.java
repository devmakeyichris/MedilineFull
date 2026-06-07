package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.pfe.dto.CreneauRequest;
import app.pfe.dto.CreneauResponse;
import app.pfe.entity.Creneau;
import app.pfe.entity.Docteur;
import app.pfe.service.CreneauService;
import app.pfe.service.DocteurService;

@RestController
@RequestMapping("/docteurs/{idDocteur}/creneaux")
public class CreneauController {

    private final CreneauService creneauService;
    private final DocteurService docteurService;

    public CreneauController(CreneauService creneauService, DocteurService docteurService) {
        this.creneauService = creneauService;
        this.docteurService = docteurService;
    }

    @PostMapping
    public ResponseEntity<CreneauResponse> addCreneau(
            @PathVariable int idDocteur,
            @RequestBody CreneauRequest dto) {

        Docteur docteur = docteurService.getDocteurById(idDocteur);

        Creneau creneau = new Creneau();
        creneau.setDateCreneau(dto.getDateCreneau());
        creneau.setHeureDebut(dto.getHeureDebut());
        creneau.setHeureFin(dto.getHeureFin());
        creneau.setBloque(dto.isBloque());
        creneau.setDocteur(docteur);

        Creneau saved = creneauService.addCreneau(creneau);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<CreneauResponse>> getCreneauxByDocteur(@PathVariable int idDocteur) {
        List<CreneauResponse> response = creneauService.getCreneauxByDocteur(idDocteur)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idCreneau}/bloquer")
    public ResponseEntity<CreneauResponse> bloquerCreneau(
            @PathVariable int idDocteur,
            @PathVariable int idCreneau) {

        Creneau creneau = creneauService.findById(idCreneau);

        if (creneau.getDocteur().getIdDocteur() != idDocteur) {
            return ResponseEntity.status(403).build();
        }

        Creneau bloque = creneauService.bloquerCreneau(idCreneau);
        return ResponseEntity.ok(toResponse(bloque));
    }

    @DeleteMapping("/{idCreneau}")
    public ResponseEntity<String> supprimerCreneau(
            @PathVariable int idDocteur,
            @PathVariable int idCreneau) {

        Creneau creneau = creneauService.findById(idCreneau);

        if (creneau.getDocteur().getIdDocteur() != idDocteur) {
            return ResponseEntity.status(403)
                    .body("Ce créneau n'appartient pas à ce docteur");
        }

        creneauService.supprimerCreneau(idCreneau);
        return ResponseEntity.ok("Créneau supprimé avec succès");
    }

    private CreneauResponse toResponse(Creneau creneau) {
        return new CreneauResponse(
                creneau.getIdCreneau(),
                creneau.getDateCreneau(),
                creneau.getHeureDebut(),
                creneau.getHeureFin(),
                creneau.isBloque(),
                creneau.getDocteur().getIdDocteur()
        );
    }
}