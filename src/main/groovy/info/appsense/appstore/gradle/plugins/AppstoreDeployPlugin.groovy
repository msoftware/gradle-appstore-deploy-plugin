package info.appsense.appstore.gradle.plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import info.appsense.appstore.gradle.plugins.tasks.BootstrapResourcesTask
import info.appsense.appstore.gradle.plugins.tasks.PublishApplicationTask
import info.appsense.appstore.gradle.plugins.tasks.PublishResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by vladimir.minakov on 16.02.15.
 */
class AppStoreDeployPlugin implements Plugin<Project> {
    private final static String GROUP_NAME = 'AppStore'

    void apply(Project project) {
        def android = project.property('android') as AppExtension
        if (!android) {
            throw new IllegalStateException("The 'com.android.application' plugin is required.")
        }
        project.extensions.create(AppStoreDeployExtension.NAME, AppStoreDeployExtension)

        android.applicationVariants.all { ApplicationVariant variant ->
            if (variant.buildType.isDebuggable()) {
                return
            }
            project.tasks.create("generateGooglePlayResources${variant.name.capitalize()}", BootstrapResourcesTask, { BootstrapResourcesTask task ->
                task.group = GROUP_NAME
                task.description = "Generate the resources directory structure from the Google Play Store for the ${variant.name} build"
                task.applicationVariant = variant
                task.outputs.upToDateWhen { false }
            })
            project.tasks.create("publishGooglePlayResources${variant.name.capitalize()}", PublishResourcesTask, { PublishResourcesTask task ->
                task.group = GROUP_NAME
                task.description = "Publish application details to the Google Play Store linsting for the ${variant.name} build"
                task.applicationVariant = variant
                task.outputs.upToDateWhen { false }
            })
            ["alpha", "beta", "production"].each {
                project.tasks.create("publishGooglePlay${it.capitalize()}Application${variant.name.capitalize()}", PublishApplicationTask, { PublishApplicationTask task ->
                    task.group = GROUP_NAME
                    task.description = "Upload application to the Google Play Store linsting for the ${variant.name} build"
                    task.applicationVariant = variant
                    task.releaseType = it
                    task.outputs.upToDateWhen { false }
                    task.dependsOn variant.assemble
                })
            }
        }
    }
}
