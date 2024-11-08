# Deploy dit næste projekt
Det er nu lykkedes at deploye et projekt til Digital Ocean, og nu vil du gerne deploye endnu et projekt.

## Goal
At få cupcake projektet op på vores portfolie side, så det kører i skyen.

## Prerequisites
* En Virtuel Maskine (Droplet) hos Digital Ocean
* Et domæne der peger på IP adressen for din VM
* En firewall der tillader indgående trafik på port 80 (HTTP) og 443 (HTTPS)
* En Postgres database der kører i en Docker container på din VM
* En Caddy server der kører i en Docker container på din VM
* Et Javalin projekt der virker, med en [fat jar](https://dat2cph.github.io/content/linux/deployment/fat-jar)
* En SSH forbindelse til serveren

## Fremgangsmåde
1. Gør subdomænet klar (opret DNS)
2. Beslut hvilken port dit Javalin projekt skal køre på. Det kan give problemer at køre på samme port som et andet projekt.
3. Opret databasen (på Droplet Postgres serveren) og opret dine tabeller
4. Opret en mappe til projektet og overfør din fat jar
5. Lav Dockerfile til Javalin projektet
6. Ret i Caddyfile (tilføj det nye subdomain)
7. Ret i docker-compose.yml (tilføj det nye Javalin projekt)
8. Ret i dine statiske HTML filer (portfolie siden)
9. `sudo docker compose up -d` og bed til at det virker i første forsøg
10. Fejlsøgning

### 1. Gør subdomænet klar (opret DNS)
Caddy lægger automatisk et SSL certifikat på serveren, men det kan den kun hvis (sub)domænet er oprettet i DNS. Derfor må dette være trin 1. Du gør det inde i Digital Oceans kontrolpanel for DNS, eller hos den DNS udbyder som du nu måtte vælge at bruge. (Dandomain, Cloudflare eller en helt fjerde.)

### 2. Beslut hvilken port dit Javalin projekt skal køre på.
Jeg har oplevet problemer med to Javalin projekter som begge brugte port 7070. En mulig løsning er bare at køre på forskellige porte, så har du allerede et projekt der kører på 7070 på Dropletten, så konfigurer dit nye projekt til at bruge port 7071.

Det er typisk omkring linje 30 i Main at du sætter porten.
```
Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7071);
```

### 3. Opret databasen (på Droplet Postgres serveren) og opret dine tabeller
Uanset om du bruger pgAdmin eller en console inde fra IntelliJ, så skal databasen og tabellerne oprettes, måske endda med noget data i dem.
```
CREATE DATABASE dbnavn;
```
### 4. Opret en mappe til projektet og overfør din fat jar
Nu er det på tide at ssh ind i serveren og lave konfigurationsfiler og andet sjov.
```
ssh user@serverip
```
Inde i deployment mappen skal du oprette en mappe til det nye projekt, det er smart at navngive mappen det samme som projektet.
```
cd deployment
mkdir <dit projekt navn>
```
Tilbage på din egen maskine skal du nu overføre din fede jar fil til serveren. Husk at du laver den fede jar fil inde i IntelliJ ved at åbne maven menuen, finde Lifecycle og trykke package. Filen ender i target folderen. Så du åbner en terminal i target folderen og overfører filen via scp eller rsync
```
scp app.jar user@serverip:~/deployment/<dit projekt navn>/
```
eller
```
rsync -av app.jar user@serverip:~/deployment/<dit projekt navn>/
```
(`rsync` opretter mappen for dig, hvis den ikke eksisterer, men `scp` opretter ikke mappen for dig.)

### 5. Lav Dockerfile til Javalin projektet
```
cd ~/deployment/<dit projekt navn>
nano Dockerfile
```
Vi skal have docker til at lave en container som indeholder og kører dit projekt. Det er vigtigt at denne container kører den version af java, som dit projekt forventer, eller i det mindste som dit projekt er kompatibelt med. Dette kan vi indstille i den såkaldte `Dockerfile`. Skabelonen er som følger:
```
FROM amazoncorretto:<java version>-alpine
COPY ./app.jar /app.jar
EXPOSE <port>
CMD ["java", "-jar", "/app.jar"]
```
Fra Dockers repository henter den et image som hedder amazoncorretto:22-alpine (eller noget i den stil), hvilket betyder at det er Amazon der har sammensat det image i et projekt de kalder Corretto, og den indeholder et java sdk i version 22 (eller hvilket nummer du nu har skrevet). Brug en version som matcher med det du har skrevet i din `pom.xml` og brugt da du udviklede i IntelliJ.

Den kopierer din app.jar fil ind i det image, laver regler i firewallen som åbner for port <port>, f.eks. port 7070, og så kører den kommandoen `java -jar /app.jar` og når/hvis programmet stopper, så kører den kommandoen igen, og igen, og igen.

(Dette image indeholder, ud over din jar fil, faktisk en hel linux installation. Så vi laver en virtuel maskine inde i en virtuel maskine. Efter vi har kørt `docker compose up` kommandoen bliver der altså skabt et image med en kopi af jar filen. Det er derfor ikke nok bare at slette jar filen fra ~/deployment/&lt;projekt>/ folderen, hvis vi skal opdatere vores jar fil.)

### 6. Ret i Caddyfile (tilføj det nye subdomain)
(Hvis du ikke allerede har en Caddyfile, så er der en komplet Caddyfile i bunden af guiden.)
```
cd ~/deployment
nano Caddyfile
```
Vi bruger Caddy som en reverse proxy, så brugeren ikke skal vide noget om nogle port-numre, men bare skrive adressen i adresselinjen (eller trykke på et link). Du skal nu tilføje informationerne om det subdomæne dit projekt skal hostes på til din `Caddyfile`. Du skal tilføje
```
<subdomæne>.<domæne.dk> {
  reverse_proxy <container_name>:<port>
}
```
### 7. Ret i docker-compose.yml (tilføj det nye Javalin projekt)
```
cd ~/deployment
nano docker-compose.yml
```
Vi skal nu fortælle Docker at den skal lave det image, som vi har konfigureret i step 5. `docker-compose.yml` filen tager indents meget alvorligt, så du skal sørge for at have det rigtige antal mellemrum med. Formatet er:
```
  <projekt navn>:
    build:
      context: ./<dit projekt navn (folderen)>
      dockerfile: Dockerfile
    container_name: <projekt navn>
    environment:
      - DEPLOYED=PROD
      - JDBC_USER=<projektets db user>
      - JDBC_PASSWORD=<projektets db kodeord>
      - JDBC_CONNECTION_STRING=jdbc:postgresql://db:5432/%s?currentSchema=public
      - JDBC_DB=<dbnavn>
    ports:
      - "<port>:<port>"
    restart: unless-stopped
```
Man kan gøre noget her, så man slipper for at rette i den port som projektet bruger. Men jeg kunne ikke få det til at virke, og det var hurtigere bare at ændre porten, end at fejlsøge mere. (Jeg tror den venstre skal matche porten i Caddyfile, og den højre skal matche porten i Dockerfile, eller også er det omvendt.)

Vi har ind til videre kun arbejdet med en bruger i databasen, nemlig `postgres`, men i princippet kunne den bruger hedde hvad som helst, og der kan være flere brugere i databasen (mindst en til hvert projekt). Har man flere brugere, så er det også rimeligt at hver bruger har sit eget kodeord.

### 8. Ret i dine statiske HTML filer (portfolie siden)
Dette trin er ikke strengt nødvendigt, men bare et nice-to-have.
```
cd ~/deployment/site
nano index.html
```
Den bid der skal tilføjes er noget i denne stil
```
<div class="card">
  <img class="card-image" alt="<dit projekt navn>" src="images/<dit projekt navn>.png" />
  <div class="card-copy"><dit projekt navn></div>
  <a class="card-link" href="https://<subdomain>.<domæne>" /><beskrivelse></a>
</div>
```
Du kan bruge `scp` eller `rsync -av` fra din lokale maskine til at overføre billedet, eller du kan hente det fra internettet direkte ned på serveren med `curl -O <url>`. Billedet skal ende i `~/deployment/site/images/`.

### 9. `sudo docker compose up -d` og bed til at det virker i første forsøg
I teorien så burde alt bare spille nu. Vi er klar til at bede Docker om at lave det nye image og (gen-)starte alle containerne.
```
cd ~/deployment
sudo docker compose up -d
```

### 10. Fejlsøgning
Se om dine Docker containere kører
```
sudo docker ps -a
```
Læg især mærke til hvor længe siden det er de er blevet created, og hvor længe de har været running. Hvis din container genstarter hele tiden, så er det tegn på at der bliver kastet en Runtime Exception.

Se i logfilerne om der er nogle brugbare fejlmeddelelser
```
sudo docker logs <container_name>
```
Hvis du får SSL fejl er det typisk noget med DNS og Caddy som ikke virker. Så kig i Caddy loggen
```
sudo docker logs caddy
```
og tjek din Caddyfile `cat ~/deployment/Caddyfile` og din DNS opsætning.

HTTP 502 Error handler typisk om at Caddy ikke kan snakke med containeren der indeholder dit projekt. Det kan være nogle porte (i alle tre filer), eller container navnet i Caddyfile der laver bøvl.

#### Genstart dine containere efter du har lavet ændringer
Luk alle containerne ned
```
sudo docker compose down
```
Start alle containerne op
```
sudo docker compose up -d
```
Nogle gange er det nødvendigt at lave et nyt image. Det gør du ved at fjerne det gamle image og køre `sudo docker compose up`.

## Fjern dit image
Hvis du har brug for at deploye en ny version af dit projekt (en ny fat jar), skal du først fjerne det gamle image.
1. Stop containeren
2. Fjern containeren
3. Fjern image
4. Lav nyt image

Først skal containerne slukkes, enten med `sudo docker compose down` (der slukker alle containerne) eller `sudo docker stop <container navn>` (der kun stopper en container).

Så kan du fjerne containeren med `sudo docker rm <container navn>`.

Så skal du fjerne det image som er blevet compilet
```
sudo docker images
sudo docker rmi [image_id_or_name]
```
Nå du har overført din nye fat jar (eller rettet i dine filer `Dockerfile` og `docer-compose.yml`), så kan du bygge et nyt image med `sudo docker compose up -d`.

## Example files
Dockerfile
```
FROM amazoncorretto:17-alpine
COPY ./app.jar /app.jar
EXPOSE 7070
CMD ["java", "-jar", "/app.jar"]
```

Caddyfile
```
{
    email jobe@cphbusiness.dk
}

fourthings.showcode.dk {
  reverse_proxy fourthings:7070
}

showcode.dk {
  root * /srv/
  file_server
}
```

docker-compose.yml
```
services:

  db:
    image: postgres:16.2
    container_name: db
    restart: unless-stopped
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: <dit_sikre_password> # Change this password and pick a hard one
    volumes:
    - ./postgres_data:/var/lib/postgresql/data/
    ports:
    - "5432:5432"

  caddy:
    image: caddy:2.7.6
    restart: unless-stopped
    container_name: caddy
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile
      - ./site:/srv
      - ./caddy_data:/data
      - ./caddy_config:/config

  fourthings:
    build:
      context: ./fourthings
      dockerfile: Dockerfile
    container_name: fourthings
    environment:
      - DEPLOYED=PROD
      - JDBC_USER=postgres
      - JDBC_PASSWORD=<dit_sikre_password>
      - JDBC_CONNECTION_STRING=jdbc:postgresql://db:5432/%s?currentSchema=public
      - JDBC_DB=fourthingsplus
    ports:
      - "7070:7070"
    restart: unless-stopped

volumes:
  postgres_data:
  caddy_data:
  caddy_config:
```