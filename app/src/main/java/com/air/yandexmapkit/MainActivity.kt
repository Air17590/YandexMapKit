package com.air.yandexmapkit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.AnnotationLanguage
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.user_location.UserLocationTapListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("cce58919-fc3a-401a-ae13-aaa1d03284da")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
        val personaLayer = MapKitFactory.getInstance().createPersonalizedPoiLayer(mapview.mapWindow)
        val trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapview.mapWindow)
//        val s = GeoObjectTapListener { it ->
//            textview.text = it.geoObject.name.toString()
//            true
//        }
        personaLayer.isVisible = true
        // mapview.map.addTapListener(s)

        btn.setOnClickListener {
            mapview.map.isDebugInfoEnabled = true
            // textview.text = mapview.map.
        }
        mapview.map.userLocationLayer.isEnabled = true

        val ssd = UserLocationTapListener {
            textview.text = "${it.latitude} ${it.longitude}"
        }
        mapview.map.userLocationLayer.setTapListener(ssd)

        DirectionsFactory.initialize(this)
        val s = DirectionsFactory.getInstance().createDrivingRouter()
        s.setVehicleType(VehicleType.DEFAULT)
        s.setAnnotationLanguage(AnnotationLanguage.RUSSIAN)
        s.resume()
        s.
        val f = s.routeSerializer()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }


}
