package ru.creditbank.apigateway.config.filter.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtClientInterceptor implements ClientInterceptor {

  private static final Metadata.Key<String> AUTHORIZATION =
      Metadata.Key.of(
          "authorization",
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
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
          headers.put(
              AUTHORIZATION,
              "Bearer " + jwtAuthentication.getToken().getTokenValue()
          );
        }

        super.start(responseListener, headers);
      }
    };
  }
}
