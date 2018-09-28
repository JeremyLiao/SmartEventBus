package com.jeremy.livecallbus;

import com.google.auto.service.AutoService;
import com.jeremy.livecallbus.anotation.LiveCallEvents;
import com.jeremy.livecallbus.inner.bean.LiveCallEvent;
import com.jeremy.livecallbus.inner.bean.LiveCallEventsInfo;
import com.jeremy.livecallbus.inner.utils.GsonUtil;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static com.google.common.base.Charsets.UTF_8;

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class LiveCallBusProcessor extends AbstractProcessor {

    private static final String TAG = "[LiveCallBusProcessor]";
    private static final String MODULAR_BUS_PATH = "META-INF/livecallbus/";
    public static final String GEN_PKG = "com.jeremy.livecallbus.generated";
    public static final String EVENTSDEFINE_CLASS_NAME = "com.jeremy.livecallbus.base.IEventsDefine";
    public static final String OBSERVABLE_CLASS_NAME = "com.jeremy.livecallbus.Observable<%s>";
    public static final String OBJECT_CLASS_NAME = "java.lang.Object";
    public static final String OANOTATION_NAME = "com.jeremy.livecallbus.anotation.EventType";
    public static final String CLN_PREFIX = "EventsDefineOf";

    protected Filer filer;
    protected Types types;
    protected Elements elements;

    private Map<String, LiveCallEventsInfo> moduleInfoMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            generateConfigFiles();
        } else {
            processAnnotations(roundEnvironment);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        process(roundEnvironment);
    }

    private void process(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(LiveCallEvents.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                PackageElement packageElement = elements.getPackageOf(element);
                LiveCallEvents liveCallEvents = typeElement.getAnnotation(LiveCallEvents.class);
                String originalClassName = typeElement.getSimpleName().toString();
                String moduleName = liveCallEvents.module();
                if (moduleName == null || moduleName.length() == 0) {
                    moduleName = packageElement.getQualifiedName().toString();
                }
                System.out.println(TAG + "moduleName: " + moduleName);
                LiveCallEventsInfo eventsInfo = new LiveCallEventsInfo();
                eventsInfo.setModule(moduleName);
                List<LiveCallEvent> events = new ArrayList<>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for (Element element1 : enclosedElements) {
                    if (element1.getKind() == ElementKind.FIELD) {
                        VariableElement variableElement = (VariableElement) element1;

                        String variableName = variableElement.getSimpleName().toString();
                        Object variableValue = variableElement.getConstantValue();
                        String eventType = getEventType(element1);
                        System.out.println(TAG + "variableName: " + variableName + " | variableValue: " + variableValue);
                        LiveCallEvent event = new LiveCallEvent();
                        event.setName(variableName);
                        if (variableValue instanceof String) {
                            event.setValue((String) variableValue);
                        }
                        event.setType(eventType);
                        events.add(event);
                    }
                }
                eventsInfo.setEvents(events);
                String interfaceClassName = generateEventInterfaceClass(eventsInfo, originalClassName, moduleName);
                eventsInfo.setInterfaceClassName(interfaceClassName);
                moduleInfoMap.put(moduleName, eventsInfo);
            }
        }
    }

    private String getEventType(Element element1) {
        List<? extends AnnotationMirror> annotationMirrors = elements.getAllAnnotationMirrors(element1);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (OANOTATION_NAME.equals(annotationMirror.getAnnotationType().toString())) {
                    System.out.println(TAG + "annotationMirror: " + annotationMirror.getAnnotationType().toString());
                    if (annotationMirror.getElementValues() != null) {
                        for (AnnotationValue annotationValue : annotationMirror.getElementValues().values()) {
                            return annotationValue.getValue().toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    private String generateEventInterfaceClass(LiveCallEventsInfo eventsInfo, String originalClassName, String moduleName) {
        String interfaceName = generateClassName(originalClassName);
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeVariableName.get(EVENTSDEFINE_CLASS_NAME));
        for (LiveCallEvent event : eventsInfo.getEvents()) {
            String returnTypeName = null;
            if (event.getType() == null || event.getType().length() == 0) {
                returnTypeName = String.format(OBSERVABLE_CLASS_NAME, OBJECT_CLASS_NAME);
            } else {
                returnTypeName = String.format(OBSERVABLE_CLASS_NAME, event.getType());
            }
            builder.addMethod(MethodSpec.methodBuilder(event.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeVariableName.get(returnTypeName))
                    .build());
        }
        TypeSpec typeSpec = builder.build();
        String packageName = GEN_PKG + "." + moduleName;
        try {
            JavaFile.builder(packageName, typeSpec)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String className = packageName + "." + interfaceName;
        System.out.println(TAG + "generateEventInterfaceClass: " + className);
        return className;
    }

    private String generateClassName(String originalClassName) {
        return CLN_PREFIX + originalClassName;
    }

    private static String upperCaseFirst(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(LiveCallEvents.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateConfigFiles() {
        System.out.println(TAG + "generateConfigFiles");
        if (moduleInfoMap.size() == 0) {
            System.out.println(TAG + "no module info found!!");
            return;
        }
        if (moduleInfoMap.size() > 1) {
            System.out.println(TAG + "more than one module info found, not allowed!!");
            return;
        }
        for (String name : moduleInfoMap.keySet()) {
            LiveCallEventsInfo liveCallEventsInfo = moduleInfoMap.get(name);
            writeServiceFile(MODULAR_BUS_PATH + name, GsonUtil.toJson(liveCallEventsInfo));
        }
    }

    public void writeServiceFile(String fileName, String content) {
        if (isEmpty(fileName) || isEmpty(content)) {
            return;
        }
        System.out.println(TAG + "writeServiceFile fileName: " + fileName + " content: " + content);
        try {
            FileObject res = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            System.out.println(TAG + res.getName());
            OutputStream os = res.openOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, UTF_8));
            writer.write(content);
            writer.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(TAG + e.toString());
        }
    }

    public static boolean isEmpty(String path) {
        return path == null || path.length() == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }
}
