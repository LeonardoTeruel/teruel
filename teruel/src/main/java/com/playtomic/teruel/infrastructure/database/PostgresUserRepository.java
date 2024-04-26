package com.playtomic.teruel.infrastructure.database;

import com.playtomic.teruel.domain.model.user.User;
import com.playtomic.teruel.domain.repository.user.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostgresUserRepository extends UserRepository, JpaRepository<User, UUID> {

}
