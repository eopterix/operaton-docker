#!/bin/bash

# Conveniently this will break execution of release.sh just right
function docker() { return 0; }
function exit() { return ${1:-0}; }

# Source original release script for functions only
source ./release.sh

# Release docker override
unset -f docker
unset -f exit

# Hacky bending or docker xbuild command
PLATFORMS="amd64 -o type=docker,dest=operaton-docker.tar"

# Additional COPY command to use artifacts
NEW_LINE="COPY /home/runner/work/operaton-docker/operaton-docker/keycloak-plugin/operaton-keycloak-run-${VERSION_KEYCLOAK}.jar /operaton/configuration/userlib/"
sed -i "0,/^COPY/{/^COPY/a\
${NEW_LINE}
}" Dockerfile

NEW_LINE="COPY /home/runner/work/operaton-docker/operaton-docker/operaton-bpm/operaton-bpm-run-${VERSION}.tar.gz ."
sed -i "0,/^COPY/{/^COPY/a\
${NEW_LINE}
}" Dockerfile

# Download line to be deleted
DEL_LINE='wget -q "$distro_file_url"'
sed -i "/$DEL_LINE/d" "download.sh"

# Call sourced function
build_and_push "${VERSION}"
