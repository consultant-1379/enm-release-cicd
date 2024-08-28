import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonAuthorization commonAuthorization = new CommonAuthorization()


def pipelineBeingGeneratedName = "GEO_Initial_Install_${cluster_id}_Scheduler"

pipelineJob(pipelineBeingGeneratedName) {

    if("${PERMISSION_GROUPS}") {
        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroupIncludingConfigure("${it}"))
        }
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '''<p>This job is used to kick off the Initial Install and Deployment Setup on the specified Environment</p>

<ul>
  <li>For help in modifying crontab, either write a support ticket on Bumblebee or build cron runtime <a href="https://crontab.guru/">here</a></li>
  <li><strong>Please note the timing's the job runs at time are LMI Athlone time +1 hour</strong></li>
  <li><strong>For example, if you want II to automatically run at 4PM then set the crontab for 5PM</strong></li>
</ul>'''))

    concurrentBuild(allowConcurrentBuild = false)

    parameters {
        stringParam('test_phase', "${test_phase}", 'Test phase being run')
        stringParam('cluster_id', "${cluster_id}", 'The Environment name to run the job against')
        stringParam('mt_utils_version', "${mt_utils_version}", '''This is the version of the scripts available in Nexus<br>
We recommend you always use RELEASE<br>
However, if there is an issue with RELEASE please use one of the versions available <a href = https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/mtg/utils/>here</a>''')
        stringParam('drop', "${drop}", '''The drop in which the product is within<br>
Example: 19.07''')
        stringParam('product_set_version', "${product_set_version}", '''<b>The product set version for your deployment
This should also be a product set contained within the drop specified<br>
Options:
<ul>
  <li>LATEST - Get latest product set version (red or green) in the drop specified above</li>
  <li>INSTALLED - Get product set version deployment is currently on</li>
  <li>GREEN - Get latest green product set version in the drop specified above</li>
  <li>Specific product set version - Can specify product set version e.g. 19.06.99</li>
</ul>
</b>''')
        stringParam('xml', "${deployment_description_xml}", '''Deployment Description XML<br>
See here for parameter format: <a href="https://confluence-nam.lmera.ericsson.se/display/EM/Release+Job+Parameters#ReleaseJobParameters-XML">XML documentation</a>
''')
        stringParam('sed_version', "${sed_version}", '''<b>What SED schema version to use
Options:
<ul>
  <li>AUTO - It will try to resolve to the PS in the ENM ISO</li>
  <li>MASTER - Gets set as master in DMT</li>
  <li>Specific Version - Uses the specific version specified</li>
</ul>
</b>''')
        stringParam('run_os_install_or_upgrade', "YES", 'Whether to install or upgrade os<br>')
        stringParam('run_litp_install_or_upgrade', "YES", 'Whether to install or upgrade litp<br>')
        stringParam('run_os_patch_install_or_upgrade', "YES", 'run_os_patch_install_or_upgrade<br>')
        stringParam('pre_install_workload_cleanup', "${pre_install_workload_cleanup}", '''Whether to clean up the workload before II or not<br>
valid options YES or NO''')
        stringParam('apt_version', "${apt_version}", '''<b>
Options:
<ul>
  <li>LATEST - Gets latest version from Nexus</li>
  <li>MAPPED - Gets version mapped to ENM ISO contained in product set specified above</li>
  <li>NOT_REQUIRED - Ignores the deployment of APT</li>
    <li>
      SPECIFIC VERSION - Deploys the version specified
      <ul>
        <li>List of versions: <a href="http://atclvm1356.athtem.eei.ericsson.se/changelogs/assertion_python_utilities.html">APT Versions Log</a></li>
        <li>e.g. 1.22.4</li>
      </ul>
    </li>
</ul>
</b>''')
        stringParam('nss_utils_version', "${nss_utils_version}", '''<b>
  Options:
  <ul>
    <li>LATEST - Gets version mapped to latest green product set version</li>
    <li>MAPPED - Gets version mapped to NSS Product Set Version present in the environment's netsims</li>
    <li>NOT_REQUIRED - Ignores the deployment of NSS Testware Utilities</li>
  </ul>
</b''')
      stringParam('SLAVE', "${PIPELINE_SLAVE}", 'Slave to run GEO Initial Install')
      stringParam('non_critical_data_list', "default" , '''<b>
  Please Enter the value for non_critical_data_list
    <ul>
      <li>default - It will keep the default configuration of non_critical_data_list as in the template</li>
      <li>Other Options which can be given other than default : "idam, fm_alarm_history, enm_logs, service_config_params, vnflcm , pm, collections_and_savedsearches, shm, new_feature_value"</li>
    </ul>
</b>''')
      choiceParam('nodesCleanUp', ['NO', 'YES'], '''<h1 style="color:red;">Customers should set this parameter to YES only if NSS 15K network is on the servers linked to ENM</h1>''')
    }

    logRotator(commonSteps.defaultLogRotatorValues())

    triggers {
        cron(crontab)
    }

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
            scriptPath("Jenkinsfiles/parent_pipelines/geo_initial_install_flow/Jenkinsfile")
        }
    }
}
