import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "MTV_Run_Workload_Assertions"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job runs the KPI and Workload Profile assertions.</p>
<ul>
  <li>Can restrict assertions by using restrict_to_features parameter.</li>
  <li>Can exclude certain assertions by using features_to_exclude parameter.</li>
  <li>Runs assertions on the deployment's wlvm</li>
  <li>Generates an Allure report containing the results of the executed assertions</li>
  <li>Job Parameter "jenkins_tag" is a flag value which is sent to APT portal to differentiate assertions on the APT Portal</li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.testPhase())
        stringParam(commonParams.clusterId())
        choiceParam('is_environment_running_robustness_load', ['false', 'true'], '''Select true if the environment is running with robustness load.<br>
If this flag is selected as "true", then all the assertions tagged as ROBUST will be triggered and rest all assertions will be skipped.<br>
If this flag is selected as "false" all assertions would be executed including the assertions tagged as ROBUST; but they will pick the old thresholds (not the new thresholds defined in TDM).<br>
<a href="https://taf-tdm-prod.seli.wh.rnd.internal.ericsson.com/#/contexts/55d4b82e-e9e1-11e5-9ce9-5e5517507c66/datasources/62432083a7b11b0001fa95b6">TDM LINK</a>
''')
        stringParam(commonParams.enmIsoVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.drop())
        stringParam(commonParams.mtUtilsVersion())
        stringParam('start_time', '', '''Start time of assertion period<br>
Example value 2016-02-24 09:30:00''')
        stringParam('end_time', '', '''End time of assertion period<br>
Example value 2016-02-25 09:30:00''')
        stringParam(commonParams.emailRecipients())
        stringParam(commonParams.restrictToFeatures())
        stringParam(commonParams.featuresToExclude())
        booleanParam('copy_to_ftp', true, 'Will copy the generated report to the APT ftp server: https://apt.seli.wh.rnd.internal.ericsson.com/reports')
        stringParam('jenkins_tag', 'MTV-FCAPS', 'APT flag to report in Portal')
        booleanParam('exclude_upgrade', true,
            'Select this to perform an upgrade check, during which the assertion start time will be changed to the upgrade end time, if upgrade is detected during the assertion period')
        stringParam('apt_config_filename', '', '''APT configuration filename to be applied<br>
The filename specified should then exist in the location "/tmp/assertion_configurations/<filename>"<br>
If no filename is entered then APT will run without a config file''')
        stringParam('SLAVE', "${TE_DOCKER_SLAVES}", 'Slave to run workload assertions.')
        stringParam('apt_utils_version', 'RELEASE', 'This is the version of the repo available in Nexus')
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
            scriptPath("Jenkinsfiles/child_jobs/mtv_run_workload_assertions/Jenkinsfile")
        }
    }
}
