import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_cENM_Secondary_Site_Setup"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroupIncludingConfigure("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the setup script for handling the Geo-Secondary Site setup on cENM.</p>
<ul>
  <li>Transfers datasets and the .crypt directory from the wlvm to general-scripting-0</li>
  <li>Sets up geo_sys_user and geo_op_user on general-scripting pod</li>
  <li>Performs the Geo Secondary Site setup steps</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run GEO Secondary Site Setup')
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
            scriptPath("Jenkinsfiles/parent_pipelines/geo_cenm_secondary_site_setup/Jenkinsfile")
        }
    }
}