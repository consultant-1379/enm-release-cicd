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

def pipelineBeingGeneratedName = "Spinnaker_cENM_Restore_Backup_Pipeline_Updater"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This job is used to update cENM Restore Backup spinnaker pipeline</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${OPENSTACK_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.spinnakerPipelineName('fem 4s11: cENM_Restore_Backup Pipeline'))
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
            scriptPath("Jenkinsfiles/spinnaker_pipelines/cenm/restore_backup/spinnaker_cenm_pipeline_restore_backup_updater/Jenkinsfile")
        }
    }
}