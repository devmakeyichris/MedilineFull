package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.pfe.dto.DocteurResponse;
import app.pfe.dto.RegisterAdminRequest;
import app.pfe.entity.Admin;
import app.pfe.service.AdminService;
import app.pfe.service.DocteurService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final AdminService adminService;
    private final DocteurService docteurService;
    
    public AdminController(AdminService adminService,DocteurService docteurService) {
        this.adminService = adminService;
        this.docteurService = docteurService;
    }
    
    @PostMapping("/add")
    public ResponseEntity<Admin> addAdmin(@RequestBody RegisterAdminRequest dto) {
        Admin admin = new Admin();
        admin.setEmailAdmin(dto.getEmail());
        admin.setMotDePasseAdmin(dto.getMotDePasse());
        
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(adminService.saveAdmin(admin));
    }
    
    @PutMapping("/docteurs/{id}/valider")
    public ResponseEntity<String> validerDocteur(@PathVariable int id) {
        adminService.validerDocteur(id);
        return ResponseEntity.ok("Docteur validé avec succès");
    }
    
    @PutMapping("/docteurs/{id}/refuser")
    public ResponseEntity<String> refuserDocteur(@PathVariable int id) {
        adminService.refuserDocteur(id);
        return ResponseEntity.ok("Docteur refusé avec succès");
    }
    
    @GetMapping("/docteurs")
    public ResponseEntity<List<DocteurResponse>> getAllDocteurs() {
        List<DocteurResponse> responses = docteurService.getAllDocteurs()
        .stream()
        .map(docteur -> new DocteurResponse(
        docteur.getIdDocteur(),
        docteur.getNomDocteur(),
        docteur.getPrenomDocteur(),
        docteur.getEmailDocteur(),
        docteur.getTelephoneDocteur(),
        docteur.getAdresseDocteur(),
        docteur.getVilleDocteur(),
        docteur.getSpecialiteDocteur(),
        docteur.getDescDocteur(),
        docteur.getSexeDocteur(),
        docteur.getValider()
        ))
        .toList();
        
        return ResponseEntity.ok(responses);
    }
}
