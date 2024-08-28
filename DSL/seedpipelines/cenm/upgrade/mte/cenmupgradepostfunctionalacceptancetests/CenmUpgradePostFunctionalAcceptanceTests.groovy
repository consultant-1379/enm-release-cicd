import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "MTE_cENM_Upgrade_Post_Functional_Acceptance_Test_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job orchestrates the post tasks of the functional feature acceptance tests.</p>
<ul>
  <li>It cleans up the environment after RFA250</li>
  <li>It kicks off reruns of failed RFA250 suites</li>
  <li>It then pushes data to DDP</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run upgrade post functional acceptance test flow against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.drop())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.tafSchedulerVersion())
        stringParam(commonParams.centralCsvVersion())
        stringParam(commonParams.tafVersion())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        stringParam(commonParams.sendConfidenceLevel())
        choiceParam('trigger_non_functional_flow', ['False', 'True'], '''Set to True if you would like to trigger Non Functional loop<br>
Set to False if you would like to trigger Functional loop.''')
        choiceParam('trigger_RFA_250_staging', ['False', 'True'], 'Select true if RFA_250 Staging job to be triggered')
        stringParam(['allure_report_url', '', '''URL of the allure report of the failing RFA250 test(s)<br>
<b>e.g. https://oss-taf-logs.seli.wh.rnd.internal.ericsson.com/af58fe70-9f63-4b20-a7c2-d218042a03b0</b><br>'''])
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm/upgrade/mte/cenm_upgrade_post_functional_acceptance_tests/Jenkinsfile")
        }
    }
}
