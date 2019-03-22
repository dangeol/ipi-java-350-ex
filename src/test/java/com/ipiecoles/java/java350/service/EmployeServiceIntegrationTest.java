package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.repository.EmployeRepository;
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
public class EmployeServiceIntegrationTest {

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
    public void integrationCalculPerformanceCommercial() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C00001", LocalDate.now(),
                Entreprise.SALAIRE_BASE, 10, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C00002", LocalDate.now(),
                Entreprise.SALAIRE_BASE, 2, 1.0));
        Long caTraite = 130000L;
        Long objectifCa = 100000L;
        String matricule = "C00002";

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        Employe e = employeRepository.findByMatricule(matricule);
        Integer performance = e.getPerformance();
        Assertions.assertEquals(6, performance.intValue());
    }
}