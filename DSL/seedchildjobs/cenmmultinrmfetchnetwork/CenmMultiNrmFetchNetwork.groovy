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

def pipelineBeingGeneratedName = "cENM_Multi_NRM_Fetch_Network"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This job is used to fetch the network for load and functional testing, for deployments that have multiple NRMs(2K+5K) rolled out... </p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.forceArneXmlCreation())
        stringParam(commonParams.multiNrm())
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_multi_nrm_fetch_network/Jenkinsfile")
        }
    }
}