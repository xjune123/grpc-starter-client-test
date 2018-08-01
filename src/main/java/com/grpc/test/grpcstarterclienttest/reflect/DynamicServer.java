package com.grpc.test.grpcstarterclienttest.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.log4j.Logger;

public class DynamicServer {
    private static Logger log = Logger.getLogger(DynamicServer.class);
    public static Map<String, Class> clazzMap = new HashMap<String, Class>();
    public static Map<String, Object> objMap = new HashMap<String, Object>();
    public static Map<String, Method> metMap = new HashMap<String, Method>();
    public static Map<String, Integer> METHOD_ACCESS_INDEX = new HashMap<String, Integer>();

    public static Map<String, MethodAccess> METHOD_ACCESS = new HashMap<String, MethodAccess>();

    public static Map<String, Map<String, String>> typeMap = new HashMap<String, Map<String, String>>();


    /**
     * 通过反射+缓存高效的调用某个类里面的某个方法
     */
    public static Object cacheExce(String clazz, String method, Object[] os, Class[] cs) throws NoSuchMethodException {
        try {
            int size = 0;
            if (cs != null) {
                size = cs.length;
            }

            Method m = metMap.get(clazz + "_" + method + "_" + size);//用于区分重载的方法
            Object obj = objMap.get(clazz);

            if (m == null || obj == null) {
                Class cl = clazzMap.get(clazz);
                if (cl == null) {
                    cl = Class.forName(clazz);
                    clazzMap.put(clazz, cl);//缓存class对象
                }

                if (obj == null) {
                    obj = cl.newInstance();
                    objMap.put(clazz, obj);//缓存对象的实例
                }

                if (m == null) {
                    m = cl.getMethod(method, cs);
                    metMap.put(clazz + "_" + method + "_" + size, m);//缓存Method对象
                }
            }
            //动态调用某个对象中的public声明的方法
            return m.invoke(obj, os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchMethodException();
        }
    }


    /**
     * 通过反射+缓存高效的调用某个类里面静态方法
     */
    public static Object cacheStaticExce(String clazz, String method) throws NoSuchMethodException {
        try {
            Method m = metMap.get(clazz + "_" + method);//用于区分重载的方法
            if (m == null) {
                Class cl = clazzMap.get(clazz);
                if (cl == null) {
                    cl = Class.forName(clazz);
                    clazzMap.put(clazz, cl);//缓存class对象
                }

                if (m == null) {
                    m = cl.getMethod(method, null);
                    metMap.put(clazz + "_" + method, m);//缓存Method对象
                }
            }
            //动态调用某个对象中的public声明的方法
            return m.invoke(null, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchMethodException();
        }
    }


    /**
     * 通过反射+缓存高效的调用某个类里面静态方法
     */
    public static Object cacheAsmExce(String clazz, String method) throws NoSuchMethodException {
        try {
            Integer methodIndex = METHOD_ACCESS_INDEX.get(clazz + "_" + method);//用于区分重载的方法

            MethodAccess methodAccess = METHOD_ACCESS.get(clazz);

            if (methodAccess == null) {
                Class cl = clazzMap.get(clazz);
                if (cl == null) {
                    cl = Class.forName(clazz);
                    //缓存class对象
                    clazzMap.put(clazz, cl);
                }
                //注意结果要缓存
                methodAccess = MethodAccess.get(cl);
                METHOD_ACCESS.put(clazz, methodAccess);
            }

            if (methodIndex == null) {

                methodIndex = methodAccess.getIndex(method);
                METHOD_ACCESS_INDEX.put(clazz + "_" + method, methodIndex);
            }
            //动态调用某个对象中的public声明的方法
            return methodAccess.invoke(null, methodIndex);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchMethodException();
        }
    }


    /**
     * 通过反射+缓存获取指定类里面 的指定方法的返回类型
     */
    public static String cacheType(String clazz, String method) throws ClassNotFoundException {
        Map<String, String> clazzs = typeMap.get(clazz);
        if (clazzs == null) {
            Map<String, String> mmap = new HashMap<String, String>();
            Class cl = Class.forName(clazz);
            //获取某个类里面的所有的公共的方法
            Method[] ms = cl.getMethods();
            for (Method m : ms) {
                //遍历出所有的方法，将方法名和返回类型存在静态的map中（缓存）
                mmap.put(m.getName(), m.getGenericReturnType().toString());
            }
            clazzs = mmap;
            typeMap.put(clazz, mmap);
        }
        return clazzs.get(method);
    }

}