package com.munan.gateway.domain.document;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "request_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HTTP request details
    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "request_uri")
    private String requestUri;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "request_time")
    private Instant requestTime;

    // Link back to the parsed resume
    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    @JoinColumn(name = "document_id")
    private ParsedDocument parsedDocument;
}
