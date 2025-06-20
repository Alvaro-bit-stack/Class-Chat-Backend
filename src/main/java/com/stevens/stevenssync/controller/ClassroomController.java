package com.stevens.stevenssync.controller;

import com.stevens.stevenssync.entities.Classroom;
import com.stevens.stevenssync.entities.User;
import com.stevens.stevenssync.repositories.ClassroomRepository;
import com.stevens.stevenssync.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ClassroomController {
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private UserRepository userRepository;



    @PostMapping("/api/classrooms")
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        classroomRepository.save(classroom);
        return new ResponseEntity<>(classroom, HttpStatus.CREATED);
    }

    @PostMapping("/api/user/{userId}/classrooms")
    public ResponseEntity<String> addUsertoClassroom(@RequestBody Classroom requested_classroom, @PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        //Checking is user exists(safety check)
        if(user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User currentUser = user.get();
        Optional<Classroom> cur_check_Classroom = classroomRepository.findById(requested_classroom.getId());
        //Checking if classroom exists
        if(cur_check_Classroom.isEmpty()) {
            return new ResponseEntity<>("Classroom not found", HttpStatus.NOT_FOUND);
        }
        Classroom checked_Classroom = cur_check_Classroom.get();
        //Checking if user already is in the class
        if (currentUser.getClassrooms().contains(checked_Classroom)) {
            return new ResponseEntity<>("You already are enrolled in the class", HttpStatus.CONFLICT);
        }
        currentUser.getClassrooms().add(cur_check_Classroom.get());
        checked_Classroom.getUserList().add(currentUser);
        userRepository.save(currentUser);
        return new ResponseEntity<>("User successfully enrolled", HttpStatus.CREATED);

    }

    @GetMapping("/api/classrooms")
    private List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }
    @GetMapping("/api/user/{userId}/classrooms")
    public ResponseEntity<List<Classroom>> getUsersClassroom(@PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User currentUser = user.get();
        return  ResponseEntity.ok(currentUser.getClassrooms());

    }
    @GetMapping("/api/classroom/{classroomId}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable int classroomId) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if (classroom.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(classroom.get(), HttpStatus.OK);
    }


}
