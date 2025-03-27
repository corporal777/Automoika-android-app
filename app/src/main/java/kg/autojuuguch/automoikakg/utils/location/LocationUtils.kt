package kg.autojuuguch.automoikakg.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kg.autojuuguch.automoikakg.exceptions.PermissionNotGrantedException
import kg.autojuuguch.automoikakg.extensions.isGPSEnabled

class LocationUtils(private val fragment: Fragment) {

    private val locationSubject: PublishSubject<Location> = PublishSubject.create()
    private var onRequest: (value: Location) -> Unit = {}

    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                //updateLocationRequest()
                startLocationUpdates()
            }

        }

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
        .apply {
            setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
            setMaxUpdates(5)
        }.build()

    private val locationProvider =
        LocationServices.getFusedLocationProviderClient(fragment.requireActivity())

    fun getLocation(block: (value: Location) -> Unit) {
        onRequest = block
        if (isGPSEnabled(fragment)) updateLocationRequest() else enableGPS(fragment)
    }

    private val compositeDisposable = CompositeDisposable()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            super.onLocationResult(location)
            if (location.lastLocation == null) startLocationUpdates()
            location.lastLocation?.let {
                onRequest.invoke(it)
                locationSubject.onNext(it)
                locationProvider.removeLocationUpdates(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationRequest() {
        locationProvider.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) startLocationUpdates()
                else onRequest.invoke(location) //locationSubject.onNext(location)
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun enableGPS(context: Fragment) {
        val googleApiClient = GoogleApiClient.Builder(context.requireContext())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {}
            })
            .addOnConnectionFailedListener { _ -> }.build()

        googleApiClient.connect()


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(context.requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener { updateLocationRequest() }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.printStackTrace()
                    launcher.launch(IntentSenderRequest.Builder(e.resolution).build())
                } catch (sendIntentException: SendIntentException) {
                    sendIntentException.printStackTrace()
                }
            }
        }
    }

    fun clearFields() {
        launcher.unregister()
    }
}