package com.dovar.router_api.router.cache;

/**
 * 由javassist在编译期间往方法中注入实现
 */
public class JavassistGenerateMethod {
    /**
     * gradle插件会修改这个方法，插入类似如下代码:
     * Map hashMap = new HashMap();
     * hashMap.put(":a", CommuStubService0.class);
     * hashMap.put(":b", CommuStubService1.class);
     * hashMap.put("c", CommuStubService2.class);
     * hashMap.put(":d", CommuStubService3.class);
     * hashMap.put("e", CommuStubService4.class);
     * hashMap.put(":f", CommuStubService5.class);
     * hashMap.put(":g", CommuStubService6.class);
     * hashMap.put("h", CommuStubService7.class);
     * hashMap.put(":i", CommuStubService8.class);
     * hashMap.put(":j", CommuStubService9.class);
     * hashMap.put(":k", CommuStubService10.class);
     * return hashMap;
     */
    //由于javassist不支持泛型，故不能返回Class,只能返回Object
    public static Object getTargetService() {

        return null;
    }

    public static Object getProxyClassNames() {
       /* ArrayList list = new ArrayList();
        list.add();
        return list;*/
        return null;
    }
}
