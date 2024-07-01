package com.teoresi.staff.security.services;

import com.teoresi.staff.security.mappers.UserSecurityMapper;
import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.repositories.old.customs.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserSecurityMapper userSecurityMapper;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    private final Logger logger = LoggerFactory.getLogger(UserSecurityService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            logger.debug("Username " + username + " not found.");
            throw new UsernameNotFoundException(username);
        } else if (Objects.equals(userOptional.get().getStatus(), "Disabled")) {
            logger.debug("User " + username + " disabled.");
            throw new UsernameNotFoundException(username);
        } else {
            logger.debug("Username " + username + " found!");
            return userSecurityMapper.mapToUserSecurityDetails(userOptional.get());
        }
    }

}