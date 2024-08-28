package commonclasses

class CommonAuthorization {

    static Object setPermissionGroupIncludingConfigure(String permissionGroup) {
        return {
            permissions(permissionGroup, [
                'hudson.model.Item.Configure',
                'hudson.model.Item.Build',
                'hudson.model.Item.Cancel',
                'hudson.model.Item.Discover',
                'hudson.model.Item.Read',
                'hudson.model.Item.Workspace',
                'hudson.model.Item.ExtendedRead'
            ])
        }
    }

    static Object setPermissionGroup(String permissionGroup) {
        return {
            permissions(permissionGroup, [
                'hudson.model.Item.Build',
                'hudson.model.Item.Cancel',
                'hudson.model.Item.Discover',
                'hudson.model.Item.Read',
                'hudson.model.Item.Workspace',
                'hudson.model.Item.ExtendedRead'
            ])
        }
    }

}
