#!/bin/bash
set -e

echo "Inicializando directorios de MySQL..."
mkdir -p /var/run/mysqld /var/lib/mysql
chown -R mysql:mysql /var/run/mysqld /var/lib/mysql

# Inicializar la base de datos si la carpeta está vacía
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "Inicializando base de datos MySQL por primera vez..."
    mysqld --initialize-insecure --user=mysql
fi

# Iniciar MySQL en segundo plano en lugar de usar 'service'
echo "Iniciando mysqld en segundo plano..."
mysqld --user=mysql &

# Esperar activamente a que el socket/puerto esté disponible
echo "Esperando a que MySQL responda..."
until mysqladmin ping --silent; do
    sleep 1
done

echo "MySQL está activo. Configurando usuario y base de datos..."

# Permisos para root y creación de la BD
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';" || true
mysql -u root -e "CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'root';" || true
mysql -u root -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;" || true
mysql -u root -e "FLUSH PRIVILEGES;" || true
mysql -u root -e "CREATE DATABASE IF NOT EXISTS gluco_api;" || true

# Importar el script SQL si existe
if [ -f /docker-entrypoint-initdb.d/GlucoAPI.sql ]; then
    echo "Importando esquema GlucoAPI.sql..."
    mysql -u root -proot gluco_api < /docker-entrypoint-initdb.d/GlucoAPI.sql
fi

echo "Iniciando Apache Tomcat..."
exec catalina.sh run