// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: v1/warning.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package codes.spectrum.svdb.model.v1;

@kotlin.jvm.JvmName("-initializewarning")
public inline fun warning(block: codes.spectrum.svdb.model.v1.WarningKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.WarningOuterClass.Warning =
  codes.spectrum.svdb.model.v1.WarningKt.Dsl._create(codes.spectrum.svdb.model.v1.WarningOuterClass.Warning.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `codes.spectrum.svdb.model.v1.Warning`
 */
public object WarningKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: codes.spectrum.svdb.model.v1.WarningOuterClass.Warning.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: codes.spectrum.svdb.model.v1.WarningOuterClass.Warning.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): codes.spectrum.svdb.model.v1.WarningOuterClass.Warning = _builder.build()

    /**
     * ```
     * код ошибки SQL
     * ```
     *
     * `int32 code = 1;`
     */
    public var code: kotlin.Int
      @JvmName("getCode")
      get() = _builder.getCode()
      @JvmName("setCode")
      set(value) {
        _builder.setCode(value)
      }
    /**
     * ```
     * код ошибки SQL
     * ```
     *
     * `int32 code = 1;`
     */
    public fun clearCode() {
      _builder.clearCode()
    }

    /**
     * ```
     * SQLState
     * ```
     *
     * `string state = 2;`
     */
    public var state: kotlin.String
      @JvmName("getState")
      get() = _builder.getState()
      @JvmName("setState")
      set(value) {
        _builder.setState(value)
      }
    /**
     * ```
     * SQLState
     * ```
     *
     * `string state = 2;`
     */
    public fun clearState() {
      _builder.clearState()
    }

    /**
     * ```
     * сообщение
     * ```
     *
     * `string reason = 3;`
     */
    public var reason: kotlin.String
      @JvmName("getReason")
      get() = _builder.getReason()
      @JvmName("setReason")
      set(value) {
        _builder.setReason(value)
      }
    /**
     * ```
     * сообщение
     * ```
     *
     * `string reason = 3;`
     */
    public fun clearReason() {
      _builder.clearReason()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun codes.spectrum.svdb.model.v1.WarningOuterClass.Warning.copy(block: `codes.spectrum.svdb.model.v1`.WarningKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.WarningOuterClass.Warning =
  `codes.spectrum.svdb.model.v1`.WarningKt.Dsl._create(this.toBuilder()).apply { block() }._build()

