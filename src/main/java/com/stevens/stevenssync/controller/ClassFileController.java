package com.stevens.stevenssync.controller;

import com.stevens.stevenssync.entities.Classroom;
import com.stevens.stevenssync.entities.File;
import com.stevens.stevenssync.entities.User;
import com.stevens.stevenssync.repositories.ClassroomFileRepository;
import com.stevens.stevenssync.repositories.ClassroomRepository;
import com.stevens.stevenssync.repositories.UserRepository;
import com.stevens.stevenssync.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
public class ClassFileController {
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ClassroomFileRepository classroomFileRepository;

    @PostMapping("/api/user/{userId}/classroom/{classroomId}/files")
    public ResponseEntity<String> saveClassroomFile(@PathVariable int classroomId, @PathVariable int userId, @RequestParam("file_name") String filename, @RequestParam("category") String category, @RequestParam("file") MultipartFile file ) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if (classroom.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Classroom cur_classroom = classroom.get();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User cur_user = user.get();
        //create a new file entity
        File newFile = new File();
        newFile.setFilename(filename);
        newFile.setCategory(category);
        newFile.setSender(cur_user);
        newFile.setClassroom(cur_classroom);
        //update this so it accepts a Multipart file too not just image
        try{
            String fileUrl = s3Service.uploadFile(file);
            newFile.setFileUrl(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("S3 upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        classroomFileRepository.save(newFile);
        //add file entity to the list of file entities corresponding to the classroom
        List<File> filesList = cur_classroom.getFileList();
        filesList.add(newFile);
        classroomRepository.save(cur_classroom);

        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @GetMapping("/api/classroom/{classroomId}/files")
    public ResponseEntity<List<File>> getClassroomFiles(@PathVariable int classroomId) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if (classroom.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Classroom cur_classroom = classroom.get();
        List<File> filesList = cur_classroom.getFileList();
        return new ResponseEntity<>(filesList, HttpStatus.OK);
    }
    @DeleteMapping("/api/classroom/{classroomId}/files/{fileId}")
    public ResponseEntity<String> deleteClassroomFile(@PathVariable int classroomId, @PathVariable int fileId) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if (classroom.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Classroom cur_classroom = classroom.get();
        File file = classroomFileRepository.findById(fileId).get();
        cur_classroom.getFileList().remove(file);
        classroomFileRepository.delete(file);
        //also have to delete it from the s3
        return ResponseEntity.noContent().build();
    }



}
