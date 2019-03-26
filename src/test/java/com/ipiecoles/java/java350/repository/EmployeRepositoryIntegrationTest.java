package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.service.EmployeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeRepositoryIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void integrationAvgPerformanceWhereMatriculeStartsWith() {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T00001", LocalDate.now(),
                Entreprise.SALAIRE_BASE, 10, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C00001", LocalDate.now(),
                Entreprise.SALAIRE_BASE, 9, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C00002", LocalDate.now(),
                Entreprise.SALAIRE_BASE, 3, 1.0));

        //When/Then
        Assertions.assertEquals(6, employeRepository.avgPerformanceWhereMatriculeStartsWith("C").intValue());
    }
}