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

def pipelineBeingGeneratedName = "CAPO_Spinnaker_cENM_Pipeline_Upgrade_Updater"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This job is used to update cENM CAPO Upgrade spinnaker pipeline</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${TE_DOCKER_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.spinnakerPipelineName('fem 4s11: cENM CAPO Upgrade Pipeline'))
        stringParam(commonParams.spinnakerApplicationName(commonConstants.E2E_SPINNAKER_APPLICATION_NAME))
        stringParam(commonParams.femControllerName(commonConstants.MAINTRACK_SPINNAKER_FEM_CONTROLLER))
        stringParam(commonParams.syncTriggerFemControllerName(commonConstants.FEM16S11_SPINNAKER_FEM_CONTROLLER))
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
            scriptPath("Jenkinsfiles/spinnaker_pipelines/cenm/capo_upgrade/spinnaker_cenm_capo_upgrade_pipeline_updater/Jenkinsfile")
        }
    }
}
