# Issu de la dernière image Tomcat 8
FROM tomcat:8.5.24-alpine
MAINTAINER vincent.zwingmann@gmail.com

# Copy de l'archive
COPY budget.war /usr/local/tomcat/webapps/ROOT.war

# Droit pour tous les users de Tomcat
RUN chmod a+r -R /usr/local/tomcat; \
    chmod 777 -R /usr/local/tomcat;