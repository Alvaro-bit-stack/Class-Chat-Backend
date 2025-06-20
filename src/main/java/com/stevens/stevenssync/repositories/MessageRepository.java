package com.stevens.stevenssync.repositories;

import com.stevens.stevenssync.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
