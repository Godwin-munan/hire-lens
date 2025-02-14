package com.munan.gateway.domain.document;

import com.munan.gateway.enums.ParseStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "parsed_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParsedDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "applicant_name")
    private String applicantName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    // File metadata
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "file_size")
    private String fileSize;

    // Parsing details
    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status")
    private ParseStatus parseStatus; // e.g. SUCCESS or FAILED

    @Column(name = "parse_timestamp")
    private Instant parseTimestamp;

    // Duration in milliseconds (or seconds, if preferred)
    @Column(name = "parse_duration")
    private String parseDuration;

    // Many-to-Many relationship with skills
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(
        name = "document_skill",
        joinColumns = @JoinColumn(name = "document_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    // One-to-One relationship with request metadata
    @OneToOne(mappedBy = "parsedDocument", cascade = CascadeType.ALL)
    private RequestMetadata requestMetadata;
}
