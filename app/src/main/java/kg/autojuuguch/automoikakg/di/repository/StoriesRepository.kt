package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.data.model.StoriesModel

interface StoriesRepository {
    fun getStories() : Maybe<List<StoriesModel>>
}