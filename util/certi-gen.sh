#!/bin/bash
echo Please type in the password for the backend certificate:
read pass
mkdir -p frontend-certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout frontend-certs/selfsigned.key -out frontend-certs/selfsigned.crt -subj "/C=HU/ST=Hungary/L=Budapest /O=Thyme /OU=Thyme-shop/CN=localhost"
mkdir -p backend-certs
rm -f ./backend-certs/*
keytool -genkeypair -alias thymecert -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ./backend-certs/thymecert.p12 -validity 3650 -dname "CN=localhost, OU=Thyme-shop, O=Thyme, L=Budapest, ST=Hungary, C=HU" -keypass $pass -storepass $pass
sed -i "/\b\(server.ssl.key-store-password\)\b/d" ../server/src/main/resources/application.properties
echo -en '\nserver.ssl.key-store-password='$pass >> ../server/src/main/resources/application.properties
