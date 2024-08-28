package commonclasses

class CommonPipelineGenerationParameters {
    static List regularSlaves(String defaultValue) {
        return ['REGULAR_SLAVES', defaultValue, 'These are the most commonly used slaves. They do not require the ability to run a TAF executor.']
    }

    static List teDockerSlaves(String defaultValue) {
        return ['TE_DOCKER_SLAVES', defaultValue, 'Slaves that must be capable of running a TAF execution. These slaves must also have docker and python version 2.7 installed']
    }

    static List openstackSlaves(String defaultValue) {
        return ['OPENSTACK_SLAVES', defaultValue, 'These are slaves which must be capable of running Jenkinsfiles and DSL groovy scripts. These slaves must also have openstack and docker installed.']
    }

    static List stcTeDockerSlaves(String defaultValue) {
        return ['STC_TE_DOCKER_SLAVES', defaultValue, 'Slaves that must be capable of running a TAF execution. These slaves must also have docker installed']
    }

    static List stcOpenstackSlaves(String defaultValue) {
        return ['STC_OPENSTACK_SLAVES', defaultValue, 'These are slaves which must be capable of running Jenkinsfiles and DSL groovy scripts. These slaves must also have openstack and docker installed.']
    }

    static List teamEmail(String defaultValue) {
        return ['TEAM_EMAIL', defaultValue, 'This is the email of the team who the pipelines are being generated for']
    }

    static List permissionGroups(String defaultValue) {
        return ['PERMISSION_GROUPS', defaultValue, 'Group or groups of users who have the ability to run pipeline etc. If left blank, users signed in on the Jenkins instance will have the ability to run jobs.']
    }
}
