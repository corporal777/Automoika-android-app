package kg.autojuuguch.automoikakg.ui.map.address

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentMapBinding
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.getBitmapDrawable
import kg.autojuuguch.automoikakg.extensions.getBitmapFromDrawable
import kg.autojuuguch.automoikakg.extensions.icon
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.map.MapViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapAddressFragment : BaseVBFragment<FragmentMapBinding>() {


    override val viewModel by viewModel<MapAddressViewModel>()


    private val objectTapListener = GeoObjectTapListener {
        val lat = it.geoObject.geometry.first().point?.latitude
        val lon = it.geoObject.geometry.first().point?.longitude

        if (lat != null && lon != null) {
            addPlaceMarkObject(Point(lat, lon))
            viewModel.getYandexGeoLocation(lat, lon)
        }
        return@GeoObjectTapListener true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            getMap().move(getCameraPosition(Point(42.876823, 74.6036357), 12f))
            getMap().addTapListener(objectTapListener)
        }
        viewModel.location.observe(viewLifecycleOwner){
            showAddressDialog(it.fullAddress ?: "")
        }
    }

    private fun showAddressDialog(text : String){
        DefaultAlertDialog(
            requireContext(),
            getString(R.string.location_text),
            text,
            getString(R.string.confirm_phone_positive),
            getString(R.string.confirm_phone_negative)
        ).setSelectCallback {  }
    }


    private fun navigateUpResult() {
        setFragmentResult("map", bundleOf("phone" to true))
        findNavController().navigateUp()
    }

    private fun addPlaceMarkObject(point: Point) {
        getMap().mapObjects.clear()
        getMap().mapObjects.addPlacemark().apply {
            geometry = point
            icon = getBitmapDrawable(R.drawable.ic_map_placemark, 30.dp)
        }
    }

    private fun getMap() = mBinding.mapview.mapWindow.map
    private fun getCameraPosition(point: Point, zoom : Float) = CameraPosition(point, zoom, 0f, 0f)

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mBinding.mapview.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mBinding.mapview.onStop()
        super.onStop()
    }

    override fun layout(): Int = R.layout.fragment_map
    override fun binding() = FragmentMapBinding::class.java
}