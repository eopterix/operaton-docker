command wget --header="Authorization: Bearer ${GITHUB_TOKEN}" -O "operaton-bpm-run-${VERSION}.tar.gz" "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_OPERATON}/zip"
mkdir -p /operaton/configuration/userlib/
command wget --header="Authorization: Bearer ${GITHUB_TOKEN}" -O "/operaton/configuration/userlib/keycloak-plugin-${VERSION_KEYCLOAK}.tar.gz" "https://github.com${GITHUB_REPO}/actions/artifacts/${ARTIFACT_ID_KEYCLOAK}/zip"
