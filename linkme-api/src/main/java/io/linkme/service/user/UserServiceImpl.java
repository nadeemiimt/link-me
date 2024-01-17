package io.linkme.service.user;

import io.linkme.domain.User;
import io.linkme.model.UserDTO;
import io.linkme.repos.UserRepository;
import io.linkme.service.contracts.UserService;
import io.linkme.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * find all users
     * @return
     */
    @Override
    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("userId"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    /**
     * loadUserByUsername
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw  new UsernameNotFoundException("Invalid login request");
        }
        List<String> roles = new ArrayList<>();
        roles.add(user.getRecruiter() ? "RECRUITER" :"JOB_SEEKER");
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(roles.toArray(new String[0]))
                        .build();
        return userDetails;
    }

    /**
     * get user by email
     * @param email
     * @return
     */
    @Override
    public UserDTO getByEmail(final String email) {
        return mapToDTO(userRepository.findByEmail(email), new UserDTO());
    }

    /**
     * get user by user id
     * @param userId
     * @return
     */
    @Override
    public UserDTO get(final Integer userId) {
        return userRepository.findById(userId)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    /**
     * create user
     * @param userDTO
     * @return
     */
    @Override
    public Integer create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getUserId();
    }

    /**
     * update user
     * @param userId
     * @param userDTO
     */
    @Override
    public void update(final Integer userId, final UserDTO userDTO) {
        final User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    /**
     * delete user
     * @param userId
     */
    @Override
    public void delete(final Integer userId) {
        userRepository.deleteById(userId);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setName(user.getName());
        userDTO.setOtherProfileDetails(user.getOtherProfileDetails());
        userDTO.setRecruiter(user.getRecruiter());
        userDTO.setRecruiter(user.getRecruiter());
        userDTO.setRecruiter(user.getRecruiter());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setOtherProfileDetails(userDTO.getOtherProfileDetails());
        user.setRecruiter(userDTO.getRecruiter());
        return user;
    }

}
