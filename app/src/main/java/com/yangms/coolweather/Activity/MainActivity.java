package com.yangms.coolweather.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangms.coolweather.R;
import com.yangms.coolweather.db.City;
import com.yangms.coolweather.db.County;
import com.yangms.coolweather.db.Province;
import com.yangms.coolweather.util.HttpUtil;
import com.yangms.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private Button mBtnBack;
    private ArrayAdapter<String> adapter;
    //private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;
    public static final String WEATHER_ID="weather_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        queryProvinces();  // 加载省级数据
        setOnListViewItemClickListener();

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        mBtnBack = (Button) findViewById(R.id.btn_back);

        mBtnBack.setOnClickListener(this);
    }

    private void setOnListViewItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    currentLevel = LEVEL_CITY;
                    mBtnBack.setVisibility(View.VISIBLE);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    currentLevel = LEVEL_COUNTY;
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                    intent.putExtra(WEATHER_ID,countyList.get(position).getWeatherId());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //Log.e("ProvinceCode",provinceList.get(2).getProvinceCode()+"");
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getId();
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getId() + "/" + selectedCity.getCityCode();
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据。
     *
     * @param
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        boolean reult = false;
                        if ("province".equals(type)) {
                            reult = Utility.handleProvinceResponse(responseText);
                        } else if ("city".equals(type)) {
                            reult = Utility.handleCityResponse(responseText, selectedProvince.getId());
                        } else if ("county".equals(type)) {
                            reult = Utility.handleCountyResponse(responseText, selectedCity.getCityCode());
                        }
                        if (reult) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeProgressDialog();
                                    if ("province".equals(type)) {
                                        queryProvinces();
                                    }
                                    if ("city".equals(type)) {
                                        queryCities();
                                    }
                                    if ("county".equals(type)) {
                                        queryCounties();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 通过runOnUiThread()方法回到主线程处理逻辑
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                Toast.makeText(MainActivity.this,
                                        "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
        );
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    mBtnBack.setVisibility(View.GONE);
                    queryProvinces();
                }
                break;
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            mBtnBack.setVisibility(View.GONE);
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
               /* Log.d(TAG, "issi");
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);*/
            }
            finish();
        }
    }


}
