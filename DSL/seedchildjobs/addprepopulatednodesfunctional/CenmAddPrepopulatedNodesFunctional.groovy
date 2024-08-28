import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_Add_Prepopulated_Nodes_Functional"

pipelineJob(jobBeingGeneratedName) {
    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job is used to prepopulate nodes in preparation for functional tests on cENM environments.</p>'''))

    logRotator(commonSteps.defaultLogRotatorValues())

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves the job will run on.')
        stringParam(commonParams.testPhase())
        stringParam(commonParams.clusterId())
        stringParam(commonParams.drop())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.centralCsvVersion())
        stringParam(commonParams.productSetVersion())
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
            scriptPath("Jenkinsfiles/child_jobs/add_prepopulated_network_functional/Jenkinsfile")
        }
    }
}
