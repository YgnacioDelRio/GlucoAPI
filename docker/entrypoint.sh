#!/bin/bash
set -e

DB_NAME="${DB_NAME:-gluco_api}"
DB_USER="${DB_USER:-gluco}"
DB_PASS="${DB_PASS:-gluco}"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"

if [ "$DB_HOST" = "127.0.0.1" ] || [ "$DB_HOST" = "localhost" ]; then
    echo "[entrypoint] Iniciando MariaDB local..."

    mkdir -p /run/mysqld
    chown -R mysql:mysql /run/mysqld /var/lib/mysql

    if [ ! -d /var/lib/mysql/mysql ]; then
        echo "[entrypoint] Inicializando el datadir de MariaDB..."
        mariadb-install-db --user=mysql --datadir=/var/lib/mysql --skip-test-db >/dev/null
    fi

    mariadbd --user=mysql --datadir=/var/lib/mysql \
        --bind-address=127.0.0.1 \
        --innodb-buffer-pool-size=32M \
        --performance-schema=OFF \
        --max-connections=50 \
        --skip-name-resolve &

    echo "[entrypoint] Esperando a MariaDB..."
    for i in $(seq 1 60); do
        if mariadb-admin ping --silent 2>/dev/null; then
            echo "[entrypoint] MariaDB lista."
            break
        fi
        sleep 1
    done

    mariadb -uroot <<SQL
CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USER}'@'%' IDENTIFIED BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USER}'@'%';
FLUSH PRIVILEGES;
SQL

    echo "[entrypoint] Aplicando GlucoAPI.sql..."
    mariadb -uroot "${DB_NAME}" < /docker-init/GlucoAPI.sql
else
    echo "[entrypoint] DB_HOST=${DB_HOST}; no se inicia MariaDB local."
    if [ "${DB_INIT:-false}" = "true" ]; then
        echo "[entrypoint] Aplicando GlucoAPI.sql en ${DB_HOST}:${DB_PORT}..."
        mariadb -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" < /docker-init/GlucoAPI.sql
    fi
fi

exec "$@"
