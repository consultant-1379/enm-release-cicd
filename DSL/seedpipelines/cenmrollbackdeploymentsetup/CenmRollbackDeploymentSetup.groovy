import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Rollback_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup deployment flow after cENM Rollback.</p>
<ul>
  <li>It performs Push Data to DDP</li>
  <li>It checks UI Launcher Link Verification</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('STC_SLAVE', "${STC_OPENSTACK_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.sendConfidenceLevel())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        choiceParam('deployment_mechanism', ['charts','csar'] , 'Deployment Mechanism to determine whether to deploy using charts or csar')
        stringParam('from_ps', '', 'cENM Product Set Version that the cENM test environment is currently deployed to')
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.tafVersion())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_rollback_deployment_setup/Jenkinsfile")
        }
    }
}
