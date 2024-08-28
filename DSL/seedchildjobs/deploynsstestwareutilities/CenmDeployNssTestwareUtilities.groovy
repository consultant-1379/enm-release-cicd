import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_Deploy_NSS_Testware_Utilities"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job deploys the NSS testware to the WLVM of a cENM environment</p>'''))

        concurrentBuild(allowConcurrentBuild = true)

        logRotator(commonSteps.defaultLogRotatorValues())

        parameters {
            stringParam(commonParams.clusterId())
            stringParam(commonParams.testPhase())
            stringParam(commonParams.mtUtilsVersion())
            stringParam(commonParams.nssUtilsVersion())
            stringParam(commonParams.productSetVersion())
        }

        label("${REGULAR_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('Deploying NSS testware on \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        buildDescription('', '<b>NSS Utils Version=${nss_utils_version}<br>MT Utils Version=${mt_utils_version}<br>Test Phase=${test_phase}</b>')

        shell(commonSteps.downloadMTUtilsRelease() + '''sh MTELoopScripts/pipeline_scripts/deploy_nss_testware_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')
    }
}
