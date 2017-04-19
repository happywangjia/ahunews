package com.example.wangjia.news.utils;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wangjia.news.R;

/**
 * Created by wangjia on 2017/4/9.
 */

public class VpSimpleFragment extends Fragment {
    private String mTitle;
    public static final String BUNDLE_TITLE="title";
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            mTitle=bundle.getString(BUNDLE_TITLE);
        }
        View view=inflater.inflate(R.layout.fragment_amuse,null);
        listView= (ListView) view.findViewById(R.id.amuse_listview);
        BaseAdapter adapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return 20;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout line=new LinearLayout(getContext());
                line.setOrientation(LinearLayout.VERTICAL);
                TextView tv1=new TextView(getContext());
                tv1.setText("标题"+position);
                tv1.setLines(1);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                tv1.setGravity(Gravity.LEFT);
                TextView tv2=new TextView(getContext());
                tv2.setText("时间"+position);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv2.setLines(1);
                tv2.setGravity(Gravity.RIGHT);
                line.addView(tv1);
                line.addView(tv2);


                return line;
            }
        };
        listView.setAdapter(adapter);

        return view;

    }

    public static VpSimpleFragment newInstance(String title){
        Bundle bundle=new Bundle();
        bundle.putString(BUNDLE_TITLE,title);
        VpSimpleFragment fragment=new VpSimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }




}
