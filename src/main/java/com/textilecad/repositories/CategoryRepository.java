package com.textilecad.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilecad.models.Category;
import com.textilecad.models.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByUser(User user);

  Optional<Category> findByIdAndUser(Long id, User user);

  Optional<Category> findByNameAndUser(String name, User user);

  boolean existsByNameAndUser(String name, User user);
  
  void deleteByUser(User user);
}
