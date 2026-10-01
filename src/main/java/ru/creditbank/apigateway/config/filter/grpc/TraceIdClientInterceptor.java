package ru.creditbank.apigateway.config.filter.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class TraceIdClientInterceptor implements ClientInterceptor {

  private static final Metadata.Key<String> TRACE_ID =
      Metadata.Key.of(
          "x-trace-id",
          Metadata.ASCII_STRING_MARSHALLER
      );

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method,
      CallOptions callOptions,
      Channel next
  ) {
    ClientCall<ReqT, RespT> call =
        next.newCall(method, callOptions);

    return new ForwardingClientCall.SimpleForwardingClientCall<>(call) {

      @Override
      public void start(
          Listener<RespT> responseListener,
          Metadata headers
      ) {
        String traceId = MDC.get("traceId");

        if (traceId != null) {
          headers.put(TRACE_ID, traceId);
        }

        super.start(responseListener, headers);
      }
    };
  }
}
