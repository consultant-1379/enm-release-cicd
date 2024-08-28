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

def pipelineBeingGeneratedName = "cENM_Restore_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This job is used to take restore of a cENM environment.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('STC_SLAVE', "${STC_OPENSTACK_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.backupName())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.mtUtilsVersion())
        choiceParam('stage_area', ['ci_internal','drop'] , 'Determine whether to download charts from ci_internal or drop')
        choiceParam('deployment_mechanism', ['charts','csar'] , 'Deployment Mechanism to determine whether to deploy using charts or csar')
        stringParam(commonParams.drop())
        choiceParam('is_dit_versioning_applied', ['False', 'True'], 'Select True if the new DIT versioning is applied on the Deployment.')
        choiceParam('integration_value_type', ['eric-enm-integration-production-values', 'eric-enm-integration-extra-large-production-values', 'eric-enm-multi-instance-functional-integration-values', 'eric-enm-single-instance-production-integration-values' , 'eric-enm-integration-functional-test-values', 'eric-enm-integration-openstack-core-values'], '''<b>
Select the type of integration values type to be used.<br>
NOTE : CSAR supports only eric-enm-integration-production-values and eric-enm-integration-extra-large-production-values.<br>
If you choose deployment mechanism as CSAR please select the value as eric-enm-integration-production-values or eric-enm-integration-extra-large-production-values only.
</b>'''
        )
        choiceParam('Artifact_type', ['csar_lite', 'csar', 'snapshot_csar_lite'], 'This parameter acts as the flow controller to select the deployment mechanism either csar or csar-lite.')
        choiceParam('use_case', ['Upgrade'], 'Select the appropriate operation to be done.')
        choiceParam('deployment_size', ['small-production', 'extra-large-production', 'single-instance', 'multi-instance'], 'Select the type of integration values type to be used.')
        choiceParam('orchestration_type', ['helm', 'EVNFM'], 'Provide the orchestration type with which deployment should be happen.')
        choiceParam('container_registry_type', ['other', 'EVNFM'], 'Select docker registry which is to be used while the deployment time, when orchestration_type is EVNFM is selected. By default other docker registry will be used.')
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_restore/Jenkinsfile")
        }
    }
}
