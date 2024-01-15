package io.linkme.service.contracts;

import io.linkme.model.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {
    List<UserDTO> findAll();

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    UserDTO getByEmail(String email);

    UserDTO get(Integer userId);

    Integer create(UserDTO userDTO);

    void update(Integer userId, UserDTO userDTO);

    void delete(Integer userId);
}
