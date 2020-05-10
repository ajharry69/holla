package com.xently.holla

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.model.Message
import com.xently.holla.data.source.schema.dao.MessageDao
import com.xently.holla.data.source.schema.dao.ContactDao
import com.xently.holla.data.source.schema.dao.ConversationDao

@Database(
    entities = [
        Contact::class,
        Message::class,
        Conversation::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(ChatTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val chatDao: MessageDao

    abstract val contactDao: ContactDao

    abstract val conversationDao: ConversationDao
}

class ChatTypeConverter {
    @TypeConverter
    fun fromTypeToString(type: Chat.Type): String = type.name

    @TypeConverter
    fun fromStringToType(type: String): Chat.Type = Chat.Type.valueOf(type)
}