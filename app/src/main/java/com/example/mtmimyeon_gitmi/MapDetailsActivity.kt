package com.example.mtmimyeon_gitmi

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.mtmimyeon_gitmi.databinding.ActivityMapDetailsBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

enum class MapType(val typeNum: Int) {
    UNIVERSITY_BUILDING(0), RECOMMENDED_PLACE(1)
}

class MapDetailsActivity : AppCompatActivity(), MapView.MapViewEventListener,
    MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {
    private lateinit var binding: ActivityMapDetailsBinding
    private lateinit var mapView: MapView
    private var isMyLocationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarActivityMapDetailsToolbar)
        // 학교 건물 찾기 혹은 MBTI 추천 시설 중 선택해서 지도 초기화 (사실상 현재 코드는 같은데 추후에 변경될 수도 있으니 일단 다른 init 함수 사용)
        if (intent.getIntExtra("mapType", 0) == MapType.UNIVERSITY_BUILDING.typeNum)
            initUniversityBuilding()
        else
            initRecommendedPlace()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_map, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toolBar_myLocation -> {
                // 현재 위치 받아오기 (테스트 할 때는 사용하면 안 됨)
                if (!this.isMyLocationEnabled) { // 현재 위치 활성화
                    this.mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading // 현재 위치 추적 트랙킹 ON
                    binding.toolbarActivityMapDetailsToolbar.menu.getItem(0).icon =
                        ContextCompat.getDrawable(this, R.drawable.ic_location_enabled)
                    this.isMyLocationEnabled = true
                } else { // 현재 위치 비활성화
                    this.mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOff // 현재 위치 추적 트랙킹 OFF
                    binding.toolbarActivityMapDetailsToolbar.menu.getItem(0).icon =
                        ContextCompat.getDrawable(this, R.drawable.ic_location_disabled)
                    this.isMyLocationEnabled = false
                }


            }
        }
        return true
    }

    override fun onDestroy() {
        Log.d("로그", "MapDetailsActivity -onDestroy() called")
        this.mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOff
        super.onDestroy()
    }

    private fun initUniversityBuilding() { // 학교 건물 지도

        // init kakao map
        this.mapView = MapView(this)

        val markerList = ArrayList<MapPOIItem>()
        val locationNameList = resources.getStringArray(R.array.campus_building_name)
        val locationLatitudeList = resources.getStringArray(R.array.campus_building_latitude)
        val locationLongitudeList = resources.getStringArray(R.array.campus_building_longitude)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)
        mapView.setMapCenterPointAndZoomLevel( // 맵 기준 위치
            MapPoint.mapPointWithGeoCoord(37.222103200831285, 127.18643712096329), 3, false
        )

        // 현재 위치 받아오기 (테스트 할 때는 사용하면 안 됨)
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
                isCustomImageAutoscale = false
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
            resources.getStringArray(R.array.campus_building_name).toMutableList()
        )

        // spinner 아이템 클릭 됐을 때
        binding.spinnerActivityMapDetailsLocationList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
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
                    mapView.setMapCenterPointAndZoomLevel(markerList[position].mapPoint, 3, true)
                    mapView.addPOIItem(markerList[position])
//                    mapView.addPOIItem(MapPOIItem().apply {
//                        itemName = "너랑 가까운 위치 테스트할 때 써봐"
//                        mapPoint =
//                            MapPoint.mapPointWithGeoCoord(37.279620656362695, 127.04734012045131)
//                        tag = locationLatitudeList.size + 1
//                        markerType = MapPOIItem.MarkerType.CustomImage
//                        customImageResourceId = R.drawable.custom_poi_marker_end
//                    })
                    mapView.setZoomLevel(2, true)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun initRecommendedPlace() { // MBTI 추천 장소
        // init kakao map
        this.mapView = MapView(this)
        val markerList = ArrayList<MapPOIItem>()
        val locationNameList = resources.getStringArray(R.array.mbti_recommended_place_name)
        val locationLatitudeList =
            resources.getStringArray(R.array.mbti_recommended_places_latitude)
        val locationLongitudeList =
            resources.getStringArray(R.array.mbti_recommended_places_longitude)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)
        mapView.setMapCenterPointAndZoomLevel( // 맵 기준 위치: 추천 시설로 줌
            MapPoint.mapPointWithGeoCoord(
                intent.getStringExtra("latitude")!!.toDouble(),
                intent.getStringExtra("longitude")!!.toDouble()
            ), 3, false
        )

        // 현재 위치 받아오기 (테스트 할 때는 사용하면 안 됨)
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
                isCustomImageAutoscale = false
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.custom_poi_marker_end
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            })
        }
//        mapView.addPOIItems(markerList.toTypedArray())

        mapView.addPOIItem(MapPOIItem().apply {
            itemName = intent.getStringExtra("placeName")
            mapPoint = MapPoint.mapPointWithGeoCoord(
                intent.getStringExtra("latitude")!!.toDouble(),
                intent.getStringExtra("longitude")!!.toDouble()
            )
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.custom_poi_marker_end
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
        })

        mapView.addPOIItem(MapPOIItem().apply {
            itemName = "내 위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(37.22425223131527, 127.18784380407806)
            tag = locationLatitudeList.size + 1
            markerType = MapPOIItem.MarkerType.RedPin
        })

        binding.relativeLayoutActivityMapDetailsMapView.addView(mapView)


        // spinner init
        binding.spinnerActivityMapDetailsLocationList.hint = "다른 MBTI 성향을 가진 친구들의 추천장소를 알고 싶으세요?"
        binding.spinnerActivityMapDetailsLocationList.setItem(
            resources.getStringArray(R.array.mbti_recommended_place_name).toMutableList()
        )

        // spinner 아이템 클릭 됐을 때
        binding.spinnerActivityMapDetailsLocationList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
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
                    mapView.setMapCenterPointAndZoomLevel(markerList[position].mapPoint, 3, true)
                    mapView.addPOIItem(markerList[position])
//                    mapView.addPOIItem(MapPOIItem().apply {
//                        itemName = "너랑 가까운 위치 테스트할 때 써봐"
//                        mapPoint =
//                            MapPoint.mapPointWithGeoCoord(37.279620656362695, 127.04734012045131)
//                        tag = locationLatitudeList.size + 1
//                        markerType = MapPOIItem.MarkerType.CustomImage
//                        customImageResourceId = R.drawable.custom_poi_marker_end
//                    })
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
        p2: MapPOIItem.CalloutBalloonButtonType?,
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}

    override fun onDaumMapOpenAPIKeyAuthenticationResult(p0: MapView?, p1: Int, p2: String?) {}

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

}