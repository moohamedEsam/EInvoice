package com.example.network.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

class DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun deserialize(decoder: Decoder): Date {
        var stringDate = decoder.decodeString()
        stringDate = stringDate.replace("T", " ")
        stringDate = stringDate.replace("Z", " ")
        return dateFormat.parse(stringDate) ?: Date()
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: Date) {
        var dateString = dateFormat.format(value)
        dateString = dateString.replace(" ", "T")
        dateString = dateString.removeSuffix("T")
        dateString += "Z"
        encoder.encodeString(dateString)
    }
}