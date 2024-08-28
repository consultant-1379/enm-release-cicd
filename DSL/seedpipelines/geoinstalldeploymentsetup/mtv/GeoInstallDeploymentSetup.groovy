import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_Physical_Deployment_Setup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This Job is used to setup specified deployment after an II.</p>
<ul>
  <li>It Copies Utils repo to the Environment</li>
  <li>It Copies Public keys to the Environment</li>
  <li>Triggers Physical_Secure_LMS Job</li>
  <li>Triggers Physical_Secure_WLVM Job</li>
  <li>It Setups Netsims</li>
  <li>It Updates Netsim info in EMT</li>
  <li>It Setups Workload VM</li>
  <li>It Setups DDC</li>
  <li>It Applys PIB Configuration</li>
  <li>It Add License</li>
  <li>It Upgrades Trust Profiles</li>
  <li>It Updates OMBS Software</li>
  <li>It Deploys NSS Testware</li>
  <li>It Deploys APT Tool</li>
  <li>It Copies RVB TestScripts</li>
  <li>It Setups LCS tool on MS</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.drop())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.clusterId())
        stringParam(commonParams.simdepRelease())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.aptVersion())
        stringParam(commonParams.nssUtilsVersion())
        stringParam('non_critical_data_list', "default" , '''<b>
  Please Enter the value for non_critical_data_list
    <ul>
      <li>default - It will keep the default configuration of non_critical_data_list as in the template</li>
      <li>Other Options which can be given other than default : "idam, fm_alarm_history, enm_logs, service_config_params, vnflcm , pm, collections_and_savedsearches, shm, new_feature_value"</li>
    </ul>
</b>''')
        stringParam('SLAVE', "${PIPELINE_SLAVE}", 'Slave to run pipeline against')
        choiceParam('nodesCleanUp', ['NO', 'YES'], '''<h1 style="color:red;">Customers should set this parameter to YES only if NSS 15K network is on the servers linked to ENM</h1>''')
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
            scriptPath("Jenkinsfiles/parent_pipelines/geo_install_deployment_setup/Jenkinsfile")
        }
    }
}
