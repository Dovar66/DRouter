package com.dovar.router_compiler;

import com.dovar.router_annotation.Router;
import com.dovar.router_annotation.RouterAnno;
import com.dovar.router_annotation.RouterStr;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
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
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouterProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private String moduleName;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(Router.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
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
        debug("process router...");

        if (annotations.isEmpty()) {
            return false;
        } else {
            Set<? extends Element> moduleList = roundEnv.getElementsAnnotatedWith(Router.class);
            generateModulesProviderMappingInit(moduleList);
            return true;
        }
    }

    private void generateModulesProviderMappingInit(Set<? extends Element> modules) {
        ClassName application = ClassName.get("android.app", "Application");
        ClassName baseApplicationLogic = ClassName.get(RouterStr.BaseApplicationLogicPackage, RouterStr.BaseApplicationLogicSimpleName);
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
                .addParameter(application, "app")
                .addParameter(String.class, "processName");

        for (Element e : modules
                ) {
            if (e.getKind() == ElementKind.CLASS) {
                String name = e.getAnnotation(Router.class).process();
                ClassName className = ClassName.get((TypeElement) e);
                initBuilder.addStatement("$T process=\"" + name + "\"", String.class);
                initBuilder.addStatement("if(processName!=null&&processName.equals(\"" + RouterAnno.MainProcess + "\"))")
                        .addStatement("process=app.getPackageName()");
                initBuilder.beginControlFlow("if(processName!=null&&processName.equals(process))")
                        .addStatement("$N($T.class,app)", registerModule, className)
                        .endControlFlow();
            }
        }


        TypeSpec providerInit = TypeSpec.classBuilder(RouterStr.proxyClassSimpleName + "$$" + moduleName)
                .addJavadoc("DO NOT EDIT!!! IT WAS GENERATED BY ROUTER.")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(RouterStr.routerInjectorPackage, RouterStr.routerInjectorSimpleName))
                .addMethod(registerModule)
                .addMethod(initBuilder.build())
                .build();
        try {
            JavaFile.builder(RouterStr.proxyClassPackage, providerInit)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void debug(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
