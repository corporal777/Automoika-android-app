package kg.autojuuguch.automoikakg.ui.gallery

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.gson.annotations.SerializedName
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.Argument
import kg.autojuuguch.automoikakg.databinding.BottomSheetDialogGalleryBinding
import kg.autojuuguch.automoikakg.extensions.parcelableArgument
import kg.autojuuguch.automoikakg.ui.base.BaseBSFragment
import kg.autojuuguch.automoikakg.utils.rxtakephoto.CropCallbackHelper
import kg.autojuuguch.automoikakg.utils.rxtakephoto.RxTakePhoto
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryBottomSheet : BaseBSFragment(true, true) {

    private val args by parcelableArgument<Argument<GalleryType>>(GALLERY_TAG)
    private val viewBinding by viewBinding(BottomSheetDialogGalleryBinding::bind)
    private val viewModel by viewModel<GalleryViewModel>()
    private val rxTakePhoto by inject<RxTakePhoto> { parametersOf(requireActivity()) }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var onCropCallback: (crop: Bitmap) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getGalleryImages(rxTakePhoto)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            cardInfo.clipToOutline = true
            galleryList.adapter = groupAdapter
            tvOpenGallery.setOnClickListener {
                viewModel.onGalleryClick(rxTakePhoto)
            }
            tvRemovePhoto.setOnClickListener { }
            tvCancel.setOnClickListener { hideBottomSheetFragment() }
        }
        setGalleryImages()
    }

    private fun setGalleryImages() {
        viewModel.galleryImages.observe(viewLifecycleOwner) {
            if (it == null) hideBottomSheetFragment()
            else groupAdapter.update(
                it.mapIndexed { index, uri ->
                    GalleryImageItem(index, uri) { showCropActivity(it) }
                }
            )
        }
        viewModel.croppedImage.observe(viewLifecycleOwner) {
            onCropCallback.invoke(it)
            hideBottomSheetFragment()
        }
        viewModel.galleryImage.observe(viewLifecycleOwner) {
            showCropActivity(it)
        }
    }

    private fun showCropActivity(uri: Uri) {
        viewModel.observeCropFinished(CropCallbackHelper.createCropFinishedRequest())
        val type = if (args.value == GalleryType.USER) "user" else "car-wash"
        val bundle = bundleOf("url" to uri.toString(), "type" to type)
        findNavController().navigate(R.id.image_crop_activity, bundle)
    }

    fun setCropCallback(block: (crop: Bitmap) -> Unit): GalleryBottomSheet {
        onCropCallback = block
        return this
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, GALLERY_TAG)

    companion object {
        const val GALLERY_TAG = "gallery_bottom_sheet_dialog"
    }

    override fun layout(): Int = R.layout.bottom_sheet_dialog_gallery
}

enum class GalleryType{
    USER, CAR_WASH,
}


