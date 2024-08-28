def downloadMtUtils() {
    sh '''
        echo "INFO: Retrieving Scripts from Nexus"
        tar_file_name="utils_${mt_utils_version}.tar.gz"
        echo "INFO: Downloading file - ${tar_file_name} - to the workspace"
        if [[ "${mt_utils_version}" == *"SNAPSHOT"* ]]; then
            curl -s --noproxy \\* -L "https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=snapshots&g=com.ericsson.mtg&a=utils&p=tar.gz&v=${mt_utils_version}" -o ${tar_file_name}
        else
            curl -s --noproxy \\* -L "https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.mtg&a=utils&p=tar.gz&v=${mt_utils_version}" -o ${tar_file_name}
        fi
        tar -zxf ${tar_file_name}
    '''
}

def exportEnvVariablesToFile(String fileName) {
    String properties = ""
    env.getEnvironment().each { name, value ->
        if(!value.contains("#") && !value.isEmpty()) {
            properties += "${name}=${value}\n"
        }
    }
    writeFile file: fileName, text: properties
}

def loadProperties(String filePath) {
    def buildProperties = readProperties file: filePath
    variableNames = buildProperties.keySet()
    for(variableName in variableNames) {
        variableValue = buildProperties["${variableName}"]
        env."${variableName}" = "${variableValue}"
    }
}

return this