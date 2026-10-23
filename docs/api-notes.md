# Atelier 5 : API REST, tests Postman et documentation Swagger

## A. Endpoints

Préfixe commun `/api` défini une seule fois (`server.servlet.context-path=/api`). Les 404 viennent
de `ResourceNotFoundException`, les 400 de `IllegalArgumentException`, tous deux traduits par
`GlobalExceptionHandler` (corps JSON : `statut`, `message`, `horodatage`).

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| `AgenceController` | `GET /api/agences` | `IAgenceService.findAll()` | 200 | — |
| `AgenceController` | `GET /api/agences/{id}` | `IAgenceService.findById(id)` | 200 | 404 |
| `AgenceController` | `POST /api/agences` | `IAgenceService.create(...)` | 201 | 400 |
| `AgenceController` | `PUT /api/agences/{id}` | `IAgenceService.update(id, ...)` | 200 | 404, 400 |
| `AgenceController` | `DELETE /api/agences/{id}` | `IAgenceService.deleteById(id)` | 204 | 404 |
| `EmployeController` | `GET /api/employes` | `IEmployeService.findAll()` | 200 | — |
| `EmployeController` | `GET /api/employes/{id}` | `IEmployeService.findById(id)` | 200 | 404 |
| `EmployeController` | `POST /api/employes` | `IEmployeService.create(...)` | 201 | 400 |
| `EmployeController` | `PUT /api/employes/{id}` | `IEmployeService.update(id, ...)` | 200 | 404, 400 |
| `EmployeController` | `DELETE /api/employes/{id}` | `IEmployeService.deleteById(id)` | 204 | 404 |
| `VehiculeController` | `GET /api/vehicules` | `IVehiculeService.findAll()` | 200 | — |
| `VehiculeController` | `GET /api/vehicules/{id}` | `IVehiculeService.findById(id)` | 200 | 404 |
| `VehiculeController` | `POST /api/vehicules` | `IVehiculeService.create(...)` | 201 | 400 |
| `VehiculeController` | `PUT /api/vehicules/{id}` | `IVehiculeService.update(id, ...)` | 200 | 404, 400 |
| `VehiculeController` | `DELETE /api/vehicules/{id}` | `IVehiculeService.deleteById(id)` | 204 | 404 |
| `EquipementController` | `GET /api/equipements` | `IEquipementService.findAll()` | 200 | — |
| `EquipementController` | `GET /api/equipements/{id}` | `IEquipementService.findById(id)` | 200 | 404 |
| `EquipementController` | `POST /api/equipements` | `IEquipementService.create(...)` | 201 | 400 |
| `EquipementController` | `PUT /api/equipements/{id}` | `IEquipementService.update(id, ...)` | 200 | 404, 400 |
| `EquipementController` | `DELETE /api/equipements/{id}` | `IEquipementService.deleteById(id)` | 204 | 404 |
| `ClientController` | `GET /api/clients` | `IClientService.findAll()` | 200 | — |
| `ClientController` | `GET /api/clients/{id}` | `IClientService.findById(id)` | 200 | 404 |
| `ClientController` | `POST /api/clients` | `IClientService.create(...)` | 201 | 400 |
| `ClientController` | `PUT /api/clients/{id}` | `IClientService.update(id, ...)` | 200 | 404, 400 |
| `ClientController` | `DELETE /api/clients/{id}` | `IClientService.deleteById(id)` | 204 | 404 |
| `ReservationController` | `GET /api/reservations` | `IReservationService.findAll()` | 200 | — |
| `ReservationController` | `GET /api/reservations/{id}` | `IReservationService.findById(id)` | 200 | 404 |
| `ReservationController` | `POST /api/reservations` | `IReservationService.create(...)` | 201 | 400 |
| `ReservationController` | `PUT /api/reservations/{id}` | `IReservationService.update(id, ...)` | 200 | 404, 400 |
| `ReservationController` | `DELETE /api/reservations/{id}` | `IReservationService.deleteById(id)` | 204 | 404 |
| `ContratController` | `GET /api/contrats` | `IContratService.findAll()` | 200 | — |
| `ContratController` | `GET /api/contrats/{id}` | `IContratService.findById(id)` | 200 | 404 |
| `ContratController` | `POST /api/contrats` | `IContratService.create(...)` | 201 | 400 |
| `ContratController` | `PUT /api/contrats/{id}` | `IContratService.update(id, ...)` | 200 | 404, 400 |
| `ContratController` | `DELETE /api/contrats/{id}` | `IContratService.deleteById(id)` | 204 | 404 |
| `PaiementController` | `GET /api/paiements` | `IPaiementService.findAll()` | 200 | — |
| `PaiementController` | `GET /api/paiements/{id}` | `IPaiementService.findById(id)` | 200 | 404 |
| `PaiementController` | `POST /api/paiements` | — (aucune méthode) | — | 405 |
| `MaintenanceController` | `GET /api/maintenances` | `IMaintenanceService.findAll()` | 200 | — |
| `MaintenanceController` | `GET /api/maintenances/{id}` | `IMaintenanceService.findById(id)` | 200 | 404 |
| `MaintenanceController` | `POST /api/maintenances` | `IMaintenanceService.create(...)` | 201 | 400 |
| `MaintenanceController` | `PUT /api/maintenances/{id}` | `IMaintenanceService.update(id, ...)` | 200 | 404, 400 |
| `MaintenanceController` | `DELETE /api/maintenances/{id}` | `IMaintenanceService.deleteById(id)` | 204 | 404 |

Cas 400 testés dans Postman : agence, employé ou client au nom vide, véhicule au tarif négatif,
équipement au libellé vide (le modèle n'a pas de quantité), réservation ou maintenance dont la
date de fin précède la date de début (le modèle de maintenance n'a pas de coût), contrat au
montant négatif ou créé avec un identifiant.

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| `Contrat.paiements` / `Paiement.contrat` | `@JsonManagedReference("contrat-paiements")` / `@JsonBackReference("contrat-paiements")` | Seule vraie composition : les paiements se créent dans le JSON du contrat (`cascade = ALL`). À la lecture, Jackson renseigne `paiement.contrat` avec le contrat parent, sans quoi la colonne `id_contrat` (non nulle) serait vide. |
| `Agence.employes`, `Agence.vehicules` | `@JsonIgnore` | Côté inverse : un employé ou un véhicule se crée seul et désigne son agence par `{"agence": {"idAgence": 1}}`. |
| `Client.reservations`, `Vehicule.reservations` | `@JsonIgnore` | Côté inverse : la réservation se crée seule avec `client` et `vehicule` en identifiants. |
| `Equipement.vehicules` | `@JsonIgnore` | Côté inverse du `@ManyToMany` porté par `Vehicule.equipements`. |
| `Reservation.contrat` | `@JsonIgnore` | Côté inverse du `@OneToOne` porté par `Contrat.reservation` : sans lui, contrat → réservation → contrat boucle. |

Les attributs propriétaires (`Employe.agence`, `Vehicule.agence`, `Reservation.client`,
`Reservation.vehicule`, `Maintenance.vehicule`, `Contrat.reservation`) ne reçoivent aucune
annotation : ils restent lus et écrits.

**`@JsonBackReference` ou `@JsonIgnore` ?** Les deux suppriment l'attribut de la réponse.
`@JsonBackReference` fonctionne en paire avec un `@JsonManagedReference` : à la lecture du JSON
parent, Jackson recâble automatiquement chaque enfant vers son parent. `@JsonIgnore` ignore
l'attribut dans les deux sens, sans recâblage : c'est le bon choix quand l'enfant est créé seul
et désigne lui-même son parent. Poser `@JsonBackReference` sur `Reservation.client` ferait ignorer
le client à la lecture du POST : la réservation serait créée sans client.

## C. Postman et Swagger

| Outil | Apport | Exemple dans l'atelier |
|---|---|---|
| Postman | Tester : requêtes rejouables rangées par dossier, variables d'environnement (`baseUrl`, `contratId`...), assertions automatiques (`pm.test`), exécution de toute la collection d'un coup. | Scénario de cascade : « Créer un contrat avec ses paiements » mémorise un `paiementId`, « Lister les paiements » vérifie qu'il est présent, puis après la suppression du contrat, `GET /api/paiements/{{paiementId}}` renvoie bien 404. La collection complète (84 requêtes, 118 assertions) passe sans échec. |
| Swagger UI | Documenter et explorer : description OpenAPI générée depuis le code, donc toujours synchronisée ; essai direct des endpoints ; schémas des entités. | `/api/swagger-ui.html` liste les neuf groupes (`@Tag`), chaque endpoint avec son résumé et ses codes 200/201/204/400/404 ; la section *Schemas* montre que `Contrat` expose `paiements` mais pas `Reservation.contrat` (ignoré). |

Collection et environnement exportés : [postman/AutoLoc.postman_collection.json](postman/AutoLoc.postman_collection.json),
[postman/local.postman_environment.json](postman/local.postman_environment.json).

### Captures Postman (environnement « local »)

CRUD complet sur Contrat (dossier « Contrats ») :

| Opération | Résultat | Capture |
|---|---|---|
| Lister | 200, tableau JSON | ![Lister les contrats](postman/screenshots/01-lister-contrats-tests.png) |
| Créer | 201 | ![Créer un contrat](postman/screenshots/02-creer-contrat-201.png) |
| Consulter | 200 | ![Consulter un contrat](postman/screenshots/03-consulter-contrat-200.png) |
| Consulter un contrat inexistant | 404 | ![Contrat 9999](postman/screenshots/04-consulter-contrat-inexistant-404.png) |
| Créer avec un montant négatif | 400 | ![Montant négatif](postman/screenshots/05-creer-contrat-montant-negatif-400.png) |
| Créer avec un identifiant | 400 | ![Identifiant fourni](postman/screenshots/06-creer-contrat-avec-identifiant-400.png) |
| Créer avec ses paiements | 201, deux paiements | ![Cascade PERSIST](postman/screenshots/07-creer-contrat-avec-paiements-201.png) |
| Modifier avec un montant négatif | 400 | ![PUT montant négatif](postman/screenshots/08-modifier-contrat-montant-negatif-400.png) |
| Modifier | 200, montant 480 → 520 | ![Modifier un contrat](postman/screenshots/09-modifier-contrat-200.png) |
| Modifier un contrat inexistant | 404 | ![PUT 9999](postman/screenshots/10-modifier-contrat-inexistant-404.png) |
| Supprimer | 204 | ![Supprimer un contrat](postman/screenshots/11-supprimer-contrat-204.png) |
| Consulter le contrat supprimé | 404 | ![Contrat supprimé](postman/screenshots/12-consulter-contrat-supprime-404.png) |

Lecture seule et cascade (dossier « Paiements ») :

| Opération | Résultat | Capture |
|---|---|---|
| Lister les paiements | 200, les deux paiements créés par cascade | ![Lister les paiements](postman/screenshots/13-lister-paiements-200.png) |
| Consulter un paiement | 200 | ![Consulter un paiement](postman/screenshots/14-consulter-paiement-200.png) |
| Consulter un paiement inexistant | 404 | ![Paiement 9999](postman/screenshots/15-consulter-paiement-inexistant-404.png) |
| `POST /api/paiements` | 405 | ![Paiement refusé](postman/screenshots/16-creer-paiement-refuse-405.png) |
| Supprimer le contrat avec ses paiements | 204 | ![Cascade REMOVE](postman/screenshots/17-supprimer-contrat-avec-paiements-204.png) |
| Consulter un paiement du contrat supprimé | 404 | ![Paiement supprimé](postman/screenshots/18-paiement-apres-suppression-contrat-404.png) |

Exécution de toute la collection (Runner) : 118 tests, 118 réussis, 0 échec.

![Run AutoLoc](postman/screenshots/19-run-collection.png)

Swagger UI (`/api/swagger-ui.html`) : neuf groupes, résumés et schémas.

![Swagger UI](postman/screenshots/20-swagger-ui.png)

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction apportée |
|---|---|---|
| `java:S4684` Persistent entities should not be used as arguments of `@RequestMapping` methods | Les méthodes `create` et `update` reçoivent directement une entité JPA en `@RequestBody` : le client peut écrire n'importe quel attribut mappé (identifiant, associations). | Volontaire et provisoire (consigne de l'atelier) : le contrôle de l'identifiant est fait dans le service (`create` refuse un id non `null`), la correction complète passe par les DTO du Chapitre 10. |
| _à relever dans IntelliJ_ | Deuxième anomalie à noter après « Analyze with SonarQube for IDE » sur `web.controller`. | _à compléter_ |

## E. Questions

1. **Quel composant reçoit toutes les requêtes ?** Le `DispatcherServlet`, point d'entrée unique.
   Il interroge le `HandlerMapping`, qui retrouve la méthode du contrôleur à partir du verbe HTTP
   et de l'URL (préfixe `@RequestMapping` de la classe + `@GetMapping`, `@PostMapping`... de la
   méthode), puis l'appelle et confie la valeur retournée à Jackson.

2. **Pourquoi `ContratController` dépend-il de `IContratService` ?** Le contrôleur reste mince :
   pas de logique métier (elle est dans le service) ni d'accès direct aux données (repository).
   Dépendre de l'interface plutôt que de `ContratServiceImpl` permet de changer d'implémentation
   sans toucher le contrôleur et laisse Spring placer son proxy transactionnel devant le service.

3. **201, 204, 404 ?** Un POST crée une ressource : 201 *Created*, avec la ressource créée (et son
   identifiant) dans le corps. Un DELETE réussi n'a rien à renvoyer : 204 *No Content*. Une
   ressource absente est une erreur du client (mauvais identifiant), pas une panne du serveur :
   `ResourceNotFoundException` est traduite en 404 par `GlobalExceptionHandler`, alors qu'une
   exception non gérée donnerait un 500.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ?** Un paiement n'existe pas sans son
   contrat : il est créé, modifié et supprimé par cascade depuis `Contrat`, et `IPaiementService`
   est en lecture seule. Un `POST /api/paiements` reçoit **405 Method Not Allowed** : la route
   existe, mais pas pour ce verbe.

5. **Risques de l'exposition directe des entités JPA ?** Couplage de l'API au schéma de la base
   (renommer une colonne casse les clients), champs internes visibles, écriture possible de
   n'importe quel attribut (mass assignment), boucles de sérialisation et proxys LAZY à gérer
   avec des annotations Jackson sur le modèle. `Contrat.paiements` utilise
   `@JsonManagedReference` parce que c'est une composition : les paiements arrivent dans le JSON
   du contrat et doivent être rattachés à leur parent. `Agence.vehicules` utilise `@JsonIgnore`
   parce qu'un véhicule se crée seul et désigne son agence par identifiant : la liste n'a besoin
   ni d'être lue ni d'être écrite.
