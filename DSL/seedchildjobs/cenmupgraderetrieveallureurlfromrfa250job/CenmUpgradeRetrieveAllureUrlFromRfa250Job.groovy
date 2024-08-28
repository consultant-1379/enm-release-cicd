import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Upgrade_Retrieve_Allure_URL_From_RFA250_Job"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This job takes in a URL from a job that produced artifacts with the an allure url. Then it retrieves that url from the job and produces new artifacts with that URL.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('rfa250_url', '', 'The URL of the RFA250 that produced artifacts with the allure URL.')
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.clusterId())
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_upgrade_retrieve_allure_url_from_rfa250_job/Jenkinsfile")
        }
    }
}
