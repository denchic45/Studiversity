package com.stuiversity.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class OptionalPropertySerializer<T>(
    private val valueSerializer: KSerializer<T>
) : KSerializer<OptionalProperty<T>> {
    final override val descriptor: SerialDescriptor = valueSerializer.descriptor

    final override fun deserialize(decoder: Decoder): OptionalProperty<T> =
        OptionalProperty.Present(valueSerializer.deserialize(decoder))

    final override fun serialize(encoder: Encoder, value: OptionalProperty<T>) {
        when (value) {
            OptionalProperty.NotPresent -> throw SerializationException(
                "Tried to serialize an optional property that had no value present." +
                        " Is encodeDefaults false?"
            )

            is OptionalProperty.Present ->
                valueSerializer.serialize(encoder, value.value)
        }
    }
}