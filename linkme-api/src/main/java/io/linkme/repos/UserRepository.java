package io.linkme.repos;

import io.linkme.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
}
