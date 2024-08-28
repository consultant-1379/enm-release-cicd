package commonclasses

class CommonParameters {

    static List clusterId() {
        return ['cluster_id', '', 'The deployment name to run the job against']
    }

    static List testPhase() {
        return ['test_phase', '', 'Test phase being run']
    }

    static List mtUtilsVersion() {
        return ['mt_utils_version', 'RELEASE', '''This is the version of the scripts available in Nexus<br>
We recommend you always use RELEASE<br>
However, if there is an issue with RELEASE please use one of the versions available <a href = https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/mtg/utils/>here</a>''']
    }

    static List nssUtilsVersion() {
        return ['nss_utils_version', '', '''<b>
  Options:
  <ul>
    <li>LATEST - Gets version mapped to latest green product set version</li>
    <li>MAPPED - Gets version mapped to NSS Product Set Version present in the environment's netsims</li>
    <li>NOT_REQUIRED - Ignores the deployment of NSS Testware Utilities</li>
  </ul>
</b>''']
    }

    static List torUtilsVersion() {
        return ['tor_utils_version', '', '''<b>
  Options:
  <ul>
    <li>MAPPED - fetches the version of torutils in the ENM PS</li>
    <li>LATEST - Gets version mapped to latest green product set version</li>
    <li>INSTALLED - Aligns all versions in the WLVM to match the version of Tor Utilities Production Package installed on ENM System. NOTE: Cloud Native is currently unsupported here.</li>
    <li>NOT_REQUIRED - Ignores the deployment of Tor Utilities.</li>
    <li>SPECIFIC VERSION - Deploys the version specified
      <ul>
        <li>List of versions: <a href="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/dms/torutility/ERICtorutilitiesinternal_CXP9030579/">Tor Utilities Versions</a></li>
        <li>e.g. 4.80.9</li>
      </ul>
    </li>
  </ul>
</b>''']
    }

    static List aptVersion() {
        return ['apt_version', '', '''<b>
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
</b>''']
    }

    static List cenmProductSetVersion() {
        return ['cenm_product_set_version', '', 'cENM Product Set Version that the cENM test environment is currently deployed to']
    }

    static List productSetVersion() {
        return ['product_set_version', '', 'Product Set Version that the test environment is currently deployed to']
    }

    static List enmIsoVersion() {
        return ['enm_iso_version', '', '''The enm iso version for your deployment<br>
This should also be the enm iso version that matches the product set specified<br>
Example: 1.74.11''']
    }

    static List drop() {
        return ['drop', '', '''The drop in which the product is within<br>
Example: 19.07''']
    }

    static List centralCsvVersion() {
        return ['central_csv_version', '', '''Version of the Central CSV to use<br>
AUTO will get the version mapped to Product Set<br>
If you do not want to use mapped version, then can select a specific version to use''']
    }

    static List simdepRelease() {
        return ['simdep_release', '1.5.773', '''Simdep release no.
Example: 1.5.216''']
    }

    static List tafVersion() {
        return ['taf_version', '', '''Version of the TAF to use<br>
AUTO will get the version mapped to Product Set<br>
If you do not want to use mapped version, select a specific version to use''']
    }

    static List tafSchedulerVersion() {
        return ['taf_scheduler_version', '', '''Version of the TAF Scheduler to use<br>
AUTO will get the version mapped to Product Set<br>
If you do not want to use mapped version, then can select a specific version to use''']
    }

    static List sendConfidenceLevel() {
        return ['send_confidence_level', 'NO', '''This parameter is used to send the confidence level to portal/radiator/auto build log''']
    }

    static List restrictToFeatures() {
        return ['restrict_to_features', '', '''Comma separated list of features to include/restrict<br>
The following features are available to restrict: AMOS,AP,CLOUD,CM,CMEVENTS_NBI,CMSYNC,DAILY_SCOPE,ENM,EXPORT,FM,FMX,IMPORT,KPI,NETEX,PHYSICAL,PM,SECUI,SHM,SYSTEM,UPGRADE,WORKLOAD,ROBUST<br>
<b>LEAVE EMPTY TO RUN FULL ASSERTIONS</b>''']
    }

    static List featuresToExclude() {
        return ['features_to_exclude', '', '''Comma separated list of features to exclude<br>
The following features are available to exclude: AMOS,AP,CLOUD,CM,CMEVENTS_NBI,CMSYNC,DAILY_SCOPE,ENM,EXPORT,FM,FMX,IMPORT,KPI,NETEX,PHYSICAL,PM,SECUI,SHM,SYSTEM,UPGRADE,WORKLOAD,ROBUST<br>
<b>LEAVE EMPTY TO RUN FULL ASSERTIONS</b>''']
    }

    static List emailRecipients() {
        return ['email_recipients', '', 'OPTIONAL: A comma separated list of email recipients.']
    }

    static List forceArneXmlCreation() {
        return ['force_arne_xml_creation', 'false', '''Set to true if you would like to force the recreation of Arne XML files on netsims regardless of whether they already exist or not.<br>
Set to false if you would like to ignore the recreation of Arne XML files on netsims if the files already exist.''' ]
    }

    static List nodesToAddToEnm() {
        return ['nodes_to_add_to_enm', 'ALL', '''How many nodes should be applied<br>
See here for parameter format: <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/EM/Release+Job+Parameters#ReleaseJobParameters-nodes_to_add_to_enm">Nodes to add to ENM documentation</a>''']
    }

    static List supervisionTypes() {
        return ['supervision_types', 'ALL', '''Specify the required types of supervision<br>
See here for parameter format: <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/EM/Maintrack+Job+Parameters#MaintrackJobParameters-supervision_types">Supervision Types Documentation</a>''']
    }

    static List nodesToAddToWorkpool() {
        return ['nodes_to_add_to_workpool', 'ALL', '''Comma separated list of nodes to add workload pool''']
    }

    static List workloadProfiles() {
        return ['workload_profiles', '', '''Specify the required workload profiles and no-exclusive argument if required<br>
See here for parameter format: <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/EM/Release+Job+Parameters#ReleaseJobParameters-workload_profiles">Profiles to start documentation</a>''']
    }

    static List broChartVersion() {
        return ['bro_chart_version', '', 'Provide bro chart version']
    }

    static List preDeployChartVersion() {
        return ['pre_deploy_chart_version', '', 'Provide pre deploy chart version']
    }

    static List infraChartVersion() {
        return ['infra_chart_version', '', 'Provide infra chart version']
    }

    static List statelessChartVersion() {
        return ['stateless_chart_version', '', 'Provide stateless chart version']
    }

    static List integrationValueVersion() {
        return ['integration_value_version', '', 'Provide integration value version']
    }

    static List csarPackageVersion() {
        return['csar_package_version', '', 'Provide csar package version']
    }

    static List monitoringIntegrationChartVersion() {
        return ['monitoring_integration_chart_version', '', 'Provide monitoring integration chart version']
    }

    static List deploymentMechanism(String defaultValue) {
        return ['deployment_mechanism', defaultValue, 'Deployment Mechanism to determine whether to deploy using charts or csar']
    }

    static List stageArea(String defaultValue) {
        return ['stage_area', defaultValue, 'Determine whether to download charts from ci_internal or drop']
    }

    static List spinnakerApplicationName(String defaultValue) {
        return ['spinnaker_application_name', defaultValue, 'Provide the spinnaker application you want to update']
    }

    static List spinnakerPipelineName(String defaultValue) {
        return ['spinnaker_pipeline_name', defaultValue, 'Provide the spinnaker pipeline name you want to update']
    }

    static List femControllerName(String defaultValue) {
        return ['fem_controller_name', defaultValue, 'Provide the fem name that will be used as a jenkins controller in the updated spinnaker pipeline']
    }

    static List syncTriggerFemControllerName(String defaultValue) {
        return ['sync_trigger_fem_controller_name', defaultValue, 'Provide the sync trigger fem name that will be used as a jenkins controller in the updated spinnaker pipeline']
    }

    static List backupName() {
        return ['backup_name', '', 'Provide Backup Name']
    }

    static List aduVersion() {
        return ['adu_version', '', '''<b>
  Options:
  <ul>
    <li>LATEST - Uses version mapped to latest green Product Set Version</li>
    <li>MAPPED - Uses version mapped to ENM ISO contained in product set specified above</li>
    <li>
      SPECIFIC VERSION - Uses the version specified, including snapshot versions
      <ul>
        <li>List of versions: <a href="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/nms/rv/taf/ERICTAFtorhatestautomation_CXP9030905/maven-metadata.xml">Availability Log</a></li>
        <li>e.g. 3.22.1/li>
        <li>e.g. 3.22.2-SNAPSHOT/li>
      </ul>
    </li>
  </ul>
</b>''']
    }

    static List availabilityWatcherVersion() {
        return ['availability_watcher_version', '', 'Required to determine the version of the availability_watcher_version tar to pull from the artifactory']
    }

    static List storageClassName() {
        return ['storage_class_name', '', 'If this field is empty the default value is nfs-{namespace} e.g. nfs-enm33,If any environment has a different value of storage class, it can be specified with this property']
    }

    static List cenmUpgradeType() {
        return ['cenm_upgrade_type', '', '''<b>
  Options:
  <ul>
    <li>ENM - Use "ENM" for enm deployment upgrades</li>
    <li>CCD - Use "CCD" for platform upgrades</li>
  </ul>
</b>''']
    }

    static List ccdAduDurationSelector() {
        return ['ccd_adu_duration_selector', '', 'ADU Duration Selector(in Hrs) This parameter is used only for CCD Upgrades']
    }

    static List dataProviderName() {
        return ['data_provider_name', '', 'Nodes Info coming from TDM']
    }

    static List usersToCreate() {
        return ['users_to_create', '', 'This parameter is passed into the taf job below specifying the user to create for testing']
    }

    static List nodePackagesDir() {
        return ['node_packages_dir', '', 'Location of the RNL upgrade packages on the slave']
    }

    static List supportedDdrnlNodeTypes() {
        return ['supported_ddrnl_node_types', '', '''<b>
This is a comma seperated list of the supported DDRNL node types.<br>
This list will be used to expose node upgrade information.<br>
Any new supported nodes must be added to the list before job build.
</b>''']
    }

    static List multiNrm() {
        return ['multi_nrm', '', '''Flag to determine whether or not the job is dealing with multiple NRMs.<br>
Note only supports 2K/5K hybrid currently.''']
    }

    static List triggerNonFunctionalFlow() {
        return ['trigger_non_functional_flow', '', '''Set to True if you would like to trigger Non Functional loop<br>
Set to False if you would like to trigger Functional loop.''']
    }

    static List publishToBuildlog() {
        return ['publish_to_buildlog', 'YES', '''This parameter is used to send the confidence level to cenm build log''']
    }

    static List loadBalancerIp() {
        return ['load_balancer_ip', '', 'load balancer ip']
    }

    static List cbrsFemControllerName(String defaultValue) {
        return ['cbrs_fem_controller_name', defaultValue, 'Provide the fem name that will be used as a jenkins controller in the updated spinnaker pipeline']
    }
}