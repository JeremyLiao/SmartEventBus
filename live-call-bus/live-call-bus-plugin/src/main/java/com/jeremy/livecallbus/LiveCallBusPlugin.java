package com.jeremy.livecallbus;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * Created by liaohailiang on 2018/8/30.
 */
public class LiveCallBusPlugin implements Plugin<Project> {

    public static final String NAME = "LiveCallBus";

    @Override
    public void apply(Project project) {
        LiveCallBusExtension extension = project.getExtensions().create(NAME, LiveCallBusExtension.class);
        LiveCallBusLogger.setConfig(extension);
        LiveCallBusLogger.info("Enter LiveCallBusPlugin");
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                if (extension.getEnable()) {
                    applyTasks(project);
                }
            }
        });
    }

    private void applyTasks(Project project) {
        DomainObjectSet<ApplicationVariant> variants =
                project.getExtensions().findByType(AppExtension.class).getApplicationVariants();
        for (ApplicationVariant variant : variants) {
            String variantName = capitalize(variant.getName());
            LiveCallBusLogger.info("variantName: " + variantName);
            LiveCallBusHandler processor = new LiveCallBusHandler();
            Task mergeJavaResTask = project.getTasks().findByName(
                    "transformResourcesWithMergeJavaResFor" + variantName);
            mergeJavaResTask.doLast(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    LiveCallBusLogger.info("task: " + task.toString());
                    processor.findServices(project, mergeJavaResTask.getOutputs(), variant);
                    processor.writeToAssets(project, variant);
                }
            });
        }
    }

    public static String capitalize(CharSequence str) {
        return (str == null || str.length() == 0) ? "" : "" + Character.toUpperCase(str.charAt(0))
                + str.subSequence(1, str.length());
    }
}
