package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPLICATION_DOCUMENT")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @Column(name = "DOCUMENT_TYPE", length = 50, nullable = false)
    private String documentType;

    @Column(name = "FILE_PATH", length = 500, nullable = false)
    private String filePath;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "VERIFICATION_STATUS", length = 50)
    private String verificationStatus; // e.g., PENDING, VERIFIED, REJECTED

    @Column(name = "UPLOAD_DATE")
    private LocalDateTime uploadDate;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;
}