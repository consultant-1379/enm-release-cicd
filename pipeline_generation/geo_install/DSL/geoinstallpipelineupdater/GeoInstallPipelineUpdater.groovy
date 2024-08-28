import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_Initial_Install_Pipeline_Updater"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to update an already generated II pipeline based on the changes to the master branch on gerrit.</p>'''))

    parameters {
        stringParam('PIPELINE_SLAVE', "${PIPELINE_SLAVE}", 'These are slaves which must be capable of running Jenkinsfiles. Slaves must also be capable of cloning repos from gerrit.')
        stringParam('TEAM_EMAIL', "${TEAM_EMAIL}", 'This is the email of the team who the pipelines are being generated for')
        stringParam('PERMISSION_GROUPS', "${PERMISSION_GROUPS}", 'Group or groups of users who have the ability to run pipeline etc. If left blank, users signed in on the Jenkins instance will have the ability to run jobs.')
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
            scriptPath("pipeline_generation/geo_install/pipeline_updater/Jenkinsfile")
        }
    }
}