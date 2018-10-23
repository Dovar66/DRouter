## 组件化的优点

1. 解耦，使得各自业务模块专注于自己的业务实现，不必关心其他业务模块。
2. 可配置，复用性强，针对不同的App，可以有不同的模块而不必做出大的改变。
3. 每个模块可以独立运行，方便开发调试。

## 组件化开发的实现

![路由原理图](assets/router.png)

### 1.组件跳转

    拦截器

### 2.进程内组件间通信机制
1. EventBus、RxBus
2. 接口下沉

### 3.多进程通信机制
通过向Router注册Action达到对其他进程暴露服务

### 4.资源文件冲突
#### 1. AndroidManifest.xml合并：
  每个module都有一份自己的AndroidManifest清单文件，在APP的编译过程中最终会将所有module的清单文件合并成一份。

  我们可以在配置为Application的module下的build/intermediates/manifests路径下找到合成后的AndroidManifest文件,对比编译前后的差异就能大致分析出合并规则和冲突处理规则。

  需要注意的是如果在多个module中出现同名资源(如 android:label="@string/app_name")，且同名资源被合成后的AndroidManifest.xml引用，则会优先取用当前ApplicationModule的资源。
#### 2. R文件：
libModule中R文件里的id不再是静态常量，而是静态变量，所以不能再使用switch..case..语法操作资源id

#### 3. 其他resource：
1. 防止出现同名资源，建议每个module下的资源命名都增加唯一识别字符，如module-live中的都带前缀"mlive_"，mlive_icon_close.png

2. 关于资源的拆分，一些style、常见的string、共用的图片、drawable等资源，建议存放在common module当中。对于属于不同模块的资源则应该存放在各自的module中。
### 5.Module可独立运行配置
    第一步：在 工程根目录 下的gradle.properties下声明对应module是否独立运行的属性，如isDebugMode。因为gradle.properties中申明的属性在各个module的build.gradle中可以被直接访问
    第二步：在module的build.gradle文件中加上红框内的三个部分：
    设置module类型：
            if (isDebugMode.toBoolean()) {
                apply plugin: 'com.android.library'
            } else {
                apply plugin: 'com.android.application'
            }
    设置applicationId：
            if (!isDebugMode.toBoolean()) {
                applicationId "com.dovar.router"
            }
    使用sourceSets配置AndroidManifest等
           sourceSets {
                  main {
                      if (!rootProject.ext.isModule1Debug) {
                          manifest.srcFile 'src/debug/AndroidManifest.xml'
                          java.srcDir 'src/debug/java/'
                          res.srcDirs=['src/debug/res']
                      } else {
                          manifest.srcFile 'src/release/AndroidManifest.xml'
                          java.srcDir 'src/release/java/'
                      }
                  }
              }
## 注意事项
1. 使用aar的形式引入依赖，有助于减少编译时间。
2. 四大组件应该在各自module里面声明。
## Router
### 框架特点
    使用编译时注解自动引导Module的初始化.
### 如何配置
1.在BaseModule中添加依赖：

    api project(':router-api')

2.在其他需要用到Router的组件中添加注解处理器的依赖：

    annotationProcessor project(':router-compiler')

    同时在这些组件的defaultConfig中配置注解参数：

     defaultConfig {
            javaCompileOptions {
                annotationProcessorOptions {
                    arguments = [moduleName: project.getName()]
                }
            }
        }

### 如何使用
1.在需要向外提供服务的组件中创建BaseApplicationLogic的子类，然后在其中注册Provider:

    @Module(process = "com.dovar.app")
    public class UserAppInit extends BaseApplicationLogic {
        @Override
        public void onCreate() {
            super.onCreate();
            registerProvider("user", new UserProvider());
        }
    }

2.创建相应的Provider子类，并在类中注册Activity和Action:

    public class UserProvider extends Provider {
        @Override
        protected void registerActions() {
            //向外提供界面
            registerActivities();
            //向外提供服务
            registerService();
        }

3.向Router注册Activity供其他组件使用:

    registerActivity("user/DemoActivity", new ActivityAction() {
                @Override
                public void navigateTo(Context mContext, Postcard mPostcard) {
                    DemoActivity.jump(mContext);
                }
            });

        然后在其他组件中使用Router进行页面跳转:

        Router.instance().navigator("user/DemoActivity").navigateTo(mContext);

4.向Router注册Action供其他组件使用:

        registerAction("demoAction", new Action() {
                @Override
                public RouterResponse invoke(Bundle requestData, Object callback) {
                    DemoService.request();
                    return null;
                }
            });

        然后在其他组件内使用Action:

        Router.instance().provider("user").action("demoAction").route();
## TODO LIST:
1.通过编译时注解实现自动注册Provider,并要同时支持Action的主动与自动注册，自动注册的Action存放在代理类中。

2.异步请求逻辑待优化。

测试Git插件
