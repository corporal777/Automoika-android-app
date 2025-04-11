package kg.autojuuguch.automoikakg.ui.dialogs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.FiltersModel
import kg.autojuuguch.automoikakg.databinding.BottomSheetDialogFiltersBinding
import kg.autojuuguch.automoikakg.extensions.getColorStateList
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onCheckedChanged
import kg.autojuuguch.automoikakg.extensions.onTextChanged
import kg.autojuuguch.automoikakg.extensions.setDrawableTint
import kg.autojuuguch.automoikakg.extensions.setLeftDrawable
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.Utils.getCarWashTypes
import kg.autojuuguch.automoikakg.utils.Utils.getCityDistricts

class FiltersBottomSheetDialog(context: Context, filter: FiltersModel) :
    BottomSheetDialog(context) {

    private val mBinding = BottomSheetDialogFiltersBinding.inflate(LayoutInflater.from(context))
    private var onAccept: (f: FiltersModel) -> Unit = {}

    private var district = filter.district
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }
    private var type = filter.typeFromServer()
    private var onlyFree = filter.onlyFree

    init {
        setContentView(mBinding.root)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        mBinding.apply {
            tvDistrict.apply {
                onTextChanged {
                    setIconTint()
                    district = it.toString()
                }
                text = district
                setOnClickListener { showVariantsList(getCityDistricts(), district) { text = it } }
            }
            tvType.apply {
                onTextChanged {
                    setIconTint()
                    type = it.toString()
                }
                text = type
                setOnClickListener { showVariantsList(getCarWashTypes(), type) { text = it } }
            }
            scFree.apply {
                isChecked = onlyFree
                onCheckedChanged { onlyFree = it }
            }

            btnAccept.setOnClickListener {
                onAccept.invoke(createFilters())
                dismiss()
            }
            btnClear.setOnClickListener { clearFilters() }
            btnClose.setOnClickListener { dismiss() }
        }
    }

    private fun TextView.showVariantsList(
        vars: List<String>,
        v: String?,
        block: (v: String?) -> Unit
    ) {
        ListBottomSheetDialog(context, vars, v).setSelectCallback { block.invoke(it) }.show()
    }

    private fun TextView.setIconTint(){
        if (text.toString().isEmpty()) setDrawableTint(R.color.menu_icon_color)
        else setDrawableTint(R.color.blue)
    }


    private fun createFilters(): FiltersModel {
        val carWashType = if (type == "Сам мой") "own-wash"
        else if (type == "Услуги мойщика") "cleaner-service"
        else null
        return FiltersModel(
            district = district,
            type = carWashType,
            onlyFree = onlyFree
        )
    }

    private fun clearFilters(){
        mBinding.apply {
            tvDistrict.text = null
            tvType.text = null
            scFree.isChecked = false
        }
    }

    fun setAcceptCallback(block: (f: FiltersModel) -> Unit): FiltersBottomSheetDialog {
        onAccept = block
        return this
    }
}