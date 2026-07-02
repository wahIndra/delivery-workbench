package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.AppUser;
import com.deliveryworkbench.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<AppUser> findByRole(UserRole role);
    List<AppUser> findByActive(boolean active);
}
