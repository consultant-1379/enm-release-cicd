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

def pipelineBeingGeneratedName = "cENM_CCD_Upgrade_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '''<p>This job will trigger the CCD Upgrade Spinnaker Pipeline </p>
<p>This job will wait until the Spinnaker pipeline is completed</p>
<ul>
  <p> Link of Spinnaker pipeline to be triggered :</p>
  <p> </p>
<b> Job developed and maintained by Bumblebee : </b> <a href="mailto:BumbleBee.ENM@tcs.com?Subject=cENM_CCD_Upgrade_Flow%20Job" target="_top">Send Mail</a> to provide feedback
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('pipeline_to_run', "", 'This is the Spinnaker pipeline to be triggered in the cn-e2e-enm Spinnaker Application controlled by webhook. Always the default value should be used')
        stringParam('spinnaker_app_name', "", 'This value of Application should not be changed. The value of this is associated with the Spinnaker webhook')
        stringParam('deployment_id', "", 'This value of deployment should be given from DTT')
        choiceParam('trigger_adu', ['False','True'] , 'To Trigger Adu job or not (True/False)')
        stringParam('branch', "master", 'Enter branch name(e.g. */master), tag name (e.g. ccd_pipeline-0.15.0), or commit id (e.g. 9eb0955c8eeb8797cf8ce7878675f02c678ea9b4) here, leave blank for master')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_ccd_upgrade_flow/Jenkinsfile")
        }
    }
}
