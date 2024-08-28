import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_Run_APT_System_Assertions"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the APT System Assertions</p>
        <ul>
          <li>Can exclude certain assertions by using assertions_to_exclude parameter.</li>
          <li>Runs assertions on the deployment's wlvm</li>
          <li>Generates an Allure report containing the results of the executed assertions</li>
          <li>Copies generated Allure report to <a href="https://apt.lmera.ericsson.se/reports/">APT Reports Portal</a></li>
          <li>Job Parameter "jenkins_tag" is a flag value which is sent to APT portal to differentiate assertions on the APT Portal</li>
        </ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.testPhase())
        stringParam(commonParams.clusterId())
        stringParam(commonParams.drop())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.restrictToFeatures())
        stringParam(commonParams.featuresToExclude())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('start_time', '', 'Start time of the assertion period, the latest upload time in DDC must be greater than this before we run the assertions job<br>Example value 2016-02-24 10:30:00')
        stringParam('end_time', '', 'End time of the assertion period, the latest upload time in DDC must be greater than this before we run the assertions job<br>Example value 2016-02-25 10:30:00')
        booleanParam('copy_to_ftp', true, 'Will copy the generated report to the APT ftp server: https://apt.lmera.ericsson.se/reports')
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'The list of slaves the job will run on.')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_run_apt_system_assertions/Jenkinsfile")
        }
    }
}
