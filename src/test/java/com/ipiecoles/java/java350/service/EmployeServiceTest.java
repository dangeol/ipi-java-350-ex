package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.times;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.85, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        //When/Then
        EntityExistsException e = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When/Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
    }

    @Test
    public void testCalculPerformanceCommercialCATraite79000() throws EmployeException {
        //Given
        Long caTraite = 79000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(5.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(1, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialCATraite95000() throws EmployeException {
        //Given
        Long caTraite = 95000L;
        Long objectifCa = 100000L;
        Integer performance = 6;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(7, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialCATraite_0_objectifCa_0() throws EmployeException {
        //Given
        Long caTraite = 0L;
        Long objectifCa = 0L;
        Integer performance = 6;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(1, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialCATraite105000() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(5.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(2, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialCATraite106000supMoyenne() throws EmployeException {
        //Given
        Long caTraite = 106000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(4, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialMoyenneNull() throws EmployeException {
        //Given
        Long caTraite = 106000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(null);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(3, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    public void testCalculPerformanceCommercialCATraite130000() throws EmployeException {
        //Given
        Long caTraite = 130000L;
        Long objectifCa = 100000L;
        Integer performance = 5;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(10, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialMinimum1() throws EmployeException {
        //Given
        Long caTraite = 80000L;
        Long objectifCa = 100000L;
        Integer performance = 1;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(2L), Entreprise.SALAIRE_BASE, performance, 1.0));
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2.5);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(1, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void testCalculPerformanceCommercialEmployeNull() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        when(employeRepository.findByMatricule(matricule)).thenReturn(null);

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa) );
        Assertions.assertEquals("Le matricule " + matricule + " n'existe pas !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeCATraiteNull() throws EmployeException {
        //Given
        Long caTraite = null;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeCATraiteNegative() throws EmployeException {
        //Given
        Long caTraite = -80000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "C00001";

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeObjectifCaNull() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = null;
        Integer performance = 2;
        String matricule = "C00001";

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeObjectifCaNegative() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = -100000L;
        Integer performance = 2;
        String matricule = "C00001";

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeMatriculeNull() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = null;

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", ee.getMessage());
    }

    @Test
    public void testCheckParametresEntreeMatriculeD() throws EmployeException {
        //Given
        Long caTraite = 105000L;
        Long objectifCa = 100000L;
        Integer performance = 2;
        String matricule = "D00001";

        //When/Then
        EmployeException ee = Assertions.assertThrows(EmployeException.class, ()
                -> employeService.checkParametresEntree(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", ee.getMessage());
    }

}