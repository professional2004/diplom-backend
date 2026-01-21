package com.textilecad.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilecad.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPasswordResetTokenHash(String passwordResetTokenHash);
  boolean existsByEmail(String email);
  void deleteByEmail(String email);
}
