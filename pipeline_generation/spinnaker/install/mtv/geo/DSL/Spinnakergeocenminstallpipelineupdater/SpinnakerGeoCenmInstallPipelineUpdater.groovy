import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonPipelineGenerationParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonPipelineGenerationParameters commonParams = new CommonPipelineGenerationParameters()

def pipelineBeingGeneratedName = "Geo_cENM_Spinnaker_Install_Pipeline_Updater"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to update an already generated spinnaker Geo cENM Secondary pipeline based on the changes to the master branch on gerrit.</p>'''))

    parameters {
        stringParam(commonParams.openstackSlaves("${OPENSTACK_SLAVES}"))
        stringParam(commonParams.teDockerSlaves("${TE_DOCKER_SLAVES}"))
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
                        }
                        extensions {
                             cleanBeforeCheckout()
                             localBranch 'master'
                        }
                }
            }
            scriptPath("pipeline_generation/spinnaker/install/mtv/geo/pipeline_updater/Jenkinsfile")
        }
    }
}