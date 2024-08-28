import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "SPINOFF_cENM_Install_Acceptance_Tests_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to perform acceptance tests after an II.</p>
<ul>
  <li>It performs a check that the UI Launcher is accessible</li>
</ul>'''))
    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.tafVersion())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        choiceParam('deployment_mechanism', ['charts', 'csar'], 'Determine whether to deploy via charts or csar')
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_install_acceptance_tests/spinoff/Jenkinsfile")
        }
    }
}
