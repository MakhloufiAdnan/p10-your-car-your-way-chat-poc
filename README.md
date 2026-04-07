# Your Car Your Way — PoC Tchat

## Présentation

Ce dépôt contient une **preuve de concept (PoC)** réalisée dans le cadre du projet *Your Car Your Way*.

L’objectif de cette PoC est de **valider la faisabilité technique de l’architecture cible** au travers d’une fonctionnalité volontairement restreinte : **le tchat**.

Cette approche permet de démontrer la viabilité du socle technique retenu sans développer l’application complète.

---

## Objectif de la PoC

La PoC vise à valider les points suivants :

- la séparation claire entre **frontend** et **backend** ;
- la persistance des données dans une **base relationnelle PostgreSQL** ;
- une logique de **contrôle d’accès minimale** ;
- une communication **quasi temps réel** ;
- une exécution locale standardisée via **Docker**.

---

## Périmètre fonctionnel

### Fonctionnalités incluses

La PoC couvre uniquement la fonctionnalité de tchat et permet :

- la connexion avec des comptes de démonstration ;
- l’affichage des conversations autorisées ;
- la consultation de l’historique des messages ;
- l’envoi d’un message ;
- la réception quasi temps réel d’un message ;
- la persistance des conversations et des messages ;
- le contrôle d’accès aux conversations.

### Hors périmètre

Les éléments suivants ne sont pas couverts par cette PoC :

- l’inscription ;
- la réinitialisation du mot de passe ;
- les pièces jointes ;
- les notifications push ;
- le chatbot ;
- la présence en ligne ;
- l’interface finale de production ;
- l’ensemble du périmètre métier complet de la future application.

---

## Architecture technique retenue

La PoC s’appuie sur la stack suivante :

- **Frontend** : Angular
- **Backend** : Java Spring Boot
- **Base de données** : PostgreSQL
- **Communication temps réel** : WebSocket
- **Conteneurisation** : Docker / Docker Compose

---

## Structure du dépôt

Le dépôt est organisé comme suit :

- `backend/` : application Spring Boot ;
- `frontend/` : application Angular ;
- `db/` : scripts liés à la base de données ;
- `docs/` : documentation complémentaire ;
- `docker-compose.yml` : orchestration locale des services ;
- `.env.example` : exemple de variables d’environnement.

---

## Prérequis

Pour travailler sur le projet, les outils suivants doivent être installés :

- **Java 21**
- **Maven**
- **Node.js**
- **npm**
- **Angular CLI**
- **Docker**
- **Docker Compose**

---
