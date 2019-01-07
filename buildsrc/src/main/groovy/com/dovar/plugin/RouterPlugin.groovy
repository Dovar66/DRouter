package com.dovar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class RouterPlugin implements Plugin<Project> {

    private StubServiceGenerator stubServiceGenerator = new StubServiceGenerator()

    @Override
    void apply(Project project) {
        /* //创建扩展属性 injectConfig，并将外部属性配置使用InjectPluginExtension进行管理
            创建后可以在build.gradle中使用injectConfig配置属性
         project.extensions.create("injectConfig", InjectPluginExtension)*/

        stubServiceGenerator.injectStubServiceToManifest(project)

        def android = project.extensions.findByType(AppExtension)
        println("================自定义插件成功！==========")
        android.registerTransform(new RouterTransform(project,stubServiceGenerator))
        println("================自定义插件成功！==========")
    }
}