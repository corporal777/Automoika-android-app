package kg.autojuuguch.automoikakg.ui.dialogs

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.BottomSheetDalogSearchBinding
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.findItem
import kg.autojuuguch.automoikakg.extensions.hideKeyboard
import kg.autojuuguch.automoikakg.extensions.onActionDone
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onFocusChanged
import kg.autojuuguch.automoikakg.extensions.setBackgroundSearch
import kg.autojuuguch.automoikakg.extensions.showKeyboard
import org.koin.java.KoinJavaComponent.inject


class SearchBottomSheet(val requireContext: Context, val search: String) :
    BottomSheetDialog(requireContext, R.style.TransparentBottomSheetDialogTheme) {

    private val mBinding = BottomSheetDalogSearchBinding.inflate(LayoutInflater.from(context))
    private var onSelect: (text: String) -> Unit = {}

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }
    private val appData by inject<AppData>(AppData::class.java)

    init {
        setContentView(mBinding.root)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        mBinding.apply {
            listContent.adapter = groupAdapter
            btnClose.setOnClickListener { dismiss() }

            etSearch.apply {
                setText(search)
                focusOnInput()
                onAfterTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                    updatedData(it.toString())
                }
                onFocusChanged { flSearch.setBackgroundSearch(it) }
                onActionDone { saveAndSendText(it.toString()) }
            }
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }

        }
        setData()
    }


    private fun setData() {
        if (appData.getSearchHistory().isEmpty())
            groupAdapter.add(SearchItem(0, search) { saveAndSendText(it) })
        else groupAdapter.apply {
            val list = arrayListOf<String>().apply {
                addAll(appData.getSearchHistory())
                add(0, search)
            }
            update(list.mapIndexed { index, s -> SearchItem(index, s) { saveAndSendText(it) } })
        }
    }

    private fun updatedData(text: String) {
        val item = groupAdapter.findItem<SearchItem>(0)
        item?.notifyChanged(text)
    }

    private fun saveAndSendText(text: String) {
        if (text.isNotBlank()) appData.saveSearchHistory(text)
        onSelect.invoke(text)
        dismiss()
    }


    private fun EditText.focusOnInput() {
        requestFocus()
        setSelection(this.length())
        showKeyboard(requireContext, this)
    }


    fun setSelectCallback(block: (text: String) -> Unit): SearchBottomSheet {
        onSelect = block
        return this
    }


}