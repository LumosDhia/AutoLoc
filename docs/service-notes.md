# Atelier 4 : couche Service, IoC et injection de dépendances

## A. Modes d'injection comparés

Expérience de l'étape 2 sur `ContratServiceImpl` : injection par constructeur
(`@RequiredArgsConstructor` + champ `final`) puis, temporairement, injection par attribut
(`@Autowired` sur le champ). Dans les deux cas l'application démarre.

| Critère | Constructeur | Attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible ? | Oui | Non : Spring écrit le champ après la construction, par réflexion. |
| Dépendance visible dans la signature de la classe ? | Oui : elle apparaît dans le constructeur. | Non : elle est cachée dans le corps de la classe. |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : impossible d'appeler le constructeur sans fournir le repository. | Non : `new ContratServiceImpl()` laisse `contratRepository` à `null`, d'où un `NullPointerException` au premier appel. |
| Signalé par SonarQube for IDE ? | Non | Oui : règle `java:S6813` (« Field dependency injection should be avoided »). |

## B. Services

Tous les services sont des singletons sans état : leur seul attribut est le repository, `final`,
injecté par le constructeur généré par Lombok.

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | CRUD des agences ; refuse un nom, une ville ou une adresse vide. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | CRUD des employés ; refuse un nom, un prénom ou un rôle absent. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | CRUD des véhicules ; refuse un tarif journalier négatif. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | CRUD des équipements ; refuse un libellé vide (le modèle n'a pas de quantité). |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | CRUD des clients ; refuse un nom, un prénom ou un e-mail vide. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | CRUD des réservations ; refuse une date de fin antérieure à la date de début. |
| `ContratServiceImpl` | `IContratRepository` | Constructeur | CRUD des contrats ; refuse un montant total négatif. La suppression supprime les paiements par cascade (`CascadeType.ALL`, `orphanRemoval`). |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Lecture seule (`findById`, `findAll`) : les paiements suivent le cycle de vie de leur contrat. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | CRUD des maintenances ; refuse une date de fin antérieure à la date de début (le modèle n'a pas de coût). |

Règles communes : `create` refuse un identifiant non `null`, `findById` et `deleteById` lèvent
`ResourceNotFoundException` si l'identifiant n'existe pas, `update` charge l'entité existante,
recopie les attributs modifiables (sans l'identifiant ni les collections) puis appelle `save`.
Une règle de gestion violée lève `IllegalArgumentException`.

## C. Messages d'erreur

**Message 1** : `NoSuchBeanDefinitionException` (enveloppée dans une
`UnsatisfiedDependencyException`). Le bean `contratController` demande un `IContratService` et
le conteneur n'en trouve aucun. Causes probables côté `ContratServiceImpl` :

1. l'annotation `@Service` a été oubliée : la classe n'est pas détectée par le scan ;
2. la classe est placée hors du package racine `tn.esprit.autoloc` (ou de ses sous-packages) :
   le scan de `@SpringBootApplication` ne la voit pas.

Correction : remettre `@Service` et placer la classe dans `tn.esprit.autoloc.service`.

**Message 2** : `NoUniqueBeanDefinitionException`. Le constructeur de `NotificationController`
attend un seul bean du type demandé, mais deux existent : `emailNotificateur` et
`smsNotificateur`. Deux corrections :

1. `@Primary` sur la **classe** à utiliser par défaut (par exemple `EmailNotificateur`) : Spring
   la choisit quand rien d'autre n'est précisé ;
2. `@Qualifier("smsNotificateur")` sur le **paramètre du constructeur** de
   `NotificationController` : choix explicite du bean par son nom. Avec Lombok, il faut alors
   écrire le constructeur à la main, ou déclarer `lombok.copyableAnnotations` pour que
   `@Qualifier` soit recopié sur le paramètre généré.

**Message 3** : dépendance circulaire (`BeanCurrentlyInCreationException`) entre
`clientServiceImpl` et `reservationServiceImpl`. Avec l'injection par constructeur, pour créer
`ClientServiceImpl` il faut déjà une instance de `ReservationServiceImpl`, qui elle-même exige
une instance de `ClientServiceImpl` : aucun des deux ne peut être construit en premier, et
Spring ne peut pas fournir un objet à moitié construit comme il le ferait avec un setter.
Piste : supprimer le cycle par la conception. Chaque service dépend des **repositories** dont il
a besoin (par exemple `ReservationServiceImpl` utilise `IClientRepository` plutôt que
`IClientService`), ou la responsabilité commune est extraite dans un troisième service dont les
deux dépendent.

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction apportée |
|---|---|---|
| `java:S6813` Field dependency injection should be avoided | Étape 2 : `@Autowired` sur le champ `contratRepository` de `ContratServiceImpl`. Le champ ne peut pas être `final` et la dépendance est cachée. | Retour à `@RequiredArgsConstructor` + champ `private final IContratRepository`. |
| `java:S112` Generic exceptions should never be thrown | Étape 4 : `new RuntimeException(...)` dans `ContratServiceImpl.findById`. Une exception générique ne dit rien de l'erreur et oblige l'appelant à tout attraper. | Utilisation de l'exception métier `ResourceNotFoundException`. |
| _à relever dans IntelliJ_ | Troisième anomalie à noter après « Analyze with SonarQube for IDE » sur les packages `service` et `exception`. | _à compléter_ |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?** Il n'apparaît nulle part dans
   notre code. C'est le conteneur Spring (`ApplicationContext`, créé par
   `SpringApplication.run`) qui, après avoir détecté `@Service` au scan, appelle le constructeur
   par réflexion en lui passant le proxy de `IContratRepository`.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour dépendre d'une abstraction : on peut changer d'implémentation (ou en fournir une de test)
   sans toucher le contrôleur. Le contrôleur ne voit que le contrat (les méthodes), pas les
   détails. C'est aussi ce qui permet à Spring de placer un proxy (transactions) devant
   l'implémentation.

3. **Pourquoi un service singleton doit-il rester sans état ?** Une seule instance est partagée
   par tous les threads (toutes les requêtes HTTP en parallèle). Exemple de bug : un attribut
   `private Contrat contratCourant;` affecté dans `update` ; si deux requêtes arrivent en même
   temps, la seconde écrase `contratCourant` avant que la première ait fini, et la première
   enregistre les données de l'autre client.

4. **Pourquoi `update` charge-t-il l'entité existante avant `save` ?** L'objet reçu (venant plus
   tard du JSON) est incomplet : passé tel quel à `save`, il déclencherait un `merge` qui écrase
   toutes les colonnes, y compris celles non envoyées (mises à `null`), et, avec
   `orphanRemoval = true`, une liste `paiements` vide supprimerait tous les paiements du
   contrat. En chargeant l'existant, on ne recopie que les attributs modifiables, on garde
   l'identifiant et les collections, et on vérifie au passage que l'entité existe.

5. **`@Component` / `@Bean`, `@Primary` / `@Qualifier`, `IPaiementService` :**
   - `@Component` se pose sur une **classe** que l'on écrit : le scan la détecte et Spring
     l'instancie. `@Bean` se pose sur une **méthode** d'une classe `@Configuration` : on crée
     l'objet soi-même et on le renvoie, utile pour une classe qu'on ne peut pas annoter
     (bibliothèque, JDK) ou une construction particulière.
   - `@Primary` se pose sur la **classe** (ou la méthode `@Bean`) et désigne le candidat par
     défaut pour tous les points d'injection. `@Qualifier("nom")` se pose au **point
     d'injection** et choisit explicitement un bean pour ce client précis ; il l'emporte sur
     `@Primary`.
   - Un paiement n'existe pas sans son contrat (`@ManyToOne(optional = false)`, cascade depuis
     `Contrat`). Créer, modifier ou supprimer un paiement isolément casserait la cohérence du
     contrat (montant, paiements orphelins) : ces opérations passent par le `Contrat`, le service
     des paiements n'offre que la lecture.
