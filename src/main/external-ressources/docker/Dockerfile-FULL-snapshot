# Issu de la dernière image Tomcat 8
FROM tomcat:8.5.34-alpine
MAINTAINER vincent.zwingmann@gmail.com

# Copy de l'archive
COPY ihm.war /usr/local/tomcat/webapps/ihm.war

# Droit pour tous les users de Tomcat
RUN chmod a+r -R /usr/local/tomcat; \
    chmod 777 -R /usr/local/tomcat;