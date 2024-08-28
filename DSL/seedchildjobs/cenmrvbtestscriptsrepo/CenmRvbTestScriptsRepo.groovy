import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonParameters

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()

def jobBeingGeneratedName = "cENM_RVB_Copy_TestScripts_Repo"

job(jobBeingGeneratedName) {
    steps {

        String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
        permissionGroupsArray.each {
            authorization(commonAuthorization.setPermissionGroup("${it}"))
        }

        description(commonSteps.defaultJobDescription(jobBeingGeneratedName, '''<p>This job copies RVB testscripts Repo to the cENM environment.</p>
'''))

        logRotator(commonSteps.defaultLogRotatorValues())

        concurrentBuild(allowConcurrentBuild = true)

        parameters {
            stringParam(commonParams.clusterId())
            stringParam(commonParams.mtUtilsVersion())
        }

        label("${OPENSTACK_SLAVES}")

        scm {
            git {
                remote {
                    name('Test')
                    url('${GERRIT_MIRROR}/OSS/com.ericsson.nms.rv/TestScripts')
                    credentials("bb_func_user")
                }
                branch('*/master')
                extensions {
                    cleanAfterCheckout()
                    cloneOptions {
                        shallow(true)
                        noTags(false)
                    }
                }
            }
        }

        wrappers {
            preBuildCleanup()
            timestamps()
            buildName('ClusterId = \${ENV,var="cluster_id"}')
        }

        shell(commonSteps.downloadMTUtilsRelease() + '''sh MTELoopScripts/pipeline_scripts/rvb_testscripts_repo_setup.sh "${WORKSPACE}/parameters.properties" || exit 1''')

        buildDescription('', '<b>Cluster ID = ${cluster_id}</b>')
    }
}
