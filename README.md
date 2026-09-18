# Ebank - Architecture Microservices (Parties 1 & 2/3)

Cette livraison contient :
- **Consul** (Discovery Service)
- **customer-service** (gestion des clients) — expose aussi un **serveur MCP** (tools : consulter les clients)
- **ebank-service** (comptes/transactions, appelle customer-service via Feign) — expose aussi un **serveur MCP** (tools : comptes, transactions, dépôt, retrait)
- **gateway-service** (Spring Cloud Gateway, point d'entrée unique, route aussi vers le chatbot)
- **ebank-chatbot-service** (Agent IA — Spring AI + OpenAI + client MCP)

La suite (Telegram + Angular) arrive dans le prochain message.

## ⚠️ Clé API OpenAI — À FAIRE AVANT DE LANCER

Le chatbot a besoin de la clé fournie par le prof, définie en **variable d'environnement**
(jamais en dur dans le code, surtout si le repo GitHub est public).

**PowerShell (Windows), à faire avant de lancer `ebank-chatbot-service` :**
```powershell
$env:OPENAI_API_KEY="ta-cle-openai-ici"
```
Ou en permanent : Recherche Windows → "Variables d'environnement" → Nouvelle variable
utilisateur → nom `OPENAI_API_KEY`, valeur = la clé.

**Dans IntelliJ** (si tu lances via l'IDE) : Run → Edit Configurations → sélectionne
`EbankChatbotServiceApplication` → Environment Variables → ajoute `OPENAI_API_KEY=ta-cle-ici`.

## Prérequis
- Java 21
- Maven 3.9+
- Docker (pour Consul)

## Ordre de démarrage

### 1. Lancer Consul
```bash
docker compose up -d
```
Vérifie sur http://localhost:8500 que l'interface Consul s'affiche.

### 2. Lancer customer-service
```bash
cd customer-service
mvn spring-boot:run
```
Attends de voir dans les logs `Started CustomerServiceApplication`.
Vérifie : http://localhost:8081/customers (doit renvoyer 3 clients de test).

### 3. Lancer ebank-service
```bash
cd ebank-service
mvn spring-boot:run
```
Vérifie : http://localhost:8082/accounts (doit renvoyer 3 comptes de test).

Teste l'appel Feign (le plus important pour la démo) :
http://localhost:8082/accounts/1/details
→ doit renvoyer le compte ET les infos du client associé (preuve que Feign + Consul fonctionnent).

### 4. Lancer gateway-service
```bash
cd gateway-service
mvn spring-boot:run
```
Vérifie que tout passe bien par la gateway :
- http://localhost:8888/api/customers
- http://localhost:8888/api/accounts
- http://localhost:8888/api/accounts/1/details

### 5. Vérifier dans Consul
Sur http://localhost:8500 → onglet "Services", tu dois voir apparaître :
`customer-service`, `ebank-service`, `gateway-service` tous en vert (healthy).

### 6. Lancer ebank-chatbot-service (après avoir défini OPENAI_API_KEY, voir plus haut)
```bash
cd ebank-chatbot-service
mvn spring-boot:run
```

Teste l'agent avec Postman ou curl :
```bash
curl -X POST http://localhost:8083/chat/ask \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"Quel est le solde du compte 1 ?\"}"
```
L'agent doit appeler automatiquement le tool `getAccountById` (côté ebank-service, via MCP)
et répondre avec le solde réel. Essaie aussi :
- `"Quels sont les comptes du client 1 ?"` → appelle `getAccountsByCustomerId`
- `"Fais un dépôt de 500 sur le compte 1"` → appelle `deposit`
- `"Qui est le client 2 ?"` → appelle `getCustomerById` (côté customer-service)

Ou via la gateway : `http://localhost:8888/api/chat/ask`

## Points clés à expliquer au prof pendant la démo
- **Discovery** : les services s'enregistrent automatiquement auprès de Consul au démarrage (`@EnableDiscoveryClient`), sans IP/port codés en dur.
- **Feign + Consul** : `ebank-service` appelle `customer-service` par son *nom logique* (`customer-service`), Consul résout l'adresse réelle → montre `/accounts/1/details`.
- **Gateway** : un seul point d'entrée (port 8888) route vers tous les microservices via `lb://` (load-balanced), avec réécriture d'URL (`/api/customers/**` → `/customers/**`).
- **MCP (Model Context Protocol)** : `customer-service` et `ebank-service` exposent chacun un **serveur MCP** (transport *Streamable HTTP*, `spring.ai.mcp.server.protocol=STREAMABLE`) qui rend leurs méthodes `@Tool` appelables par un agent IA. `ebank-chatbot-service` est un **client MCP** qui se connecte aux deux et donne ces tools à un `ChatClient` (Spring AI + OpenAI).
- **Agent IA** : quand l'utilisateur pose une question en langage naturel, le LLM (GPT-4o-mini) décide lui-même quel(s) tool(s) appeler (function calling), récupère les données réelles, puis formule la réponse — l'agent ne "devine" jamais les chiffres.

## Simplification assumée (à mentionner si le prof demande)
Les connexions MCP du chatbot vers `customer-service`/`ebank-service` utilisent des URLs
statiques (`localhost:8081`, `localhost:8082`) plutôt qu'une résolution dynamique via Consul.
Pour une version 100% "load-balancée", il faudrait un `WebClient.Builder` annoté `@LoadBalanced`
côté client MCP — simplification raisonnable vu le délai, à mentionner comme piste d'amélioration.

## Prochaine étape
Bot Telegram (branché sur `/chat/ask`) + dashboard Angular.
