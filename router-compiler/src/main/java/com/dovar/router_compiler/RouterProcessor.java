package com.dovar.router_compiler;

import com.dovar.router_annotation.Path;
import com.dovar.router_annotation.Router;
import com.dovar.router_annotation.string.RouterStr;
import com.dovar.router_annotation.ServiceLoader;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouterProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private String moduleName;
    private Types types;
    private Elements elements;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(Router.class.getCanonicalName());
        supportTypes.add(Path.class.getCanonicalName());
        supportTypes.add(ServiceLoader.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();

        Map<String, String> options = processingEnv.getOptions();
        if (!options.isEmpty()) {
            moduleName = options.get("moduleName");
        }
        if (moduleName != null && moduleName.length() > 0) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            debug("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            debug("There's no module name, at 'build.gradle', like :\n" +
                    "android {\n" +
                    "    defaultConfig {\n" +
                    "        ...\n" +
                    "        javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                arguments = [moduleName: project.getName()]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("Router::Compiler >>> No module name, for more information, look at gradle log.");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        } else {
            Set<? extends Element> moduleList = roundEnv.getElementsAnnotatedWith(Router.class);
            generateModuleInitMap(moduleList);
            Set<? extends Element> uiPath = roundEnv.getElementsAnnotatedWith(Path.class);
            generateUIPathMap(uiPath);
            Set<? extends Element> serviceLoaders = roundEnv.getElementsAnnotatedWith(ServiceLoader.class);
            generateServiceMap(serviceLoaders);
            return true;
        }
    }

    private void generateServiceMap(Set<? extends Element> serviceLoaders) {
        if (serviceLoaders == null || serviceLoaders.size() == 0) return;
        debug("Process ServiceLoader...");
        MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .returns(HashMap.class);

        initBuilder.addStatement("$T map= new $T()", HashMap.class, HashMap.class);
        for (Element e : serviceLoaders
                ) {
            if (e.getKind() == ElementKind.CLASS) {
                String key = e.getAnnotation(ServiceLoader.class).key();
                ClassName className = ClassName.get((TypeElement) e);
                if (isConcreteSubType(e, RouterStr.Provider_CLASS)) {
                    initBuilder.addStatement("map.put(\"" + key + "\",new $T())", className);
                } else {
                    debug(className.getClass().getName() + "不是有效值，标注为ServiceLoader的类必须是" + RouterStr.Provider_CLASS + "的非抽象子类");
                }
            }
        }
        initBuilder.addStatement("return map");

        TypeSpec typeSpec = TypeSpec.classBuilder(RouterStr.ProxyClassSimpleName + "$$" + moduleName + "$$serviceLoader")
                .addJavadoc("DO NOT EDIT!!! IT WAS GENERATED BY ROUTER.")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(RouterStr.RouterInjectorPackage, RouterStr.ServiceLoaderInjectorSimpleName))
                .addMethod(initBuilder.build())
                .build();
        try {
            JavaFile.builder(RouterStr.ProxyClassPackage, typeSpec)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //only generate maps in mainProcess
    private void generateUIPathMap(Set<? extends Element> paths) {
        if (paths == null || paths.size() == 0) return;
        debug("Process Path...");
//        ClassName application = ClassName.get("android.app", "Application");

        MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
//                .addParameter(application, "app")
//                .addParameter(String.class, "processName")
                .returns(HashMap[].class);

//        initBuilder.addStatement("$T process=app.getPackageName()", String.class);
        initBuilder.addStatement("$T mapActivity= new $T()", HashMap.class, HashMap.class);
        initBuilder.addStatement("$T mapInterceptor= new $T()", HashMap.class, HashMap.class);

//        initBuilder.beginControlFlow("if(processName!=null&&processName.equals(process))");
        for (Element e : paths
                ) {
            if (e.getKind() == ElementKind.CLASS) {
                String path = e.getAnnotation(Path.class).path();
                try {
                    //To get [Class] will throw MirroredTypesException here.
                    Class cls = e.getAnnotation(Path.class).interceptor();
                } catch (MirroredTypesException mte) {
                    List<? extends TypeMirror> typeMirrors = mte.getTypeMirrors();
                    if (typeMirrors != null && typeMirrors.size() > 0) {
                        for (TypeMirror type : typeMirrors) {
                            if (type instanceof Type.ClassType) {
                                Symbol.TypeSymbol typeSymbol = ((Type.ClassType) type).asElement();
                                if (typeSymbol instanceof Symbol.ClassSymbol) {
                                    ClassName className = ClassName.get((TypeElement) typeSymbol);
                                    if (isConcreteSubType(typeSymbol, RouterStr.IInterceptor_CLASS)) {
                                        initBuilder.addStatement("mapInterceptor.put(\"" + path + "\",$T.class)", className);
                                    } else {
                                        debug(className.getClass().getName() + "不是有效值，注解Path的interceptor必须是" + RouterStr.IInterceptor_CLASS + "的实现类");
                                    }
                                }
                            }
                        }
                    }
                }

                ClassName className = ClassName.get((TypeElement) e);
                if (isConcreteSubType(e, "android.app.Activity")) {
                    initBuilder.addStatement("mapActivity.put(\"" + path + "\",$T.class)", className);
                } else {
                    debug(className.getClass().getName() + "不是有效值，path必须是Activity的非抽象子类");
                }
            }
        }
//        initBuilder.endControlFlow();

        initBuilder.addStatement("$T maps={mapActivity,mapInterceptor}", HashMap[].class);
        initBuilder.addStatement("return maps");

        TypeSpec typeSpec = TypeSpec.classBuilder(RouterStr.ProxyClassSimpleName + "$$" + moduleName + "$$path")
                .addJavadoc("DO NOT EDIT!!! IT WAS GENERATED BY ROUTER.")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(RouterStr.RouterInjectorPackage, RouterStr.PathInjectorSimpleName))
                .addMethod(initBuilder.build())
                .build();
        try {
            JavaFile.builder(RouterStr.ProxyClassPackage, typeSpec)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void generateModuleInitMap(Set<? extends Element> modules) {
        if (modules == null || modules.size() == 0) return;
        debug("Process Router...");
        ClassName application = ClassName.get("android.app", "Application");
        ClassName baseApplicationLogic = ClassName.get(RouterStr.BaseAppInitPackage, RouterStr.BaseAppInitSimpleName);
        MethodSpec registerModule = MethodSpec.methodBuilder("registerModule")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(Class.class, "logicClass")
                .addParameter(application, "app")
                .addStatement("if (logicClass == null) return")
                .beginControlFlow("try")
                .addStatement("$T instance = ($T)logicClass.newInstance()", baseApplicationLogic, baseApplicationLogic)
                .addStatement("instance.setApplication(app)")
                .addStatement("instance.onCreate()")
                .endControlFlow("catch ($T e) { e.printStackTrace(); }", ClassName.get(Exception.class))
                .build();

        MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(application, "app");
//                .addParameter(String.class, "processName");

        for (Element e : modules
                ) {
            if (e.getKind() == ElementKind.CLASS) {
                ClassName className = ClassName.get((TypeElement) e);
                initBuilder.addStatement("$N($T.class,app)", registerModule, className);
            }
        }


        TypeSpec typeSpec = TypeSpec.classBuilder(RouterStr.ProxyClassSimpleName + "$$" + moduleName)
                .addJavadoc("DO NOT EDIT!!! IT WAS GENERATED BY ROUTER.")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(RouterStr.RouterInjectorPackage, RouterStr.RouterInjectorSimpleName))
                .addMethod(registerModule)
                .addMethod(initBuilder.build())
                .build();
        try {
            JavaFile.builder(RouterStr.ProxyClassPackage, typeSpec)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    //check Not-ABSTRACT
    public boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(Modifier.ABSTRACT);
    }

    public boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    public boolean isSubType(TypeMirror type, String className) {
        return type != null && types.isSubtype(type, typeMirror(className));
    }

    //String --> TypeElement
    public TypeElement typeElement(String className) {
        return elements.getTypeElement(className);
    }

    //String --> TypeMirror
    public TypeMirror typeMirror(String className) {
        return typeElement(className).asType();
    }

    private void debug(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
