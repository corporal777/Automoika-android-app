package kg.autojuuguch.automoikakg.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.LatLng
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kg.autojuuguch.automoikakg.databinding.FragmentMapBinding
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.getBitmapDrawable
import kg.autojuuguch.automoikakg.extensions.getBitmapFromDrawable
import kg.autojuuguch.automoikakg.extensions.icon
import kg.autojuuguch.automoikakg.extensions.isVisibleAnim
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.extensions.mapObjectTapListener
import kg.autojuuguch.automoikakg.extensions.objectTapListener
import kg.autojuuguch.automoikakg.ui.auth.registerCarWash.contacts.RegisterContactsFragmentArgs
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : BaseVBFragment<FragmentMapBinding>() {

    private val args: MapFragmentArgs by navArgs()
    override val viewModel by viewModel<MapViewModel>()

    private lateinit var clusterObjects: ClusterizedPlacemarkCollection

    private val clusterListener = ClusterListener { cluster ->
        cluster.appearance.setText(cluster.placemarks.size.toString())
        val bitmap = ImageProvider.fromBitmap(getBitmapFromDrawable(R.drawable.ic_map_group))
        cluster.appearance.setIcon(bitmap)
    }

    private val objectTapListener = objectTapListener {
        addPlaceMarkObject(Point(it.latitude, it.longitude))
        viewModel.getYandexGeoLocation(it.latitude, it.longitude)
    }

    private val placeMarkTapListener = mapObjectTapListener { mapObject ->
        Toast.makeText(requireContext(), "Map object", Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initMap(args.fromReg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnClose.setOnClickListener { navigateUp() }
        }
        viewModel.fromRegister.observe {
            val zoom = if (!it) 11f else 12f
            getMap().move(cameraPosition(Point(42.876823, 74.6036357), zoom))

            if (!it) addClusterObjects()
            else addTapObjects()
        }

    }

    private fun addClusterObjects() {
        val points = listOf(
            Point(42.883690349820775, 74.54727319884053),
            Point(42.8766273760105, 74.60370624308153),
            Point(42.88038846878444, 74.57297948945612),
            Point(42.87515739911273, 74.58201478374562),
        )
        clusterObjects = getMap().mapObjects.addClusterizedPlacemarkCollection(clusterListener)
        clusterObjects.apply {
            points.forEach { point -> addPlacemark().addPlaceMarkObject(point) }
            clusterPlacemarks(60.0, 15)
        }
    }


    private fun addTapObjects(){
        getMap().addTapListener(objectTapListener)
        viewModel.startInstructionsTimer()
        viewModel.location.observe { showAddressDialog(it) }
        viewModel.instructions.observe { mBinding.clInstructions.isVisibleAnim = it }
    }


    private fun navigateUpResult(model: YandexGeoModel) {
        val bundle = Bundle().apply { putParcelable("loc", model) }
        setFragmentResult("map", bundle)
        findNavController().navigateUp()
    }

    private fun showAddressDialog(model: YandexGeoModel) {
        DefaultAlertDialog(
            requireContext(),
            getString(R.string.location_text),
            model.fullAddress ?: "",
            getString(R.string.confirm_phone_positive),
            getString(R.string.confirm_phone_negative)
        ).setSelectCallback { navigateUpResult(model) }
    }

    private fun PlacemarkMapObject.addPlaceMarkObject(point: Point): PlacemarkMapObject {
        return this.apply {
            geometry = point
            icon = getBitmapFromDrawable(R.drawable.ic_map_placemark)
            addTapListener(placeMarkTapListener)
        }
    }

    private fun addPlaceMarkObject(point: Point) {
        getMap().mapObjects.clear()
        getMap().mapObjects.addPlacemark().apply {
            geometry = point
            icon = getBitmapDrawable(R.drawable.ic_map_placemark, 30.dp)
        }
    }

    private fun getMap() = mBinding.mapview.mapWindow.map
    private fun cameraPosition(point: Point, zoom: Float) = CameraPosition(point, zoom, 0f, 0f)


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