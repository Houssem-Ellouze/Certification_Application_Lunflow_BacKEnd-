package com.houssem.back.Services;

import com.houssem.back.model.User;

import com.houssem.back.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User addUser(User user) {
        user.setName ( user.getName () );
        user.setEmail ( user.getEmail () );
        user.setFileName ( user.getFileName () );
        User savedUser = userRepository.save(user);
        user.setVerificationLink("http://localhost:8100/VerifyCertificate/" + user.getId());

        System.out.println("User received in controller: " + user); // Affiche l'objet complet

        return userRepository.save(savedUser);
    }
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {

        return userRepository.findById(id);
    }

    public User updateUser(String id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);  // Assurez-vous de définir l'ID pour la mise à jour
            user.setVerificationLink("http://localhost:8100/VerifyCertificate/" + user.getId());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }


    public void deleteUser(String id) {

        userRepository.deleteById(id);
    }

    public boolean verifyCertificate(String id) {
        return userRepository.existsById(id);
    }

    public User saveUser(User user) {

        user.setVerificationLink("http://localhost:8100/VerifyCertificate/" + user.getId());

        return userRepository.save(user);
    }


}
