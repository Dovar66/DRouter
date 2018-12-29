## 组件化的优点

1. 业务隔离，使得各业务模块专注于自己的业务实现，而不必关心其他业务模块.
2. 单独调试，每个模块可以独立运行，方便开发调试.
3. 组件可复用性，针对有重叠业务的不同APP，可直接使用组件来组装.
4. 适合AOP.

## 组件化开发的实现(Router)

![路由原理图](assets/router.png)

### 1.组件跳转

    可添加跳转拦截器.

### 2.进程内组件间通信机制

    1. 服务提供者向Router注册Action对其他进程暴露服务.
    2. 多对多：LiveEventBus.

### 3.多进程通信机制

    1. 服务提供者向Router注册Action对其他进程暴露服务.
    2. 多对多：(跨进程的多对多通信呢？)

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

     更好的实现方式应该是这样的：设置一个可运行的壳module，如demo中的app.在壳module中根据需要配置依赖

## 建议

    1. 使用aar的形式引入依赖，有助于减少编译时间。
    2. 四大组件应该在各自module里面声明。

## Router
### 框架特点

    * 使用编译时注解自动引导Module的初始化.
    * 支持跨进程调用，且不需要使用者去bindService或自定义AIDL.

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

0.注意：如果你的项目是多进程项目，需要使用多进程广域路由，那么请让你的Application添加实现 IMultiProcess 接口，广域路由默认是关闭状态，只有实现了该接口才会启用。

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

### 如何使用事件总线：LiveEventBus

#### 订阅消息
- **observe**
生命周期感知，不需要手动取消订阅

```java
LiveEventBus.instance()
	.with("key_name", String.class)
	.observe(this, new Observer<String>() {
	    @Override
	    public void onChanged(@Nullable String s) {
	    }
	});
```
- **observeForever**
需要手动取消订阅

```java
LiveEventBus.instance()
	.with("key_name", String.class)
	.observeForever(observer);
```

```java
LiveEventBus.instance()
	.with("key_name", String.class)
	.removeObserver(observer);
```

#### 发送消息
- **setValue**
在主线程发送消息
```java
LiveEventBus.instance().with("key_name").setValue(value);
```
- **postValue**
在后台线程发送消息，订阅者会在主线程收到消息
```java
LiveEventBus.instance().with("key_name").postValue(value);
```
#### Sticky模式
支持在注册订阅者的时候设置Sticky模式，这样订阅者可以接收到订阅之前发送的消息

- **observeSticky**
生命周期感知，不需要手动取消订阅，Sticky模式

```java
LiveEventBus.instance()
        .with("sticky_key", String.class)
        .observeSticky(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s){
            }
        });
```
- **observeStickyForever**
需要手动取消订阅，Sticky模式

```java
LiveEventBus.instance()
        .with("sticky_key", String.class)
        .observeStickyForever(observer);
```

```java
LiveEventBus.instance()
        .with("sticky_key", String.class)
        .removeObserver(observer);
```

## TODO LIST:
1.通过编译时注解实现自动注册Provider,并要同时支持Action的主动与自动注册，自动注册的Action存放在代理类中。

2.异步请求逻辑待优化。

3.增加支持系统Action跳转，如拨打电话、发送短信

4.支持跨APP的组件调用。

5.异步调用需要设置超时。


