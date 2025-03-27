package kg.autojuuguch.automoikakg.ui.crop

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import io.reactivex.subjects.SingleSubject
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ActivityCropImageBinding
import kg.autojuuguch.automoikakg.databinding.ActivityMainBinding
import kg.autojuuguch.automoikakg.utils.rxtakephoto.CropCallbackHelper

class CropImageActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityCropImageBinding
    private lateinit var cropSubject: SingleSubject<Bitmap?>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

        mBinding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        intent.extras?.let { CropImageActivityArgs.fromBundle(it) }?.apply {
            setImage(url.toUri(), type)
        }


        this.cropSubject = CropCallbackHelper.getCropFinishedRequest()
        mBinding.btnCrop.setOnClickListener { cropImage() }
        mBinding.btnCancel.setOnClickListener { finish() }
    }


    private fun setImage(uri: Uri, type : String) {
        imageUri = uri
        mBinding.apply {
            customCropView.setCropMode(
                if (type == "user") CropImageView.CropMode.CIRCLE_SQUARE
                else CropImageView.CropMode.RATIO_16_9
            )
            customCropView.load(imageUri).execute(object : LoadCallback {
                override fun onError(e: Throwable?) {}
                override fun onSuccess() {
                    startPostponedEnterTransition()
                }
            })
        }
    }

    private fun cropImage(){
        mBinding.customCropView.crop(imageUri).execute(object : CropCallback {
            override fun onError(e: Throwable) { cropSubject.onError(e) }
            override fun onSuccess(cropped: Bitmap?) {
                if (cropped == null) cropSubject.onError(NullPointerException())
                else closeCropActivity(cropped)
            }
        })
    }


    fun closeCropActivity(bitmap: Bitmap) {
        finish()
        cropSubject.onSuccess(bitmap)
    }
}