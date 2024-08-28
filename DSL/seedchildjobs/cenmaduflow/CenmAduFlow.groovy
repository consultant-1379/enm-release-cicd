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

def pipelineBeingGeneratedName = "cENM_Adu_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This Job orchestrates the availability of ENM during upgrade on a cloud native based platform</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.sendConfidenceLevel())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.tafVersion())
        stringParam(commonParams.aduVersion())
        stringParam(commonParams.availabilityWatcherVersion())
        stringParam(commonParams.deploymentMechanism())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        choiceParam('cENM_Upgrade_Type', ['ENM', 'CCD'], '''<ul><li>ENM - Use "ENM" for enm deployment upgrades</li>
    <li>CCD - Use "CCD" for platform upgrades</li></ul>''')
        choiceParam('CCD_ADU_Duration_Selector', ['0', '1', '2', '3', '4', '5', '6', '7', '8'], 'ADU Duration Selector(in Hrs) This parameter is used only for CCD Upgrades')
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
        choiceParam('adu_coverage_selector', ['infra', 'infra_apps'], 'Select infra_apps if the pipeline will run with full coverage')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_adu_flow/Jenkinsfile")
        }
    }
}