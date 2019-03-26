package com.ipiecoles.java.java350.acceptance;

import com.ipiecoles.java.java350.Java350Application;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import com.ipiecoles.java.java350.service.EmployeService;
import cucumber.api.java8.En;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
public class EmployeStepDefinitions implements En {


    @Autowired
    EmployeService employeService;


    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    public EmployeStepDefinitions() {
        When("J\'embauche une personne appelée {string} {string} diplômée d\'un {string} en tant que {string} avec un taux d\'activité de {double}", (String prenom, String nom, String diplome, String poste, Double txActivite) -> {
            employeService.embaucheEmploye(nom, prenom, Poste.valueOf(poste.toUpperCase()), NiveauEtude.valueOf(diplome.toUpperCase()), txActivite);
        });
        Then("J\'obtiens bien un nouvel employé appelé {string} {string} portant le matricule {string} et touchant un salaire de {double} €", (String prenom, String nom, String matricule, Double salaire) -> {
            Employe e = employeRepository.findByMatricule(matricule);
            Assertions.assertEquals(Entreprise.PERFORMANCE_BASE, e.getPerformance());
            Assertions.assertEquals(salaire, e.getSalaire());
            Assertions.assertEquals(prenom, e.getPrenom());
            Assertions.assertEquals(nom, e.getNom());
        });
    }

    public void CalculPerformanceStepDefinitions() {
        When("Je cherche un commercial avec le matricule {matricule} qui a une chiffre d'affaire de {caTraite} et un objectif de chiffre d'affaire de {objectifCa}", (String matricule, Long caTraite, Long objectifCa) -> {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
        });
        Then("Quand je cherche le commercial portant le matricule {matricule}, je trouve bien une performance de {performance}", (String matricule, Integer performance) -> {
            Employe e = employeRepository.findByMatricule(matricule);
            Assertions.assertEquals(performance, e.getPerformance());
        });
    }
}