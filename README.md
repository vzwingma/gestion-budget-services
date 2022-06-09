# Micro-services Budget

Ce projet utilise maintenant  Quarkus, le Supersonic Subatomic Java Framework.

| Module                                                          | Version                                                                                                                                           |
|-----------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| [IHM](https://github.com/vzwingma/gestion-budget)               | [![GitHub version](https://badge.fury.io/gh/vzwingma%2Fgestion-budget.svg)](https://badge.fury.io/gh/vzwingma%2Fgestion-budget)                   |
| [Services](https://github.com/vzwingma/gestion-budget-services) | [![GitHub version](https://badge.fury.io/gh/vzwingma%2Fgestion-budget-services.svg)](https://badge.fury.io/gh/vzwingma%2Fgestion-budget-services) |

### Statut

[![Build Status](https://github.com/vzwingma/gestion-budget-services/actions/workflows/build-on-master.yml/badge.svg)](https://github.com/vzwingma/gestion-budget-services/actions/workflows/build-on-master.yml)
[![Build Status](https://github.com/vzwingma/gestion-budget-services/actions/workflows/build-on-tags.yml/badge.svg)](https://github.com/vzwingma/gestion-budget-services/actions/workflows/build-on-tags.yml)
[![GitHub issues](https://img.shields.io/github/issues-raw/vzwingma/gestion-budget-services.svg?style=flat-square)](https://github.com/vzwingma/gestion-budget-services/issues)

[![Known Vulnerabilities](https://snyk.io/test/github/vzwingma/gestion-budget-services/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/vzwingma/gestion-budget-services)
[![Dépendences](https://img.shields.io/librariesio/github/vzwingma/gestion-budget-services.png)](https://libraries.io/github/vzwingma/gestion-budget-services)

<a href="https://sonarcloud.io/dashboard?id=gestion-budget-services"><img alt="Sonar Build Status" src="https://sonarcloud.io/api/project_badges/measure?project=gestion-budget-services&metric=coverage" /></a>
<a href="https://sonarcloud.io/dashboard?id=gestion-budget-services"><img alt="Sonar Build Status" src="https://sonarcloud.io/api/project_badges/measure?project=gestion-budget-services&metric=sqale_rating" /></a>
<a href="https://sonarcloud.io/dashboard?id=gestion-budget-services"><img alt="Sonar Build Status" src="https://sonarcloud.io/api/project_badges/measure?project=gestion-budget-services&metric=reliability_rating" /></a>
<a href="https://sonarcloud.io/dashboard?id=gestion-budget-services"><img alt="Sonar Build Status" src="https://sonarcloud.io/api/project_badges/measure?project=gestion-budget-services&metric=security_rating" /></a>


### Lancer les microservices en `dev mode`

via la commande :
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus déploie une Dev UI, accessible en dev mode seulement à http://localhost:8080/q/dev/.

> **_NOTE:_**  Quarkus présente une OpenAPI UI, accessible en dev mode seulement à http://localhost:8080/q/swagger-ui/.

### Packaging and lancement des applications natives

L'exécurable native est généré automatiquement par la commande :

```shell script
./mvnw package -Pnative
```

Ou, sans avoir de GraalVM installé, l'exécutable natif est généré dans un conteneur par la commande :
 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Le package généré peut être exécuté via la commande : `./target/services-runner`

### Frameworks utilisés

- Mutiny ([guide](https://quarkus.io/guides/mutiny)): A reactive programming framework for Java.
- MongoDB with Panache ([guide](https://quarkus.io/guides/mongodb-panache)): Simplify your persistence code for MongoDB via the active record or the repository pattern
- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more
- Micrometer metrics ([guide](https://quarkus.io/guides/micrometer)): Instrument the runtime and your application with dimensional metrics using Micrometer.

