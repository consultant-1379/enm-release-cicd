import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_STC_Check_Latest_Time_In_DDP"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName,
            '<p>This job checks the latest time data uploaded in DDP specific to an STC Network based Environment.</p>'))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.clusterId())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.testPhase())
            stringParam(commonParams.productSetVersion())
            stringParam(commonParams.drop())
            stringParam('ddp_upload_timeout_seconds', '3600', '''The amount of time (in seconds) that the test in this job will loop for until the DDC upload time is greater than the end_time.''')
            stringParam('start_time', '', '''Start time of the upgrade. Example: 2020-02-24 08:30:00''')
            stringParam('end_time', '', '''End time of the upgrade, the latest upload time in DDP must be greater than this before we run the upgrade assertions job. Example: 2020-02-24 10:30:00''')
        }

        label("${STC_TE_DOCKER_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('ClusterId = \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''sh MTELoopScripts/pipeline_scripts/check_latest_time_in_ddp_setup.sh "\${WORKSPACE}/parameters.properties" || exit 1''')

        envInjectBuilder {
            propertiesFilePath("\${WORKSPACE}/build.properties")
            propertiesContent("")
        }

        buildDescription('', '<b>Test Phase = \${test_phase} <br> Cluster ID = \${cluster_id}</b>')

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
                name('enm_schedule_kvm_check_last_upload_time_in_ddp.xml')
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
            tafVersion("\${taf_version}")
            userDefinedGAVs("")
            additionalTestProperties('''end_time=\${end_time}
ddc.db.password=ddpAccount
start_time=\${start_time}
site.prefix=LMI_
ddp_upload_timeout_seconds=\${ddp_upload_timeout_seconds}
tdm.api.host=https://taf-tdm-prod.seli.wh.rnd.internal.ericsson.com/api/
clusterId=\${cluster_id}
ddc.db.port=\${ddp_port}
ddc.db.username=emaintra''')
            breakBuildOnTestsFailure("true")
            ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
            tafTestExecutorHostname("")
            tafTestExecutorPort("8080")
            globalTestGroups("")
        }
    }
}
