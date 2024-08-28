import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "SPINOFF_cENM_Install_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup cENM environments after an II.</p>
<ul>
  <li>It copies Bumblebee's Utils repo to the Environment</li>
  <li>It Setups Workload VM</li>
  <li>It Setups DDC</li>
  <li>It deploys NSS Testware Utilities</li>
  <li>It Add Licenses</li>
  <li>It Updates Trust Profiles</li>
  <li>It performs Mediations Restart</li>
  <li>It Adds Prepopulated nodes for RFA250</li>
</ul>'''))

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.nssUtilsVersion())
        stringParam(commonParams.torUtilsVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(['deployment_type', '', 'ENM flavor of environment being installed'])
        choiceParam('deployment_mechanism', ['charts', 'csar'], 'Determine whether to deploy via charts or csar')
        stringParam(commonParams.simdepRelease())
        choiceParam('nodesCleanUp', ['YES', 'NO'], 'Customers should set this parameter to YES only if NSS 15K network or 2K network is on the servers linked to ENM')
        stringParam(commonParams.centralCsvVersion())
        choiceParam('is_dit_versioning_applied', ['True', 'False'], 'Select True if the new DIT versioning is applied on the Deployment.')
        stringParam(commonParams.publishToBuildlog())
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
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_install_deployment_setup/spinoff/Jenkinsfile")
        }
    }
}
