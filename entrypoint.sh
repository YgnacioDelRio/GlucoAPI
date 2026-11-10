#!/bin/bash
set -e

# Iniciar MySQL
echo "Iniciando servicio MySQL..."
service mysql start

# Esperar activamente hasta que MySQL acepte conexiones
echo "Esperando a que MySQL esté listo en el puerto 3306..."
until mysqladmin ping --silent; do
    sleep 1
done

echo "MySQL está activo. Configurando base de datos..."

# Configurar usuario y base de datos
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';" || true
mysql -u root -e "CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'root';" || true
mysql -u root -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;" || true
mysql -u root -e "FLUSH PRIVILEGES;" || true
mysql -u root -e "CREATE DATABASE IF NOT EXISTS gluco_api;" || true

# Importar esquema SQL si existe
if [ -f /docker-entrypoint-initdb.d/GlucoAPI.sql ]; then
    echo "Importando esquema GlucoAPI.sql..."
    mysql -u root -proot gluco_api < /docker-entrypoint-initdb.d/GlucoAPI.sql
fi

echo "Arrancando Tomcat..."
exec catalina.sh run