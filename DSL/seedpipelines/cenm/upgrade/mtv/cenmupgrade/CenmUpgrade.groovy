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

def pipelineBeingGeneratedName = "MTV_cENM_Upgrade_NonFunctional_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job is used to orchestrate the upgrade of ENM on a cENM environment.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('STC_SLAVE', "${STC_OPENSTACK_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        choiceParam('deployment_mechanism', ['csar', 'charts'], 'Deployment Mechanism to determine whether to deploy using charts or csar')
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.sendConfidenceLevel())
        stringParam(commonParams.testPhase())
        choiceParam('integration_value_type', ['eric-enm-integration-production-values', 'eric-enm-integration-extra-large-production-values', 'eric-enm-multi-instance-functional-integration-values', 'eric-enm-single-instance-production-integration-values' , 'eric-enm-integration-functional-test-values', 'eric-enm-integration-openstack-core-values'], '''<b>
Select the type of integration values type to be used.<br>
NOTE : CSAR supports only eric-enm-integration-production-values and eric-enm-integration-extra-large-production-values.<br>
If you choose deployment mechanism as CSAR please select the value as eric-enm-integration-production-values or eric-enm-integration-extra-large-production-values only.
</b>'''
        )
        stringParam(commonParams.backupName())
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
        choiceParam('trigger_adu', ['False','True'] , 'To Trigger Adu job or not (True/False)')
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm/upgrade/mtv/cenm_upgrade/Jenkinsfile")
        }
    }
}
