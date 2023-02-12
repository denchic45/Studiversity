package com.denchic45.firebasemultiplatform

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement

abstract class MyJsonParametricSerializer<T : Any>(descriptorName: String) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(descriptorName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        selectSerializer(value).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T {
        val input = decoder.asJsonInput()
        val tree = input.decodeJsonElement()

        return input.json.decodeFromJsonElement(selectSerializer(tree), tree)
    }

    protected abstract fun selectSerializer(element: JsonElement): KSerializer<T>
    protected abstract fun selectSerializer(value: T): KSerializer<T>

    private fun Decoder.asJsonInput(): JsonDecoder = this as? JsonDecoder
        ?: throw IllegalStateException(
            "This serializer can be used only with Json format." +
                    "Expected Decoder to be JsonInput, got ${this::class}"
        )
}