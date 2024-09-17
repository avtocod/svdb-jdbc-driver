package codes.spectrum.svdb.model.v1

import codes.spectrum.svdb.model.v1.SvdbServiceGrpc.getServiceDescriptor
import com.google.protobuf.Empty
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Holder for Kotlin coroutine-based client and server APIs for
 * codes.spectrum.svdb.model.v1.SvdbService.
 */
public object SvdbServiceGrpcKt {
  public const val SERVICE_NAME: String = SvdbServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = SvdbServiceGrpc.getServiceDescriptor()

  public val queryMethod: MethodDescriptor<Query.QueryOptions, Queryresult.QueryResult>
    @JvmStatic
    get() = SvdbServiceGrpc.getQueryMethod()

  public val fetchMethod: MethodDescriptor<Fetch.FetchOptions, Queryresult.QueryResult>
    @JvmStatic
    get() = SvdbServiceGrpc.getFetchMethod()

  public val pingMethod: MethodDescriptor<Empty, StateOuterClass.State>
    @JvmStatic
    get() = SvdbServiceGrpc.getPingMethod()

  public val cancelMethod: MethodDescriptor<Cancel.CancelOptions, StateOuterClass.State>
    @JvmStatic
    get() = SvdbServiceGrpc.getCancelMethod()

  /**
   * A stub for issuing RPCs to a(n) codes.spectrum.svdb.model.v1.SvdbService service as suspending
   * coroutines.
   */
  @StubFor(SvdbServiceGrpc::class)
  public class SvdbServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<SvdbServiceCoroutineStub>(channel, callOptions) {
    public override fun build(channel: Channel, callOptions: CallOptions): SvdbServiceCoroutineStub
        = SvdbServiceCoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun query(request: Query.QueryOptions, headers: Metadata = Metadata()):
        Queryresult.QueryResult = unaryRpc(
      channel,
      SvdbServiceGrpc.getQueryMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun fetch(request: Fetch.FetchOptions, headers: Metadata = Metadata()):
        Queryresult.QueryResult = unaryRpc(
      channel,
      SvdbServiceGrpc.getFetchMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun ping(request: Empty, headers: Metadata = Metadata()): StateOuterClass.State =
        unaryRpc(
      channel,
      SvdbServiceGrpc.getPingMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun cancel(request: Cancel.CancelOptions, headers: Metadata = Metadata()):
        StateOuterClass.State = unaryRpc(
      channel,
      SvdbServiceGrpc.getCancelMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the codes.spectrum.svdb.model.v1.SvdbService service based on Kotlin
   * coroutines.
   */
  public abstract class SvdbServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for codes.spectrum.svdb.model.v1.SvdbService.Query.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun query(request: Query.QueryOptions): Queryresult.QueryResult = throw
        StatusException(UNIMPLEMENTED.withDescription("Method codes.spectrum.svdb.model.v1.SvdbService.Query is unimplemented"))

    /**
     * Returns the response to an RPC for codes.spectrum.svdb.model.v1.SvdbService.Fetch.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun fetch(request: Fetch.FetchOptions): Queryresult.QueryResult = throw
        StatusException(UNIMPLEMENTED.withDescription("Method codes.spectrum.svdb.model.v1.SvdbService.Fetch is unimplemented"))

    /**
     * Returns the response to an RPC for codes.spectrum.svdb.model.v1.SvdbService.Ping.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun ping(request: Empty): StateOuterClass.State = throw
        StatusException(UNIMPLEMENTED.withDescription("Method codes.spectrum.svdb.model.v1.SvdbService.Ping is unimplemented"))

    /**
     * Returns the response to an RPC for codes.spectrum.svdb.model.v1.SvdbService.Cancel.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun cancel(request: Cancel.CancelOptions): StateOuterClass.State = throw
        StatusException(UNIMPLEMENTED.withDescription("Method codes.spectrum.svdb.model.v1.SvdbService.Cancel is unimplemented"))

    public final override fun bindService(): ServerServiceDefinition =
        builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SvdbServiceGrpc.getQueryMethod(),
      implementation = ::query
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SvdbServiceGrpc.getFetchMethod(),
      implementation = ::fetch
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SvdbServiceGrpc.getPingMethod(),
      implementation = ::ping
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SvdbServiceGrpc.getCancelMethod(),
      implementation = ::cancel
    )).build()
  }
}
