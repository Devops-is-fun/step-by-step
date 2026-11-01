// Step by Step CI/CD pipeline.
// CI (stories 011-014): secrets, lint, unit tests, integration tests, quality gate, versioned artefact in Nexus.
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
                    def project = "stepbystep-ci-${env.BUILD_TAG}".toLowerCase().replaceAll('[^a-z0-9_-]', '-')
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
                        withCredentials([usernamePassword(credentialsId: 'nexus-deploy-creds',
                                                          usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                            sh """${MVN} -s ci/maven-settings.xml deploy:deploy-file \
                                -Dfile=target/step-by-step-0.1.0-SNAPSHOT.jar \
                                -DgroupId=com.esgi -DartifactId=step-by-step -Dversion=${APP_VERSION} \
                                -Dpackaging=jar -DgeneratePom=true \
                                -DrepositoryId=nexus -Durl=${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/"""
                        }
                    }
                }
                archiveArtifacts artifacts: 'target/step-by-step-*.jar', fingerprint: true
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
