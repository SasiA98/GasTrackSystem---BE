package com.client.staff.services;


import com.client.staff.entities.User;
import com.client.staff.repositories.UserRepository;
import com.client.staff.security.services.SessionService;
import com.client.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BasicService {


    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USER_ID_NOT_FOUND = "User with id %d not found.";


    public UserService(SessionService sessionService, UserRepository userRepository) {
        super(sessionService, LoggerFactory.getLogger(UserService.class));
        this.userRepository = userRepository;
    }


    public User getById(Long id) {
        return getById(userRepository, id, USER_ID_NOT_FOUND);
    }

}
