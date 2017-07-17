package chao.gaodemapdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    MapView mMapView;
    AMap aMap;
    private ArrayList<String> list;
    private Button weixing;
    private Button yejing;
    private Button putong;
    private boolean value = true;
    private LatLng latLng;
    private LatLng mylatlng;
    private View v;
    private ListView lv;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weixing = (Button) findViewById(R.id.weixing);
        yejing = (Button) findViewById(R.id.yejing);
        putong = (Button) findViewById(R.id.putong);
        mMapView = (MapView) findViewById(R.id.map);
        v = View.inflate(this, R.layout.navlist, null);
        lv = (ListView) v.findViewById(R.id.ls);
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mMapView.getMap();
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);//显示室内图层
        weixing.setOnClickListener(this);
        yejing.setOnClickListener(this);
        putong.setOnClickListener(this);
        LatLng latLng = new LatLng(39.906901, 116.397972);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        AMap.OnInfoWindowClickListener listener = new AMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                initLv(value);

                alertDialog.show();

            }
        };
        //绑定信息窗点击事件
        aMap.setOnInfoWindowClickListener(listener);
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void initLv(boolean value) {
        if (!value) {
            return;
        }
        if (value) {
            this.value = false;
        }
        boolean installqq = isInstallByread("com.tencent.map");
        boolean installnav = isInstallByread("com.autonavi.minimap");
        boolean installbaidu = isInstallByread("com.baidu.BaiduMap");
        list = new ArrayList<String>();
        if (installqq) {
            list.add("腾讯地图");
        }
        if (installbaidu) {
            list.add("百度地图");
        }
        if (installnav) {
            list.add("高德地图");
        } else {
            startActivity(new Intent(MainActivity.this, DaoHang.class));
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).equals("腾讯地图")) {
                    //腾讯地图
                    if (mylatlng != null && latLng != null) {
                        // 腾讯地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=" + "&fromcoord=" + mylatlng.latitude + "," + mylatlng.longitude + "&to=" + null + "&tocoord=" + latLng.latitude + "," + latLng.longitude + "&policy=0&referer=appName"));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("百度地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 百度地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + latLng.latitude + "," + latLng.longitude));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("高德地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 高德地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=目的地&dev=0&t=2"));
                        startActivity(naviIntent);
                    }
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(lv);
        alertDialog = builder.create();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weixing:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.putong:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.yejing:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
        }
    }
}
