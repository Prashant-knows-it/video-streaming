package com.example.JWT_Learn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.JWT_Learn.model.Human;

@Repository
public interface HumanRepository extends JpaRepository<Human, Long> {
    Optional<Human> findByUsername(String username);
    Optional<Human> findByEmail(String email);
}
