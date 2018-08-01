package com.grpc.test.grpcstarterclienttest.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.test.grpc.performance.TestPerformanceGrpc;
import com.test.grpc.performance.TestReply;
import com.test.grpc.performance.TestRequest;
import io.grpc.Channel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demo class
 *
 * @author junqiang.xiao@hand-china.com
 * @date 2018/5/7
 */
@Service
public class TestPerformanceGrpcService {
    private static final Logger logger = Logger.getLogger(TestPerformanceGrpcService.class.getName());
    public static final int CALL_TIMES = 1;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @GrpcClient("grpc-server-test")
    private Channel serverChannel;

    public TestReply sendMessage(String name, String tenantId) {
        TestPerformanceGrpc.TestPerformanceBlockingStub stub = TestPerformanceGrpc.newBlockingStub(serverChannel);
        Iterator<TestReply> response = null;

        Date beginDate = new Date();
        System.out.println("begin:" + beginDate);
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("P_TENANT_ID", Metadata.ASCII_STRING_MARSHALLER), tenantId);

        stub = MetadataUtils.attachHeaders(stub, metadata);
        try {

            for (int i = 0; i < CALL_TIMES; i++) {
                TestRequest request = TestRequest.newBuilder().setName(name).build();

                response = stub.sayHello(request);

            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }

        Date endDate = new Date();
        System.out.println("end:" + endDate);

        System.out.println("time:" + (endDate.getTime() - beginDate.getTime()) / 1000 + " s");
        TestReply testReply = null;
        while (response.hasNext()) {
            testReply = response.next();
        }
        return testReply;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public TestReply sendHystrixMessage(String name, String tenantId) {
        TestPerformanceGrpc.TestPerformanceBlockingStub stub = TestPerformanceGrpc.newBlockingStub(serverChannel);
        Iterator<TestReply> response = null;

        Date beginDate = new Date();
        System.out.println("begin:" + beginDate);

        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("P_TENANT_ID", Metadata.ASCII_STRING_MARSHALLER), tenantId);
        stub = MetadataUtils.attachHeaders(stub, metadata);

        try {

            for (int i = 0; i < CALL_TIMES; i++) {
                TestRequest request = TestRequest.newBuilder().setName(name).build();

                response = stub.sayHello(request);

            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }

        Date endDate = new Date();
        System.out.println("end:" + endDate);

        System.out.println("time:" + (endDate.getTime() - beginDate.getTime()) / 1000 + " s");
        TestReply testReply = null;
        while (response.hasNext()) {
            testReply = response.next();
        }
        return testReply;
    }

    public TestReply fallback(String name, String tenantId) {
        TestReply testReply  = TestReply.newBuilder().setField2(-1).build();
        int i = atomicInteger.incrementAndGet();
        System.out.println("fallback:" + i);
        return null;
    }
}
