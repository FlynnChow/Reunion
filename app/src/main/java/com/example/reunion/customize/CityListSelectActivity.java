package com.example.reunion.customize;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reunion.R;
import com.lljjcoder.style.citylist.bean.CityInfoBean;
import com.lljjcoder.style.citylist.sortlistview.CharacterParser;
import com.lljjcoder.style.citylist.sortlistview.PinyinComparator;
import com.lljjcoder.style.citylist.sortlistview.SideBar;
import com.lljjcoder.style.citylist.sortlistview.SortModel;
import com.lljjcoder.style.citylist.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citylist.widget.CleanableEditView;
import com.lljjcoder.style.citypickerview.R.id;
import com.lljjcoder.style.citypickerview.R.layout;
import com.lljjcoder.utils.PinYinUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CityListSelectActivity extends AppCompatActivity {
    CleanableEditView mCityTextSearch;
    TextView mCurrentCityTag;
    TextView mCurrentCity;
    TextView mLocalCityTag;
    TextView mLocalCity;
    ListView sortListView;
    TextView mDialog;
    SideBar mSidrbar;
    ImageView imgBack;
    public SortAdapter adapter;
    private CharacterParser characterParser;
    private List<SortModel> sourceDateList;
    private PinyinComparator pinyinComparator;
    private List<CityInfoBean> cityListInfo = new ArrayList();
    private CityInfoBean cityInfoBean = new CityInfoBean();
    public static final int CITY_SELECT_RESULT_FRAG = 50;
    public static List<CityInfoBean> sCityInfoBeanList = new ArrayList();
    public PinYinUtils mPinYinUtils = new PinYinUtils();

    public CityListSelectActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.cust_city_select);
        this.initView();
        this.initList();
        this.setCityData(CityListLoader.getInstance().getCityListData());
    }

    private void initView() {
        this.mCityTextSearch = (CleanableEditView)this.findViewById(R.id.cityInputText);
        this.mCurrentCityTag = (TextView)this.findViewById(R.id.currentCityTag);
        this.mCurrentCity = (TextView)this.findViewById(R.id.currentCity);
        this.mLocalCityTag = (TextView)this.findViewById(R.id.localCityTag);
        this.mLocalCity = (TextView)this.findViewById(R.id.localCity);
        this.sortListView = (ListView)this.findViewById(R.id.country_lvcountry);
        this.mDialog = (TextView)this.findViewById(R.id.dialog);
        this.mSidrbar = (SideBar)this.findViewById(R.id.sidrbar);
        this.imgBack = (ImageView)this.findViewById(R.id.imgBack);
        this.imgBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CityListSelectActivity.this.finish();
            }
        });
    }

    private void setCityData(List<CityInfoBean> cityList) {
        this.cityListInfo = cityList;
        if (this.cityListInfo != null) {
            int count = cityList.size();
            String[] list = new String[count];

            for(int i = 0; i < count; ++i) {
                list[i] = ((CityInfoBean)cityList.get(i)).getName();
            }

            this.sourceDateList.addAll(this.filledData(cityList));
            Collections.sort(this.sourceDateList, this.pinyinComparator);
            this.adapter.notifyDataSetChanged();
        }
    }

    private List<SortModel> filledData(List<CityInfoBean> cityList) {
        List<SortModel> mSortList = new ArrayList();

        for(int i = 0; i < cityList.size(); ++i) {
            CityInfoBean result = (CityInfoBean)cityList.get(i);
            if (result != null) {
                SortModel sortModel = new SortModel();
                String cityName = result.getName();
                if (!TextUtils.isEmpty(cityName) && cityName.length() > 0) {
                    String pinyin = "";
                    if (cityName.equals("重庆市")) {
                        pinyin = "chong";
                    } else if (cityName.equals("长沙市")) {
                        pinyin = "chang";
                    } else if (cityName.equals("长春市")) {
                        pinyin = "chang";
                    } else {
                        pinyin = this.mPinYinUtils.getStringPinYin(cityName.substring(0, 1));
                    }

                    if (!TextUtils.isEmpty(pinyin)) {
                        sortModel.setName(cityName);
                        String sortString = pinyin.substring(0, 1).toUpperCase();
                        if (sortString.matches("[A-Z]")) {
                            sortModel.setSortLetters(sortString.toUpperCase());
                        } else {
                            sortModel.setSortLetters("#");
                        }

                        mSortList.add(sortModel);
                    } else {
                    }
                }
            }
        }

        return mSortList;
    }

    private void initList() {
        this.sourceDateList = new ArrayList();
        this.adapter = new SortAdapter(this, this.sourceDateList);
        this.sortListView.setAdapter(this.adapter);
        this.characterParser = CharacterParser.getInstance();
        this.pinyinComparator = new PinyinComparator();
        this.mSidrbar.setTextView(this.mDialog);
        this.mSidrbar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String s) {
                int position = CityListSelectActivity.this.adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    CityListSelectActivity.this.sortListView.setSelection(position);
                }

            }
        });
        this.sortListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityName = ((SortModel)CityListSelectActivity.this.adapter.getItem(position)).getName();
                CityListSelectActivity.this.cityInfoBean = CityInfoBean.findCity(CityListSelectActivity.this.cityListInfo, cityName);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("cityinfo", CityListSelectActivity.this.cityInfoBean);
                intent.putExtras(bundle);
                CityListSelectActivity.this.setResult(-1, intent);
                CityListSelectActivity.this.finish();
            }
        });
        this.mCityTextSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CityListSelectActivity.this.filterData(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = this.sourceDateList;
        } else {
            ((List)filterDateList).clear();
            Iterator var3 = this.sourceDateList.iterator();

            label22:
            while(true) {
                SortModel sortModel;
                String name;
                do {
                    if (!var3.hasNext()) {
                        break label22;
                    }

                    sortModel = (SortModel)var3.next();
                    name = sortModel.getName();
                } while(!name.contains(filterStr) && !this.characterParser.getSelling(name).startsWith(filterStr));

                ((List)filterDateList).add(sortModel);
            }
        }

        Collections.sort((List)filterDateList, this.pinyinComparator);
        this.adapter.updateListView((List)filterDateList);
    }
}
