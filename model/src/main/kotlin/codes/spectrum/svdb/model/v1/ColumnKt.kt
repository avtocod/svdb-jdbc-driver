// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: v1/column.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package codes.spectrum.svdb.model.v1;

@kotlin.jvm.JvmName("-initializecolumn")
public inline fun column(block: codes.spectrum.svdb.model.v1.ColumnKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.ColumnOuterClass.Column =
  codes.spectrum.svdb.model.v1.ColumnKt.Dsl._create(codes.spectrum.svdb.model.v1.ColumnOuterClass.Column.newBuilder()).apply { block() }._build()
/**
 * ```
 * Определение поля
 * ```
 *
 * Protobuf type `codes.spectrum.svdb.model.v1.Column`
 */
public object ColumnKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: codes.spectrum.svdb.model.v1.ColumnOuterClass.Column.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: codes.spectrum.svdb.model.v1.ColumnOuterClass.Column.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): codes.spectrum.svdb.model.v1.ColumnOuterClass.Column = _builder.build()

    /**
     * ```
     * код поля
     * ```
     *
     * `string code = 1;`
     */
    public var code: kotlin.String
      @JvmName("getCode")
      get() = _builder.getCode()
      @JvmName("setCode")
      set(value) {
        _builder.setCode(value)
      }
    /**
     * ```
     * код поля
     * ```
     *
     * `string code = 1;`
     */
    public fun clearCode() {
      _builder.clearCode()
    }

    /**
     * ```
     * тип данных
     * ```
     *
     * `.codes.spectrum.svdb.model.v1.DataType dataType = 2;`
     */
    public var dataType: codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
      @JvmName("getDataType")
      get() = _builder.getDataType()
      @JvmName("setDataType")
      set(value) {
        _builder.setDataType(value)
      }
    public var dataTypeValue: kotlin.Int
      @JvmName("getDataTypeValue")
      get() = _builder.getDataTypeValue()
      @JvmName("setDataTypeValue")
      set(value) {
        _builder.setDataTypeValue(value)
      }
    /**
     * ```
     * тип данных
     * ```
     *
     * `.codes.spectrum.svdb.model.v1.DataType dataType = 2;`
     */
    public fun clearDataType() {
      _builder.clearDataType()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class OptionsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
     public val options: com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      @kotlin.jvm.JvmSynthetic
      @JvmName("getOptionsMap")
      get() = com.google.protobuf.kotlin.DslMap(
        _builder.getOptionsMap()
      )
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
    @JvmName("putOptions")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      .put(key: kotlin.String, value: kotlin.String) {
         _builder.putOptions(key, value)
       }
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("setOptions")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      .set(key: kotlin.String, value: kotlin.String) {
         put(key, value)
       }
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("removeOptions")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      .remove(key: kotlin.String) {
         _builder.removeOptions(key)
       }
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("putAllOptions")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      .putAll(map: kotlin.collections.Map<kotlin.String, kotlin.String>) {
         _builder.putAllOptions(map)
       }
    /**
     * ```
     * опции
     * ```
     *
     * `map<string, string> options = 3;`
     */
    @kotlin.jvm.JvmSynthetic
    @JvmName("clearOptions")
    public fun com.google.protobuf.kotlin.DslMap<kotlin.String, kotlin.String, OptionsProxy>
      .clear() {
         _builder.clearOptions()
       }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun codes.spectrum.svdb.model.v1.ColumnOuterClass.Column.copy(block: codes.spectrum.svdb.model.v1.ColumnKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.ColumnOuterClass.Column =
  codes.spectrum.svdb.model.v1.ColumnKt.Dsl._create(this.toBuilder()).apply { block() }._build()

