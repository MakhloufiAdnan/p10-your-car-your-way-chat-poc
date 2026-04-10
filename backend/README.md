# Backend — Spring Boot (PoC chat)

Partie API et temps réel du projet full stack.  
Pour le contexte global, les prérequis et l’ordre de démarrage, lire d’abord le `README.md` à la racine du dépôt.

## À quoi sert cette partie ?

Le backend gère :

- l’authentification ;
- l’accès aux conversations ;
- l’historique des messages ;
- l’envoi des messages ;
- la diffusion des messages en temps réel ;
- l’initialisation des données de démonstration.

## Environnement local

Depuis le dossier `backend/` :

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

Le backend démarre par défaut sur :

```txt
http://localhost:8080
```

## Technologies

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- WebSocket / STOMP
- Maven Wrapper

## Build et tests

### Build

#### Windows PowerShell

```powershell
.\mvnw.cmd clean package
```

#### macOS / Linux

```bash
./mvnw clean package
```

## Base de données

Le backend utilise PostgreSQL.

Avec Docker Compose, l’URL attendue est :

```txt
jdbc:postgresql://db:5432/chatpoc
```

## Comptes de démonstration

Au démarrage, le backend crée automatiquement :

- `client1` / `Client123#`
- `agent1` / `Agent123#`
- `client2` / `Client234#`
- `agent2` / `Agent234#`

Il crée aussi des conversations de démonstration pour tester immédiatement le chat.

## Endpoints utiles

- `POST /api/auth/login`
- `GET /api/conversations`
- `GET /api/conversations/{conversationId}/messages`
- `POST /api/conversations/{conversationId}/messages`

## WebSocket

Endpoint exposé :

```txt
/ws-chat
```

Le backend publie les nouveaux messages pour les utilisateurs connectés afin de mettre à jour l’interface sans rechargement manuel.

## Structure utile

```txt
backend/
├── src/main/java/com/openclassroom/chatpoc/
│   ├── auth/           # login, sécurité, utilisateur authentifié
│   ├── config/         # sécurité et WebSocket
│   ├── conversation/   # conversations et participants
│   ├── message/        # messages et publication temps réel
│   └── user/           # utilisateurs et seed de démo
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```
