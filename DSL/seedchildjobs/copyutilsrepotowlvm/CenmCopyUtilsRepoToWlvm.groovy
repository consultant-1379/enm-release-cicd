import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_Copy_Utils_Repo_To_Environment"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job copies the MT utils repo to the Workload VM of a specified cENM environment.</p>'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.clusterId())
            stringParam(commonParams.mtUtilsVersion())
        }

        label("${REGULAR_SLAVES}")

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('Environment = \${ENV,var="cluster_id"}')
        }

        exportParametersBuilder(commonSteps.exportParametersProperties())

        shell(commonSteps.downloadMTUtilsRelease() + '''echo "mt_utils_repo=true" >> "${WORKSPACE}/parameters.properties"

sh MTELoopScripts/pipeline_scripts/copy_repos_to_servers_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        buildDescription('', '<b>MT Utils Version = ${mt_utils_version}</b>')
    }
}
