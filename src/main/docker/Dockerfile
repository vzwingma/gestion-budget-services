# Issu de la dernière image Tomcat 8
FROM tomcat:8.5.24-alpine
MAINTAINER vincent.zwingmann@gmail.com

# Ajout des packages manquants pour wget
RUN apk update && apk add ca-certificates && update-ca-certificates && apk add openssl;

# Droit pour tous les users de Tomcat
RUN chmod a+r -R /usr/local/tomcat; \
	chmod 777 -R /usr/local/tomcat;

COPY /home/travis/build/vzwingma/gestion-budget/target/Budget.tar.gz /usr/local/tomcat/webapps/Budget.tar.gz
	
RUN cd /usr/local/tomcat/webapps; \
	tar xvzf Budget.tar.gz -C  /usr/local/tomcat/webapps; \
	rm -f *.sh; \
	rm -f *.tar.gz; \
	mv ROOT.war budget.war

COPY /home/travis/build/vzwingma/gestion-budget/src/main/docker/context.xml /usr/local/tomcat/conf/context.xml