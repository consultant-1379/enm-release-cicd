import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_RFA250"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job is used to execute the RFA 250 test suites against cENM environments</p>
<ul>
  <li>It can also send confidence level events to keep track of RFA250 status.</li>
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
            stringParam(commonParams.tafVersion())
            stringParam(commonParams.centralCsvVersion())
            stringParam(commonParams.tafSchedulerVersion())
            stringParam(commonParams.sendConfidenceLevel())
        }

        label("${TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('Cluster ID = \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''echo "tafVersion=${taf_version}" >> "${WORKSPACE}/parameters.properties"
echo "centralCSVVersion=${central_csv_version}" >> "${WORKSPACE}/parameters.properties"
echo "tafSchedulerVersion=${taf_scheduler_version}" >> "${WORKSPACE}/parameters.properties"
echo "conf_level_for_this_job=RFA" >> "${WORKSPACE}/parameters.properties"

sh MTELoopScripts/pipeline_scripts/rfa_250_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>Test Phase = ${test_phase}<br>Product Set Version = ${product_set_version}<br>cENM Product Set Version = ${cenm_product_set_version}<br>ENM ISO = ${enm_iso_version}<br>Central CSV Version = ${central_csv_version}<br>TAF Scheduler Version = ${taf_scheduler_version}<br>TAF Version = ${taf_version}</b>')

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
                name('cenm/cenm_RFA250_svc_scp.xml')
                groupId('com.ericsson.cifwk')
                artifactId('enm-schedule-kvm')
                version("\${taf_scheduler_version}")
                xml(null)
                testPropertiesAsString(null)
                scheduleType(null)
                tafScheduleName(null)
                tafScheduleVersion(null)
                scheduleClassName(null)
            }
            tunnellingOn("true")
            tafVersion("\${taf_version}")
            userDefinedGAVs("\${etl_bom_version_for_gav}")
            additionalTestProperties('''host.UiTestGrid.ip=${UiTestGridIP}
    deployment.type=cloud''')
            breakBuildOnTestsFailure("false")
            ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
            tafTestExecutorHostname("")
            tafTestExecutorPort("8080")
            globalTestGroups("")
        }

        shell('''#!/bin/sh

echo "TE_ALLURE_LOG_URL=${TE_ALLURE_LOG_URL}" >> "${WORKSPACE}/build.properties"''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }
    }

    publishers {
        postBuildTestAnalysisStep {
            testResultsJsonLocation("data/csvExport.json")
            failIfNoTestResults(false)
            failIfTeRunCrashed(false)
            buildStatusUpdateOption { }
        }
    }
}
