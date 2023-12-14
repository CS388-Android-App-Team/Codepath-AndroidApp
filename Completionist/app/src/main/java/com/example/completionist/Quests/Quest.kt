package com.example.completionist.Quests

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "quest_name")
    val questName: String?,

    @ColumnInfo(name = "quest_points")
    val questPoints: Int?,

    @ColumnInfo(name = "quest_date")
    val questDate: String?, // Assuming your date is stored as a String, you might want to use a proper Date type

    @ColumnInfo(name = "is_complete")
    var isComplete: Boolean
) : Parcelable {

    // Primary constructor used for creating instances in your code
    constructor(
        questName: String?,
        questPoints: Int?,
        questDate: String?,
        isComplete: Boolean
    ) : this(0, questName, questPoints, questDate, isComplete)

    // Parcelable implementation

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questName)
        questPoints?.let { parcel.writeInt(it) }
        parcel.writeString(questDate)
        parcel.writeByte(if (isComplete) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quest> {
        override fun createFromParcel(parcel: Parcel): Quest {
            return Quest(parcel)
        }

        override fun newArray(size: Int): Array<Quest?> {
            return arrayOfNulls(size)
        }
    }
}
