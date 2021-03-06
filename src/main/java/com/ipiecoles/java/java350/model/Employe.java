package com.ipiecoles.java.java350.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Employe {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private LocalDate dateEmbauche;

    private Double salaire = Entreprise.SALAIRE_BASE;

    private Integer performance = Entreprise.PERFORMANCE_BASE;

    private Double tempsPartiel = 1.0;

    public Employe() {
    }

    public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire, Integer performance, Double tempsPartiel) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.performance = performance;
        this.tempsPartiel = tempsPartiel;
    }

    /**
     * Retourne le nombre d'année d'ancienneté de l'employé par rapport à sa date d'embauche (on ne prend pas en compte
     * les mois et les jours. Il faut en revanche que la d'embauche soit non nulle et l'année antérieure à l'année courante
     * sinon on renvoie une ancienneté de 0
     *
     * @return le nombre d'année d'ancienneté
     */
    public final Integer getNombreAnneeAnciennete() {
        return dateEmbauche != null && LocalDate.now().getYear() >= dateEmbauche.getYear() ? LocalDate.now().getYear() - dateEmbauche.getYear() : 0;
    }

    public Integer getNbConges() {
        return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
    }



    /**
     * Nombre de jours de RTT =
     *   Nombre de jours dans l'année
     * – plafond maximal du forfait jours de la convention collective
     * – nombre de jours de repos hebdomadaires
     * – jours de congés payés
     * – nombre de jours fériés tombant un jour ouvré
     *
     * Au prorata de son pourcentage d'activité (arrondi au supérieur)
     * Les dates des jours fériés (notemmant Pâques) sont disponibles pour les années entre 2016 et 2040.
     *
     * @return le nombre de jours de RTT
     * @throws IllegalArgumentException si l'année est avant 2016 ou après 2040
     */
    public Integer getNbRtt(){
        return getNbRtt(LocalDate.now());
    }

    public Integer getNbRtt(LocalDate date) {
        if (date.getYear() < 2016 || date.getYear() > 2040) {
            throw new IllegalArgumentException("L'année doit être compris entre 2016 et 2040 !");
        }
        int nombreJoursAnnee = date.isLeapYear() ? 366 : 365;
        int nombreSamDim = 104;
        switch (LocalDate.of(date.getYear(),1,1).getDayOfWeek()){
            case FRIDAY:
                if(date.isLeapYear()) ++nombreSamDim;
                break;
            case SATURDAY:
                if(date.isLeapYear()) nombreSamDim += 2;
                else ++nombreSamDim;
                break;
            case SUNDAY:
                ++nombreSamDim;
                break;
            default:
                break;
        }
        //Calculer le nombre des jours fériers hors samedi et dimanche
        int nombreJoursFeriesPasWe = (int) Entreprise.joursFeries(date).stream().filter(localDate
                -> localDate.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();
        return (int) Math.ceil((nombreJoursAnnee - Entreprise.NB_JOURS_MAX_FORFAIT
                - nombreSamDim - Entreprise.NB_CONGES_BASE - nombreJoursFeriesPasWe) * tempsPartiel);
    }

    /**
     * Calcul de la prime annuelle selon la règle :
     * Pour les managers : Prime annuelle de base bonnifiée par l'indice prime manager
     * Pour les autres employés, la prime de base plus éventuellement la prime de performance calculée si l'employé
     * n'a pas la performance de base, en multipliant la prime de base par un l'indice de performance
     * (égal à la performance à laquelle on ajoute l'indice de prime de base)
     *
     * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en multipliant le nombre d'année
     * d'ancienneté avec la prime d'ancienneté. La prime est calculée au pro rata du temps de travail de l'employé
     *
     * @return la prime annuelle de l'employé en Euros et cents
     */
    public Double getPrimeAnnuelle(){
        //Calcule de la prime d'ancienneté
        Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        Double prime;
        //Prime du manager (matricule commençant par M) : Prime annuelle de base multipliée par l'indice prime manager
        //plus la prime d'anciennté.
        if(matricule != null && matricule.startsWith("M")) {
            prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        }
        //Pour les autres employés en performance de base, uniquement la prime annuelle plus la prime d'ancienneté.
        else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)){
            prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        }
        //Pour les employés plus performance, on bonnifie la prime de base en multipliant par la performance de l'employé
        // et l'indice de prime de base.
        else {
            prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE) + primeAnciennete;
        }
        //Au pro rata du temps partiel.
        return Math.round(prime * this.tempsPartiel * 100)/100.0;
    }

    /**
     * Méthode qui augmente le salaire par un certain pourcentage. Le pourcentage autorisé est un double
     * entre 0.0 et 1.0. Le pourcentage maximal autorisé est de 1.0 (100%) afin d'empêcher une augmentation
     * trop importante.
     * Pour rappel: un double ne peut pas être "null".
     *
     * @param pourcentage
     * @throws IllegalArgumentException
     */
    public void augmenterSalaire(double pourcentage) throws IllegalArgumentException {
        if (pourcentage < 0.0 || pourcentage > 1.0) {
            throw new IllegalArgumentException("Le pourcentage doit être un double compris entre 0 et 1 !");
        }
        this.salaire = this.salaire * (1 + pourcentage);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the matricule
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * @param matricule the matricule to set
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return the dateEmbauche
     */
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    /**
     * @param dateEmbauche the dateEmbauche to set
     */
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    /**
     * @return the salaire
     */
    public Double getSalaire() {
        return salaire;
    }

    /**
     * @param salaire the salaire to set
     */
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Double getTempsPartiel() {
        return tempsPartiel;
    }

    public void setTempsPartiel(Double tempsPartiel) {
        this.tempsPartiel = tempsPartiel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employe)) return false;
        Employe employe = (Employe) o;
        return Objects.equals(id, employe.id) &&
                Objects.equals(nom, employe.nom) &&
                Objects.equals(prenom, employe.prenom) &&
                Objects.equals(matricule, employe.matricule) &&
                Objects.equals(dateEmbauche, employe.dateEmbauche) &&
                Objects.equals(salaire, employe.salaire) &&
                Objects.equals(performance, employe.performance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, matricule, dateEmbauche, salaire, performance);
    }
}
