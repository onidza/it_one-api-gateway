package ru.creditbank.apigateway.config;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import ru.creditbank.apigateway.config.filter.grpc.JwtClientInterceptor;
import ru.creditbank.apigateway.config.filter.grpc.TraceIdClientInterceptor;
import ru.creditbank.contract.credit.v1.CreditServiceGrpc;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {

  private final JwtClientInterceptor jwtClientInterceptor;
  private final TraceIdClientInterceptor traceIdClientInterceptor;

  @Bean
  public CreditServiceGrpc.CreditServiceBlockingStub creditServiceBlockingStub(
      GrpcChannelFactory channelFactory
  ) {

    Channel channel = channelFactory.createChannel("credit-service");
    return CreditServiceGrpc.newBlockingStub(channel)
        .withInterceptors(
            jwtClientInterceptor,
            traceIdClientInterceptor
        );
  }
}