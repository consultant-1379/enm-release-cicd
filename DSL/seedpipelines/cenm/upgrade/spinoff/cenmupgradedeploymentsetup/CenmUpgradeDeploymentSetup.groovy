import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "SPINOFF_cENM_Upgrade_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup cENM environments after an upgrade.</p>
<ul>
  <li>It deploys Tor Utilities</li>
  <li>It deploys NSS Testware</li>
  <li>DDC Setup</li>
  <li>Push Data to DDP</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.nssUtilsVersion())
        stringParam(commonParams.torUtilsVersion())
        stringParam(commonParams.cenmProductSetVersion())
        choiceParam('deployment_mechanism', ['csar', 'charts'], 'Determine whether to deploy via charts or csar')
        choiceParam('trigger_non_functional_flow', ['False', 'True'], '''Set to True if you would like to trigger Non Functional loop<br>
Set to False if you would like to trigger Functional loop.''')
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.sendConfidenceLevel())
        stringParam(commonParams.publishToBuildlog())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm/upgrade/spinoff/cenm_upgrade_deployment_setup/Jenkinsfile")
        }
    }
}
