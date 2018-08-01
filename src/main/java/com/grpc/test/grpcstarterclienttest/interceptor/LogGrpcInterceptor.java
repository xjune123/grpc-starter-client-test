package com.grpc.test.grpcstarterclienttest.interceptor;

import com.grpc.test.grpcstarterclienttest.reflect.DynamicServer;
import com.test.grpc.performance.TestPerformanceGrpc;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 2016/12/6
 */
public class LogGrpcInterceptor implements ClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogGrpcInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        //log.info(method.getFullMethodName());
        //method = (MethodDescriptor<ReqT, RespT>) TestPerformanceGrpc.getSayHelloBMethod();
        /*try {
            Class<?> testClass = Class.forName("com.test.grpc.performance.TestPerformanceGrpc");
            Method method1 = testClass.getMethod("getSayHelloBMethod");
            method = (MethodDescriptor<ReqT, RespT>) method1.invoke(null, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/
        try {
            method = (MethodDescriptor<ReqT, RespT>) DynamicServer.cacheAsmExce("com.test.grpc.performance.TestPerformanceGrpc", "getSayHelloBMethod");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        return next.newCall(method, callOptions);
    }
}
