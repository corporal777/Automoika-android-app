package kg.autojuuguch.automoikakg.ui.stories

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentStoriesBinding
import kg.autojuuguch.automoikakg.extensions.setEnterSharedElement
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.main.MainActivity
import kg.autojuuguch.automoikakg.ui.views.stories.StoriesProgressView
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt


class StoriesFragment : BaseVBFragment<FragmentStoriesBinding>() {

    override val viewModel by viewModel<StoriesViewModel>()

    private var position = 0
    private var pressTime = 0L
    private var limit = 500L


    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                mBinding.stories.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                mBinding.stories.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    private val storiesCallback = object : StoriesProgressView.StoriesListener {
        override fun onComplete() { findNavController().navigateUp() }
        override fun onPrev() = viewModel.onPrevStory()
        override fun onNext() = viewModel.onNextStory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.storiesId = StoriesFragmentArgs.fromBundle(requireArguments()).storyId
        viewModel.loadStories()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentContainerView
            duration = 200
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().getColor(R.color.trans))
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            reverse.apply {
                setOnClickListener { stories.reverse() }
                setOnTouchListener(onTouchListener)
            }
            skip.apply {
                setOnClickListener { stories.skip() }
                setOnTouchListener(onTouchListener)
            }
            ivClose.setOnClickListener { navigateUp() }
            flingLayout.apply {
                transitionName = viewModel.storiesId
                setEnterSharedElement(flingLayout)
                positionChangeListener = { _, _, rate ->
                    if (rate <= 0f) mBinding.stories.resume()
                    else mBinding.stories.pause()
                    setBackgroundColor(Color.argb((255 * (1.0F - rate)).roundToInt(), 0, 0, 0))
                }
                dismissListener = { findNavController().navigateUp() }
            }

        }
        observeStory()
    }

    private fun observeStory() {
        viewModel.stories.observe {
            mBinding.stories.apply {
                setStoriesCount(viewModel.getStoriesCount())
                setStoryDuration(3500L)
                setStoriesListener(storiesCallback)
                startStories()
            }
        }
        viewModel.story.observe {
            mBinding.apply {
                tvTitle.text = it.title
                tvDescription.text = it.message
                ivBackgroundImage.setImage(it.image)
            }
        }
    }


    override fun binding() = FragmentStoriesBinding::class.java
    override fun layout(): Int = R.layout.fragment_stories
}