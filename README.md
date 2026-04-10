# Your Car Your Way — PoC chat full stack

Application Angular (frontend) + Spring Boot (backend API + WebSocket).  
Ce dépôt regroupe les deux parties dans un seul projet pour faciliter le développement, la démonstration et la livraison.

## À quoi sert ce projet ?

Cette application est une **preuve de concept (PoC)** réalisée dans le cadre du projet **Your Car Your Way**.

Elle permet de valider :

- une architecture séparée entre **frontend** et **backend** ;
- une base de données **PostgreSQL** ;
- une authentification simple par **session HTTP** ;
- l’envoi et la réception de messages en **temps réel** avec **WebSocket / STOMP** ;
- un lancement local avec ou sans **Docker Compose**.

Le périmètre fonctionnel est volontairement limité au **chat**.

## Structure du dépôt

- `frontend/` : interface utilisateur Angular
- `backend/` : API Spring Boot + WebSocket
- `docs/` : fichiers tests Postman
- `docker-compose.yml` : orchestration locale des services
- `.env.example` : exemple de variables d’environnement
- `README.md` : ce fichier

## Prérequis

Avant de lancer le projet, vérifier que les outils suivants sont installés.

### Pour un lancement local sans Docker

- **Java 21**
- **Node.js 22+**
- **npm**
- **Maven** : non obligatoire si vous utilisez le wrapper Maven fourni dans `backend/`

Commandes utiles :

```bash
java -version
node -v
npm -v
```

### Pour un lancement avec Docker

- **Docker Desktop** (ou équivalent)
- **Docker Compose v2**

Commandes utiles :

```bash
docker --version
docker compose version
```

## Comptes de démonstration

Le backend crée automatiquement des comptes de démonstration au démarrage.

Comptes disponibles :

- `client1` / `Client123#`
- `agent1` / `Agent123#`
- `client2` / `Client234#`
- `agent2` / `Agent234#`

## Démarrage avec Docker Compose

À la racine du dépôt :

```bash
docker compose up --build
```

Services disponibles :

- interface : http://localhost:4200
- API : http://localhost:8080
- WebSocket : ws://localhost:8080/ws-chat
- base de données : localhost:5432

Le frontend est servi par Nginx dans le conteneur `frontend`.  
Le backend Spring Boot tourne dans le conteneur `backend`.  
PostgreSQL tourne dans le conteneur `db`.

### Arrêt

```bash
docker compose down
```

Pour supprimer aussi le volume de base de données :

```bash
docker compose down -v
```

### Reconstruction après changement

```bash
docker compose up --build
```

## Démarrage rapide sans Docker

### 1. Cloner le dépôt

```bash
git clone https://github.com/MakhloufiAdnan/p10-your-car-your-way-chat-poc.git
cd p10-your-car-your-way-chat-poc
```

### 2. Lancer le back-end

Depuis la racine du projet :

#### Windows PowerShell

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

#### macOS / Linux

```bash
cd backend
./mvnw spring-boot:run
```

Par défaut, le backend écoute sur :

```txt
http://localhost:8080
```

Laissez ce terminal ouvert tant que l’API tourne.

### 3. Lancer le front-end

Ouvrir un second terminal, à la racine du projet :

#### Windows / macOS / Linux

```bash
cd frontend
npm install
npm run start
```

Le frontend est alors disponible sur :

```txt
http://localhost:4200
```

## Vérifier la démo

Pour tester rapidement le chat :

1. ouvrir deux navigateurs ou une fenêtre privée ;
2. se connecter avec deux comptes différents, par exemple `client1` et `agent1` ;
3. ouvrir la même conversation ;
4. envoyer un message depuis une session ;
5. vérifier qu’il apparaît dans l’autre sans rechargement manuel.

## Structure utile du dépôt

```txt
.
├── backend/              # Spring Boot + WebSocket + Dockerfile
├── frontend/             # Angular + Dockerfile + nginx.conf
├── docs/                 # Documents projet
├── docker-compose.yml    # frontend + backend + db
├── .env.example          # Exemple de variables d'environnement
└── README.md             # Ce fichier
```

Pour le détail du frontend, voir `frontend/README.md`.  
Pour le détail du backend, voir `backend/README.md`.

## Dépannage

### Port déjà utilisé

Un autre programme utilise déjà `4200`, `8080` ou `5432`.

Vérifier les ports utilisés ou arrêter l’application qui bloque.

### Le front ne parle pas au back

Vérifier que :

- le backend est bien démarré ;
- l’API répond sur `http://localhost:8080` ;
- le WebSocket est bien exposé sur `ws://localhost:8080/ws-chat`.

### Le temps réel ne fonctionne pas

Vérifier :

- que le backend est bien lancé sans erreur ;
- que le WebSocket est joignable ;
- que les deux utilisateurs sont connectés à la même conversation ;
- que Docker n’a pas échoué au démarrage du service `backend`.

### `npm install` échoue

Vérifier la version de Node.js, puis supprimer `frontend/node_modules` et relancer :

```bash
npm install
```

### Le backend ne démarre pas

Vérifier :

- Java 21 ;
- les variables d’environnement ;
- la disponibilité de PostgreSQL ;
- les logs Spring Boot dans le terminal ou via Docker Compose.
