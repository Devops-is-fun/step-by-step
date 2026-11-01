// Blue/green deployment to production with automatic rollback (stories 017-018, module N2a).
def call(String version) {
    withCredentials([usernamePassword(credentialsId: 'production-db-creds',
                                      usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD')]) {
        sh "./scripts/deploy-production.sh ${version}"
    }
}
