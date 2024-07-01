package com.teoresi.staff.services.old;


import com.teoresi.staff.components.old.UserSpecificationsFactory;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.utils.ComparableWrapper;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.mappers.old.UserMapper;
import com.teoresi.staff.repositories.old.customs.UserRepository;
import com.teoresi.staff.security.services.LoginAttemptService;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService extends BasicService {

    private final ResourceService resourceService;
    private final LoginAttemptService loginAttemptService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserSpecificationsFactory userSpecificationsFactory;
    private final EmailService emailService;
    private static final String USER_ID_NOT_FOUND = "User with id %d not found.";

    private final Map<String, Function<User, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("name", user -> user.getName() != null ? new ComparableWrapper(user.getName()) : null);
        put("surname", user -> user.getSurname() != null ? new ComparableWrapper(user.getSurname()) : null);
        put("roles", user -> user.getRoles() != null ? new ComparableWrapper(user.getRoles()) : null);
    }};


    public UserService(ResourceService resourceService, LoginAttemptService loginAttemptService, UserMapper userMapper, SessionService sessionService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserSpecificationsFactory userSpecificationsFactory, EmailService emailService) {
        super(sessionService, LoggerFactory.getLogger(UserService.class));
        this.resourceService = resourceService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSpecificationsFactory = userSpecificationsFactory;
        this.emailService = emailService;
        this.loginAttemptService = loginAttemptService;
    }

    public User create(User user) {
        user.setId(null);
        String tempPassword = PasswordTokenService.generateRandomString();
        user.setPassword(tempPassword);
        User newUser = save(user);
        Resource resource = resourceService.getById(newUser.getResource().getId());
        emailService.sendTempPasswordEmail(resource.getEmail(), tempPassword);

        return newUser;
    }

    public User resetPassword(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            User user = optUser.get();
            String tempPassword = PasswordTokenService.generateRandomString();
            user.setPassword(tempPassword);
            userRepository.save(user);
            emailService.sendTempPasswordEmail(user.getResource().getEmail(), tempPassword);
            return user;
        }
        return null;
    }

    @Transactional
    public Page<User> searchAdvanced(Optional<Filter<User>> filter, Pageable pageable) {
        try {

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<User> usersPage;

            if(isSorted) {
                List<User> users = filter.map(userFilter ->
                        userRepository.findAll(getSpecificationForAdvancedSearch(userFilter))
                ).orElseGet(userRepository::findAll);

                usersPage = getPage(sortingFields, users, pageable, sortingProperty);

            } else {
                usersPage = filter.map(userFilter ->
                        userRepository.findAll(getSpecificationForAdvancedSearch(userFilter), pageable)
                ).orElseGet(() -> userRepository.findAll(pageable));

            }

            usersPage = removeDuplicates(usersPage);
            return applyRoleVisibilityFilter(usersPage);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }



    private Page<User> applyRoleVisibilityFilter(Page<User> usersPage) {

        List<User> filteredUsers = new ArrayList<>();

        for (User user : usersPage.getContent()) {
            if (!user.getRoles().contains(Role.ADMIN))
                filteredUsers.add(user);
        }
        return new PageImpl<>(filteredUsers, usersPage.getPageable(), usersPage.getTotalElements());
    }



    private Specification<User> getSpecificationForAdvancedSearch(Filter<User> userFilter){
        return userFilter.toSpecification(userSpecificationsFactory);
    }

    public User update(User user) {

        Optional<User> oldUserOptional = userRepository.findById(user.getId());
        if (oldUserOptional.isEmpty())
            throw buildEntityWithIdNotFoundException(user.getId(), USER_ID_NOT_FOUND);

        User oldUser = oldUserOptional.get();

        if(!hasUserPermissionToChangeRoles(oldUser.getRoles()))
            throw buildDumCannotModifyPermissionsException();

        if(isUserEnabled(user))
            loginAttemptService.unlock(oldUser.getEmail());

        return save(user);
    }


    public void disableUser(User user){
        user.setStatus("Disabled");
        save(userRepository, user);
    }

    private boolean isUserEnabled(User user) {
        return user.getStatus().equals("Enabled");
    }

    public Set<User> getHigherAuthorityUsers(){
        List<User> users = getAll();
        return users.stream()
                .filter(u -> (u.getRoles().contains(Role.ADMIN) ||
                        u.getRoles().contains(Role.DUM) || u.getRoles().contains(Role.GDM))).collect(Collectors.toSet());
    }

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }


    public User partialUpdate(User user) {

        Optional<User> oldUserOptional = userRepository.findById(user.getId());
        if (oldUserOptional.isEmpty()) {
            throw buildEntityWithIdNotFoundException(user.getId(), USER_ID_NOT_FOUND);
        }
        User oldUser = oldUserOptional.get();

        if(!hasUserPermissionToChangeRoles(oldUser.getRoles()))
            throw buildDumCannotModifyPermissionsException();

        if(isUserEnabled(user))
            loginAttemptService.unlock(oldUser.getEmail());

        userMapper.updateModel(user, oldUser);
        return saveFromPartialUpdate(oldUser, user);
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(userRepository, user);
    }

    public User getById(Long id) {
        return getById(userRepository, id, USER_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        Optional<User> oldUserOptional = userRepository.findById(id);

        if (oldUserOptional.isEmpty())
            throw buildEntityWithIdNotFoundException(id, USER_ID_NOT_FOUND);

        User oldUser = oldUserOptional.get();

        if(!hasUserPermissionToChangeRoles(oldUser.getRoles()))
            throw buildDumCannotModifyPermissionsException();

        deleteById(userRepository, id, USER_ID_NOT_FOUND);
    }

    public List<User> getAll(){
        return getAll(userRepository);
    }

    private User saveFromPartialUpdate(User oldUser, User user) {
        if (user.getPassword() != null) {
            oldUser.setPassword(passwordEncoder.encode(oldUser.getPassword()));
        }
        try {
            return userRepository.save(oldUser);
        } catch (DataIntegrityViolationException ex) {
            String message = String.format(CONSTRAINT_VIOLATION, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public boolean existsByResourceId(Long resourceId){
        Optional<User> optionalUser = userRepository.findByResourceId(resourceId);
        return optionalUser.isPresent();
    }

}
