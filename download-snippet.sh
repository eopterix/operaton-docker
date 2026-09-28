WGET_ARGS=(
  --header="Authorization: Bearer ${GITHUB_TOKEN}"
  -O "operaton-bpm-run-${VERSION}.tar.gz"
  "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_OPERATON}/zip"
)
command wget "${WGET_ARGS[@]}"
echo "Pulled operaton-bpm."

mkdir -p /operaton/configuration/userlib/
WGET_ARGS=(
  --header="Authorization: Bearer ${GITHUB_TOKEN}"
  -O "/operaton/configuration/userlib/keycloak-plugin-${VERSION_KEYCLOAK}.tar.gz"
  "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_KEYCLOAK}/zip"
)
command wget "${WGET_ARGS[@]}"
echo "Pulled keycloak-plugin."
