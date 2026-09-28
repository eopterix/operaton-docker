command wget --header="Authorization: Bearer ${GITHUB_TOKEN}" -O "operaton-bpm-${VERSION}.tar.gz" "https://github.com/${GITHUB_REPO}/actions/runs/${GITHUB_RUN_ID}/artifacts/${ARTIFACT_ID_OPERATON}/zip"
mkdir -p /operaton/configuration/userlib/
command wget --header="Authorization: Bearer ${GITHUB_TOKEN}" -O "/operaton/configuration/userlib/operaton-keycloak-run-${VERSION_KEYCLOAK}.jar" "https://github.com/${GITHUB_REPO}/actions/runs/${GITHUB_RUN_ID}/artifacts/${ARTIFACT_ID_KEYCLOAK}/zip"
