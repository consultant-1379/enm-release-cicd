import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_Physical_Secondary_Site_Setup"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroupIncludingConfigure("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the setup script for handling the Geo-Secondary Site setup.</p>
<ul>
  <li>Transfers datasets and the .crypt directory from the wlvm to scp-1-scripting</li>
  <li>Sets up geo_sys_user and geo_op_user on scp-1-scripting</li>
  <li>Performs the Geo Secondary Site setup steps</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('SLAVE', "${PIPELINE_SLAVE}", 'Slave to run GEO Secondary Site Setup')
        stringParam('non_critical_data_list', "default" , '''<b>
  Please Enter the value for non_critical_data_list
    <ul>
      <li>default - It will keep the default configuration of non_critical_data_list as in the template</li>
      <li>Other Options which can be given other than default : "idam, fm_alarm_history, enm_logs, service_config_params, vnflcm , pm, collections_and_savedsearches, shm, new_feature_value"</li>
    </ul>
</b>''')
    }

    logRotator(commonSteps.defaultLogRotatorValues())

    concurrentBuild(allowConcurrentBuild = false)

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
            scriptPath("Jenkinsfiles/child_jobs/geo_physical_secondary_site_setup/Jenkinsfile")
        }
    }
}
