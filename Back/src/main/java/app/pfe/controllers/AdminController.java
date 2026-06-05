package app.pfe.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.pfe.dto.RegisterAdminRequest;
import app.pfe.entity.Admin;
import app.pfe.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
}
