package app.pfe.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.pfe.dto.DocumentRequest;
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
   public ResponseEntity<Document> addDocument(@RequestBody DocumentRequest dto) {
    Docteur docteur = docteurService.getDocteurById(dto.getDocteurId());

    Document document = new Document();
    document.setNameDocument(dto.getName());
    document.setTypeDocument(dto.getType());
    document.setUrlDocument(dto.getUrl());
    document.setDocteur(docteur);

    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(documentService.AddDocument(document));
}

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Document>> uploadDocuments(
            @RequestParam("idDocteur") Integer docteurId,
            @RequestParam(value = "identite", required = false) List<MultipartFile> identiteFiles,
            @RequestParam(value = "diplomes", required = false) List<MultipartFile> diplomesFiles,
            @RequestParam(value = "autresDocs", required = false) List<MultipartFile> autresDocsFiles)
            throws IOException {

        Docteur docteur = docteurService.getDocteurById(docteurId);
        Path uploadPath = Paths.get("uploads");
        Files.createDirectories(uploadPath);

        List<Document> savedDocuments = new ArrayList<>();

        if (identiteFiles != null) {
            for (MultipartFile file : identiteFiles) {
                Document saved = saveUploadedFile(file, "identite", docteur, uploadPath);
                if (saved != null) savedDocuments.add(saved);
            }
        }

        if (diplomesFiles != null) {
            for (MultipartFile file : diplomesFiles) {
                Document saved = saveUploadedFile(file, "diplomes", docteur, uploadPath);
                if (saved != null) savedDocuments.add(saved);
            }
        }

        if (autresDocsFiles != null) {
            for (MultipartFile file : autresDocsFiles) {
                Document saved = saveUploadedFile(file, "autres", docteur, uploadPath);
                if (saved != null) savedDocuments.add(saved);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDocuments);
    }

    private Document saveUploadedFile(MultipartFile file, String typeDocument, Docteur docteur, Path uploadPath)
            throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        originalFilename = Paths.get(originalFilename).getFileName().toString();
        String storedFilename = System.currentTimeMillis() + "_" + originalFilename.replaceAll("\\s+", "_");
        Path targetFile = uploadPath.resolve(storedFilename);

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        Document document = new Document();
        document.setNameDocument(originalFilename);
        document.setTypeDocument(typeDocument);
        document.setUrlDocument("uploads/" + storedFilename);
        document.setDocteur(docteur);

        return documentService.AddDocument(document);
    }

    @PutMapping("update/{nomDocument}")
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

    
    
}
