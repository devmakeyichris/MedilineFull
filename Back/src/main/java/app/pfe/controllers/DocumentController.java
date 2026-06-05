package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.pfe.dto.DocumentRequest;
import app.pfe.dto.DocumentResponse;
import app.pfe.entity.Docteur;
import app.pfe.entity.Document;
import app.pfe.service.DocteurService;
import app.pfe.service.DocumentService;



@RestController
@RequestMapping("/documents")
public class DocumentController {
    
    private final DocumentService documentService;
    private final DocteurService docteurService;
    
    public DocumentController( DocumentService documentService,DocteurService docteurService){
        this.documentService = documentService;
        this.docteurService = docteurService;
    }
    
    
    @PostMapping("/add")
    public ResponseEntity<?> addDocument(@RequestBody DocumentRequest dto) {
        try {
            Docteur docteur = docteurService.getDocteurById(dto.getDocteurId());
            
            Document document = new Document();
            document.setNameDocument(dto.getName());
            document.setTypeDocument(dto.getType());
            document.setUrlDocument(dto.getUrl());
            document.setDocteur(docteur);
            
            Document saved = documentService.AddDocument(document);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @PutMapping("/update/{nomDocument}")
    public ResponseEntity<Document> updateDocument(@PathVariable String nomDocument, @RequestBody DocumentRequest dto) {
        
        Docteur docteur = docteurService.getDocteur(dto.getDocteurId());
        
        Document document = new Document();
        document.setNameDocument(dto.getName());
        document.setTypeDocument(dto.getType());
        document.setUrlDocument(dto.getUrl());
        document.setDocteur(docteur);
        
        return ResponseEntity.ok(documentService.updateDocument(nomDocument, document));
    }
    
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteDocument(@PathVariable int id){
        return ResponseEntity.ok(documentService.deleteDocumentById(id));
        
    }
    
    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
        document.getIdDocument(),
        document.getNameDocument(),
        document.getTypeDocument(),
        document.getUrlDocument(),
        document.getDocteur().getIdDocteur()
        );
    }

}
    
    
    

