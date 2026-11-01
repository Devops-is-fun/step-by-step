// Deploys a version of the Nexus artefact to staging (stories 015-016): idempotent script.
def call(String version) {
    withCredentials([usernamePassword(credentialsId: 'staging-db-creds',
                                      usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD')]) {
        sh "./scripts/deploy-staging.sh ${version}"
    }
}
