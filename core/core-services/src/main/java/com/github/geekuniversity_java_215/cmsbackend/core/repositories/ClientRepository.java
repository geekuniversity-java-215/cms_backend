package com.github.geekuniversity_java_215.cmsbackend.core.repositories;

import com.github.geekuniversity_java_215.cmsbackend.core.entities.Client;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.user.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends CustomRepository<Client, Long> {

    Optional<Client> findOneByUserLastNameAndUserFirstName(String lastName, String firstName);
    Optional<Client> findOneByUser(User user);
    Optional<Client> findOneByUserUsername(String username);

}
