import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "GEO_cENM_Primary_Site_Setup"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroupIncludingConfigure("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the setup script for handling the Geo-cENM-Primary Site setup.</p>
<ul>
  <li>Cleans up the required directories scripting pod</li>
  <li>Sets up geo_sys_user and geo_op_user</li>
  <li>Performs the Geo cENM Primary Site setup steps</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.clusterId())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run GEO Primary Site Setup')
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
            scriptPath("Jenkinsfiles/child_jobs/geo_cenm_primary_site_setup/Jenkinsfile")
        }
    }
}
