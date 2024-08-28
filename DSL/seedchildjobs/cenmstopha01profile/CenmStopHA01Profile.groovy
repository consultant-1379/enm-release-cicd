import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonConstants

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonConstants commonConstants = new CommonConstants()

def pipelineBeingGeneratedName = "cENM_MTE_Post_ADU_Cleanup"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job is used to clean up the deployment and stop the workload profile HA_01</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('trigger_ha_01', "stop", 'Stop the HA_01 Workload Profile,Note:This parameter should be given as stop only')
        stringParam('nodes_to_add_to_enm', "ERBS,Router6672,BSC", 'Adds the nodes of node types ERBS,RadioNode,BSC')
        stringParam(commonParams.drop())
        stringParam(commonParams.clusterId())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.centralCsvVersion())
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_stop_ha_01_profile/Jenkinsfile")
        }
    }
}