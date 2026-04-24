# GestCode API REST (Spring Boot)

Aquest és el projecte de backend per a l'aplicació GestCode, implementat en **Java 17** i **Spring Boot 3**. Proporciona una API REST completa per gestionar una biblioteca (usuaris, llibres, préstecs, reserves, etc.) utilitzant un sistema d'autenticació basat en JWT (*JSON Web Tokens*).

## 📖 Documentació de l'API (Swagger)

Aquest projecte integra **SpringDoc OpenAPI** (Swagger UI) per documentar i provar de manera interactiva tots els endpoints disponibles de l'API REST.

Un cop l'aplicació està en execució, pots accedir a la documentació a través de l'adreça següent:

- **Entorn Local (Desenvolupament):**
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **Entorn de Producció (Servidor Isard):**
  [https://401c000f-26f1-447e-b499.e9734fe78f0a.bastion.elmeuescriptori.cat/swagger-ui/index.html](https://401c000f-26f1-447e-b499.e9734fe78f0a.bastion.elmeuescriptori.cat/swagger-ui/index.html)

A l'entorn de producció, l'accés s'ha de fer mitjançant **HTTPS** gràcies al proxy invers que gestiona els certificats SSL.

## 🚀 Desplegament en Producció (HTTPS)

Per preparar el servidor d'Ubuntu i que l'API es pugui exposar de forma segura a Internet mitjançant HTTPS (port 443), s'ha creat un script automàtic d'instal·lació i configuració.

Aquest script:
1. Obre els ports 80 i 443 al tallafocs (`ufw`).
2. Instal·la **Nginx** i **Certbot**.
3. Configura Nginx com a Proxy Invers per redirigir les peticions cap al port intern `8080` de Spring Boot.
4. Genera automàticament els certificats SSL de **Let's Encrypt**.

### Com executar l'script al Servidor (Isard)

1. Transfereix l'script `scripts/setup-https.sh` i el `.jar` compilat al teu servidor d'Isard.
2. Dona permisos d'execució a l'script:
   ```bash
   chmod +x setup-https.sh
   ```
3. Executa l'script amb permisos de superusuari (root):
   ```bash
   sudo ./setup-https.sh
   ```

Un cop Nginx estigui configurat amb èxit, cal que iniciïs la teva aplicació Spring Boot (el `.jar`)
