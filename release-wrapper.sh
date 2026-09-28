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

# Now point wget in download.sh to local artifacts
function wget() {
  WGET_ARGS=(
     --header="Authorization: Bearer ${GITHUB_TOKEN}"
     -O "operaton-bpm-${VERSION}.tar.gz"
     "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_OPERATON}/zip"
  )
  command wget "${WGET_ARGS[@]}"

  mkdir -p /operaton/configuration/userlib/
  WGET_ARGS=(
     --header="Authorization: Bearer ${GITHUB_TOKEN}"
     -O "/operaton/configuration/userlib/keycloak-plugin-${VERSION_KEYCLOAK}.tar.gz"
     "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_KEYCLOAK}/zip"
  )
  command wget "${WGET_ARGS[@]}"
}

# Call sourced function
build_and_push "${VERSION}"

# Release wget
unset -f wget