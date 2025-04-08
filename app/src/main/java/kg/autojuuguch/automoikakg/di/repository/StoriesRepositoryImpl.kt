package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.model.StoriesModel

class StoriesRepositoryImpl(private val api : ApiService) : StoriesRepository {

    override fun getStories(): Maybe<List<StoriesModel>> {
        return api.getStories()
    }
}