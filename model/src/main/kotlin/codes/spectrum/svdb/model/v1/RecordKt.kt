// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: v1/record.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package codes.spectrum.svdb.model.v1;

@kotlin.jvm.JvmName("-initializerecord")
public inline fun record(block: codes.spectrum.svdb.model.v1.RecordKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.RecordOuterClass.Record =
  codes.spectrum.svdb.model.v1.RecordKt.Dsl._create(codes.spectrum.svdb.model.v1.RecordOuterClass.Record.newBuilder()).apply { block() }._build()
/**
 * ```
 * запись с полями в виде байтов
 * ```
 *
 * Protobuf type `codes.spectrum.svdb.model.v1.Record`
 */
public object RecordKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: codes.spectrum.svdb.model.v1.RecordOuterClass.Record.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: codes.spectrum.svdb.model.v1.RecordOuterClass.Record.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): codes.spectrum.svdb.model.v1.RecordOuterClass.Record = _builder.build()

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class FieldsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     */
     public val fields: com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getFieldsList()
      )
    /**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     * @param value The fields to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addFields")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.add(value: com.google.protobuf.ByteString) {
      _builder.addFields(value)
    }/**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     * @param value The fields to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignFields")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.plusAssign(value: com.google.protobuf.ByteString) {
      add(value)
    }/**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     * @param values The fields to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllFields")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.ByteString>) {
      _builder.addAllFields(values)
    }/**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     * @param values The fields to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllFields")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.ByteString>) {
      addAll(values)
    }/**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     * @param index The index to set the value at.
     * @param value The fields to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setFields")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.set(index: kotlin.Int, value: com.google.protobuf.ByteString) {
      _builder.setFields(index, value)
    }/**
     * ```
     * поля в виде байтов
     * ```
     *
     * `repeated bytes fields = 1;`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearFields")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.ByteString, FieldsProxy>.clear() {
      _builder.clearFields()
    }}
}
@kotlin.jvm.JvmSynthetic
public inline fun codes.spectrum.svdb.model.v1.RecordOuterClass.Record.copy(block: `codes.spectrum.svdb.model.v1`.RecordKt.Dsl.() -> kotlin.Unit): codes.spectrum.svdb.model.v1.RecordOuterClass.Record =
  `codes.spectrum.svdb.model.v1`.RecordKt.Dsl._create(this.toBuilder()).apply { block() }._build()

