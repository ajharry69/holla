package com.xently.holla

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Type
import com.xently.holla.data.source.schema.dao.ContactDao
import com.xently.holla.data.source.schema.dao.ConversationDao
import com.xently.holla.data.source.schema.dao.MessageDao
import java.util.*

@Database(
    entities = [
        Contact::class,
        Message::class,
        Conversation::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(ChatTypeConverter::class, TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val chatDao: MessageDao

    abstract val contactDao: ContactDao

    abstract val conversationDao: ConversationDao
}

class ChatTypeConverter {
    @TypeConverter
    fun fromTypeToString(type: Type): String = type.name

    @TypeConverter
    fun fromStringToType(type: String): Type = Type.valueOf(type)
}

class TimestampConverter {
    @TypeConverter
    fun fromTimestampToInt(timestamp: Timestamp): Long = timestamp.toDate().time

    @TypeConverter
    fun fromIntToTimestamp(timestamp: Long): Timestamp = Timestamp(Date(timestamp))
}