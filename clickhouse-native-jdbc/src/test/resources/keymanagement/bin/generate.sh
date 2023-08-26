#!/bin/sh

# Versions:
# Windows 10
# OpenSSL Version: OpenSSL 3.1.2 1
# Keytool Version: openjdk-17.0.2

KEY_FILE=server.key
CRT_FILE=server.crt
PKCS12_FILE=server.p12
JKS_FILE=server.jks
PASSWORD=mypassword
ALIAS=myalias

echo "Generating the private key and certificate..."
openssl req -subj "//CN=localhost" -new -newkey rsa:2048 -days 365 -nodes -x509 -keyout ${KEY_FILE} -out ${CRT_FILE}
if [ $? -ne 0 ]; then
    echo "Failed to generate the private key and certificate."
    exit 1
fi

echo "Converting to PKCS12 format..."
openssl pkcs12 -export -in ${CRT_FILE} -inkey ${KEY_FILE} -out ${PKCS12_FILE} -name ${ALIAS} -password pass:${PASSWORD}
if [ $? -ne 0 ]; then
    echo "Failed to convert to PKCS12 format."
    exit 1
fi

echo "Importing keystore ${PKCS12_FILE} to ${JKS_FILE}..."
keytool -importkeystore\
        -srckeystore ${PKCS12_FILE}\
        -srcstoretype PKCS12\
        -srcstorepass ${PASSWORD}\
        -destkeystore ${JKS_FILE}\
        -deststoretype JKS\
        -deststorepass ${PASSWORD}
if [ $? -ne 0 ]; then
    echo "Failed to import keystore."
    exit 1
fi

echo "Done!"