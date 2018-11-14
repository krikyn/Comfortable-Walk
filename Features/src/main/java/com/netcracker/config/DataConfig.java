package com.netcracker.config;

import com.netcracker.model.User;
import com.netcracker.model.UserRepository;
import org.springframework.context.annotation.Configuration;

// daba это не конфигурация, это сервис
@Configuration
public class DataConfig {

    private static UserRepository userRepository;

    public DataConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // daba нужен комментарий на тему того, что вообще здесь происходит
    // daba статик-метод в классе конфига, который пишет в базу, ЯННП
    public static User saveUser(String socialName, Object sub, Object userName, Object picture) {
        String id = socialName + "_" + sub;
        User user = userRepository.findById(id).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(id);
            newUser.setName((String) userName);
            newUser.setPicture((String) picture);
            return newUser;
        });
        return userRepository.save(user);
    }
}
