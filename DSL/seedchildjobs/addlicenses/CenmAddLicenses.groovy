import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_Add_Licenses"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescriptionOtherTeams(jobBeingGeneratedName, '''<p>This job is used to install the licenses onto cENM environments.</p>
<h3>For any licences queries, Please contact Team Decepticons.</h3>
'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.testPhase())
            stringParam(commonParams.clusterId())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.drop())
            stringParam(commonParams.productSetVersion())
            // TODO Update deployment_type being passed to cloud_native through Jira RTD-13768
            stringParam(['deployment_type', 'cloud', 'Type of deployment being used'])
        }

        label("${TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('ClusterId = \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''sh MTELoopScripts/pipeline_scripts/add_licenses_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>Test Phase = ${test_phase} <br> Cluster ID = ${cluster_id}</b>')

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
            testwarePropertiesFile("")
            schedule {
                name('enm_schedule_kvm_add_licenses.xml')
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
            tunnellingOn("")
            tafVersion("\${tafVersion}")
            userDefinedGAVs("")
            additionalTestProperties('''taf.profiles=license_rollout
tdm.api.host=https://taf-tdm-prod.seli.wh.rnd.internal.ericsson.com/api/''')
            breakBuildOnTestsFailure("true")
            ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
            tafTestExecutorHostname("")
            tafTestExecutorPort("8080")
            globalTestGroups("")
        }
    }
}
