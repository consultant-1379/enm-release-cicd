import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Send_Confidence_Level"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This job communicates with the CI Portal to send cENM confidence levels via REST.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('product_set_version', '', 'The cENM product set version to send the confidence levels for.<br>Any confidence levels sent will be mapped to this product set version')
        choiceParam('confidence_level_name', ['cENM-Build-Integration-Charts', 'cENM-RFA', 'cENM-Deploy-II-CSAR-Lite',
                                              'cENM-Deploy-II-Charts', 'cENM-Deploy-II-CSAR', 'cENM-Build-CSAR',
                                              'cENM-Deploy-UG-Charts', 'cENM-UG-Availability', 'cENM-Deploy-UG-CSAR', 'cENM-Deploy-UG-CSAR-Lite',
                                              'cENM-Deploy-II-Charts-SI', 'UG-Performance'], 'The name of the confidence level to send up')
        choiceParam('confidence_level_status', ['in_progress', 'failed', 'passed'], 'The status of the confidence level to send up')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_send_confidence_level/Jenkinsfile")
        }
    }
}
