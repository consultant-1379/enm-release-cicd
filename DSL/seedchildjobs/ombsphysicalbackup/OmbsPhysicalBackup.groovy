import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "Physical_OMBS_Backup"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the OMBS Backup for a physical server.</p>
<ul>
  <li>Physical OMBS Policy update can be selected with trigger_ombs_policy_upgrade parameter.</li>
  <li>The OMBS Server details must be present on the utils repo</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = false)

    parameters {
        stringParam(commonParams.clusterId())
        stringParam(commonParams.testPhase())
        choiceParam('trigger_ombs_policy_upgrade', ['true', 'false'], '''Select true if the ombs policy update is required.<br>
Select false if the ombs policy update is not required.
''')
        stringParam(commonParams.mtUtilsVersion())
        stringParam('deploy_phase', '', 'Enter the deploy phase')
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
            scriptPath("Jenkinsfiles/child_jobs/ombs_physical_backup_setup/Jenkinsfile")
        }
    }
}
