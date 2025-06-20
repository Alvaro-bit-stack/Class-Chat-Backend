package com.stevens.stevenssync.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;
    private String timestamp;


    @JsonIgnoreProperties("messages")
    @ManyToOne
    @JoinColumn(name = "user_id")  // Person sending the message
    private User sender;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "classroom_id") // Classroom where message was posted
    private Classroom classroom;
}
