#!/bin/bash
mkdir -p certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout frontend-certs/selfsigned.key -out frontend-certs/selfsigned.crt
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout backend-certs/selfsigned.key -out backend-certs/selfsigned.crt
