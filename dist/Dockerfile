FROM tomcat:11.0-jdk21

# Instalar MySQL Server y cliente
RUN apt-get update && \
    apt-get install -y mysql-server mysql-client bash && \
    rm -rf /var/lib/apt/lists/*

# Limpiar aplicaciones por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war

# Copiar archivos
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/ROOT.war
COPY dist/GlucoAPI.sql /docker-entrypoint-initdb.d/GlucoAPI.sql

# Configurar script de entrada
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]