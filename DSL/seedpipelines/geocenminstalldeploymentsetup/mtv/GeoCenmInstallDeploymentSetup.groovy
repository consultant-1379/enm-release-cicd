import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_cENM_Install_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup cENM environments after an II.</p>
<ul>
  <li>It Copies Bumblebee's Utils repo to the Environment</li>
  <li>It Setups Workload VM</li>
  <li>It Setups DDC</li>
  <li>It Deploys NSS Testware Utilities</li>
  <li>It Add Licenses</li>
  <li>It Updates Trust Profiles</li>
  <li>It Deploys APT</li>
  <li>It copies RVB TestScripts repo to the Environment</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('STC_SLAVE', "", 'Slave to run pipeline against if the network type is STC')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.nssUtilsVersion())
        stringParam(commonParams.torUtilsVersion())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(['deployment_type', '', 'ENM flavor of environment being installed'])
        stringParam(commonParams.centralCsvVersion())
        stringParam(commonParams.simdepRelease())
        stringParam(commonParams.aptVersion())
        choiceParam('nodesCleanUp', ['YES', 'NO'], 'Customers should set this parameter to YES only if NSS 15K network or 2K network is on the servers linked to ENM')
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
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
            scriptPath("Jenkinsfiles/parent_pipelines/geo_cenm_install_deployment_setup/mtv/geo/Jenkinsfile")
        }
    }
}
