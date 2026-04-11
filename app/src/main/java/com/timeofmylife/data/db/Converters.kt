package com.timeofmylife.data.db

import androidx.room.TypeConverter
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.data.model.Reliability

class Converters {
    @TypeConverter fun fromReliability(r: Reliability): String = r.name

    @TypeConverter fun toReliability(s: String): Reliability = Reliability.valueOf(s)

    @TypeConverter fun fromItemType(t: ItemType): String = t.name

    @TypeConverter fun toItemType(s: String): ItemType = ItemType.valueOf(s)
}
