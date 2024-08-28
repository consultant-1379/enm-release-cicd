import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_UI_Launcher_Link_Verification"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job is used to check is the cENM UI Launcher Link verification working.</p>'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.testPhase())
            stringParam(commonParams.clusterId())
            stringParam(commonParams.drop())
            stringParam(commonParams.productSetVersion())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.tafVersion())
        }

        label("${TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('Cluster ID = \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''echo "tafVersion=${taf_version}" >> "${WORKSPACE}/parameters.properties"

sh MTELoopScripts/pipeline_scripts/ui_launcher_link_verification_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>Product Set Version=${product_set_version}<br>ENM ISO Version = ${enm_iso_version}<br>TAF Version=${taf_version}</b>')

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
            sutClusterId("\${cluster_id}")
            citeHostPropertiesFile("")
            testwarePropertiesFile("\${WORKSPACE}/MTELoopScripts/libraries/rfa_250/taf.properties")
            schedule {
                name('enm_schedule_kvm_ui_launcher.xml')
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
            tafVersion("\${tafVersion}")
            userDefinedGAVs("\${etl_bom_version_for_gav}")
            additionalTestProperties('''drop=${drop}
host.UiTestGrid.ip=${UiTestGridIP}
deployment.type=cloud_native
enm_iso_version=${enm_iso_version}''')
            breakBuildOnTestsFailure("true")
            ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
            tafTestExecutorHostname("")
            tafTestExecutorPort("8080")
            globalTestGroups("")
        }
    }
}
