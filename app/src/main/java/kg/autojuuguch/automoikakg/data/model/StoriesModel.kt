package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class StoriesModel (
    val id: String,
    val title: String,
    val image : String,
    val stories : List<StoryModel>
) : Parcelable

@Parcelize
data class StoryModel(
    val title: String,
    val message: String,
    val image: String
) : Parcelable