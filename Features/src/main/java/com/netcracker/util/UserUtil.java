package com.netcracker.util;

import com.netcracker.data.model.User;
import com.netcracker.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Util for working with user's info
 * @author prokhorovartem
 */
@RequiredArgsConstructor
@Component
public class UserUtil {

    private final UserRepository userRepository;

    /**
     * Saving new user or editing user with new info
     * @param socialName name of social network
     * @param sub id in social network
     * @param userName nickname or first name + last name
     * @param picture avatar from social network
     */
    public void saveUser(String socialName, Object sub, Object userName, Object picture) {
        String id = socialName + "_" + sub;
        User user = userRepository.findById(id).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(id);
            newUser.setName((String) userName);
            newUser.setPicture((String) picture);
            return newUser;
        });
        userRepository.save(user);
    }
}
