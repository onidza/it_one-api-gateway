package ru.creditbank.apigateway.auth.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.apigateway.auth.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findById(UUID id);
}
