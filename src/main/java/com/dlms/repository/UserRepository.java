package com.dlms.repository;

import com.dlms.model.User;
import com.dlms.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // =========================
    // BASIC LOOKUPS
    // =========================

    Optional<User> findByEmail(String email);

    Optional<User> findByCnic(String cnic);

    List<User> findByRole(UserRole role);

    List<User> findByAccountStatus(String accountStatus);

    List<User> findByDistrict(String district);
    
 // Add this line inside your UserRepository interface
    List<User> findByRoleIn(List<UserRole> roles);

    // =========================
    // EXISTS CHECKS
    // =========================

    boolean existsByEmail(String email);

    boolean existsByCnic(String cnic);

    // =========================
    // CUSTOM QUERIES
    // =========================

    @Query("SELECT u FROM User u WHERE u.district = :district AND u.accountStatus = 'ACTIVE'")
    List<User> findActiveUsersByDistrict(@Param("district") String district);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.accountStatus = 'ACTIVE'")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
}