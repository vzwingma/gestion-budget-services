# Issu de la dernière image Tomcat 8
FROM tomcat:8.5.24-alpine
MAINTAINER vincent.zwingmann@gmail.com

# Copy des fichiers 
COPY context.xml /usr/local/tomcat/conf/context.xml
COPY Budget.tar.gz /usr/local/tomcat/webapps/Budget.tar.gz

# Droit pour tous les users de Tomcat
RUN chmod a+r -R /usr/local/tomcat; \
	chmod 777 -R /usr/local/tomcat;
	
RUN cd /usr/local/tomcat/webapps; \
	tar xvzf Budget.tar.gz -C  /usr/local/tomcat/webapps; \
	rm -f *.sh; \
	rm -f *.tar.gz; \
	mv ROOT.war budget.war