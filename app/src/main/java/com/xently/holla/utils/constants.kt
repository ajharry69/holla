package com.xently.holla.utils

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.intellij.lang.annotations.Language
import java.text.DateFormat

val JSON_CONVERTER: Gson = GsonBuilder()
//        .registerTypeAdapter(Id::class.java, IdTypeAdapter())
    .enableComplexMapKeySerialization()
    .serializeNulls()
    .setDateFormat(DateFormat.LONG)
    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .setPrettyPrinting()
//        .setVersion(1.0)
    .create()

inline fun <reified T> objectFromJson(json: String?): T? = if (json.isNullOrBlank()) null else try {
    JSON_CONVERTER.fromJson(json, T::class.java)
} catch (ex: Exception) {
    null
}

interface IData<T> {
    fun fromJson(@Language("JSON") json: String?): T?

    fun fromMap(map: Map<String, Any?>): T? = fromJson(JSON_CONVERTER.toJson(map))
}