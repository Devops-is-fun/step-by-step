// Step by Step CI pipeline. Story 010: minimal pipeline proving webhook -> agent -> Maven.
// Story 011 adds the build, test and analysis stages.
pipeline {
    agent { label 'docker-agent' }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Validate') {
            steps {
                sh './mvnw -B validate'
            }
        }
    }
}