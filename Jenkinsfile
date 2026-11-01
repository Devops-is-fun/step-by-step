// Step by Step CI/CD pipeline.
// CI (stories 011-014): secrets, lint, unit tests, integration tests, quality gate, versioned artefact in Nexus.
// CD (stories 015-018), main only: staging, manual approval, blue/green production with automatic rollback.
// Every stage runs on the docker-agent node, never on the controller.

def APP_VERSION = ''

pipeline {
    agent { label 'docker-agent' }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds(abortPrevious: true)
    }

    environment {
        MVN = './mvnw -B -ntp'
        NEXUS_URL = 'http://nexus:8081'
        NEXUS_REPOSITORY = 'maven-releases'
    }

    stages {
        stage('Version') {
            steps {
                script {
                    // Artefact version = semantic version + short commit hash (EXG-COM-12)
                    def shortSha = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                    APP_VERSION = "0.1.0-${shortSha}"
                    currentBuild.displayName = "#${env.BUILD_NUMBER} ${APP_VERSION}"
                }
                echo "Artefact version: ${APP_VERSION}"
            }
        }

        stage('Checks') {
            parallel {
                stage('Secrets') {
                    steps {
                        sh './scripts/check-no-secrets.sh'
                    }
                }
                stage('Lint') {
                    steps {
                        sh "${MVN} checkstyle:check"
                    }
                    post {
                        always {
                            recordIssues(tools: [checkStyle(pattern: 'target/checkstyle-result.xml')])
                        }
                    }
                }
                stage('Unit tests') {
                    steps {
                        sh "${MVN} test"
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/TEST-*.xml'
                        }
                    }
                }
            }
        }

        stage('Integration tests') {
            steps {
                script {
                    // One throwaway PostgreSQL per build (EXG-COM-10): own Compose project, random host port
                    // Kept short: the database container name must fit in a 63-character DNS label
                    def project = "ci-${env.BUILD_NUMBER}-${env.BRANCH_NAME}".toLowerCase().replaceAll('[^a-z0-9_-]', '-').take(40)
                    withEnv(["COMPOSE_PROJECT_NAME=${project}", 'TEST_DB_PORT=']) {
                        try {
                            sh 'docker compose -f src/test/resources/compose-test.yml up -d --wait'
                            // The agent joins the test network and reaches the database by its container name
                            sh 'docker network connect "${COMPOSE_PROJECT_NAME}_default" "$(hostname)"'
                            withEnv(["SPRING_DATASOURCE_URL=jdbc:postgresql://${project}-postgres-1:5432/stepbystep_test"]) {
                                sh "${MVN} verify"
                            }
                        } finally {
                            sh 'docker network disconnect "${COMPOSE_PROJECT_NAME}_default" "$(hostname)" || true'
                            sh 'docker compose -f src/test/resources/compose-test.yml down'
                        }
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/TEST-*.xml'
                    recordCoverage(tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco-merged/jacoco.xml']],
                                   sourceDirectories: [[path: 'src/main/java']])
                }
            }
        }

        stage('Quality gate') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh "${MVN} org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectVersion=${APP_VERSION}"
                }
                // Blocking gate (EXG-COM-11): the pipeline stops if SonarQube says ERROR
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Publish') {
            when { branch 'main' }
            steps {
                script {
                    def path = "com/esgi/step-by-step/${APP_VERSION}/step-by-step-${APP_VERSION}.jar"
                    def status = sh(script: "curl -s -o /dev/null -w '%{http_code}' ${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/${path}",
                                    returnStdout: true).trim()
                    if (status == '200') {
                        // Releases are immutable in Nexus: a replayed build reuses the published artefact
                        echo "Artefact ${APP_VERSION} already in Nexus, not republished"
                    } else {
                        // Same upload as the witness of story 004: a plain HTTP PUT of the jar and its pom
                        withEnv(["APP_VERSION=${APP_VERSION}"]) {
                            withCredentials([usernamePassword(credentialsId: 'nexus-deploy-creds',
                                                              usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                                sh '''
                                    set -eu
                                    base="$NEXUS_URL/repository/$NEXUS_REPOSITORY/com/esgi/step-by-step/$APP_VERSION"
                                    cat > target/publish.pom <<POM
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.esgi</groupId>
  <artifactId>step-by-step</artifactId>
  <version>$APP_VERSION</version>
  <packaging>jar</packaging>
</project>
POM
                                    curl -fsS -u "$NEXUS_USER:$NEXUS_PASSWORD" --upload-file target/step-by-step-0.1.0-SNAPSHOT.jar "$base/step-by-step-$APP_VERSION.jar"
                                    curl -fsS -u "$NEXUS_USER:$NEXUS_PASSWORD" --upload-file target/publish.pom "$base/step-by-step-$APP_VERSION.pom"
                                    echo "Published $base/step-by-step-$APP_VERSION.jar"
                                '''
                            }
                        }
                    }
                }
                archiveArtifacts artifacts: 'target/step-by-step-*.jar', fingerprint: true
            }
        }

        stage('Deploy staging') {
            when { branch 'main' }
            steps {
                // Automatic from main (story 016): the Nexus artefact, idempotent script
                withCredentials([usernamePassword(credentialsId: 'staging-db-creds',
                                                  usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD')]) {
                    sh "./scripts/deploy-staging.sh ${APP_VERSION}"
                }
            }
        }

        stage('Approve production') {
            when { branch 'main' }
            steps {
                // Manual validation (story 018): only the submitter can deploy the same artefact to production
                timeout(time: 30, unit: 'MINUTES') {
                    input message: "Deploy ${APP_VERSION} (already running in staging) to production?",
                          ok: 'Deploy to production', submitter: 'admin'
                }
            }
        }

        stage('Deploy production') {
            when { branch 'main' }
            steps {
                // Blue/green with health check and automatic rollback (story 017, module N2a)
                withCredentials([usernamePassword(credentialsId: 'production-db-creds',
                                                  usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD')]) {
                    sh "./scripts/deploy-production.sh ${APP_VERSION}"
                }
            }
        }
    }

    post {
        always {
            script {
                def icon = currentBuild.currentResult == 'SUCCESS' ? '✅' : '❌'
                def message = "${icon} **${env.JOB_NAME}** #${env.BUILD_NUMBER} ${APP_VERSION}: " +
                              "${currentBuild.currentResult} in ${currentBuild.durationString.replace(' and counting', '')}"
                writeJSON file: 'discord.json', json: [content: message]
                // Module N1a: every build is notified on Discord
                withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD_WEBHOOK')]) {
                    sh 'curl -fsS -H "Content-Type: application/json" -d @discord.json "$DISCORD_WEBHOOK" || echo "Discord notification failed"'
                }
            }
        }
    }
}
