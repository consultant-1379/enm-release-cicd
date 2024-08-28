import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Send_Confidence_Level_To_Build_Log_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This Job is a pipeline job. Please do not trigger this job Manually</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('deployment', '', '	The deployment name to run the job against')
        stringParam(commonParams.testPhase())
        choiceParam('usecase', ['cENM-Deploy-II-Charts', 'cENM-Deploy-II-CSAR', 'cENM-Deploy-UG-Charts',
                                'cENM-Deploy-UG-CSAR', 'cENM-Deploy-II-Charts-SI', 'cENM-Deploy-Rollback-Charts',
                                'cENM-Deploy-Rollback-CSAR'], 'The name of the confidence level i.e., the primary usecase (II/UG/ROLLBACK) to send to the cENM build log')
        choiceParam('usecase_status', ['in_progress', 'failed', 'passed'], 'The status of the confidence level i.e., the primary usecase (II/UG/ROLLBACK) to send to the cENM build log')
        stringParam(commonParams.drop())
        stringParam('from_ps', '', 'cENM Product Set Version that the cENM test environment is currently deployed to')
        stringParam('to_ps', '', 'cENM Product Set Version that the cENM test environment is going to be deployed to')
        choiceParam('build_log_action', ['create', 'update'], '''Please select: <br>
                                          1) Create -> If it is a new entry in build log (II/UG/ROLLBACK), <br>
                                          2) Update -> To update an existing entry in build log''')
        choiceParam('confidence_level', ['', 'cENM-RFA', 'cENM-RFA-Rerunner', 'cENM-RFA-Staging', 'cENM-Build-Integration-Charts', 'cENM-Build-CSAR', 'cENM-UI-Link-Launcher',
                                         'cENM-UG-Availability', 'UG-Performance'], 'Please select the Confidence Level which needs to be updated for the select Usecase')
        choiceParam('status', ['in_progress', 'failed', 'passed'], 'The status of the confidence level to send up to the Build log')
        stringParam('job_link', '', 'Please enter the job link')
        stringParam('allure_link', '', 'Please enter the allure link if applicable')
        stringParam('timestamp', '', 'Please enter the timestamp (YYYY-MM-DD HH:MM:SS or in seconds)')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_send_confidence_level_to_build_log_Flow/JenkinsFile")
        }
    }
}
