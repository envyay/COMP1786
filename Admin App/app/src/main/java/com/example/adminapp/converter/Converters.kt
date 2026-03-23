package com.example.adminapp.converter

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(time: Long?): Date? {
        return time?.let { Date(it) }
    }
}