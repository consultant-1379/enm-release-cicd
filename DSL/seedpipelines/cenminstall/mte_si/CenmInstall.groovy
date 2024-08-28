import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "MTE_SI_cENM_Initial_Install_Functional_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job is used to orchestrate the install of ENM on a cENM SI environment.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.sendConfidenceLevel())
        choiceParam('deployment_mechanism', ['charts', 'csar'], 'Determine whether to deploy via charts or csar')
        choiceParam('integration_value_type', ['eric-enm-integration-production-values', 'eric-enm-integration-extra-large-production-values', 'eric-enm-multi-instance-functional-integration-values', 'eric-enm-single-instance-production-integration-values' , 'eric-enm-integration-functional-test-values', 'eric-enm-integration-openstack-core-values'], '''<b>
Select the type of integration values type to be used.<br>
NOTE : CSAR supports only eric-enm-integration-production-values and eric-enm-integration-extra-large-production-values.<br>
If you choose deployment mechanism as CSAR please select the value as eric-enm-integration-production-values or eric-enm-integration-extra-large-production-values only.
</b>'''
        )
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_install/mte_si/Jenkinsfile")
        }
    }
}