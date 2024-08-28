import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonPipelineGenerationParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonPipelineGenerationParameters commonParams = new CommonPipelineGenerationParameters()

def pipelineBeingGeneratedName = "cENM_Spinnaker_cENM_CBRS_Intg_Upgrade_Pipeline_Generator"

pipelineJob(pipelineBeingGeneratedName) {

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to generate an initial spinnaker cENM CBRS Integrated Upgrade pipeline.</p>'''))

    parameters {
        stringParam(commonParams.openstackSlaves(""))
        stringParam(commonParams.regularSlaves(""))
        stringParam(commonParams.teamEmail(""))
        stringParam(commonParams.permissionGroups(""))
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
            scriptPath("pipeline_generation/spinnaker/integrated_cbrs_upgrade/cdl/pipeline_generator/Jenkinsfile")
        }
    }
}