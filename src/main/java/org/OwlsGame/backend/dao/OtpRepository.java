package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByEmailAndOtpAndUsedFalse(String email, String otp);

    @Transactional
    void deleteByEmail(String email);
}