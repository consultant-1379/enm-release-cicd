import commonclasses.CommonParameters
import commonclasses.CommonSteps
import commonclasses.CommonAuthorization
import commonclasses.CommonConstants

///////////////////////////
//   DSL SCRIPT START   //
/////////////////////////

CommonSteps commonSteps = new CommonSteps()
CommonParameters commonParams = new CommonParameters()
CommonAuthorization commonAuthorization = new CommonAuthorization()
CommonConstants commonConstants = new CommonConstants()

def pipelineBeingGeneratedName = "cENM_Restore_Backup_Flow"

pipelineJob(pipelineBeingGeneratedName) {

    String[] permissionGroupsArray = "${PERMISSION_GROUPS}".split(',')
    permissionGroupsArray.each {
        authorization(commonAuthorization.setPermissionGroup("${it}"))
    }

    description(commonSteps.defaultJobDescription(pipelineBeingGeneratedName, '<p>This job is used to take backup of a cENM environment.</p>'))

    concurrentBuild(allowConcurrentBuild = true)

    parameters {
        stringParam('SLAVE', "${REGULAR_SLAVES}", 'Slave to run pipeline against')
        stringParam('STC_SLAVE', "${STC_OPENSTACK_SLAVES}", 'Slave to run pipeline against if the network type is STC')
        choiceParam('is_network_type_STC', ['false', 'true'], 'Select true if the deployment is based on STC Network')
        stringParam(commonParams.clusterId())
        stringParam(commonParams.backupName())
        stringParam(commonParams.cenmProductSetVersion())
        stringParam(commonParams.mtUtilsVersion())
        choiceParam('stage_area', ['ci_internal','drop'] , 'Determine whether to download charts from ci_internal or drop')
        choiceParam('BRO_DEFAULT_BACKUP_TYPE', ['Internal','External'] , 'If seleted "External" Cenmproxy should be up and running on your environment.If selected "Internal" backup action will done from the Brocli.')
        choiceParam('deployment_mechanism', ['charts','csar'] , 'Deployment Mechanism to determine whether to deploy using charts or csar')
        stringParam(commonParams.drop())
    }

    logRotator(commonSteps.defaultLogRotatorValues())

    definition {
        cpsScm {
            scm {
                git {
                    branch('master')
                        remote {
                            url("\${GERRIT_MIRROR}/OSS/com.ericsson.oss.ci.rtd/enm-release-cicd")
                            credentials("bb_func_user")
                        }
                        extensions {
                             cleanBeforeCheckout()
                             localBranch 'master'
                        }
                }
            }
            scriptPath("Jenkinsfiles/parent_pipelines/cenm_restore_backup/Jenkinsfile")
        }
    }
}
