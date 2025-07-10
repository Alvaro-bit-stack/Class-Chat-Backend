package com.stevens.stevenssync.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "app_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String filename;
    private String fileUrl;
    private String category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name= "user_id")
    private User sender;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name= "classroom_id")
    private Classroom classroom;

}
