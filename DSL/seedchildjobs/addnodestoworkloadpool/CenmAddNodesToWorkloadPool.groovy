import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Add_Nodes_To_Workload_Pool"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '<p>This job is used to add nodes to the workload pool.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves the job will run on.')
        stringParam('STC_SLAVE', "${STC_TE_DOCKER_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.nodesToAddToWorkpool())
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
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
            scriptPath("Jenkinsfiles/child_jobs/add_nodes_to_workload_pool/Jenkinsfile")
        }
    }
}

