package kg.autojuuguch.automoikakg.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kg.autojuuguch.automoikakg.extensions.onScrolled
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kotlin.math.abs

abstract class BaseToolbarFragment<VB : ViewBinding> : BaseVBFragment<VB>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scrollView = scrollingView()
        when (scrollView) {
            is NestedScrollView -> {
                setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                scrollView.onScrolled { _, _, _, _ ->
                    setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                }
            }

            is RecyclerView -> {
                setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                scrollView.onScrolled { _, _ ->
                    setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                }
            }

            else -> setActivityAppBarElevation(0)
        }
        toolbarView()?.getBackButton()?.setOnClickListener { navigateUp() }
    }

    private fun setActivityAppBarElevation(value: Int) {
        try {
            toolbarView()?.setToolbarShadow(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    open fun toolbarView(): ToolbarLayoutView? = null
    open fun scrollingView(): View? = null
    open val title: CharSequence = ""
}