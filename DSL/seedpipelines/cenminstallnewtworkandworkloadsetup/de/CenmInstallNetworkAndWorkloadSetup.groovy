import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "DE_cENM_NonFunctional_Network_and_Workload_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job is used to set up the network and workload on a nonfunctional cloud native environment.</p>'))

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run non functional network and workload setup flow against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.drop())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.nodesToAddToEnm())
        stringParam(commonParams.nodesToAddToWorkpool())
        stringParam(commonParams.workloadProfiles())
        stringParam(commonParams.supervisionTypes())
        stringParam(commonParams.forceArneXmlCreation())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_install_network_and_workload_setup/de/Jenkinsfile")
        }
    }
}

