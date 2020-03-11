package com.xently.holla.data.repository

import android.content.Context
import com.xently.holla.data.repository.schema.IChatRepository

class ChatRepository internal constructor(context: Context) : BaseRepository(), IChatRepository {
}