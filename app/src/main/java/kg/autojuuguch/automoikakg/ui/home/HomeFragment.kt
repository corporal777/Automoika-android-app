package kg.autojuuguch.automoikakg.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.adapter.CarWashPagingAdapter
import kg.autojuuguch.automoikakg.adapter.CarWashPagingAdapter.Companion.withLoadStateAdapters
import kg.autojuuguch.automoikakg.adapter.CarWashPlaceholderAdapter
import kg.autojuuguch.automoikakg.databinding.FragmentHomeBinding
import kg.autojuuguch.automoikakg.extensions.offsetChangedListener
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.detail.CarWashDetailFragmentArgs
import kg.autojuuguch.automoikakg.ui.dialogs.SearchBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class HomeFragment : BaseVBFragment<FragmentHomeBinding>() {

    override val viewModel by viewModel<HomeViewModel>()

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CarWashPagingAdapter { showDetailFragment(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    CarWashPlaceholderAdapter(2),
                    CarWashPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }
            }
            tvSearch.apply {
                onAfterTextChanged { viewModel.onSearchRequest(it.toString()) }
                setOnClickListener { showSearchBottomSheet(tvSearch.text.toString()) }
            }
            appBarLayout.offsetChangedListener { _, i -> updateAppBarViews(abs(i).toFloat()) }
            setPagingData()
        }
    }

    private fun setPagingData() {
        viewModel.pagingData.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(lifecycle, it)
        }
        viewModel.updateData.observe(viewLifecycleOwner) {
            pagingAdapter.updateCarWashItem(it)
        }
    }

    private fun showSearchBottomSheet(text : String){
        SearchBottomSheet(requireContext(), text)
            .setSelectCallback { mBinding.tvSearch.text = it }
            .show()
    }

    private fun showDetailFragment(id: String) {
        val args = CarWashDetailFragmentArgs.Builder(id).build().toBundle()
        findNavController().navigate(R.id.car_wash_detail_fragment, args)
    }

    private fun showLoginFragment(){
        findNavController().navigate(R.id.login_fragment)
    }

    override fun setAppBarOffset(offset: Float) {
        mBinding.tvLabelLarge.alpha = if (offset == 0.0f) 1f else 1.0f - (offset / 100)
    }

    override fun layout(): Int = R.layout.fragment_home
    override fun binding() = FragmentHomeBinding::class.java
    override fun viewPlaceholder(): View = mBinding.tvEmptyData
}