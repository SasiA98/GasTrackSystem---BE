package com.teoresi.staff.security.services;

import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.services.old.EmailService;
import com.teoresi.staff.services.old.UserService;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final UserService userService;
    private final EmailService emailService;
    private final int MAX_ATTEMPT = 5;
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentSet<String> lockCache = new ConcurrentSet<>();

    public LoginAttemptService(@Lazy UserService userService, @Lazy EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }


    public void loginFailed(String email) {
        int attempts = attemptsCache.getOrDefault(email, 0);
        attempts++;
        attemptsCache.put(email, attempts);
        if (attempts >= MAX_ATTEMPT)
            if(disableUser(email))
                lockCache.add(email);

    }

    private boolean disableUser(String email){
        Optional<User> user = userService.getByEmail(email);

        if(user.isEmpty())
            return false;

        userService.disableUser(user.get());
        emailService.notifyUnauthorizedAccess(user.get());
        return true;
    }

    public boolean isBlocked(String email) {
        return lockCache.contains(email);
    }

    public void unlock(String email){
        attemptsCache.remove(email);
        lockCache.remove(email);
    }

}

