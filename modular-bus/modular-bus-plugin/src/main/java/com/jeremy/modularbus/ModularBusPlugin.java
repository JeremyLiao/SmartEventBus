package com.jeremy.modularbus;

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
public class ModularBusPlugin implements Plugin<Project> {

    private static final String TAG = "-----------ModularBusPlugin----------";

    @Override
    public void apply(Project project) {
        System.out.println(TAG + "enter apply");
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                applyTasks(project);
            }
        });
    }

    private void applyTasks(Project project) {
        System.out.println(TAG + "enter applyTasks");
        DomainObjectSet<ApplicationVariant> variants =
                project.getExtensions().findByType(AppExtension.class).getApplicationVariants();
        for (ApplicationVariant variant : variants) {
            String variantName = capitalize(variant.getName());
            System.out.println(TAG + "variantName: " + variantName);
            ModularBusProcessor processor = new ModularBusProcessor();
            Task mergeJavaResTask = project.getTasks().findByName(
                    "transformResourcesWithMergeJavaResFor" + variantName);
            mergeJavaResTask.doLast(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    System.out.println(TAG + "task: " + task.toString());
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
