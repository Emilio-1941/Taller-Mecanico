package com.taller.auth.talleres.repository;

import com.taller.auth.talleres.model.TallerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TallerInfoRepository extends JpaRepository<TallerInfo, Long> {
    boolean existsByRfcIgnoreCaseOrEmailIgnoreCase(String rfc, String email);
}
