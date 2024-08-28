package commonclasses

class CommonSteps {

    static String downloadMTUtilsRelease() {
        '''#!/bin/sh

echo "Retrieving Scripts from Nexus"
tarFileName="utils_${mt_utils_version}.tar.gz"
echo "Downloading file - ${tarFileName} - to the workspace"
curl -s --noproxy \\* -L "https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.mtg&a=utils&p=tar.gz&v=${mt_utils_version}" -o ${tarFileName}
tar -zxf ${tarFileName}

'''
    }

    static Object defaultLogRotatorValues() {
        return {
            daysToKeep 25
            numToKeep 20
        }
    }

    static Object exportParametersProperties() {
        return {
            filePath('parameters')
            fileFormat('properties')
            keyPattern('')
            useRegexp(false)
        }
    }

    static String defaultJobDescription(String jobNameBeingGenerated, String jobDescription) {
        String defaultJobDescriptionHeader = '''<h3>Job Description</h3>
'''
        String defaultJobDescriptionFooter = '''<br>
<br>
<b> Job developed and maintained by Bumblebee : </b> <a href="mailto:BumbleBee.ENM@tcs.com?Subject=''' + jobNameBeingGenerated + '''%20Job" target="_top">Send Mail</a> to provide feedback'''
        return defaultJobDescriptionHeader + jobDescription + defaultJobDescriptionFooter
    }

    static String defaultJobDescriptionOtherTeams(String jobNameBeingGenerated, String jobDescription) {
        String defaultJobDescriptionHeader = '''<h3>Job Description</h3>
'''
        String defaultJobDescriptionFooter = '''<br>
<br>'''
        return defaultJobDescriptionHeader + jobDescription + defaultJobDescriptionFooter
    }

}
