import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonPipelineGenerationParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonPipelineGenerationParameters commonParams = new CommonPipelineGenerationParameters()

def pipelineBeingGeneratedName = "cENM_Spinnaker_cENM_CBRS_Intg_Upgrade_Pipeline_Updater"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to update an already generated spinnaker cENM CBRS Integrated Upgrade pipeline based on the changes to the master branch on gerrit.</p>'''))

    parameters {
        stringParam(commonParams.openstackSlaves("${OPENSTACK_SLAVES}"))
        stringParam(commonParams.regularSlaves("${REGULAR_SLAVES}"))
        stringParam(commonParams.teamEmail("${TEAM_EMAIL}"))
        stringParam(commonParams.permissionGroups("${PERMISSION_GROUPS}"))
    }

    logRotator(commonSteps.defaultLogRotatorValues())

    triggers {
        gerrit {
            project("OSS/com.ericsson.oss.ci.rtd/enm-release-cicd", "master")
            events {
                changeMerged()
            }
        }
    }

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
            scriptPath("pipeline_generation/spinnaker/integrated_cbrs_upgrade/cdl/pipeline_updater/Jenkinsfile")
        }
    }
}