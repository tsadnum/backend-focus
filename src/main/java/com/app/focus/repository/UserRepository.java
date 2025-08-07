package com.app.focus.repository;

import com.app.focus.entity.User;
import com.app.focus.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Query("""
        SELECT u FROM User u
        WHERE u.createdAt >= COALESCE(:startDate, u.createdAt)
          AND u.createdAt <= COALESCE(:endDate, u.createdAt)
          AND (:status IS NULL OR u.status = :status)
    """)
    List<User> findAllWithFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") UserStatus status
    );

    long countByLastLoginAtIsNotNull();
    long countByLastLoginAtAfter(LocalDateTime dateTime);

}
