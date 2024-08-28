import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def pipelineBeingGeneratedName = "cENM_RFA_250_LCS_log_collection"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName,
        '''<p>This job is used to collect the LCS logs for the failed RFA250 test suites..</p>
<ul>
  <li>Takes in an allure report as input.</li>
  <li>It will take the yaml associated with the RFA 250 test suites from the kvm repo - <a href="https://gerrit.ericsson.se/gitweb?p=OSS/com.ericsson.cifwk.taf/taf_scheduler_kvm.git;a=blob;f=src/main/resources/lcs/cenm/lcs_suites_to_features_mapping.json;h=24c80107b57ee6308256876184169908498499cf;hb=HEAD" target="_top">taf_kvm_repo</a> </li>
  <li>It will collect all the logs w.r.t yaml's list associated with each failed suite</li>
  <li>The failed logs can be found: <a href="http://10.45.207.96/ENMlogs/RFA250_LCSLogs/" target="_top">LCS LOGS PATH</a></li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam(commonParams.clusterId())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.productSetVersion())
        stringParam(commonParams.testPhase())
        stringParam(commonParams.mtUtilsVersion())
        stringParam(commonParams.drop())
        stringParam(['allure_report_url', '', '''URL of the allure report of the failing RFA250 test(s)<br>
<b>e.g. https://oss-taf-logs.seli.wh.rnd.internal.ericsson.com/af58fe70-9f63-4b20-a7c2-d218042a03b0</b><br>'''])
        choiceParam('debug_timeout', ['0', '300', '600', '1200', '1800', '2700'], '''Timeout is in seconds. Default time out 0 - This means debug will not be enable<br>
Any timeout other than 0 will set the Debug/trace levels in JBoss. You need to reproduce the issue within the selected timeout.''')
        stringParam('remote_tar_location', '/ericsson/enm/dumps/lcs/', 'location for tar file in cluster')
        stringParam('ftp_upload_base_path', '/proj/ENMLoggingServer/web/ENMlogs/RFA250_LCSLogs', 'location to store/copy the logs on ftp server')
        stringParam('ftp_server_ip', '10.45.207.96', '')
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
            scriptPath("Jenkinsfiles/child_jobs/cenm_rfa250_lcs_log_collection/Jenkinsfile")
        }
    }
}