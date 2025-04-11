package kg.autojuuguch.automoikakg.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupieAdapter
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.FiltersModel
import kg.autojuuguch.automoikakg.databinding.BottomSheetDialogFiltersBinding
import kg.autojuuguch.automoikakg.databinding.BottomSheetDialogListBinding
import kg.autojuuguch.automoikakg.ui.holders.VariantItem

class ListBottomSheetDialog(context: Context, val variants: List<String>, val chosen: String?) :
    BottomSheetDialog(context) {

    private val mBinding = BottomSheetDialogListBinding.inflate(LayoutInflater.from(context))
    private var onSelected: (variant: String?) -> Unit = {}

    private val groupAdapter = GroupieAdapter().apply {
        update(variants.map { VariantItem(it, chosen){ sendVariant(it) } })
    }

    init {
        setContentView(mBinding.root)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        mBinding.apply {
            recyclerView.adapter = groupAdapter
            btnClear.setOnClickListener { sendVariant(null) }
        }
    }

    private fun sendVariant(v : String?){
        onSelected.invoke(v)
        dismiss()
    }

    fun setSelectCallback(block: (v : String?) -> Unit): ListBottomSheetDialog {
        onSelected = block
        return this
    }
}