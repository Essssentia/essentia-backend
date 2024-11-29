package com.sw.essentiabackend.domain.auth.repository;

import com.sw.essentiabackend.domain.auth.entity.ApprovalStatus;
import com.sw.essentiabackend.domain.auth.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(Long googleId);
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);
    // 탈퇴한 후 일정 기간(30일) 이상 지난 사용자 목록을 조회
    List<User> findAllByResignedAtBefore(LocalDateTime dateTime);
}