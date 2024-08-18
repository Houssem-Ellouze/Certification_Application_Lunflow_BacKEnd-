package com.houssem.back.controllers;

import com.houssem.back.Services.FileService;
import com.houssem.back.Services.UserService;
import com.houssem.back.model.User;
import com.houssem.back.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController


public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private FileService fileService;

    @CrossOrigin(origins = "http://localhost:4200")

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestParam(required = false) String name) {
        if (name == null) {
            return ResponseEntity.ok("Name parameter is missing.");
        }
        return ResponseEntity.ok("Received name: " + name);
    }
    @PostMapping("/AddUser")
    public ResponseEntity<String> addUser(
            @RequestParam("name") @NotNull String name,
            @RequestParam("email") @NotNull @Email String email,
            @RequestParam("file") MultipartFile file) {
        try {
            // Save the file and process user details
            String fileName = fileService.save(file); // Ensure fileService is injected and implemented
            User user = new User(name, email, fileName); // Assuming User has a corresponding constructor
            userService.addUser(user);

            System.out.println("name: " + name);
            System.out.println("Email: " + email);
            System.out.println("File Name: " + file.getOriginalFilename());
            System.out.println("File Size: " + file.getSize());

            return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/DisplayUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll ();
        return new ResponseEntity<> ( users, HttpStatus.OK );
    }

    @GetMapping("/GetUsers/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        Optional<User> optionalUser = userRepository.findById ( id );
        return optionalUser.map ( user -> new ResponseEntity<> ( user, HttpStatus.OK ) )
                .orElseGet ( () -> new ResponseEntity<> ( HttpStatus.NOT_FOUND ) );
    }

    @PutMapping(value = "/UpdateUser/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateUser(
            @PathVariable String id,
            @RequestParam("name") @NotNull String name,
            @RequestParam("email") @NotNull @Email String email,
            @RequestParam("file") MultipartFile file) {
        try {
            // Enregistrer le fichier et traiter les détails de l'utilisateur
            String fileName = fileService.save(file);

            // Trouver l'utilisateur existant
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                existingUser.setName(name);
                existingUser.setEmail(email);
                existingUser.setFileName(fileName);
                existingUser.setVerificationLink("http://localhost:8100/VerifyCertificate/" + id);

                // Appeler le service pour mettre à jour l'utilisateur
                userService.updateUser(id, existingUser);

                return ResponseEntity.status(HttpStatus.OK).body("User updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @DeleteMapping("/DeleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        if (userRepository.existsById ( id )) {
            userRepository.deleteById ( id );
            return new ResponseEntity<> ( "User deleted successfully", HttpStatus.NO_CONTENT );
        } else {
            return new ResponseEntity<> ( "User not found", HttpStatus.NOT_FOUND );
        }
    }

    @GetMapping("/VerifyCertificate/{id}")
    public ResponseEntity<String> verifyCertificate(@PathVariable String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String response = "<div class='certificate-details' style='text-align: center; margin: 0 auto; width: fit-content; padding: 50px ; margin-top : 50px; border: 3px solid #ddd; border-radius: 8px; background : #CDECF7' ; justify-content: center; align-items: center;>" + "<h1 style=\"color: #2c3e50;\">Certification Details From Lunflow</h1>" +
                    "<h2 style=\"color: #3498db\n;\">Certificate for <span class=\"name\" style=\"font-weight: bold;\";\" color : #ffff ;\"><span class='name' \" >" + user.getName() + "</span> is valid.</h2>" +
                    "<h3 style=\"color: #3498db;\"><strong>Details:</strong></h3>" +
                    "<h3>ID: <span class='id'>" + user.getId() + "</span></h3>" +
                    "<h3>Name: <span class='name'>" + user.getName() + "</span></h3>" +
                    "<h3> Email: <span class='email'>" + user.getEmail() + "</span></h3>" +
                    "<h3>FileName: <span class='filename'>" + user.getFileName() + "</span></h3>" +
                    "<h3>Verification Link: <a href='" + user.getVerificationLink() + "' class='verification-link'>" + user.getVerificationLink() + "</a></h3>" +
                    " <button><div class=\"button-container\"><a href=\"http://localhost:4200/display-user\" class=\"back-button\">Go to Home Page</a></div></button>"+
                    "</div>";
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Certificate not found.", HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if ("admin".equals(user.getName()) && "admin".equals(user.getEmail())) {

            return ResponseEntity.ok("Authenticated");
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }




}




