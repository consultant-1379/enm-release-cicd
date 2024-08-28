import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonPipelineGenerationParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonPipelineGenerationParameters commonParams = new CommonPipelineGenerationParameters()

def pipelineBeingGeneratedName = "SPINOFF_cENM_Initial_Install_Pipeline_Generator"

pipelineJob(pipelineBeingGeneratedName) {

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to generate an initial cENM II pipeline.</p>'''))

    parameters {
        stringParam(commonParams.regularSlaves())
        stringParam(commonParams.teDockerSlaves())
        stringParam(commonParams.stcTeDockerSlaves())
        stringParam(commonParams.openstackSlaves())
        stringParam(commonParams.stcOpenstackSlaves())
        stringParam(commonParams.teamEmail())
        stringParam(commonParams.permissionGroups())
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
            scriptPath("pipeline_generation/cenm_install_deployment_setup/pipeline_generator/spinoff/Jenkinsfile")
        }
    }
}
