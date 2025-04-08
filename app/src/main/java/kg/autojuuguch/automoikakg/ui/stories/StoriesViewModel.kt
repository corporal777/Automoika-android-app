package kg.autojuuguch.automoikakg.ui.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.StoryModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG

class StoriesViewModel(private val appData: AppData) : BaseViewModel(appData) {

    var storiesId = ""
    private val storiesList = arrayListOf<StoryModel>()
    private var position = 0
    private val _story = MutableLiveData<StoryModel>()
    val story : LiveData<StoryModel> get() = _story

    private val _stories = SingleLiveEvent<Unit>()
    val stories : LiveData<Unit> get() = _stories


    fun loadStories(){
        compositeDisposable += Maybe.just(appData.getStories(storiesId))
            .doOnSuccess { it?.stories?.let { storiesList.addAll(it) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _stories.setValue(Unit)
                _story.setValue(storiesList[position])
            }
    }


    fun onNextStory() {
        if (position < storiesList.size - 1){
            position += 1
            _story.setValue(storiesList[position])
        }
    }

    fun onPrevStory() {
        if (position > 0){
            position -= 1
            _story.setValue(storiesList[position])
        }
    }

    fun getStoriesCount() = storiesList.size
}