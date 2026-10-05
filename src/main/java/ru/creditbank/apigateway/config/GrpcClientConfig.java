package ru.creditbank.apigateway.config;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import ru.creditbank.apigateway.config.filter.grpc.JwtClientInterceptor;
import ru.creditbank.apigateway.config.filter.grpc.TraceIdClientInterceptor;
import ru.creditbank.contract.credit.v1.CreditServiceGrpc;
import ru.creditbank.contract.s3.v1.FileServiceGrpc;
import ru.creditbank.contract.test_interactions.v1.TestInteractionsServiceGrpc;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {

  private final JwtClientInterceptor jwtClientInterceptor;
  private final TraceIdClientInterceptor traceIdClientInterceptor;

  private static final String CREDIT_SERVICE = "credit-service";

  @Bean
  public CreditServiceGrpc.CreditServiceBlockingStub creditServiceBlockingStub(
      GrpcChannelFactory channelFactory
  ) {
    Channel channel = channelFactory.createChannel(CREDIT_SERVICE);

    return CreditServiceGrpc.newBlockingStub(channel)
        .withInterceptors(
            jwtClientInterceptor,
            traceIdClientInterceptor
        );
  }

  @Bean
  public TestInteractionsServiceGrpc.TestInteractionsServiceStub testInteractionsAsyncStub(
      GrpcChannelFactory channelFactory
  ) {
    Channel channel = channelFactory.createChannel(CREDIT_SERVICE);

    return TestInteractionsServiceGrpc.newStub(channel)
        .withInterceptors(
            jwtClientInterceptor,
            traceIdClientInterceptor
        );
  }

  @Bean
  public TestInteractionsServiceGrpc.TestInteractionsServiceFutureStub testInteractionsFutureStub(
      GrpcChannelFactory channelFactory
  ) {
    Channel channel = channelFactory.createChannel(CREDIT_SERVICE);

    return TestInteractionsServiceGrpc.newFutureStub(channel)
        .withInterceptors(
            jwtClientInterceptor,
            traceIdClientInterceptor
        );
  }

  @Bean
  public FileServiceGrpc.FileServiceBlockingStub fileServiceImplBase(
      GrpcChannelFactory channelFactory
  ) {
    Channel channel = channelFactory.createChannel(CREDIT_SERVICE);

    return FileServiceGrpc.newBlockingStub(channel)
        .withInterceptors(
            jwtClientInterceptor,
            traceIdClientInterceptor
        );
  }
}