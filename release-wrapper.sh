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

# Prepare replacement snippet
REPLACEMENT=$(eval "cat <<EOF
$(cat download-snippet.sh  | awk '{printf "%s\\n", $0}' )
EOF"
)

# Replace wget command in download.sh
sed -i "s|wget -q \"\$distro_file_url\"|$REPLACEMENT|"

# Call sourced function
build_and_push "${VERSION}"
