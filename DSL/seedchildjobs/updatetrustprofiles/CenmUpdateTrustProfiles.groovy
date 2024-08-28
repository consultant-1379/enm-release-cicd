import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_Update_Trust_Profile"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescriptionOtherTeams(jobBeingGeneratedName, '''<p>This job imports external CA certs and updates trust profile on cENM environments.</p>
<p>This is mainly a ENM activity needs to be run after Intial Install and there is no interaction with netsim or netsim nodes.</p>
<h3>For any Trust Profile queries, Please contact Team NSS.</h3>
<h1><a href="https://confluence-nam.lmera.ericsson.se/display/PDUCD/TLS+Solution+on+NSS" color: green;>More Info about TLS solution</a></h1>
'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(['clusterId', '', 'This parameter refers to cluster id of the deployment on which you want to update certficates.'])
            stringParam(commonParams.drop())
            stringParam(commonParams.simdepRelease())
            // TODO Update deployment_type being passed to cloud_native through Jira RTD-13768
            stringParam(['deployment_type', 'Cloud', 'Platform type of environment'])
            choiceParam('nodesCleanUp', ['YES', 'NO'], 'Customers should set this parameter to YES only if NSS 15K network is on the servers linked to ENM')
            stringParam(commonParams.mtUtilsVersion())
        }

        label("${OPENSTACK_SLAVES}")
        wrappers {
            preBuildCleanup()
            timestamps()
            environmentVariables {
                env('TRUST_PROFILE_LOG', '"/var/tmp/trustProfile.log"')
            }
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''sh MTELoopScripts/pipeline_scripts/cenm_enm_gui_parameter.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/parameters.properties")
            propertiesContent("")
        }

        buildDescription('', '<br>Cluster ID = ${clusterId} <br>  Simdep Version = ${simdep_release} <br> Utils Version = ${MT_utils_version}</b>')

        shell('''echo "###########################################################################################"

#!/bin/bash

URL="https://arm901-eiffel004.athtem.eei.ericsson.se:8443/nexus/content/repositories/nss-releases/com/ericsson/nss/NSS_UpdateCertsOnENM/1.0.1/NSS_UpdateCertsOnENM-1.0.1.sh"

status=$(curl --write-out %{http_code} --silent --output /dev/null ${URL} || exit_code=1)
if [[ ${status} -eq 200 ]] ; then
    wget -O NSS_UpdateCertsOnENM.sh https://arm901-eiffel004.athtem.eei.ericsson.se:8443/nexus/content/repositories/nss-releases/com/ericsson/nss/NSS_UpdateCertsOnENM/1.0.1/NSS_UpdateCertsOnENM-1.0.1.sh
    sh NSS_UpdateCertsOnENM.sh
else
    echo "ARM901 not reachable Enableing Failover to ARM1S11"
    wget -O NSS_UpdateCertsOnENM.sh https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/nss-repositories/com/ericsson/nss/NSS_UpdateCertsOnENM/1.0.1/NSS_UpdateCertsOnENM-1.0.1.sh
    sh NSS_UpdateCertsOnENM.sh
fi''')
}
}