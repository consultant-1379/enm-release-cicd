import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "MTE_cENM_Upgrade_Non_Functional_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup cENM environments after an upgrade.</p>
<ul>
  <li>It deploys APT Tool</li>
  <li>Check Latest time on DDP</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.aptVersion())
        choiceParam('trigger_non_functional_flow', ['False', 'True'], '''Set to True if you would like to trigger Non Functional loop<br>
Set to False if you would like to trigger Functional loop.''')
        stringParam('ddp_upload_timeout_seconds', '3600', '''The amount of time (in seconds) that the test in this job will loop for until the DDC upload time is greater than the end_time.''')
        stringParam('start_time', '', '''Start time of the upgrade. Example: 2020-02-24 08:30:00''')
        stringParam('end_time', '', '''End time of the upgrade, the latest upload time in DDP must be greater than this before we run the upgrade assertions job. Example: 2020-02-24 10:30:00''')
        stringParam(commonParams.sendConfidenceLevel())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm/upgrade/mte/cenm_upgrade_non_functional_deployment_setup/Jenkinsfile")
        }
    }
}
