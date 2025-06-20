package com.stevens.stevenssync.repositories;

import com.stevens.stevenssync.entities.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
}
