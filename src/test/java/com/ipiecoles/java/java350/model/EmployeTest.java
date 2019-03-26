package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void getNombreAnneeAncienneteNow(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now());

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNminus2(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().minusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(2, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNull(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(null);

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNplus2(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().plusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 'T12345', 0, 1.0, 1000.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "2, 'T12345', 0, 1.0, 2300.0",
            "2, 'T12345', 1, 1.0, 2400.0",
            "1, 'M12345', 0, 1.0, 1700.0",
            "1, 'M12345', 5, 1.0, 2200.0",
            "2, 'M12345', 0, 1.0, 1700.0",
            "2, 'M12345', 8, 1.0, 2500.0"
    })
    public void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle){
        //Given
        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertEquals(primeAnnuelle, prime);

    }

    @Test
    public void augmenterSalaireParZero() {
        //Given
        Employe e = new Employe();
        e.setSalaire(1000.0);

        //When
        e.augmenterSalaire(0.0);
        Double salaireAugmente = e.getSalaire();
        Double salaireTheorique = 1000.0;

        //Then
        Assertions.assertEquals(salaireTheorique, salaireAugmente);
    }

    @Test
    public void augmenterSalairePar100() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setSalaire(1000.0);

        //When
        e.augmenterSalaire(1.0);
        Double salaireAugmente = e.getSalaire();
        Double salaireTheorique = 2000.0;

        //Then
        Assertions.assertEquals(salaireTheorique, salaireAugmente);
    }

    @Test
    public void augmenterSalairePar110() throws IllegalArgumentException {
        //Given
        Employe e = new Employe();

        //When/Then
        IllegalArgumentException ee = Assertions.assertThrows(IllegalArgumentException.class, () -> e.augmenterSalaire(1.1));
        Assertions.assertEquals("Le pourcentage doit être un double compris entre 0 et 1 !", ee.getMessage());
    }

    @Test
    public void augmenterSalaireParNegative() throws IllegalArgumentException{
        //Given
        Employe e = new Employe();

        //When/Then
        IllegalArgumentException iae = Assertions.assertThrows(IllegalArgumentException.class, () -> e.augmenterSalaire(-0.1));
        Assertions.assertEquals("Le pourcentage doit être un double compris entre 0 et 1 !", iae.getMessage());
    }


    @ParameterizedTest
    @CsvSource({
            "2019, 8, 4",
            "2021, 11, 6",
            "2022, 10, 5",
            "2032, 12, 6",
            "2023, 8, 4",
            "2028, 8, 4",
            "2016, 10, 5"
    })
    public void getNbRtt(int annee, Integer RTTcalculeTempsComplet, Integer RTTcalculeTempsPartiel) {
        //Given
        Employe employeTempsComplet = new Employe("Doe", "John", "C12345",
                LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 5, 1.0);
        Employe employeTempsPartiel = new Employe("Doe", "John", "C12346",
                LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 5, 0.5);

        //When
        Integer RTTTempsComplet = employeTempsComplet.getNbRtt(LocalDate.of(annee,1,1));
        Integer RTTTempsPartiel = employeTempsPartiel.getNbRtt(LocalDate.of(annee,1,1));

        //Then
        Assertions.assertEquals(RTTcalculeTempsComplet, RTTTempsComplet);
        Assertions.assertEquals(RTTcalculeTempsPartiel, RTTTempsPartiel);
    }

    @Test
    public void getNbRtt() {
        //Given
        Employe employe = new Employe();

        //When
        Integer RTT = employe.getNbRtt();

        //Then
        Assertions.assertEquals(8, RTT.intValue());
    }

    @Test
    public void getNbRttAnneeAvant2016() throws IllegalArgumentException {
        //Given
        Employe e = new Employe();

        //When/Then
        IllegalArgumentException iae = Assertions.assertThrows(IllegalArgumentException.class,
                () -> e.getNbRtt(LocalDate.of(2015,1,1)));
        Assertions.assertEquals("L'année doit être compris entre 2016 et 2040 !", iae.getMessage());
    }

    @Test
    public void getNbRttAnneeApres2040() throws IllegalArgumentException {
        //Given
        Employe e = new Employe();

        //When/Then
        IllegalArgumentException iae = Assertions.assertThrows(IllegalArgumentException.class,
                () -> e.getNbRtt(LocalDate.of(2041,1,1)));
        Assertions.assertEquals("L'année doit être compris entre 2016 et 2040 !", iae.getMessage());
    }

}