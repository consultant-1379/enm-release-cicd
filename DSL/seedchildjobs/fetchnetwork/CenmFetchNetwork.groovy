import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Fetch_Network"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '''<p>This job is used to fetch the network for load testing.</p>
<p>Please note:</p>
<ul>
  <li>This job may pass even though not all files were fetched from the netsims</li>
  <li>This is expected behavior  as the netsim tool seems to throw a lot of errors and warnings making it impossible to know if it actually failed or not.</li>
  <li>Its been therefore agreed with Maintrack, that we would log any error with a message to investigate but that we would not be able to actually fail the job.</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves  the job will run on.')
        stringParam('STC_SLAVE', "${STC_TE_DOCKER_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.forceArneXmlCreation())
        stringParam(commonParams.testPhase())
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
            scriptPath("Jenkinsfiles/child_jobs/fetch_network/Jenkinsfile")
        }
    }
}

