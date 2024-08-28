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

def pipelineBeingGeneratedName = "RNL_cENM_Upgrade_Functional_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job is used to orchestrate an upgrade of ENM on a cENM deployment in the Real Node Loop.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        choiceParam('deployment_mechanism', ['csar', 'charts'], 'Deployment Mechanism to determine whether to deploy using charts or csar')
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.sendConfidenceLevel())
        stringParam(commonParams.mtUtilsVersion())
        choiceParam('integration_value_type', ['eric-enm-integration-production-values', 'eric-enm-integration-extra-large-production-values', 'eric-enm-multi-instance-functional-integration-values', 'eric-enm-single-instance-production-integration-values' , 'eric-enm-integration-functional-test-values', 'eric-enm-integration-openstack-core-values'], '''<b>
Select the type of integration values type to be used.<br>
NOTE : CSAR supports only eric-enm-integration-production-values and eric-enm-integration-extra-large-production-values.<br>
If you choose deployment mechanism as CSAR please select the value as eric-enm-integration-production-values or eric-enm-integration-extra-large-production-values only.
</b>'''
        )
        stringParam(commonParams.drop())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.productSetVersion())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the  Deployment.')
        choiceParam('Artifact_type', ['csar_lite', 'csar', 'snapshot_csar_lite'], 'This parameter acts as the flow controller to select the deployment mechanism either csar or csar-lite.')
        choiceParam('use_case', ['Upgrade'], 'Select the appropriate operation to be done.')
        choiceParam('deployment_size', ['small-production', 'extra-large-production', 'single-instance', 'multi-instance'], 'Select the type of integration values type to be used.')
        choiceParam('orchestration_type', ['helm', 'EVNFM'], 'Provide the orchestration type with which deployment should be happen.')
        choiceParam('container_registry_type', ['other', 'EVNFM'], 'Select docker registry which is to be used while the deployment time, when orchestration_type is EVNFM is selected. By default other docker registry will be used.')
        choiceParam('scope', ['ROLLBACK', 'DEFAULT'], 'This parameter needs to be passed when use_csae is selected as Backup.')
        stringParam(commonParams.backupName())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm/upgrade/rnl/cenm_upgrade/Jenkinsfile")
        }
    }
}