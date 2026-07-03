package com.dlms.controller;

import com.dlms.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applicant/documents") 
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // ─── 1. FETCH DOCUMENTS FOR DASHBOARD ───
    @GetMapping
    public ResponseEntity<?> getDocuments(@RequestParam("applicationId") Long applicationId) {
        try {
            // Fetch the combined list of documents (Standard + Medical) from the service
            List<Map<String, Object>> docs = documentService.getApplicationDocuments(applicationId);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ─── 2. UPLOAD NEW DOCUMENTS ───
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("applicationId") Long applicationId) {
        
        try {
            // Sends the file to the DocumentService to be saved
            documentService.processAndSaveDocument(file, applicationId, documentType);
            
            // Returns a success message
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Document uploaded successfully"
            ));
            
        } catch (Exception e) {
            e.printStackTrace(); // Prints the error in your terminal if something breaks
            // Returns the error to the frontend
            return ResponseEntity.status(500).body(Map.of(
                "success", false, 
                "message", "Upload failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping(value = "/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getApplicantPhoto(@RequestParam Long applicationId) {
        // 1. Find the Document entity where type == "PHOTO" and applicationId == applicationId
        // 2. Get the file bytes (from DB or file system)
        byte[] imageBytes = documentService.getPhotoBytes(applicationId); 
        
        return ResponseEntity.ok(imageBytes);
    }
}