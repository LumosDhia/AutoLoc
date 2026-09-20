# AutoLoc

<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,hibernate,maven,mysql,git,github,idea,postman" alt="Java, Spring, Hibernate, Maven, MySQL, Git, GitHub, IntelliJ IDEA, Postman" />
</p>

Plateforme de gestion de location de véhicules **multi-agences** — étude de cas du module
UP ASI (Architecture des Systèmes d'Information), ESPRIT.

> **Statut : v0 (Atelier 0)** — mise en place de l'environnement et cadrage initial.

## 1. Objectifs du projet

- Permettre à des **clients** de consulter le parc de véhicules disponibles et de réserver en ligne.
- Permettre aux **agences** de gérer leur parc (véhicules, disponibilité, maintenance) et leurs locations.
- Offrir aux **responsables d'agence** un suivi de l'activité de leur agence.
- Offrir aux **administrateurs** une vue globale et la gestion des agences et des utilisateurs.
- Exposer le tout via une **API REST** documentée (Swagger / springdoc-openapi).

## 2. Acteurs identifiés

| Acteur | Rôle |
|---|---|
| **Client** | Recherche un véhicule, réserve, consulte et annule ses réservations. |
| **Agent d'agence** | Gère le parc de son agence, enregistre départs/retours, crée des locations au comptoir. |
| **Responsable d'agence** | Supervise son agence : statistiques, tarifs, validation des opérations sensibles. |
| **Administrateur** | Gère les agences, les comptes utilisateurs, les rôles et la configuration globale. |

## 3. Cas d'utilisation (première liste)

**Client**
- S'inscrire / se connecter
- Rechercher des véhicules disponibles (agence, dates, catégorie)
- Réserver un véhicule
- Consulter / annuler ses réservations

**Agent d'agence**
- Gérer les véhicules de l'agence (ajout, modification, retrait)
- Enregistrer la remise et le retour d'un véhicule
- Créer une location au comptoir
- Déclarer une maintenance ou un sinistre

**Responsable d'agence**
- Consulter les statistiques de l'agence (taux d'occupation, chiffre d'affaires)
- Définir et modifier les tarifs
- Gérer les agents de son agence

**Administrateur**
- Créer et configurer les agences
- Gérer les utilisateurs et leurs rôles
- Superviser l'activité globale de la plateforme

## 4. Stack technique

| Catégorie | Outils |
|---|---|
| Langage / Build | Java 17, Maven |
| Framework | Spring Boot, Spring Data JPA, Spring MVC, Spring AOP, Spring Scheduler |
| Base de données | MySQL 8 (développement), H2 en mémoire (tests) |
| Productivité | Lombok, SLF4J/Logback, MapStruct (optionnel) |
| Documentation API | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, MockMvc, Jacoco |
| Outillage | Git/GitHub, Postman, IntelliJ IDEA Ultimate |

## 5. Environnement de développement

Prérequis installés (Atelier 0) : JDK 17, IntelliJ IDEA Ultimate, MySQL 8, Postman, Git.

Base de données locale : `autoloc_db` (`utf8mb4`).

```sql
CREATE DATABASE autoloc_db CHARACTER SET utf8mb4;
```

Vérification de la chaîne d'outils :

```bash
java -version    # 17.x
javac -version   # 17.x
git --version
```

## 6. Preuve de l'environnement fonctionnel

Voir [`docs/environment.md`](docs/environment.md).
