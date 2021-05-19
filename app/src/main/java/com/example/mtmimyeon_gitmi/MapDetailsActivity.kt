package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.mtmimyeon_gitmi.databinding.ActivityMapDetailsBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapDetailsActivity : AppCompatActivity(), MapView.MapViewEventListener,
    MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {
    private lateinit var binding: ActivityMapDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        // init kakao map
        val mapView = MapView(this)
        val markerList = ArrayList<MapPOIItem>()
        val locationNameList = resources.getStringArray(R.array.campus_location_name)
        val locationLatitudeList = resources.getStringArray(R.array.latitude)
        val locationLongitudeList = resources.getStringArray(R.array.longitude)


        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(37.222103200831285, 127.18643712096329), 3, false
        )

        // 현재 위치 받아오기
//        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading


        // 마커 데이터 삽입2
        for (i in locationNameList.indices) {
            markerList.add(MapPOIItem().apply {
                itemName = locationNameList[i]
                tag = i
                mapPoint = MapPoint.mapPointWithGeoCoord(
                    locationLatitudeList[i].toDouble(),
                    locationLongitudeList[i].toDouble()
                )
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.custom_poi_marker_end
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            })
        }
//        mapView.addPOIItems(markerList.toTypedArray())

        mapView.addPOIItem(MapPOIItem().apply {
            itemName = "내 위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(37.22425223131527, 127.18784380407806)
            tag = locationLatitudeList.size + 1
            markerType = MapPOIItem.MarkerType.RedPin
        })

        binding.relativeLayoutActivityMapDetailsMapView.addView(mapView)


        // spinner init
        binding.spinnerActivityMapDetailsLocationList.setItem(
            resources.getStringArray(R.array.campus_location_name).toMutableList()
        )

        // spinner 아이템 클릭 됐을 때
        binding.spinnerActivityMapDetailsLocationList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d("로그", "$position")
                    mapView.removeAllPOIItems()
                    mapView.addPOIItem(MapPOIItem().apply {
                        itemName = "내 위치"
                        mapPoint =
                            MapPoint.mapPointWithGeoCoord(37.22425223131527, 127.18784380407806)
                        tag = locationLatitudeList.size + 1
                        markerType = MapPOIItem.MarkerType.RedPin
                    })
                    mapView.addPOIItem(markerList[position])
                    mapView.setZoomLevel(2, true)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    override fun onMapViewInitialized(p0: MapView?) {}

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}

    override fun onDaumMapOpenAPIKeyAuthenticationResult(p0: MapView?, p1: Int, p2: String?) {}

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

}