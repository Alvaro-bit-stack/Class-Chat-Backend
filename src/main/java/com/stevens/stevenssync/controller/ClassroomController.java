package com.stevens.stevenssync.controller;

import com.stevens.stevenssync.entities.Classroom;
import com.stevens.stevenssync.entities.User;
import com.stevens.stevenssync.repositories.ClassroomRepository;
import com.stevens.stevenssync.repositories.UserRepository;
import com.stevens.stevenssync.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
public class ClassroomController {
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;




    @PostMapping("/api/classrooms")
    public ResponseEntity<Classroom> createClassroomWithImage(
            @RequestParam("courseName") String courseName,
            @RequestParam("courseCode") String courseCode,
            @RequestParam("semester") String semester,
            @RequestParam("instructor") String instructor,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            Classroom classroom = new Classroom();
            classroom.setCourseName(courseName);
            classroom.setCourseCode(courseCode);
            classroom.setSemester(semester);
            classroom.setInstructor(instructor);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = s3Service.uploadImage(imageFile);
                classroom.setImageUrl(imageUrl);
            }

            classroomRepository.save(classroom);
            return new ResponseEntity<>(classroom, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    @PutMapping("/api/classroom/{classroomId}")
    public ResponseEntity<Classroom> addClassroomImage(@PathVariable int classroomId, @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try{
            Optional<Classroom> classroom = classroomRepository.findById(classroomId);
            if(classroom.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Classroom cur_classroom = classroom.get();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = s3Service.uploadImage(imageFile);
                cur_classroom.setImageUrl(imageUrl);
            }
            classroomRepository.save(cur_classroom);
            return new ResponseEntity<>(cur_classroom, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping ("/api/classroom/{classroomId}")
    ResponseEntity<HttpStatus> deleteClassroom(@PathVariable int classroomId){
        try{
            Optional<Classroom> classroom = classroomRepository.findById(classroomId);
            if(classroom.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Classroom cur_classroom = classroom.get();
            List<User> userList = cur_classroom.getUserList();
            for (User user : userList) {
                user.getClassrooms().remove(cur_classroom);
                userRepository.save(user);
            }
            cur_classroom.getUserList().clear();
            classroomRepository.save(cur_classroom);
            classroomRepository.delete(cur_classroom);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
