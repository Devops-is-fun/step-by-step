// Publishes the jar built by the CI to Nexus as com.esgi:step-by-step:<version> (EXG-COM-12).
// Releases are immutable in Nexus: an already published version is reused, never overwritten.
def call(String version, String jarFile, String repository = 'maven-releases') {
    def nexusUrl = env.NEXUS_URL ?: 'http://nexus:8081'
    def path = "com/esgi/step-by-step/${version}/step-by-step-${version}.jar"
    def status = sh(script: "curl -s -o /dev/null -w '%{http_code}' ${nexusUrl}/repository/${repository}/${path}",
                    returnStdout: true).trim()
    if (status == '200') {
        echo "Artefact ${version} already in Nexus, not republished"
        return
    }
    withEnv(["APP_VERSION=${version}", "JAR_FILE=${jarFile}", "NEXUS_BASE=${nexusUrl}/repository/${repository}/com/esgi/step-by-step/${version}"]) {
        withCredentials([usernamePassword(credentialsId: 'nexus-deploy-creds',
                                          usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
            sh '''
                set -eu
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
                # Exactly the jar of this build (named after the pom version by Maven); Nexus gets the build version
                test -f "$JAR_FILE"
                curl -fsS -u "$NEXUS_USER:$NEXUS_PASSWORD" --upload-file "$JAR_FILE" "$NEXUS_BASE/step-by-step-$APP_VERSION.jar"
                curl -fsS -u "$NEXUS_USER:$NEXUS_PASSWORD" --upload-file target/publish.pom "$NEXUS_BASE/step-by-step-$APP_VERSION.pom"
                echo "Published $NEXUS_BASE/step-by-step-$APP_VERSION.jar"
            '''
        }
    }
}
