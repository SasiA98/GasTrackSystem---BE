package com.client.staff.security.services;

import com.client.staff.entities.User;
import com.client.staff.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserRepository userRepository;

    private static final String USER_ID_NOT_FOUND = "User with id %d not found.";

    public User getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw buildUserWithIdNotFoundException(id);
        }
        return user.get();
    }

    private ResponseStatusException buildUserWithIdNotFoundException(Long id) {
        String message = String.format(USER_ID_NOT_FOUND, id);
        log.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
