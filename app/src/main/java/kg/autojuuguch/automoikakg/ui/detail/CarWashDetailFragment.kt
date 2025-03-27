package kg.autojuuguch.automoikakg.ui.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentCarWashDetailBinding
import kg.autojuuguch.automoikakg.extensions.cardShadow
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.onScroll
import kg.autojuuguch.automoikakg.extensions.px
import kg.autojuuguch.automoikakg.extensions.statusBarColorValue
import kg.autojuuguch.automoikakg.extensions.tint
import kg.autojuuguch.automoikakg.extensions.topMargin
import kg.autojuuguch.automoikakg.extensions.updateGroup
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.detail.items.CarWashAddReviewItem
import kg.autojuuguch.automoikakg.ui.detail.items.CarWashImageItem
import kg.autojuuguch.automoikakg.ui.detail.items.CarWashLabelItem
import kg.autojuuguch.automoikakg.ui.detail.items.CarWashLocationItem
import kg.autojuuguch.automoikakg.ui.detail.items.CarWashReviewItem
import kg.autojuuguch.automoikakg.ui.views.LinearLayoutManagerAccurateOffset
import kg.autojuuguch.automoikakg.utils.SYSTEM_UI_LIGHT_STATUS_BAR
import kg.autojuuguch.automoikakg.data.Twice
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class CarWashDetailFragment : BaseVBFragment<FragmentCarWashDetailBinding>() {

    override val viewModel by viewModel<CarWashDetailViewModel>()
    private val args: CarWashDetailFragmentArgs by navArgs()


    private val offsetMap = mutableMapOf<Int, Int>()
    private val groupAdapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.carWashId = args.carWashId
        viewModel.getCarWashDetail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            contentList.apply {
                layoutManager = LinearLayoutManagerAccurateOffset(requireContext(), offsetMap)
                adapter = groupAdapter
                onScroll { viewModel.changeScrollOffset(computeVerticalScrollOffset()) }
            }
            toolbar.apply {
                ivLike.setOnClickListener { }
                ivShare.setOnClickListener { }
                ivBack.setOnClickListener { navigateUp() }
            }
            swipeToRefreshLayout.setOnRefreshListener { }
        }
        setCarWashData()
        setupToolbarBackgroundColor()
    }

    private fun setCarWashData() {
        viewModel.carWashDetail.observe(viewLifecycleOwner) {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            groupAdapter.updateGroup(Section().apply {
                add(CarWashImageItem(it))

                add(CarWashLocationItem(it.address))

                add(CarWashLabelItem(R.string.reviews_text))

                addAll(it.binds.reviews.map { CarWashReviewItem(it) })

                add(CarWashAddReviewItem(it.binds.reviews.isEmpty()))
            })
        }
    }


    fun setupToolbarTopMargin(margin: Int) {
        try {
            mBinding.toolbar.viewSize.topMargin =
                if (margin.px in 20..30) 26.dp
                else if (margin.px in 30..40) 35.dp
                else if (margin.px in 40..50) 45.dp
                else if (margin.px in 50..70) 50.dp
                else 55.dp
        } catch (e: Exception) {
            e.printStackTrace()
            mBinding.toolbar.viewSize.topMargin = 26.dp
        }
    }

    private fun setupToolbarElevation(value: Int) {
        val buttonsTint: (Int) -> Unit = {
            mBinding.toolbar.ivBack.tint = it
            mBinding.toolbar.ivLike.tint = it
            mBinding.toolbar.ivShare.tint = it
        }
        mBinding.toolbar.apply {
            if (value >= 850) {
                buttonsTint(R.color.app_main_color)
                requireActivity().statusBarColorValue = SYSTEM_UI_LIGHT_STATUS_BAR
                mBinding.cardViewToolbar.cardShadow = value.toFloat()
            } else {
                buttonsTint(R.color.white)
                requireActivity().statusBarColorValue = 0
                mBinding.cardViewToolbar.cardShadow = 0f
            }
        }

    }

    private var back = Twice(0f, 0f)
    private var like = Twice(0f, 0f)
    private var share = Twice(0f, 0f)
    private fun setupToolbarBackgroundColor() {
        val maskAlpha: (Int) -> Unit = {
            mBinding.toolbar.backMask.alpha = if (it <= 0) 1f else 1f - abs(it / (700).toFloat())
            mBinding.toolbar.likeMask.alpha = if (it <= 0) 1f else 1f - abs(it / (700).toFloat())
            mBinding.toolbar.shareMask.alpha = if (it <= 0) 1f else 1f - abs(it / (700).toFloat())
        }
        viewModel.scrollOffsetData.observe(viewLifecycleOwner) { offset ->
            setupToolbarElevation(offset)
            maskAlpha.invoke(offset)
            mBinding.toolbar.apply {
                if (offset <= 0) tbBackground.alpha = 0f
                else {
                    tbBackground.alpha = abs(offset / (1450).toFloat())

                    if (back.first == 0f && back.second == 0f) back = Twice(ivBack.x, backMask.x)
                    if (like.first == 0f && like.second == 0f) like = Twice(ivLike.x, likeMask.x)
                    if (share.first == 0f && share.second == 0f) share = Twice(ivShare.x, shareMask.x)

                    if (offset in 1..1300) {
                        //back
                        val xBack = back.first - abs(offset / (30).toFloat())
                        if (xBack in viewLeft.x..back.first) ivBack.x = xBack
                        val xBackMask = back.second - abs(offset / (30).toFloat())
                        if (xBackMask in viewLeft.x..back.second) backMask.x = xBackMask

                        //like
                        val xLike = like.first + abs(offset / (30).toFloat())
                        if (xLike in like.first..840f) ivLike.x = xLike
                        val xLikeMask = like.second + abs(offset / (30).toFloat())
                        if (xLikeMask in like.second..840f) likeMask.x = xLikeMask

                        //share
                        val xShare = share.first + abs(offset / (30).toFloat())
                        if (xShare in share.first..viewRight.x) ivShare.x = xShare
                        val xShareMask = share.second + abs(offset / (30).toFloat())
                        if (xShareMask in share.second..viewRight.x) shareMask.x = xShareMask
                    }
                }
            }
        }
    }

    override fun animationType() = AnimType.FADE
    override fun binding() = FragmentCarWashDetailBinding::class.java
    override fun layout(): Int = R.layout.fragment_car_wash_detail
}