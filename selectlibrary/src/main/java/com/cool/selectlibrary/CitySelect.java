package com.cool.selectlibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.cool.selectlibrary.R;

/**
 * Created by cool on 2017/12/26.
 */

public class CitySelect implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static int TAB_PROVINCE = 1;
    private final static int TAB_CITY = 2;
    private final static int TAB_AREA = 3;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View itemView;
    private TextView mProvinceTextView;
    private TextView mCityTextView;
    private TextView mAreaTextView;
    private View mIndicatorView;
    private ListView mListView;
    private List<Province> mProvinces;
    private List<String> mProvinceData = new ArrayList<>();
    private List<String> mCityData = new ArrayList<>();
    private List<String> mAreaData = new ArrayList<>();
    private List<String> mData = new ArrayList<>();

    private int provinceSelectIndex = -1;
    private int citySelectIndex = -1;
    private int areaSelectIndex = -1;
    private SelectAdapter mSelectAdapter;

    private int tabSelect = TAB_PROVINCE;
    private Province province;
    private List<Province.City> mCities;
    private Province.City city;
    private int mainColor = Color.parseColor("#FF4040");
    private Dialog mDialog;

    public CitySelect(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);

        provinceSelectIndex = -1;
        citySelectIndex = -1;
        areaSelectIndex = -1;
        tabSelect = TAB_PROVINCE;

        fillData();
        initView();
        initAdapter();
    }

    private void fillData() {
        ProvinceData provinceData = new ProvinceData();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Province>>() {
        }.getType();
        mProvinces = gson.fromJson(provinceData.data, type);
    }

    private void initView() {
        itemView = mLayoutInflater.inflate(R.layout.layout_city_select, null);
        mProvinceTextView = itemView.findViewById(R.id.tv_province);
        mCityTextView = itemView.findViewById(R.id.tv_city);
        mAreaTextView = itemView.findViewById(R.id.tv_area);
        mIndicatorView = itemView.findViewById(R.id.indicator);
        mListView = itemView.findViewById(R.id.lv_list);

        mProvinceTextView.setOnClickListener(this);
        mCityTextView.setOnClickListener(this);
        mAreaTextView.setOnClickListener(this);
        mProvinceTextView.measure(0, 0);
        ViewGroup.LayoutParams layoutParams = mIndicatorView.getLayoutParams();
        layoutParams.width = mProvinceTextView.getMeasuredWidth();
        mIndicatorView.setLayoutParams(layoutParams);
        mProvinceTextView.setTextColor(mainColor);
    }

    private void initAdapter() {
        mSelectAdapter = new SelectAdapter();
        mListView.setAdapter(mSelectAdapter);
        mListView.setOnItemClickListener(this);
        fillProvincesData();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_province) {//省
            if (tabSelect == TAB_PROVINCE) {
                return;
            }
            tabSelect = TAB_PROVINCE;
            mData.clear();
            mData.addAll(mProvinceData);
            mSelectAdapter.notifyDataSetChanged();
            selectProvinceTab();
            doIndicatorAnim(mProvinceTextView);
        } else if (v.getId() == R.id.tv_city) {//市
            if (tabSelect == TAB_CITY) {
                return;
            }
            tabSelect = TAB_CITY;
            mData.clear();
            mData.addAll(mCityData);
            mSelectAdapter.notifyDataSetChanged();
            selectCityTab();
            doIndicatorAnim(mCityTextView);
        } else if (v.getId() == R.id.tv_area) {//区
            if (tabSelect == TAB_AREA) {
                return;
            }
            tabSelect = TAB_AREA;
            mData.clear();
            mData.addAll(mAreaData);
            mSelectAdapter.notifyDataSetChanged();
            selectAreaTab();
            doIndicatorAnim(mAreaTextView);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (tabSelect) {
            case TAB_PROVINCE://省
                provinceSelectIndex = position;
                citySelectIndex = -1;
                areaSelectIndex = -1;
                province = mProvinces.get(position);
                String provinceName = province.name;
                mProvinceTextView.setText(provinceName);
                mCityTextView.setVisibility(View.VISIBLE);
                mCityTextView.setText("请选择");
                mAreaTextView.setVisibility(View.INVISIBLE);
                selectCityTab();
                fillCityData();
                tabSelect = TAB_CITY;
                doIndicatorAnim(mCityTextView);
                break;
            case TAB_CITY://市
                citySelectIndex = position;
                areaSelectIndex = -1;
                city = mCities.get(position);
                String cityName = city.name;
                mCityTextView.setText(cityName);
                mAreaTextView.setVisibility(View.VISIBLE);
                mAreaTextView.setText("请选择");
                selectAreaTab();
                fillAreaData();
                tabSelect = TAB_AREA;
                doIndicatorAnim(mAreaTextView);
                break;
            case TAB_AREA://区
                areaSelectIndex = position;
                String area = mAreaData.get(position);
                mAreaTextView.setText(area);
                String provinceString = mProvinceData.get(provinceSelectIndex);
                String cityString = mCityData.get(citySelectIndex);
                String areaString = mAreaData.get(areaSelectIndex);
                doIndicatorAnim(mAreaTextView);
                mSelectAdapter.notifyDataSetChanged();
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                if (onSelectListener != null) {
                    onSelectListener.onSelect(provinceString, cityString, areaString);
                }
                break;
        }
    }

    /**
     * 填充省数据
     */
    private void fillProvincesData() {
        mProvinceData.clear();
        for (Province province : mProvinces) {
            mProvinceData.add(province.name);
        }
        mData.clear();
        mData.addAll(mProvinceData);
        mSelectAdapter.notifyDataSetChanged();
    }

    /**
     * 填充市数据
     */
    private void fillCityData() {
        mCityData.clear();
        if (province != null) {
            mCities = province.city;
            for (Province.City city : mCities) {
                mCityData.add(city.name);
            }
            mData.clear();
            mData.addAll(mCityData);
            mSelectAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 填充区县市
     */
    private void fillAreaData() {
        mAreaData.clear();
        if (city != null) {
            List<String> areas = city.area;
            for (String area : areas) {
                mAreaData.add(area);
            }
            mData.clear();
            mData.addAll(mAreaData);
            mSelectAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选上区TAB
     */
    private void selectAreaTab() {
        mProvinceTextView.setTextColor(Color.BLACK);
        mCityTextView.setTextColor(Color.BLACK);
        mAreaTextView.setTextColor(mainColor);
    }

    /**
     * 选上市TAB
     */
    private void selectCityTab() {
        mProvinceTextView.setTextColor(Color.BLACK);
        mCityTextView.setTextColor(mainColor);
        mAreaTextView.setTextColor(Color.BLACK);
    }

    /**
     * 选上省TAB
     */
    private void selectProvinceTab() {
        mProvinceTextView.setTextColor(mainColor);
        mCityTextView.setTextColor(Color.BLACK);
        mAreaTextView.setTextColor(Color.BLACK);
    }

    /**
     * 游标动画
     */
    private void doIndicatorAnim(final TextView tabTextView) {
        tabTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float endX = tabTextView.getX();
                float endWidth = tabTextView.getMeasuredWidth();
                doAnim(endX, endWidth);
                tabTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 真正做动画
     *
     * @param endX
     * @param endWidth
     */
    private void doAnim(float endX, float endWidth) {
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mIndicatorView, "X", mIndicatorView.getX(), endX);
        final ViewGroup.LayoutParams layoutParams = mIndicatorView.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofFloat(layoutParams.width, endWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                layoutParams.width = (int) animatedValue;
                mIndicatorView.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(new Animator[]{translationAnimator, widthAnimator});
        set.start();
    }

    public View getView() {
        removeParentFirstifExit();
        return itemView;
    }

    private void removeParentFirstifExit() {
        ViewGroup parent = (ViewGroup) itemView.getParent();
        if (parent != null) {
            parent.removeView(itemView);
        }
    }


    /**
     * 创建并返回dialog
     *
     * @return Dialog
     */
    public Dialog dialog() {
        removeParentFirstifExit();
        mDialog = new Dialog(mContext, R.style.m_white_bg_dialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(itemView);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setWindowAnimations(R.style.AnimDialog);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        layoutParams.width = width;
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, mContext.getResources().getDisplayMetrics());
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        mDialog.setCancelable(true);
        return mDialog;
    }

    /**
     * 设置主题颜色
     *
     * @param color
     */
    public CitySelect setMainColor(int color) {
        this.mainColor = color;
        mProvinceTextView.setTextColor(color);
        mIndicatorView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置省市区的数据
     *
     * @param provinces
     */
    public CitySelect setProvinceData(List<Province> provinces) {
        this.mProvinces = provinces;
        fillProvincesData();
        return this;
    }

    private OnSelectListener onSelectListener;

    public CitySelect listener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }

    public interface OnSelectListener {
        void onSelect(String province, String city, String area);
    }


    class SelectAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public SelectAdapter() {
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_city_name, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mTextView = convertView.findViewById(R.id.tv_text);
                viewHolder.mCheckview = convertView.findViewById(R.id.checkview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String city = mData.get(position);
            viewHolder.mTextView.setText(city);
            viewHolder.mCheckview.setVisibility(View.INVISIBLE);
            viewHolder.mCheckview.setColor(mainColor);
            viewHolder.mTextView.setTextColor(Color.BLACK);

            switch (tabSelect) {
                case TAB_PROVINCE://省
                    if (provinceSelectIndex == position) {
                        viewHolder.mCheckview.setVisibility(View.VISIBLE);
                        viewHolder.mTextView.setTextColor(mainColor);
                    }
                    break;
                case TAB_CITY://市
                    if (citySelectIndex == position) {
                        viewHolder.mCheckview.setVisibility(View.VISIBLE);
                        viewHolder.mTextView.setTextColor(mainColor);
                    }
                    break;
                case TAB_AREA://区
                    if (areaSelectIndex == position) {
                        viewHolder.mCheckview.setVisibility(View.VISIBLE);
                        viewHolder.mTextView.setTextColor(mainColor);
                    }
                    break;
            }

            return convertView;
        }


        class ViewHolder {
            public TextView mTextView;
            public CheckView mCheckview;
        }
    }
}
