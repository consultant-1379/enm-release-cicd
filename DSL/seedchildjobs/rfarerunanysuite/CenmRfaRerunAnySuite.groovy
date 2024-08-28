import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_RFA250_Rerun_Any_Suite"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job is used to rerun RFA250 test suites individually.</p>
<ul>
  <li>Suite to run is built by selecting Suite Name, Component and Suite XML in the build parameters.</li>
</ul>'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.clusterId())
            stringParam(commonParams.drop())
            stringParam(commonParams.productSetVersion())
            stringParam(commonParams.cenmProductSetVersion())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.testPhase())
            stringParam(commonParams.centralCsvVersion())
            stringParam(commonParams.tafVersion())
            activeChoiceParam('suite_name') {
                description('Select suite name')
                filterable()
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script('''def list = []
def xmlslist = ["enm_schedule_RFA250_evt.xml","enm_schedule_RFA250_scp.xml","enm_schedule_RFA250_svc.xml","enm_schedule_RFA250_svc_evt.xml","enm_schedule_RFA250_svc_scp.xml","enm_schedule_RFA250_svc_scp_evt.xml","enm_schedule_Users_And_Nodes.xml"]
def metadata
xmlslist.each {
def addr = "https://gerrit.ericsson.se/a/gitweb?p=OSS/com.ericsson.cifwk.taf/taf_scheduler_kvm.git;a=blob_plain;f=src/main/resources/${it};hb=refs/heads/master"
def authString = "YmJmdW5jdXNyOm1FNTViNDkxTG00NHdGa0huUlJWbDJjNg"

def connection = addr.toURL().openConnection()
connection.setRequestProperty( "Authorization", "Basic ${authString}" )
xmlString = connection.content.text

File kvmFile = new File('kvm.xml')
kvmFile.write(xmlString)
xmlString = kvmFile.getText()
kvmFile.delete()

metadata = new XmlSlurper().parseText(xmlString)

metadata.'**'.findAll { it.name() == 'item' }.each {
list.add (it.name)
}
}
return list''')
                    fallbackScript('"None"')
                }
            }
            activeChoiceReactiveParam('component') {
                description('Select component name. To run a specific version of a suite, add it after the CXP. e.g. ERICTAFnodesecurity_CXP9032282:1.59.2. NOTE: This can only be done in a rebuild.')
                filterable()
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script('''def list = []
def xmlslist = ["enm_schedule_RFA250_evt.xml","enm_schedule_RFA250_scp.xml","enm_schedule_RFA250_svc.xml","enm_schedule_RFA250_svc_evt.xml","enm_schedule_RFA250_svc_scp.xml","enm_schedule_RFA250_svc_scp_evt.xml","enm_schedule_Users_And_Nodes.xml"]
def metadata

xmlslist.each {
def addr = "https://gerrit.ericsson.se/a/gitweb?p=OSS/com.ericsson.cifwk.taf/taf_scheduler_kvm.git;a=blob_plain;f=src/main/resources/${it};hb=refs/heads/master"
def authString = "YmJmdW5jdXNyOm1FNTViNDkxTG00NHdGa0huUlJWbDJjNg"

def connection = addr.toURL().openConnection()
connection.setRequestProperty( "Authorization", "Basic ${authString}" )
xmlString = connection.content.text

File kvmFile = new File('kvm.xml')
kvmFile.write(xmlString)
xmlString = kvmFile.getText()
kvmFile.delete()

metadata = new XmlSlurper().parseText(xmlString)

metadata.'**'.findAll { it.name() == 'item' }.each {
if(suite_name.contains(it.name.toString())) {
list.add (it.component.toString())
}
}
}
return list''')
                    fallbackScript('"None"')
                }
                referencedParameter('suite_name')
            }
            activeChoiceReactiveParam('suite_xml') {
                description('Select suite XML')
                filterable()
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script('''def list = []
def xmlslist = ["enm_schedule_RFA250_evt.xml","enm_schedule_RFA250_scp.xml","enm_schedule_RFA250_svc.xml","enm_schedule_RFA250_svc_evt.xml","enm_schedule_RFA250_svc_scp.xml","enm_schedule_RFA250_svc_scp_evt.xml","enm_schedule_Users_And_Nodes.xml"]
def metadata
xmlslist.each {
def addr = "https://gerrit.ericsson.se/a/gitweb?p=OSS/com.ericsson.cifwk.taf/taf_scheduler_kvm.git;a=blob_plain;f=src/main/resources/${it};hb=refs/heads/master"
def authString = "YmJmdW5jdXNyOm1FNTViNDkxTG00NHdGa0huUlJWbDJjNg"

def connection = addr.toURL().openConnection()
connection.setRequestProperty( "Authorization", "Basic ${authString}" )
xmlString = connection.content.text

File kvmFile = new File('kvm.xml')
kvmFile.write(xmlString)
xmlString = kvmFile.getText()
kvmFile.delete()

metadata = new XmlSlurper().parseText(xmlString)

metadata.'**'.findAll { it.name() == 'item' }.each {
if(suite_name.contains(it.name.toString())) {
list.add (it.suites.toString())
}
}
}
return list''')
                    fallbackScript('"None"')
                }
                referencedParameter('suite_name')
            }
        }

        label("${TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('\${ENV,var="suite_name"} on \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''echo "tafVersion=${taf_version}" >> "${WORKSPACE}/parameters.properties"
echo "centralCSVVersion=${central_csv_version}" >> "${WORKSPACE}/parameters.properties"
sh MTELoopScripts/pipeline_scripts/rfa250_rerun_any_suite_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/MTELoopScripts/libraries/rfa_250/taf.properties")
            propertiesContent("")
        }

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>MT Utils Version: ${mt_utils_version}<br>Product Set Version: ${product_set_version}<br>cENM Product Set Version = ${cenm_product_set_version}<br>Central CSV Version: ${central_csv_version}<br>TAF Version: ${taf_version}</b>')

        baselineDefinedMessageDispatcher {
            ciArtifacts {
                isoProduct("ENM")
                isoDrop("\${drop}")
                isoVersion("\${enm_iso_version}")
                jobType("Entry Loop")
                teamName("")
                artifactsClassName("")
            }
            armId("")
            downloadRepoName("")
            uploadRepoName("")
            httpString("https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/groups/public")
            ftpString("")
            nfsString("")
            armName("")
            armPassword("")
            armDescription("")
            sutClusterId("000")
            citeHostPropertiesFile("")
            testwarePropertiesFile("\${WORKSPACE}/MTELoopScripts/libraries/rfa_250/taf.properties")
            schedule {
                name(null)
                groupId(null)
                artifactId(null)
                version(null)
                xml('''<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item-group parallel="true">
        <item timeout-in-seconds="1800">
            <name>${suite_name}</name>
            <component>${component}</component>
            <suites>${suite_xml}</suites>
            <groups>RFA250</groups>
        </item>
    </item-group>
</schedule>''')
                testPropertiesAsString(null)
                scheduleType(null)
                tafScheduleName(null)
                tafScheduleVersion(null)
                scheduleClassName("com.ericsson.oss.axis.BaselineDefinedMessageDispatcher\$ScheduleAsXml\$1")
            }
            tunnellingOn("true")
            tafVersion("\${taf_version}")
            userDefinedGAVs("\${etl_bom_version_for_gav}")
            // TODO Cleanup additional test properties via Jira RTD-14280
            additionalTestProperties('''taf.config.dit.deployment.internal.nodes=fmhistory_1,fmhistory_2,fmhistory_3,fmhistory_4,fmalarmprocessing_1,fmalarmprocessing_2,fmalarmprocessing_3,fmalarmprocessing_4,fmx_1,fmx_2,fmserv_1,fmserv_2,fmserv_3,fmserv_4,jms_1
deployment.type=cloud''')
            breakBuildOnTestsFailure("true")
            ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
            tafTestExecutorHostname("")
            tafTestExecutorPort("8080")
            globalTestGroups("")
        }
    }
}
