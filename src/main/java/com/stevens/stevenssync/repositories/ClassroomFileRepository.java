package com.stevens.stevenssync.repositories;

import com.stevens.stevenssync.entities.Classroom;
import com.stevens.stevenssync.entities.File;
import com.stevens.stevenssync.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface ClassroomFileRepository extends JpaRepository<File,Integer> {
    List<File> findByClassroomId(int classroomId);
}
