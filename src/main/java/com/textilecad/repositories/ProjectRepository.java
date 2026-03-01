package com.textilecad.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilecad.models.Project;
import com.textilecad.models.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByUser(User user);

  Optional<Project> findByIdAndUser(Long id, User user);

  Optional<Project> findByNameAndUser(String name, User user);

  boolean existsByNameAndUser(String name, User user);
  
  void deleteByUser(User user);
}
