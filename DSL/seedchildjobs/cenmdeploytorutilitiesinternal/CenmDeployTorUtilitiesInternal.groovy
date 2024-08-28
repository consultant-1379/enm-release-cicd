import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Deploy_Tor_Utilities_Internal"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '''<p>This job is used to deploy Tor Utilities on a cloud native environment's WLVM.</p>
<ul>
  <li>Depending on option passed into tor_utils_version, the job will do different things</li>
  <li>Please read the description of tor_utils_version parameter for more information</li>
  <li>At the end of changing Tor Utils versions, it will get a list of all Workload Profiles that need restarting as a result of the Tor Utils version change.</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${OPENSTACK_SLAVES}", 'The list of slaves the job will run on.')
        stringParam('STC_SLAVE', "${STC_TE_DOCKER_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.torUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.productSetVersion())
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
    }

    logRotator(commonSteps.defaultLogRotatorValues())

    definition {
        cpsScm {
            scm {
                git {
                    branch('master')
                        remote {
                            url("\${GERRIT_MIRROR}/OSS/com.ericsson.oss.ci.rtd/enm-release-cicd")
                            credentials("bb_func_user")
                        }
                        extensions {
                             cleanBeforeCheckout()
                             localBranch 'master'
                        }
                }
            }
            scriptPath("Jenkinsfiles/child_jobs/cenm_deploy_tor_utilities_internal/Jenkinsfile")
        }
    }
}
