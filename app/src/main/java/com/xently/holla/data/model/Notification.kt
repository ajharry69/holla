package com.xently.holla.data.model

import com.xently.holla.utils.IData
import com.xently.holla.utils.JSON_CONVERTER
import com.xently.holla.utils.objectFromJson

data class ObjectOperation(val id: String?, val objectType: Type?, val operation: Operation?) {
    enum class Type {
        CONVERSATION,
        MESSAGE,
        CONTACT
    }

    enum class Operation {
        UPDATE,
        DELETE
    }

    override fun toString(): String = JSON_CONVERTER.toJson(this)

    companion object : IData<ObjectOperation> {
        override fun fromJson(json: String?): ObjectOperation? = objectFromJson(json)
    }
}

data class Notification(
    val title: String?,
    val body: String?,
    val imageUrl: String?,
    val senderId: String?,
    val messageId: String?
) {
    override fun toString(): String = JSON_CONVERTER.toJson(this)

    companion object : IData<Notification> {
        override fun fromJson(json: String?): Notification? = objectFromJson(json)
    }
}