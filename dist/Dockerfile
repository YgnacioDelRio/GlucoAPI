# Imagen oficial de Tomcat 11 corriendo sobre JDK 21
FROM tomcat:11.0-jdk21

# Limpia la aplicación por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/ROOT.war

# Copia el .war usando la ruta relativa del repositorio
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]