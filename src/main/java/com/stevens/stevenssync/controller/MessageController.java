package com.stevens.stevenssync.controller;

import com.stevens.stevenssync.entities.Classroom;
import com.stevens.stevenssync.entities.Message;
import com.stevens.stevenssync.entities.User;
import com.stevens.stevenssync.repositories.ClassroomRepository;
import com.stevens.stevenssync.repositories.MessageRepository;
import com.stevens.stevenssync.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassroomRepository classroomRepository;

    @PostMapping("/api/user/{userId}/classrooms/{classroomId}/messages")
    public ResponseEntity<String> createMessage(@PathVariable int userId, @PathVariable int classroomId, @RequestBody Message message) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if(classroom.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User currentUser = user.get();
        Classroom currentClassroom = classroom.get();
        //Spring and JPA understand that the message belongs to one user and one classroom due to the annoations
        message.setSender(currentUser);
        message.setClassroom(currentClassroom);
        message.setTimestamp(LocalDateTime.now().toString());

        messageRepository.save(message);
        return new ResponseEntity<>("Message sent", HttpStatus.CREATED);

    }
    @GetMapping("/api/classroom/{classroomId}/messages")
    public ResponseEntity<List<Message>> getClassroomMessages(@PathVariable int classroomId) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if(classroom.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Classroom currentClassroom = classroom.get();
        return new ResponseEntity<>(currentClassroom.getMessageList(), HttpStatus.OK);
    }


}
