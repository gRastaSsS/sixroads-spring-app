package com.fluffytiger.earlygamewebapp.repositories;

import com.fluffytiger.earlygamewebapp.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByUsername(String username);
}
