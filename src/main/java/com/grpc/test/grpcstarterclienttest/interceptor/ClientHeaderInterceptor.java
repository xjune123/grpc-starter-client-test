package com.grpc.test.grpcstarterclienttest.interceptor;

import com.test.grpc.performance.TestPerformanceGrpc;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;

public class ClientHeaderInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) { //创建client System.out.println("创建client1");
        /*method.toBuilder().setFullMethodName("com.test.grpc.performance.TestPerformance/SayHelloB")
                .setSchemaDescriptor(TestPerformanceGrpc.getSayHelloBMethod())
                          .setSchemaDescriptor( new TestPerformanceGrpc.TestPerformanceMethodDescriptorSupplier("SayHelloA"));
*/
        final ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);
        return new ForwardingClientCall<ReqT, RespT>() {
            @Override
            protected ClientCall<ReqT, RespT> delegate() {
                return clientCall;
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                System.out.println("拦截器1,在此可以对header参数进行修改");
                Metadata.Key<String> token = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(token, "123456");
                super.start(responseListener, headers);
            }
        };
    }
}

