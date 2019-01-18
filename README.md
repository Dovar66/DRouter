
## DRouter：支持多进程的组件化方案

[demo下载]()

### 框架特点

    * 支持给Activity定义url，然后通过url跳转到Activity的页面路由
    * 页面路由支持添加拦截器.
    * 跨进程的事件总线.
    * 支持跨进程的API调用，且不需要使用者去bindService或自定义AIDL.
    * 充分实现模块解耦，页面路由、API调用、事件总线均支持跨模块使用.
    * 基于AOP引导Module的初始化以及页面、拦截器、provider的自动注册.

### 如何配置
1.在BaseModule中添加依赖：

    api project(':router-api')

2.在其他需要用到DRouter的组件中添加注解处理器的依赖：

    annotationProcessor project(':router-compiler')

    同时在这些组件的defaultConfig中配置注解参数，指定唯一的组件名：

     defaultConfig {
            javaCompileOptions {
                annotationProcessorOptions {
                    arguments = [moduleName: project.getName()]
                }
            }
        }

3.多进程配置：

    * 如果你的项目需要使用多进程广域路由，那么请让你的Application实现 IMultiProcess 接口，广域路由默认是关闭状态，只有实现了该接口才会启用。

    * 在App module的build.gradle文件中，且必须在apply plugin: 'com.android.application'之后引用编译插件RouterPlugin，具体如下：

        apply plugin: 'com.android.application'

        apply plugin: "com.dovar.router.plugin" //必须在apply plugin: 'com.android.application'之后，否则找不到AppExtension

        buildscript {
            repositories {
                google()
                maven {
                    url "https://plugins.gradle.org/m2/"
                }
            }
            dependencies {
                classpath "gradle.plugin.RouterPlugin:plugin:1.1.6"
            }
        }

### 如何使用

#### 在Application.onCreate()中完成初始化

    DRouter.init(app);

#### 页面路由

     添加Path注解,可通过interceptor设置拦截器:

     @Path(path = "/b/main", interceptor = BInterceptor.class)
     public class MainActivity extends AppCompatActivity {

         @Override
         protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.module_b_activity_main);
         }
     }

     然后在项目中使用DRouter进行页面跳转:

     DRouter.navigator("/b/main").navigateTo(mContext);

#### 动作路由(API调用)

    创建相应的Provider子类并添加ServiceLoader注解，然后在类中注册Action:

    @ServiceLoader(key = "a")
    public class AProvider extends Provider {
        @Override
        protected void registerActions() {

            registerAction("test1", new Action() {
                @Override
                public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                    Toast.makeText(appContext, "弹个窗", Toast.LENGTH_SHORT).show();
                    return null;
                }
            });

            registerAction("test2", new Action() {
                 @Override
                 public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                    if (extra instanceof Context) {
                       Toast.makeText((Context) extra, params.getString("content"), Toast.LENGTH_SHORT).show();
                    }
                    return null;
                 }
            });
        }
    }

    接下来就可以在项目中使用:

           DRouter.router("a","test1").route();

           DRouter.router("a","test2")
                           .withString("content","也弹个窗")
                           .extra(context)
                           .route();

#### 事件总线

##### 订阅事件

    生命周期感知，不需要手动取消订阅：
    
        DRouter.subscribe(this, ServiceKey.EVENT_A, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件A", Toast.LENGTH_SHORT).show();
            }
        });
    
    需要手动取消订阅：
    
        Observer<Bundle> mObserver = DRouter.subscribeForever("event_a", new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件A", Toast.LENGTH_SHORT).show();
            }
        });

##### 发布事件(在任意线程)

     Bundle bundle = new Bundle();
     bundle.putString("content", "事件A");
     DRouter.publish(ServiceKey.EVENT_A, bundle);

##### 退订事件(通过subscribeForever()订阅时,需要及时取消订阅)

     DRouter.unsubscribe("event_a", mObserver);

#### 创建组件初始化入口(非必须)

    在组件中创建BaseAppInit的子类，添加Router注解:

        @Router
        public class AInit extends BaseAppInit {

            @Override
            public void onCreate() {
                super.onCreate();
                //与Application.onCreate()的执行时机相同
                //建议在这里完成组件内的初始化工作
            }
        }

### TODO LIST

* 支持跨APP的组件调用。
