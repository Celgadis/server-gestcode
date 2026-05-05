#!/bin/bash

# ==============================================================================
# Script de configuració HTTPS per al projecte GestCode API
# Autor: Jordi Verdalet Carrera
# 
# Aquest script automatitza:
# 1. Configuració del UFW (Tallafocs) per obrir ports 80 i 443
# 2. Instal·lació de Nginx i Certbot
# 3. Configuració del Proxy Invers de Nginx
# 4. Generació de certificats SSL de Let's Encrypt per al domini Isard
# ==============================================================================

# Domini específic de Isard on es desplegarà el servidor
DOMAIN="401c000f-26f1-447e-b499.e9734fe78f0a.bastion.elmeuescriptori.cat"
INTERNAL_PORT="8080"

# Comprovar que s'executa com a root
if [ "$EUID" -ne 0 ]; then
  echo "Si us plau, executa aquest script com a administrador (sudo)"
  exit 1
fi

echo "Iniciant la configuració de seguretat HTTPS..."

# 1. Configuració de UFW
echo "Obrint els ports 80 (HTTP) i 443 (HTTPS) al tallafocs..."
ufw allow 80/tcp
ufw allow 443/tcp
ufw reload

# 2. Instal·lació de dependències
echo "Instal·lant Nginx i Certbot..."
apt update
apt install -y nginx certbot python3-certbot-nginx

# 3. Configuració de Nginx com a Proxy Invers
echo "Configurant Nginx per al domini $DOMAIN..."
NGINX_CONF="/etc/nginx/sites-available/gestcode-api"

cat > "$NGINX_CONF" <<EOF
server {
    listen 80;
    server_name $DOMAIN;

    location / {
        proxy_pass http://localhost:$INTERNAL_PORT;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Habilitar el lloc i desactivar el de defecte si existeix
ln -sf "$NGINX_CONF" /etc/nginx/sites-enabled/
if [ -f "/etc/nginx/sites-enabled/default" ]; then
    rm /etc/nginx/sites-enabled/default
fi

# Comprovar si la configuració de Nginx és vàlida
nginx -t
if [ $? -eq 0 ]; then
    echo "Reiniciant Nginx..."
    systemctl restart nginx
else
    echo "Error en la configuració de Nginx. Revisa el fitxer: $NGINX_CONF"
    exit 1
fi

# 4. Generació i aplicació de certificats SSL amb Certbot
echo "Generant els certificats SSL amb Let's Encrypt..."
certbot --nginx -d "$DOMAIN" --non-interactive --agree-tos --register-unsafely-without-email

#No registro el meu email per el certificat, ja que el projecte es per al treball de grup i  la durara es curta.
#Però en un entorn real registraria el meu email per a rebre les notificacions de renovació del certificat.

echo "====================================================================="
echo "Configuració completada amb èxit!"
echo "Pots accedir a la teva API de forma segura a: https://$DOMAIN"
echo "====================================================================="
