package com.jeremyliao.im.processor;

import com.google.auto.service.AutoService;
import com.jeremyliao.im.base.annotation.InvokingEventsDefine;
import com.jeremyliao.im.base.common.IEventsDefine;
import com.jeremyliao.im.processor.bean.EventInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class InvokingMessageProcessor extends AbstractProcessor {

    private static final String TAG = "[InvokingMessageProcessor]";
    //    private static final String MODULAR_BUS_PATH = "META-INF/invoking-message/";
    private static final String LEB_PACKAGE_NAME = "com.jeremyliao.liveeventbus";
    private static final String LEB_CLASS_NAME = "LiveEventBus";
    private static final String LEB_OBSERVER_CLASS_NAME = "Observable";
    private static final String ANNOTATION_NAME = "com.jeremyliao.im.base.annotation.EventType";
    private static final String GEN_PKG = ".generated.im";
    private static final String CLN_PREFIX = "EventsDefineAs";

    Filer filer;
    Types types;
    Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(InvokingEventsDefine.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
        } else {
            processAnnotations(roundEnvironment);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        process(roundEnvironment);
    }

    private void process(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(InvokingEventsDefine.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                PackageElement packageElement = elements.getPackageOf(element);
                String originalPackageName = packageElement.getQualifiedName().toString();
                String originalClassName = typeElement.getSimpleName().toString();
                List<EventInfo> events = new ArrayList<>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for (Element element1 : enclosedElements) {
                    if (element1.getKind() == ElementKind.FIELD) {
                        VariableElement variableElement = (VariableElement) element1;

                        String variableName = variableElement.getSimpleName().toString();
                        Object variableValue = variableElement.getConstantValue();
                        String eventType = getEventType(element1);
                        System.out.println(TAG + "variableName: " + variableName + " | variableValue: " + variableValue);
                        EventInfo eventInfo = new EventInfo();
                        eventInfo.setName(variableName);
                        if (variableValue instanceof String) {
                            eventInfo.setValue((String) variableValue);
                        }
                        eventInfo.setType(eventType);
                        events.add(eventInfo);
                    }
                }
                generateEventInterfaceClass(events, originalPackageName, originalClassName);
            }
        }
    }

    private String getEventType(Element element1) {
        List<? extends AnnotationMirror> annotationMirrors = elements.getAllAnnotationMirrors(element1);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (ANNOTATION_NAME.equals(annotationMirror.getAnnotationType().toString())) {
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

    private String generateEventInterfaceClass(List<EventInfo> events, String originalPackageName, String originalClassName) {
        String interfaceName = generateClassName(originalClassName);
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IEventsDefine.class)
                .addJavadoc("Auto generate code, do not modify!!!");
        for (EventInfo event : events) {
            ClassName lebClassName = ClassName.get(LEB_PACKAGE_NAME, LEB_CLASS_NAME);
            ClassName obsClassName = lebClassName.nestedClass(LEB_OBSERVER_CLASS_NAME);
            TypeName returnType;
            String eventTypeStr = event.getType();
            if (eventTypeStr == null || eventTypeStr.length() == 0) {
                returnType = ParameterizedTypeName.get(obsClassName, ClassName.get(Object.class));
            } else {
                Type eventType = getType(eventTypeStr);
                if (eventType != null) {
                    returnType = ParameterizedTypeName.get(obsClassName, ClassName.get(eventType));
                } else {
                    returnType = ParameterizedTypeName.get(obsClassName, TypeVariableName.get(eventTypeStr));
                }
            }
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(event.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(returnType);
            builder.addMethod(methodBuilder.build());
        }
        TypeSpec typeSpec = builder.build();
        String packageName = originalPackageName + GEN_PKG;
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

    private Type getType(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }
}
