package com.dlms.service;

import com.dlms.model.Application;
import com.dlms.model.ApplicationDocument;
import com.dlms.model.MedicalCertificate;
import com.dlms.model.Notification;
import com.dlms.model.enums.ApplicationStage;
import com.dlms.repository.ApplicationDocumentRepository;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.MedicalCertificateRepository;
import com.dlms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final MedicalCertificateRepository medicalRepository;
    private final NotificationRepository notificationRepository;
    private final JdbcTemplate jdbcTemplate;

    // Pulls the directory path from your application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public void processAndSaveDocument(MultipartFile file, Long applicationId, String docType) throws IOException {
        // 1. Validate Application
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // 2. Save Physical File to Disk and get the path
        String savedFilePath = savePhysicalFile(file, applicationId, docType);

        // 3. Route to the correct database table and workflow based on Document Type
        if ("CNIC_COPY".equals(docType) || "PHOTO".equals(docType)) {
            handleNadraDocument(app, docType, savedFilePath, file.getSize());
        } 
        else if ("MEDICAL".equals(docType)) {
            handleMedicalCertificate(app, savedFilePath);
        } 
        else {
            handleStandardDocument(app, docType, savedFilePath, file.getSize());
        }
    }

    public List<Map<String, Object>> getApplicationDocuments(Long applicationId) {
        List<Map<String, Object>> documentList = new ArrayList<>();

        // 1. Fetch CNIC and Photos from the ApplicationDocument table
        List<ApplicationDocument> standardDocs = documentRepository.findByApplication_ApplicationId(applicationId);
        for (ApplicationDocument doc : standardDocs) {
            Map<String, Object> docData = new HashMap<>();
            docData.put("documentType", doc.getDocumentType());
            docData.put("verificationStatus", doc.getVerificationStatus());
            documentList.add(docData);
        }

        // 2. Fetch Medical Certificate from the MedicalCertificate table
        medicalRepository.findTopByApplication_ApplicationIdOrderByCertificateIdDesc(applicationId).ifPresent(med -> {
            Map<String, Object> medData = new HashMap<>();
            medData.put("documentType", "MEDICAL");
            medData.put("verificationStatus", med.getStatus());
            documentList.add(medData);
        });

        return documentList;
    }

    private void handleNadraDocument(Application app, String docType, String filePath, long fileSize) {
        jdbcTemplate.update("CALL SP_SAVE_NADRA_DOCUMENT(?, ?, ?, ?)", 
            app.getApplicationId(), docType, filePath, fileSize);

        String docName = docType.equals("CNIC_COPY") ? "CNIC" : "Passport Photo";
        sendNotification(app, "DOCUMENT_VERIFIED", "NADRA Verified", 
                "Your " + docName + " has been automatically verified by NADRA.");
    }

    private void handleMedicalCertificate(Application app, String filePath) {
        jdbcTemplate.update("CALL SP_SAVE_MEDICAL_CERT(?, ?)", app.getApplicationId(), filePath);

        sendNotification(app, "DOCUMENT_SUBMITTED", "Medical Certificate Uploaded", 
                "Your Medical Certificate has been sent to the Testing Officer for manual verification.");
    }

    private void handleStandardDocument(Application app, String docType, String filePath, long fileSize) {
        ApplicationDocument doc = new ApplicationDocument();
        doc.setApplication(app);
        doc.setDocumentType(docType);
        doc.setFilePath(filePath);
        doc.setFileSize(fileSize);
        doc.setVerificationStatus("UNDER_REVIEW");
        doc.setUploadDate(LocalDateTime.now());
        
        documentRepository.save(doc);
    }

    // ─── CORE FILE SYSTEM LOGIC ───
    private String savePhysicalFile(MultipartFile file, Long applicationId, String docType) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        // Create directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique file name
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFileName = "APP_" + applicationId + "_" + docType + "_" + UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(newFileName);

        // Save the file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + newFileName; // The lightweight string stored in Oracle
    }

    // ─── HELPER: SEND NOTIFICATIONS ───
    private void sendNotification(Application app, String type, String subject, String body) {
        Notification notif = Notification.builder()
                .user(app.getApplicant().getUser())
                .type(type)
                .subject(subject)
                .messageBody(body)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        
        notificationRepository.save(notif);
    }
    
    public byte[] getPhotoBytes(Long applicationId) {
        // 1. Get all documents for this application
        List<ApplicationDocument> docs = documentRepository.findByApplication_ApplicationId(applicationId);

        // 2. Find the one that is the Passport Photo
        ApplicationDocument photoDoc = docs.stream()
                .filter(d -> "PHOTO".equals(d.getDocumentType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo not found for application: " + applicationId));

        // 3. The DB stores the path as "/uploads/filename.jpg". Extract just the filename.
        String dbPath = photoDoc.getFilePath();
        String fileName = dbPath.substring(dbPath.lastIndexOf("/") + 1);

        // 4. Locate the physical file in your uploadDir and read it into bytes
        try {
            Path physicalPath = Paths.get(uploadDir).resolve(fileName).normalize();
            return Files.readAllBytes(physicalPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not read physical photo file from disk: " + e.getMessage());
        }
    }
}