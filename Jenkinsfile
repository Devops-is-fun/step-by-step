// Step by Step CI/CD pipeline.
// CI (stories 011-014): secrets, lint, unit tests, integration tests, quality gate, versioned artefact in Nexus.
// CD (stories 015-018), main only: staging, manual approval, blue/green production with automatic rollback.
// Common steps come from the Shared Library step-by-step-lib (pipeline-library/), pinned by tag (story 020).
// Every stage runs on the docker-agent node, never on the controller.

@Library('step-by-step-lib@lib-v1.1.1') _

def APP_VERSION = ''

pipeline {
    agent { label 'docker-agent' }

    options {
        timeout(time: 40, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds(abortPrevious: true)
    }

    environment {
        MVN = './mvnw -B -ntp'
        NEXUS_URL = 'http://nexus:8081'
    }

    stages {
        stage('Version') {
            steps {
                script {
                    // Artefact version = version of pom.xml (set by release-please, module N1b) + short commit hash
                    // (EXG-COM-12), for example 0.2.0-1a2b3c4
                    def pomVersion = sh(script: "${MVN} -q help:evaluate -Dexpression=project.version -DforceStdout",
                                        returnStdout: true).trim().replace('-SNAPSHOT', '')
                    def shortSha = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                    APP_VERSION = "${pomVersion}-${shortSha}"
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
                integrationTests(env.MVN)
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
                publishToNexus(APP_VERSION)
                archiveArtifacts artifacts: 'target/step-by-step-*.jar', fingerprint: true
            }
        }

        stage('Deploy staging') {
            when { branch 'main' }
            steps {
                // Automatic from main (story 016): the Nexus artefact, idempotent script
                deployStaging(APP_VERSION)
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
                deployProduction(APP_VERSION)
            }
        }
    }

    post {
        always {
            // Module N1a: every build is notified on Discord
            notifyDiscord(APP_VERSION)
        }
    }
}
