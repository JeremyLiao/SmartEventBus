package com.jeremy.livecallbus;

import com.android.SdkConstants;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.builder.model.AndroidProject;
import com.android.utils.FileUtils;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskOutputs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by liaohailiang on 2018/8/30.
 */
public class LiveCallBusHandler {

    private static final String MODULAR_PATH = "META-INF/livecallbus/";
    private static final String ASSET_PATH = "livecallbus/";
    private static final String ASSET_File = "events_info";

    private List<String> moduleInfos = new ArrayList<>();

    public void findServices(Project project, TaskOutputs outputs, ApplicationVariant variant) {
        if (!outputs.getHasOutput()) {
            LiveCallBusLogger.info("no output");
            return;
        }

        LiveCallBusLogger.info("Start findServices");
        long ms = System.currentTimeMillis();
        FileCollection files = outputs.getFiles();
        for (File file : files) {
            String path = file.getPath();
            FileTree tree = project.fileTree(new HashMap<String, Object>() {{
                LiveCallBusLogger.info("put dir: " + file);
                put("dir", file);
            }});
            LiveCallBusLogger.info("process file tree: " + file);

            processDirectories(path, tree);

            processJarFiles(tree);
        }

        // 处理Service文件
        String servicesFolderName = getClassesDir(project, variant, MODULAR_PATH);
        FileTree servicesTree = project.fileTree(new HashMap<String, Object>() {{
            put("dir", servicesFolderName);
        }});

        LiveCallBusLogger.info("process classes dir:" + servicesTree);
        servicesTree.visit(new FileVisitor() {
            @Override
            public void visitDir(FileVisitDetails fileVisitDetails) {

            }

            @Override
            public void visitFile(FileVisitDetails fileVisitDetails) {
                processFile(fileVisitDetails.getFile());
            }
        });
    }

    public void writeToAssets(Project project, ApplicationVariant variant) {
        if (moduleInfos.isEmpty()) {
            LiveCallBusLogger.info("writeToAssets kipped, no service found");
            return;
        }

        LiveCallBusLogger.info("writeToAssets start...");
        long ms = System.currentTimeMillis();
        File dir = new File(getAssetsDir(project, variant, ASSET_PATH));
        if (dir.isFile()) {
            dir.delete();
        }
        dir.mkdirs();
        try (PrintWriter writer = new PrintWriter(new File(dir, ASSET_File))) {
            for (String moduleInfo : moduleInfos) {
                LiveCallBusLogger.info("writeToAssets moduleInfo: " + moduleInfo);
                writer.println(moduleInfo);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LiveCallBusLogger.info("writeToAssets cost: " + (System.currentTimeMillis() - ms));
    }

    private static String getAssetsDir(Project project, ApplicationVariant variant, String path) {
        return FileUtils.join(project.getBuildDir().getPath(),
                AndroidProject.FD_INTERMEDIATES,
                "assets",
                variant.getDirName(),
                path);
    }

    private void processDirectories(String path, FileTree tree) {
        FileCollection filter = tree.filter(new Spec<File>() {
            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getPath().substring(path.length()).contains(MODULAR_PATH);
            }
        });
        for (File f : filter) {
            processFile(f);
        }
    }

    private void processJarFiles(FileTree tree) {
        FileCollection filter = tree.filter(new Spec<File>() {
            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getName().endsWith(SdkConstants.DOT_JAR);
            }
        });
        for (File jarFile : filter) {
            processJarFile(jarFile);
        }
    }

    private void processFile(File f) {
        LiveCallBusLogger.info("processFile: " + f);
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            processFileContent(reader, f.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.delete();
    }

    private void processFileContent(BufferedReader reader, String moduleName)
            throws IOException {
        LiveCallBusLogger.info("processFileContent moduleName: " + moduleName);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        moduleInfos.add(stringBuffer.toString());
    }

    private void processJarFile(File jarFile) {
        LiveCallBusLogger.info("processJarFile: " + jarFile);
        try (ZipFile zipFile = new ZipFile(jarFile)) {
            if (hasServiceEntry(zipFile)) {
                LiveCallBusLogger.info("hasServiceEntry: " + zipFile);
                File tempFile = new File(jarFile.getPath() + ".tmp");
                try (ZipInputStream is = new ZipInputStream(new FileInputStream(jarFile));
                     ZipOutputStream os = new ZipOutputStream(new FileOutputStream(tempFile))) {
                    ZipEntry entry;
                    while ((entry = is.getNextEntry()) != null) {
                        if (isServiceEntry(entry)) {
                            try {
                                LiveCallBusLogger.info("entry name: " + entry.getName());
                                String moduleName = getSuffix(entry.getName(), "/");
                                LiveCallBusLogger.info("moduleName: " + moduleName);
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(is));
                                processFileContent(reader, moduleName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            os.putNextEntry(new ZipEntry(entry));
                            IOUtils.copy(is, os);
                        }
                    }
                    jarFile.delete();
                    tempFile.renameTo(jarFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSuffix(String s, String splitter) {
        int i = s.lastIndexOf(splitter);
        if (i < 0) {
            return s;
        } else {
            return s.substring(i + splitter.length());
        }
    }

    private boolean hasServiceEntry(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (isServiceEntry(entry)) {
                return true;
            }
        }
        return false;
    }

    private boolean isServiceEntry(ZipEntry entry) {
        return !entry.isDirectory() && entry.getName().startsWith(MODULAR_PATH);
    }

    private static String getClassesDir(Project project, ApplicationVariant variant, String path) {
        return FileUtils.join(project.getBuildDir().getPath(),
                AndroidProject.FD_INTERMEDIATES,
                "classes",
                variant.getDirName(),
                path);
    }

    private static String getAptDir(Project project, ApplicationVariant variant) {
        return FileUtils.join(project.getBuildDir().getPath(),
                AndroidProject.FD_GENERATED,
                "source",
                "apt",
                variant.getDirName());
    }
}
