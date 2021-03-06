package com.example.pbs_moblie

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

import org.jetbrains.anko.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

//,SensorEventListener
//걸음 감지 센서(TYPE_STEP_COUNTER)를 사용하기 위해 SensorEventListener 인터페이스를  상속

//, SensorEventListener <-센서때문에 잠깐 빼두겠숩니다~
class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    private val polyLineOptions = PolylineOptions().width(7f).color(Color.RED) // 이동경로를 그릴 선
    private lateinit var mMap: GoogleMap // 마커, 카메라 지정을 위한 구글 맵 객체
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 위치 요청 메소드 담고 있는 객체
    private lateinit var locationRequest: LocationRequest // 위치 요청할 때 넘겨주는 데이터에 관한 객체
    private lateinit var locationCallback: MyLocationCallBack // 위치 확인되고 호출되는 객체


    // 위치 정보를 얻기 위한 각종 초기화
    private fun locationInit() {
        // FusedLocationProviderClient 객체 생성
        // 이 객체의 메소드를 통해 위치 정보를 요청할 수 있음
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        // 위치 갱신되면 호출되는 콜백 생성
        locationCallback = MyLocationCallBack()
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체 생성
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 가장 정확한 위치를 요청한다,
        locationRequest.interval = 10 // 위치를 갱신하는데 필요한 시간 <밀리초 기준>
        locationRequest.fastestInterval = 5000 // 다른 앱에서 위치를 갱신했을 때 그 정보를 가져오는 시간 <밀리초 기준>

    }

    // 위치 정보를 찾고 나서 인스턴스화되는 클래스
    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            // lastLocation프로퍼티가 가리키는 객체 주소를 받는다.
            // 그 객체는 현재 경도와 위도를 프로퍼티로 갖는다.
            // 그러나 gps가 꺼져 있거나 위치를 찾을 수 없을 때는 lastLocation은 null을 가진다.
            val location = locationResult?.lastLocation
            //  gps가 켜져 있고 위치 정보를 찾을 수 있을 때 다음 함수를 호출한다. <?. : 안전한 호출>1
            location?.run {
                var distance: Double? = null
                // 현재 경도와 위도를 LatLng메소드로 설정한다.
                var latLng = LatLng(latitude, longitude)
                // 카메라를 이동한다.(이동할 위치,줌 수치)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                // 마커를 추가한다.
                // mMap.addMarker(MarkerOptions().position(latLng).title("위치이동"))
                // polyLine에 좌표 추가
                polyLineOptions.add(latLng)
                // 선 그리기
                mMap.addPolyline(polyLineOptions)
            }
        }
    }

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var nickname: String = " "  //intent로 받은 닉네임을 저장할 변수
    var currentdate: String = ""
    var currentstep: Int = 0
    var finalstep: Int = 0
    var lastTime: Long = 0
    var sumTime: Long = 0

    // 이 메소드부터 프로그램 시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Date: LocalDate = LocalDate.now() //현재 날짜 표시
        currentdate = Date.format(DateTimeFormatter.ofPattern("yyyy-M-dd")) //Date를 String으로 변환
        val intent1 = getIntent()


        nickname = intent1.getStringExtra("nickname") //intent로 받아온 닉네임을 nickname에 저장

         val sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepcountsensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

         if (stepcountsensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show()
        }



        calbtn.setOnClickListener {
            intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("nickname", nickname) //닉네임을 보냄
            startActivity(intent)
        }

        //랭킹 버튼만들거
        rankbtn.setOnClickListener {
            intent = Intent(this, RankActivity::class.java)
            startActivity(intent)
        }


        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 세로 모드로 화면 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 위치 정보를 얻기 위한 각종 초기화
        locationInit()
        // 프래그먼트 매니저로부터 SupportMapFragment프래그먼트를 얻는다. 이 프래그먼트는 지도를 준비하는 기능이 있다.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // 지도가 준비되면 알림을 받는다. (아마, get함수에서 다른 함수를 호출해서 처리하는 듯)
        mapFragment.getMapAsync(this)
    }

    // 프로그램이 켜졌을 때만 위치 정보를 요청한다
    override fun onResume() {
        super.onResume()
        // '앱이 gps사용'에 대한 권한이 있는지 체크
        // 거부됐으면 showPermissionInfoDialog(알림)메소드를 호출, 승인됐으면 addLocationListener(위치 요청)메소드를 호출
        permissionCheck(cancel = { showPermissionInfoDialog() },
            ok = { addLocationListener() })

        //액티비티가 동작할 때만 센서가 동작하게 하기 위함(터리 소모를 방지하기 위해)
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        //두번째 인자는 사용할 센서 종류
        //세번째 인자는 값을 얼마나 자주 받을것인지 (화면 방향이 전환될 때 적합한 정도로 값을 받음)
    }

    // 프로그램이 중단되면 위치 요청을 삭제한다
    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

        //액티비티가 가려진 경우(사용하지 않을 경우)
        // 걸음 감지 세서를 멈춤(비활성화)
        sensorManager.unregisterListener(this)
    }

    // 위치 요청 메소드
    @SuppressLint("MissingPermission")
    private fun addLocationListener() {
        // 위치 정보 요청
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체, 위치 갱신되면 호출되는 콜백, 특정 스레드 지정(별 일 없으니 null)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        // googleMap객체 생성
        // googleMap을 이용하여 마커나 카메라,선 등을 조정하는 것이다
        mMap = googleMap
        // 서울을 기준으로 위도,경도 지정
        val SEOUL = LatLng(37.56, 126.97)
        // MarkerOptions에 위치 정보를 넣을 수 있음
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        // 마커 추가
        // mMap.addMarker(markerOptions)
        // 카메라 서울로 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL))
        // 10만큼 줌인 하기
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
        // P.s. onMapReady가 호출된 후에 위치 수정이 가능하다!!
    }

    /** 아래 내용은 사용자에게 권한 부여를 받고 처리하는 메소드이다. **/
    private val gps_request_code = 1000 // gps에 관한 권한 요청 코드(번호)
    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) {
        // 앱에 GPS이용하는 권한이 없을 때
        // <앱을 처음 실행하거나, 사용자가 이전에 권한 허용을 안 했을 때 성립>
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {  // <PERMISSION_DENIED가 반환됨>
            // 이전에 사용자가 앱 권한을 허용하지 않았을 때 -> 왜 허용해야되는지 알람을 띄움
            // shouldShowRequestPermissionRationale메소드는 이전에 사용자가 앱 권한을 허용하지 않았을 때 ture를 반환함
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                cancel()
            }
            // 앱 처음 실행했을 때
            else
            // 권한 요청 알림
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), gps_request_code
                )
        }
        // 앱에 권한이 허용됨
        else
            ok()
    }

    // 사용자가 권한 요청<허용,비허용>한 후에 이 메소드가 호출됨
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // (gps 사용에 대한 사용자의 요청)일 때
            gps_request_code -> {
                // 요청이 허용일 때
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    addLocationListener()
                // 요청이 비허용일 때
                else {
                    toast("권한 거부 됨")
                    finish()
                }
            }
        }
    }

    // 사용자가 이전에 권한을 거부했을 때 호출된다.
    private fun showPermissionInfoDialog() {
        alert("지도 정보를 얻으려면 위치 권한이 필수로 필요합니다", "") {
            yesButton {
                // 권한 요청
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), gps_request_code
                )
            }
            noButton {
                toast("권한 거부 됨")
                finish()
            }
        }.show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) { //동작을 감지하면 이벤트를 발생시켜 1씩 증가된
        // 최종 값을 전달하는 함수

        var currentTime = System.nanoTime()
        work_num.text = "Step Count: " + "${event!!.values[0].toInt()}" //걸음 수를 worknum_text에 출력
        var lastTime = System.nanoTime()


        var gabTime = lastTime - currentTime

        database.child("Step").child(nickname).child(currentdate).child("stepcount")
            .setValue("${event!!.values[0].toInt()}") //파이어베이스에 걸음 수 저장
        database.child("Step").child("ranking").child(nickname).child("nickname")
            .setValue(nickname) //파이어베이스에 닉네임 저장
        database.child("Step").child("ranking").child(nickname)
            .child("stepcount") //파이어베이스 랭킹에 걸음 수 저장
            .setValue("${event!!.values[0].toInt()}")

        sumTime+=gabTime

        work_sec.text = "${sumTime / 1000000}초" //초 work_sec에 출력

        work_min.text = "${sumTime / 60000000}분" //분 work_min에 출력


        database.child("Step").child(nickname).child(currentdate).child("timecount")
            .setValue( "${sumTime / 60000000}분" ) //파이어베이스에 걸은 시간 저장

        database.child("Step").child("timeranking").child(nickname).child("time")
            .setValue("${sumTime / 60000000}분") //파이어베이스에 걸은 시간 저장

        database.child("Step").child("timeranking").child(nickname).child("nickname")
            .setValue(nickname) //파이어베이스에 닉네임 저장


    }
}




