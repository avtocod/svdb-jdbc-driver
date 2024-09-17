package codes.spectrum.svdb.model.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * интерфейс основного сервиса для удаленного обмена данными
 * 1. При установке соединения и успешной аутентификации клиент открывает сессию
 * 2. Команды выполняются в рамках сессии
 * 3. C закрытием соединения закроется и сессия
 * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
 *    запросе следующего он будет закрыт, более полное API подразумевает
 *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
 *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
 *    уточнене после опытной эксплуатации
 * 5. получив корректный ответ на запрос, клиент может выполнять 
 *    последовательный Fetch из него - по одной записи или батчами
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.56.1)",
    comments = "Source: v1/dbservice.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SvdbServiceGrpc {

  private SvdbServiceGrpc() {}

  public static final String SERVICE_NAME = "codes.spectrum.svdb.model.v1.SvdbService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Query.QueryOptions,
      codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Query",
      requestType = codes.spectrum.svdb.model.v1.Query.QueryOptions.class,
      responseType = codes.spectrum.svdb.model.v1.Queryresult.QueryResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Query.QueryOptions,
      codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getQueryMethod() {
    io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Query.QueryOptions, codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getQueryMethod;
    if ((getQueryMethod = SvdbServiceGrpc.getQueryMethod) == null) {
      synchronized (SvdbServiceGrpc.class) {
        if ((getQueryMethod = SvdbServiceGrpc.getQueryMethod) == null) {
          SvdbServiceGrpc.getQueryMethod = getQueryMethod =
              io.grpc.MethodDescriptor.<codes.spectrum.svdb.model.v1.Query.QueryOptions, codes.spectrum.svdb.model.v1.Queryresult.QueryResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Query"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.Query.QueryOptions.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.Queryresult.QueryResult.getDefaultInstance()))
              .setSchemaDescriptor(new SvdbServiceMethodDescriptorSupplier("Query"))
              .build();
        }
      }
    }
    return getQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Fetch.FetchOptions,
      codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getFetchMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Fetch",
      requestType = codes.spectrum.svdb.model.v1.Fetch.FetchOptions.class,
      responseType = codes.spectrum.svdb.model.v1.Queryresult.QueryResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Fetch.FetchOptions,
      codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getFetchMethod() {
    io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Fetch.FetchOptions, codes.spectrum.svdb.model.v1.Queryresult.QueryResult> getFetchMethod;
    if ((getFetchMethod = SvdbServiceGrpc.getFetchMethod) == null) {
      synchronized (SvdbServiceGrpc.class) {
        if ((getFetchMethod = SvdbServiceGrpc.getFetchMethod) == null) {
          SvdbServiceGrpc.getFetchMethod = getFetchMethod =
              io.grpc.MethodDescriptor.<codes.spectrum.svdb.model.v1.Fetch.FetchOptions, codes.spectrum.svdb.model.v1.Queryresult.QueryResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Fetch"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.Fetch.FetchOptions.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.Queryresult.QueryResult.getDefaultInstance()))
              .setSchemaDescriptor(new SvdbServiceMethodDescriptorSupplier("Fetch"))
              .build();
        }
      }
    }
    return getFetchMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      codes.spectrum.svdb.model.v1.StateOuterClass.State> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = com.google.protobuf.Empty.class,
      responseType = codes.spectrum.svdb.model.v1.StateOuterClass.State.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      codes.spectrum.svdb.model.v1.StateOuterClass.State> getPingMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, codes.spectrum.svdb.model.v1.StateOuterClass.State> getPingMethod;
    if ((getPingMethod = SvdbServiceGrpc.getPingMethod) == null) {
      synchronized (SvdbServiceGrpc.class) {
        if ((getPingMethod = SvdbServiceGrpc.getPingMethod) == null) {
          SvdbServiceGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, codes.spectrum.svdb.model.v1.StateOuterClass.State>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.StateOuterClass.State.getDefaultInstance()))
              .setSchemaDescriptor(new SvdbServiceMethodDescriptorSupplier("Ping"))
              .build();
        }
      }
    }
    return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Cancel.CancelOptions,
      codes.spectrum.svdb.model.v1.StateOuterClass.State> getCancelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Cancel",
      requestType = codes.spectrum.svdb.model.v1.Cancel.CancelOptions.class,
      responseType = codes.spectrum.svdb.model.v1.StateOuterClass.State.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Cancel.CancelOptions,
      codes.spectrum.svdb.model.v1.StateOuterClass.State> getCancelMethod() {
    io.grpc.MethodDescriptor<codes.spectrum.svdb.model.v1.Cancel.CancelOptions, codes.spectrum.svdb.model.v1.StateOuterClass.State> getCancelMethod;
    if ((getCancelMethod = SvdbServiceGrpc.getCancelMethod) == null) {
      synchronized (SvdbServiceGrpc.class) {
        if ((getCancelMethod = SvdbServiceGrpc.getCancelMethod) == null) {
          SvdbServiceGrpc.getCancelMethod = getCancelMethod =
              io.grpc.MethodDescriptor.<codes.spectrum.svdb.model.v1.Cancel.CancelOptions, codes.spectrum.svdb.model.v1.StateOuterClass.State>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Cancel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.Cancel.CancelOptions.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  codes.spectrum.svdb.model.v1.StateOuterClass.State.getDefaultInstance()))
              .setSchemaDescriptor(new SvdbServiceMethodDescriptorSupplier("Cancel"))
              .build();
        }
      }
    }
    return getCancelMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SvdbServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SvdbServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SvdbServiceStub>() {
        @java.lang.Override
        public SvdbServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SvdbServiceStub(channel, callOptions);
        }
      };
    return SvdbServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SvdbServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SvdbServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SvdbServiceBlockingStub>() {
        @java.lang.Override
        public SvdbServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SvdbServiceBlockingStub(channel, callOptions);
        }
      };
    return SvdbServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SvdbServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SvdbServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SvdbServiceFutureStub>() {
        @java.lang.Override
        public SvdbServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SvdbServiceFutureStub(channel, callOptions);
        }
      };
    return SvdbServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * интерфейс основного сервиса для удаленного обмена данными
   * 1. При установке соединения и успешной аутентификации клиент открывает сессию
   * 2. Команды выполняются в рамках сессии
   * 3. C закрытием соединения закроется и сессия
   * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
   *    запросе следующего он будет закрыт, более полное API подразумевает
   *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
   *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
   *    уточнене после опытной эксплуатации
   * 5. получив корректный ответ на запрос, клиент может выполнять 
   *    последовательный Fetch из него - по одной записи или батчами
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * выполняет запрос, открывает курсор и получает QueryResult, 
     * в котором есть токен для последующих fetch
     * в нынешней реализации 
     * </pre>
     */
    default void query(codes.spectrum.svdb.model.v1.Query.QueryOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryMethod(), responseObserver);
    }

    /**
     * <pre>
     * выполняет получение следующей записи по result_uid,
     * NOTE: в версии 0.1.0 - 0.2.0 это единственный вариант
     * фетча - по одной записи, то есть массив records 
     * будет из одного элемента и по текущему результату
     * по KISS пока делаем только такую сигнатуру
     * имеется возможность передать cursorUid
     * в Fetch и выбрать любой курсор из текущей сессии
     * </pre>
     */
    default void fetch(codes.spectrum.svdb.model.v1.Fetch.FetchOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFetchMethod(), responseObserver);
    }

    /**
     * <pre>
     * запрос для теста соединения
     * реализует логику первого запроса после установки соединения
     * для аутентификации и инициализации сессии
     * </pre>
     */
    default void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     * <pre>
     * позволяет удалить сессию или курсор до того,
     * как ее зачистит внутренний сборщик мусора svdb
     * передача uid сессии при закрытии курсора обязательна!!
     * </pre>
     */
    default void cancel(codes.spectrum.svdb.model.v1.Cancel.CancelOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SvdbService.
   * <pre>
   * интерфейс основного сервиса для удаленного обмена данными
   * 1. При установке соединения и успешной аутентификации клиент открывает сессию
   * 2. Команды выполняются в рамках сессии
   * 3. C закрытием соединения закроется и сессия
   * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
   *    запросе следующего он будет закрыт, более полное API подразумевает
   *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
   *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
   *    уточнене после опытной эксплуатации
   * 5. получив корректный ответ на запрос, клиент может выполнять 
   *    последовательный Fetch из него - по одной записи или батчами
   * </pre>
   */
  public static abstract class SvdbServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return SvdbServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SvdbService.
   * <pre>
   * интерфейс основного сервиса для удаленного обмена данными
   * 1. При установке соединения и успешной аутентификации клиент открывает сессию
   * 2. Команды выполняются в рамках сессии
   * 3. C закрытием соединения закроется и сессия
   * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
   *    запросе следующего он будет закрыт, более полное API подразумевает
   *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
   *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
   *    уточнене после опытной эксплуатации
   * 5. получив корректный ответ на запрос, клиент может выполнять 
   *    последовательный Fetch из него - по одной записи или батчами
   * </pre>
   */
  public static final class SvdbServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SvdbServiceStub> {
    private SvdbServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SvdbServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SvdbServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * выполняет запрос, открывает курсор и получает QueryResult, 
     * в котором есть токен для последующих fetch
     * в нынешней реализации 
     * </pre>
     */
    public void query(codes.spectrum.svdb.model.v1.Query.QueryOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * выполняет получение следующей записи по result_uid,
     * NOTE: в версии 0.1.0 - 0.2.0 это единственный вариант
     * фетча - по одной записи, то есть массив records 
     * будет из одного элемента и по текущему результату
     * по KISS пока делаем только такую сигнатуру
     * имеется возможность передать cursorUid
     * в Fetch и выбрать любой курсор из текущей сессии
     * </pre>
     */
    public void fetch(codes.spectrum.svdb.model.v1.Fetch.FetchOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFetchMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * запрос для теста соединения
     * реализует логику первого запроса после установки соединения
     * для аутентификации и инициализации сессии
     * </pre>
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * позволяет удалить сессию или курсор до того,
     * как ее зачистит внутренний сборщик мусора svdb
     * передача uid сессии при закрытии курсора обязательна!!
     * </pre>
     */
    public void cancel(codes.spectrum.svdb.model.v1.Cancel.CancelOptions request,
        io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SvdbService.
   * <pre>
   * интерфейс основного сервиса для удаленного обмена данными
   * 1. При установке соединения и успешной аутентификации клиент открывает сессию
   * 2. Команды выполняются в рамках сессии
   * 3. C закрытием соединения закроется и сессия
   * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
   *    запросе следующего он будет закрыт, более полное API подразумевает
   *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
   *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
   *    уточнене после опытной эксплуатации
   * 5. получив корректный ответ на запрос, клиент может выполнять 
   *    последовательный Fetch из него - по одной записи или батчами
   * </pre>
   */
  public static final class SvdbServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SvdbServiceBlockingStub> {
    private SvdbServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SvdbServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SvdbServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * выполняет запрос, открывает курсор и получает QueryResult, 
     * в котором есть токен для последующих fetch
     * в нынешней реализации 
     * </pre>
     */
    public codes.spectrum.svdb.model.v1.Queryresult.QueryResult query(codes.spectrum.svdb.model.v1.Query.QueryOptions request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * выполняет получение следующей записи по result_uid,
     * NOTE: в версии 0.1.0 - 0.2.0 это единственный вариант
     * фетча - по одной записи, то есть массив records 
     * будет из одного элемента и по текущему результату
     * по KISS пока делаем только такую сигнатуру
     * имеется возможность передать cursorUid
     * в Fetch и выбрать любой курсор из текущей сессии
     * </pre>
     */
    public codes.spectrum.svdb.model.v1.Queryresult.QueryResult fetch(codes.spectrum.svdb.model.v1.Fetch.FetchOptions request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFetchMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * запрос для теста соединения
     * реализует логику первого запроса после установки соединения
     * для аутентификации и инициализации сессии
     * </pre>
     */
    public codes.spectrum.svdb.model.v1.StateOuterClass.State ping(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * позволяет удалить сессию или курсор до того,
     * как ее зачистит внутренний сборщик мусора svdb
     * передача uid сессии при закрытии курсора обязательна!!
     * </pre>
     */
    public codes.spectrum.svdb.model.v1.StateOuterClass.State cancel(codes.spectrum.svdb.model.v1.Cancel.CancelOptions request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SvdbService.
   * <pre>
   * интерфейс основного сервиса для удаленного обмена данными
   * 1. При установке соединения и успешной аутентификации клиент открывает сессию
   * 2. Команды выполняются в рамках сессии
   * 3. C закрытием соединения закроется и сессия
   * 4. В версии 0.1.0 - 0.2.0 позволяет открыть один запрос и при 
   *    запросе следующего он будет закрыт, более полное API подразумевает
   *    дизайн с несколькими одновременными результатами (это сразу будет сделано, 
   *    но только для embedded режима, а не на gRPC), публикация на gRPC будет
   *    уточнене после опытной эксплуатации
   * 5. получив корректный ответ на запрос, клиент может выполнять 
   *    последовательный Fetch из него - по одной записи или батчами
   * </pre>
   */
  public static final class SvdbServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SvdbServiceFutureStub> {
    private SvdbServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SvdbServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SvdbServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * выполняет запрос, открывает курсор и получает QueryResult, 
     * в котором есть токен для последующих fetch
     * в нынешней реализации 
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> query(
        codes.spectrum.svdb.model.v1.Query.QueryOptions request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * выполняет получение следующей записи по result_uid,
     * NOTE: в версии 0.1.0 - 0.2.0 это единственный вариант
     * фетча - по одной записи, то есть массив records 
     * будет из одного элемента и по текущему результату
     * по KISS пока делаем только такую сигнатуру
     * имеется возможность передать cursorUid
     * в Fetch и выбрать любой курсор из текущей сессии
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<codes.spectrum.svdb.model.v1.Queryresult.QueryResult> fetch(
        codes.spectrum.svdb.model.v1.Fetch.FetchOptions request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFetchMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * запрос для теста соединения
     * реализует логику первого запроса после установки соединения
     * для аутентификации и инициализации сессии
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<codes.spectrum.svdb.model.v1.StateOuterClass.State> ping(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * позволяет удалить сессию или курсор до того,
     * как ее зачистит внутренний сборщик мусора svdb
     * передача uid сессии при закрытии курсора обязательна!!
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<codes.spectrum.svdb.model.v1.StateOuterClass.State> cancel(
        codes.spectrum.svdb.model.v1.Cancel.CancelOptions request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY = 0;
  private static final int METHODID_FETCH = 1;
  private static final int METHODID_PING = 2;
  private static final int METHODID_CANCEL = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY:
          serviceImpl.query((codes.spectrum.svdb.model.v1.Query.QueryOptions) request,
              (io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult>) responseObserver);
          break;
        case METHODID_FETCH:
          serviceImpl.fetch((codes.spectrum.svdb.model.v1.Fetch.FetchOptions) request,
              (io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.Queryresult.QueryResult>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State>) responseObserver);
          break;
        case METHODID_CANCEL:
          serviceImpl.cancel((codes.spectrum.svdb.model.v1.Cancel.CancelOptions) request,
              (io.grpc.stub.StreamObserver<codes.spectrum.svdb.model.v1.StateOuterClass.State>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getQueryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              codes.spectrum.svdb.model.v1.Query.QueryOptions,
              codes.spectrum.svdb.model.v1.Queryresult.QueryResult>(
                service, METHODID_QUERY)))
        .addMethod(
          getFetchMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              codes.spectrum.svdb.model.v1.Fetch.FetchOptions,
              codes.spectrum.svdb.model.v1.Queryresult.QueryResult>(
                service, METHODID_FETCH)))
        .addMethod(
          getPingMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.protobuf.Empty,
              codes.spectrum.svdb.model.v1.StateOuterClass.State>(
                service, METHODID_PING)))
        .addMethod(
          getCancelMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              codes.spectrum.svdb.model.v1.Cancel.CancelOptions,
              codes.spectrum.svdb.model.v1.StateOuterClass.State>(
                service, METHODID_CANCEL)))
        .build();
  }

  private static abstract class SvdbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SvdbServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return codes.spectrum.svdb.model.v1.Dbservice.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SvdbService");
    }
  }

  private static final class SvdbServiceFileDescriptorSupplier
      extends SvdbServiceBaseDescriptorSupplier {
    SvdbServiceFileDescriptorSupplier() {}
  }

  private static final class SvdbServiceMethodDescriptorSupplier
      extends SvdbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SvdbServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SvdbServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SvdbServiceFileDescriptorSupplier())
              .addMethod(getQueryMethod())
              .addMethod(getFetchMethod())
              .addMethod(getPingMethod())
              .addMethod(getCancelMethod())
              .build();
        }
      }
    }
    return result;
  }
}
