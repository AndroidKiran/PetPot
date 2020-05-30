package com.droid47.petfriend.base.storage.db

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.droid47.petfriend.search.data.models.search.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object PetDbConverter {

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>?): String? {
        val inputValue = value ?: return null
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(inputValue, type)
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String?): List<String> {
        val inputValue = value ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(inputValue, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromBreed(breedEntity: BreedEntity?): String? {
        val inputBreed = breedEntity ?: return null
        return Gson().toJson(inputBreed)
    }

    @TypeConverter
    @JvmStatic
    fun toBreed(value: String?): BreedEntity {
        val inputValue = value ?: return BreedEntity()
        return Gson().fromJson(inputValue, BreedEntity::class.java)
    }


    @TypeConverter
    @JvmStatic
    fun fromPhotoList(value: List<PhotosItemEntity>?): String? {
        val inputValue = value ?: return null
        val type = object : TypeToken<List<PhotosItemEntity>>() {}.type
        return Gson().toJson(inputValue, type)
    }

    @TypeConverter
    @JvmStatic
    fun toPhotoList(value: String?): List<PhotosItemEntity> {
        val inputValue = value ?: return emptyList()
        val type = object : TypeToken<List<PhotosItemEntity>>() {}.type
        return Gson().fromJson(inputValue, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromColor(value: ColorsEntity?): String? {
        val inputValue = value ?: return null
        return Gson().toJson(inputValue)
    }

    @TypeConverter
    @JvmStatic
    fun toColor(value: String?): ColorsEntity {
        val inputValue = value ?: return ColorsEntity()
        return Gson().fromJson(inputValue, ColorsEntity::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromEnvironment(value: EnvironmentEntity?): String? {
        val inputValue = value ?: return null
        return Gson().toJson(inputValue)
    }

    @TypeConverter
    @JvmStatic
    fun toEnvironment(value: String?): EnvironmentEntity {
        val inputValue = value ?: return EnvironmentEntity()
        return Gson().fromJson(inputValue, EnvironmentEntity::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromContact(value: ContactEntity?): String? {
        val inputValue = value ?: return null
        return Gson().toJson(inputValue)
    }

    @TypeConverter
    @JvmStatic
    fun toContact(value: String?): ContactEntity {
        val inputValue = value ?: return ContactEntity()
        return Gson().fromJson(inputValue, ContactEntity::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromAttributes(value: AttributesEntity?): String? {
        val inputValue = value ?: return null
        return Gson().toJson(inputValue)
    }

    @TypeConverter
    @JvmStatic
    fun toAttributes(value: String?): AttributesEntity {
        val inputValue = value ?: return AttributesEntity()
        return Gson().fromJson(inputValue, AttributesEntity::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromVideoList(value: List<VideoItemEntity>?): String? {
        val inputValue = value ?: return null
        val type = object : TypeToken<List<VideoItemEntity>>() {}.type
        return Gson().toJson(inputValue, type)
    }

    @TypeConverter
    @JvmStatic
    fun toVideoList(value: String?): List<VideoItemEntity> {
        val inputValue = value ?: return emptyList()
        val type = object : TypeToken<List<VideoItemEntity>>() {}.type
        return Gson().fromJson(inputValue, type)
    }


    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: String?): Date? {
        val inputValue = value ?: return null
        return try {
            val timeZone: TimeZone = TimeZone.getTimeZone("UTC")
            df.timeZone = timeZone
            df.parse(inputValue)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(value: Date?): String? {
        val timeZone = TimeZone.getTimeZone("UTC")
        df.timeZone = timeZone
        return if (value == null) null else df.format(value)
    }
}