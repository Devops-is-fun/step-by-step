// Posts the result of the build on Discord (module N1a). A notification failure never fails the build.
def call(String version = '') {
    def icon = currentBuild.currentResult == 'SUCCESS' ? '✅' : '❌'
    def message = "${icon} **${env.JOB_NAME}** #${env.BUILD_NUMBER} ${version}: " +
                  "${currentBuild.currentResult} in ${currentBuild.durationString.replace(' and counting', '')}"
    writeJSON file: 'discord.json', json: [content: message]
    withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD_WEBHOOK')]) {
        sh 'curl -fsS -H "Content-Type: application/json" -d @discord.json "$DISCORD_WEBHOOK" || echo "Discord notification failed"'
    }
}
