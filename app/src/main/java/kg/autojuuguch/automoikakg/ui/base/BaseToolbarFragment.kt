package kg.autojuuguch.automoikakg.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ScrollingView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kg.autojuuguch.automoikakg.extensions.computeVerticalOffset
import kg.autojuuguch.automoikakg.extensions.onScroll
import kg.autojuuguch.automoikakg.extensions.onScrolled
import kg.autojuuguch.automoikakg.ui.main.MainActivity
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kotlin.math.abs

abstract class BaseToolbarFragment<VB : ViewBinding> : BaseVBFragment<VB>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (scrollingView() == null) setAppBarElevation(0)
        else scrollingView()?.apply {
            when (this) {
                is NestedScrollView -> {
                    setAppBarElevation(computeVerticalOffset())
                    onScroll { setAppBarElevation(computeVerticalOffset()) }
                }
                is RecyclerView -> {
                    setAppBarElevation(computeVerticalOffset())
                    onScroll { setAppBarElevation(computeVerticalOffset()) }
                }
            }
        }
    }

    private fun setAppBarElevation(value: Int) {
        try {
            (mActivity as MainActivity).setAppbarElevation(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    open fun scrollingView(): ScrollingView? = null
    open val title: CharSequence = ""
}