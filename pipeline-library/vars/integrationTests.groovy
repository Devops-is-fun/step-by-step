// Integration tests against a throwaway PostgreSQL (EXG-COM-10).
// One Compose project per build, random host port: parallel builds never clash.
def call(String mvn = './mvnw -B -ntp') {
    // Kept short: the database container name must fit in a 63-character DNS label
    def project = "ci-${env.BUILD_NUMBER}-${env.BRANCH_NAME}".toLowerCase().replaceAll('[^a-z0-9_-]', '-').take(40)
    withEnv(["COMPOSE_PROJECT_NAME=${project}", 'TEST_DB_PORT=']) {
        try {
            sh 'docker compose -f src/test/resources/compose-test.yml up -d --wait'
            // The agent joins the test network and reaches the database by its container name
            sh 'docker network connect "${COMPOSE_PROJECT_NAME}_default" "$(hostname)"'
            withEnv(["SPRING_DATASOURCE_URL=jdbc:postgresql://${project}-postgres-1:5432/stepbystep_test"]) {
                sh "${mvn} verify"
            }
        } finally {
            sh 'docker network disconnect "${COMPOSE_PROJECT_NAME}_default" "$(hostname)" || true'
            sh 'docker compose -f src/test/resources/compose-test.yml down'
        }
    }
}
