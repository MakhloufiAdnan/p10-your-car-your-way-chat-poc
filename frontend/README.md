# Frontend — Angular (PoC chat)

Partie interface du projet full stack.  
Pour le contexte global, les prérequis et l’ordre de démarrage, lire d’abord le `README.md` à la racine du dépôt.

## À quoi sert cette partie ?

Le frontend permet :

- la connexion avec des comptes de démonstration ;
- l’affichage des conversations ;
- la consultation de l’historique des messages ;
- l’envoi d’un message ;
- la réception des nouveaux messages en temps réel.

## Environnement local

Depuis le dossier `frontend/` :

```bash
npm install
```

Lancer le serveur de développement :

```bash
npm run start
```

L’application est servie sur :

```txt
http://localhost:4200
```

Le backend Spring Boot doit être lancé en parallèle si l’application utilise l’API ou le WebSocket.

## Technologies

- Angular
- TypeScript
- RxJS
- STOMP JS
- Standalone components

## Dépendances du frontend

Le frontend dépend du backend pour :

- l’API REST : `http://localhost:8080/api`
- le WebSocket : `ws://localhost:8080/ws-chat`

## Commandes utiles

### Démarrage du serveur de développement

```bash
npm run start
```

### Build de production

```bash
npm run build
```

### Tests

```bash
npm test
```

## Structure utile

```txt
frontend/
├── src/
│   ├── app/
│   │   ├── core/        # auth, interceptor HTTP, services transverses
│   │   └── features/    # auth et chat
│   └── environments/    # configuration frontend
├── angular.json
├── package.json
└── README.md
```
