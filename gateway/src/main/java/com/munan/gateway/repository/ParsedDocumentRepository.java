package com.munan.gateway.repository;

import com.munan.gateway.domain.document.ParsedDocument;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParsedDocumentRepository extends JpaRepository<ParsedDocument, Long> {}
