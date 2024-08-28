import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Update_Overall_Status_To_Build_Log"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This job communicates with the cENM CI Portal Build log to update overall status for build_log entry via REST.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('deployment', '', 'The deployment name to run the job against')
        stringParam(commonParams.testPhase())
        choiceParam('usecase', ['cENM-Deploy-II-Charts', 'cENM-Deploy-II-CSAR', 'cENM-Deploy-UG-Charts',
                                'cENM-Deploy-UG-CSAR', 'cENM-Deploy-II-Charts-SI', 'cENM-Deploy-Rollback-Charts',
                                'cENM-Deploy-Rollback-CSAR'], 'The name of the confidence level i.e., the primary usecase (II/UG/ROLLBACK) to send to the cENM build log')
        stringParam('drop', '', 'The drop in which you want to update the overall status in Build log')
        stringParam('to_ps', '', 'cENM Product Set Version that the cENM test environment is going to be deployed to')
        choiceParam('overall_status', ['in_progress', 'failed', 'passed'], '''The overall status of the confidence level.<br>
Please Select the overall status to which you want to override in the Build Log.''')
        stringParam(commonParams.mtUtilsVersion())
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves the job will run on.')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_update_overall_status_to_build_log/Jenkinsfile")
        }
    }
}
