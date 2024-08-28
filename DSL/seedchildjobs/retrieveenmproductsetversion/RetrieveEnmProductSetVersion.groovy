import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Retrieve_ENM_Product_Set_Version"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This job communicates with the CI Portal to retrieve the ENM product set version mapped to a cENM product set version.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('product_set_version', '', 'The cENM product set version which will be used to get the mapped ENM product set version.')
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.clusterId())
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves the job will run on.')
        stringParam(commonParams.testPhase())
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_retrieve_enm_product_set_version/Jenkinsfile")
        }
    }
}
