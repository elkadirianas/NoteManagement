package com.example.notemanagment.Repository;

import com.example.notemanagment.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {

    List<Student> findByFieldIdAndSemesterId(Integer fieldId, Long semesterId);
}
