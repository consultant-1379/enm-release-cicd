import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_RFA250_Rerunner"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job is used to rerun RFA250 test suites.</p>
<ul>
  <li>Takes in an allure report as input.</li>
  <li>It will form XML Scheduler from the suites that failed</li>
  <li>It will rerun the failed suites</li>
  <li>Will send confidence levels as RFA250 Staging if send_confidence_level is set to NO</li>
</ul>'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(['allure_report_url', '', '''URL of the allure report of the failing RFA250 test(s)<br>
<b>e.g. https://oss-taf-logs.seli.wh.rnd.internal.ericsson.com/af58fe70-9f63-4b20-a7c2-d218042a03b0/<b><br>'''])
            stringParam(commonParams.clusterId())
            stringParam(commonParams.drop())
            stringParam(commonParams.productSetVersion())
            stringParam(commonParams.cenmProductSetVersion())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.testPhase())
            stringParam(commonParams.centralCsvVersion())
            stringParam(commonParams.tafVersion())
            stringParam(commonParams.sendConfidenceLevel())
        }

        label("${TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('Kicking off reruns on \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''echo "tafVersion=${taf_version}" >> "${WORKSPACE}/parameters.properties"
echo "centralCSVVersion=${central_csv_version}" >> "${WORKSPACE}/parameters.properties"
echo "conf_level_for_this_job=RFAStaging" >> "${WORKSPACE}/parameters.properties"

sh MTELoopScripts/pipeline_scripts/rfa250_rerunner_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/suites_xml_schema.properties")
            propertiesContent("")
        }

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>MT Utils Version: ${mt_utils_version}<br>Product Set Version: ${product_set_version}<br>cENM Product Set Version = ${cenm_product_set_version}<br>Central CSV Version: ${central_csv_version}<br>TAF Version: ${taf_version}</b>')

        singleConditionalBuilder  {
            condition {
                not {
                    condition {
                        stringsMatchCondition {
                            arg1("\${suites_xml_schema}")
                            arg2("")
                            ignoreCase(false)
                        }
                    }
                }
            }
            runner {
                fail()
            }
            buildStep {
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
               ${suites_xml_schema}
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
    }
}
