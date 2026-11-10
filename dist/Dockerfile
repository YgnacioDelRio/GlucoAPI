FROM tomcat:11.0-jdk21

# Instalar MySQL Server y bash
RUN apt-get update && apt-get install -y mysql-server bash && rm -rf /var/lib/apt/lists/*

# Limpiar las aplicaciones por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/ROOT.war

# Copiar el .war del proyecto
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/ROOT.war

# Copiar el script .sql a la ruta donde lo leerá el entrypoint
COPY dist/GlucoAPI.sql /docker-entrypoint-initdb.d/GlucoAPI.sql

# Copiar y dar permisos de ejecución al script entrypoint
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]