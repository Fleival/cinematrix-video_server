package com.denspark.config.startup_event;

import com.denspark.db.cinemix.persistance.PrivilegeRepository;
import com.denspark.db.cinemix.persistance.RoleRepository;
import com.denspark.db.cinemix.persistance.UserRepository;
import com.denspark.model.user.Privilege;
import com.denspark.model.user.Role;
import com.denspark.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

    @Override
    @Transactional("transactionEmManager")
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<Privilege>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<Privilege>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role userRole = createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial user
        createUserIfNotFound("testmail@t.com", "Boris", "XrenPopadesh", "male", "Kyiv", "Ukraine", "1111", new ArrayList<Role>(Arrays.asList(userRole)));

        alreadySetup = true;
    }

    @Transactional("transactionEmManager")
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional("transactionEmManager")
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional("transactionEmManager")
    private final User createUserIfNotFound(final String email, final String firstName, final String lastName, final String gender, final String city, final String country, final String password, final Collection<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setGender(gender);
            user.setCity(city);
            user.setCountry(country);
            user.setEnabled(true);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }

}