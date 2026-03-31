# Projet TPP - Application de gestion des TPP

## Auteur

| Nom | Numéro | Rôles |
| --- | :----: | --- |
| DOMINGUES Kévin | 11607884 | Scrum Master, DevOps |
| DORRY Nina | 12412522 | Product Manager, Dev Front |
| ELKHEDIM Ilyes | 12102216 | Chef de projet, Dev Back / Architecte logiciel |
| FERREIRA Remi | 12107991 | Responsable qualité, Dev Back |
| PAULUS Noëllie | 12100318 | Responsable qualité, Architecte Données |
| PEREZ Stella | 12103226 | Designer UI/UX, Dev Front |

## Description

L'objectif de ce projet est de développer une application web pour suivre les créneaux « Travaux Personnels et Projet » (TPP) et garantir le respect des obligations de présence des étudiants en alternance.

- Contexte
  - Les étudiants en alternance doivent totaliser 35 heures hebdomadaires.
  - Pendant les cycles de 15 jours de formation, les TPP complètent le présentiel nécessaire.

- Fonctions principales attendues
  - Suivi des présences via feuilles signées par les enseignants et le responsable de formation.
  - Possibilité de substitution : membre de la cellule Formation Continue ou de la scolarité peut signer si le responsable est absent.
  - Contrôles inopinés (surtout en début de séance) pour vérifier la ponctualité et la présence.
  - Soumission d'un compte rendu en fin de séance par les étudiants pour valoriser le temps passé en TPP.
  - Agrégation des contrôles et comptes rendus pour calculer une note pour l'UE « Connaissances Métier ».

- Contraintes / points à respecter
  - Preuves de présence hebdomadaires (feuilles signées).
  - Gestion des rôles (étudiants, enseignants, responsable formation, cellule FC, scolarité).
  - Traçabilité des contrôles et des comptes rendus pour le calcul de la note finale.


## Fonctionnalités


## Documentation API

L'application dispose d'une documentation API complète générée avec Swagger/OpenAPI.

### Accès à Swagger UI

Une fois l'application backend démarrée, accédez à l'interface Swagger à :
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **Spécification OpenAPI JSON** : http://localhost:8080/api-docs

### Endpoints disponibles

- **Students** : Gestion des étudiants et présences
- **Courses** : Gestion des promotions et des groupes d'étudiants
- **Supervisors** : Gestion des encadrants
- **TimeSlots** : Gestion des créneaux horaires TPP


## VM :
les VM qui ont été attribuées (clé ssh identique à ggmd) 
- 192.168.74.140 -> dev
  - :80 pour le front
  - :8080 pour le back
  - :5432 pour la BDD
  - :443 pour l'interface de la BDD
- 192.168.74.146 -> prod

## Test and Deploy

- Pour lancer pour la première foi un docker en local et pouvoir tester
  - docker compose -f docker-compose-local.yml up -d --build
- Pour accéder au SonarQube : https://sonar.info.univ-lyon1.fr/dashboard?id=Projet-TPP  

# tpp-app
