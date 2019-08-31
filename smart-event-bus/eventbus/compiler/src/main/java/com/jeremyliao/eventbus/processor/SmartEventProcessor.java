package com.jeremyliao.eventbus.processor;

import com.google.auto.service.AutoService;
import com.jeremyliao.eventbus.base.annotation.SmartEvent;
import com.jeremyliao.eventbus.base.annotation.SmartEventConfig;
import com.jeremyliao.eventbus.processor.bean.EventInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class SmartEventProcessor extends AbstractProcessor {

    private static final String TAG = "[SmartEventProcessor]";

    private static final String DEFAULT_BUS_NAME = "DefaultSmartEventBus";
    private static final String OBSERVABLE_PACKAGE_NAME = "com.jeremyliao.liveeventbus.core";
    private static final String OBSERVABLE_CLASS_NAME = "Observable";
    private static final String EVENTBUS_PACKAGE_NAME = "com.jeremyliao.liveeventbus";
    private static final String EVENTBUS_CLASS_NAME = "LiveEventBus";

    Filer filer;
    Types types;
    Elements elements;

    String defaultPackageName = null;
    String packageName = null;
    String busName = null;
    List<EventInfo> eventInfos = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(com.jeremyliao.eventbus.base.annotation.SmartEvent.class.getCanonicalName());
        annotations.add(SmartEventConfig.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            processAnnotations(roundEnvironment);
        } else {
            generateBusCode();
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(SmartEvent.class)) {
            TypeElement typeElement = (TypeElement) element;
            PackageElement packageElement = elements.getPackageOf(element);
            String packageName = packageElement.getQualifiedName().toString();
            String className = typeElement.getSimpleName().toString();
            if (defaultPackageName == null) {
                defaultPackageName = packageName;
            }
            String targetClassName = packageName + "." + className;
            List<Object> keys = getAnnotation(element, SmartEvent.class, "keys");
            if (keys != null && keys.size() > 0) {
                for (Object key : keys) {
                    String processedKey = key.toString().replaceAll("\"", "").trim();
                    EventInfo eventInfo = new EventInfo(processedKey, targetClassName);
                    eventInfos.add(eventInfo);
                }
            } else {
                EventInfo eventInfo = new EventInfo(className, targetClassName);
                eventInfos.add(eventInfo);
            }
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(SmartEventConfig.class)) {
            busName = getAnnotation(element, SmartEventConfig.class, "busName");
            packageName = getAnnotation(element, SmartEventConfig.class, "packageName");
        }
    }

    private <T> T getAnnotation(Element element, Class<? extends Annotation> type, String name) {
        String canonicalName = type.getCanonicalName();
        List<? extends AnnotationMirror> annotationMirrors = elements.getAllAnnotationMirrors(element);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (canonicalName.equals(annotationMirror.getAnnotationType().toString())) {
                    if (annotationMirror.getElementValues() != null) {
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                                annotationMirror.getElementValues().entrySet()) {
                            ExecutableElement annotationName = entry.getKey();
                            AnnotationValue annotationValue = entry.getValue();
                            if (annotationName.getSimpleName().toString().equals(name)) {
                                return (T) annotationValue.getValue();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private TypeName getTypeName(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        java.lang.reflect.Type type = getType(name);
        if (type != null) {
            return ClassName.get(type);
        } else {
            return TypeVariableName.get(name);
        }
    }

    private Type getType(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void generateBusCode() {
        if (eventInfos == null || eventInfos.size() == 0) {
            return;
        }
        if (packageName == null || packageName.length() == 0) {
            packageName = defaultPackageName;
        }
        String className = (busName != null && busName.length() > 0) ? busName : DEFAULT_BUS_NAME;
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Auto generate code, do not modify!!!");
        for (EventInfo eventInfo : eventInfos) {
            //添加每一个方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(eventInfo.getKey())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            //添加return
            ClassName baseClassName = ClassName.get(OBSERVABLE_PACKAGE_NAME, OBSERVABLE_CLASS_NAME);
            TypeName typeName = getTypeName(eventInfo.getType());
            TypeName returnType = ParameterizedTypeName.get(baseClassName, typeName);
            methodBuilder.returns(returnType);
            //添加方法体
            ClassName lebClass = ClassName.get(EVENTBUS_PACKAGE_NAME, EVENTBUS_CLASS_NAME);
            methodBuilder.addStatement("return $T.get($S, $T.class)",
                    lebClass, eventInfo.getKey(), typeName);

            builder.addMethod(methodBuilder.build());
        }

        TypeSpec typeSpec = builder.build();
        try {
            JavaFile.builder(packageName, typeSpec)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
