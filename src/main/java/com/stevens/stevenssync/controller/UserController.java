package com.stevens.stevenssync.controller;

import com.stevens.stevenssync.entities.User;
import com.stevens.stevenssync.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody User user) {
        Optional<User> username_check = userRepository.findByUsername(user.getUsername());
        //checking if the username that the user entered already exists in the database
        if(username_check.isPresent()){
            String wanted_username = user.getUsername();
            int additional_nums = new Random().nextInt(1000);
            String new_username = wanted_username + additional_nums;
            Optional<User> username_check2 = userRepository.findByUsername(new_username);
            while(username_check2.isPresent()){
                new_username = "";
                additional_nums = new Random().nextInt(1000);
                new_username = new_username + additional_nums;
            }
            return new ResponseEntity<>("Username already in use"+ '\n' + "Available Username: " + new_username, HttpStatus.CONFLICT);
        }
        //checking if the email that the user entered already exists in the database
        Optional<User> email_check = userRepository.findByEmail(user.getEmail());
        if(email_check.isPresent()){
            return new ResponseEntity<>("Email already in use", HttpStatus.CONFLICT);
        }
        //if none of those are true then the user is able to create an account with the information that is entered.
        userRepository.save(user);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/{userId}")
    public ResponseEntity getUser(@PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<Integer> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User current_user = user.get();
        return ResponseEntity.ok(current_user.getId());
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{userId}")
    public ResponseEntity updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User existingUser = userOptional.get();
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        userRepository.save(existingUser);
        return ResponseEntity.ok().build();
    }
}
