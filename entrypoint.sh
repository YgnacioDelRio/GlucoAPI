#!/bin/bash

# Iniciar el servicio de MySQL en segundo plano
service mysql start

# Esperar unos segundos a que MySQL esté listo
sleep 5

# Configurar usuario root, contraseña y crear la base de datos
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';"
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS gluco_api;"

# Importar el script SQL si existe
if [ -f /docker-entrypoint-initdb.d/GlucoAPI.sql ]; then
    mysql -u root -proot gluco_api < /docker-entrypoint-initdb.d/GlucoAPI.sql
fi

# Iniciar Tomcat en primer plano
exec catalina.sh run