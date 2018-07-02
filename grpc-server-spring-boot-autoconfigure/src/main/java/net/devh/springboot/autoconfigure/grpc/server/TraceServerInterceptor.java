package net.devh.springboot.autoconfigure.grpc.server;

import io.grpc.ForwardingServerCall;
import brave.Span;
import brave.Tracer;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceServerInterceptor implements ServerInterceptor {

    private Tracer tracer;

    TraceServerInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            private Span span;

            @Override
            public void request(int numMessages) {
                span = tracer.nextSpan().name("grpc:" + call.getMethodDescriptor().getFullMethodName()).start();
                span.tag("grpc-request", call.getMethodDescriptor().getFullMethodName());

                super.request(numMessages);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                Status.Code statusCode = status.getCode();
                span.tag("grpc-status", String.valueOf(statusCode.value()));
                if (!status.isOk()) {
                    span.tag("error", status.getDescription());
                }
                span.finish();
                super.close(status, trailers);
            }
        }, headers);
    }
}
