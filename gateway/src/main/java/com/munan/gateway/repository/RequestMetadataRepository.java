package com.munan.gateway.repository;

import com.munan.gateway.domain.document.RequestMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestMetadataRepository extends JpaRepository<RequestMetadata, Long> {}
