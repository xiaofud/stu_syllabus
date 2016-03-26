package com.hjsmallfly.syllabus.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.MenuInfo;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.StoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowMenuInfoActivity extends AppCompatActivity {

    StoreInfo storeInfo;
    ListView subMenuinfoListView;
    ListView distInfoListView;
    TextView subMenuNameTextView;
    Map<String, List<MenuInfo>> menuList;
    FloatingActionButton callPhoneButton;


    int nowSubMenuPos = 0;

    List<String> subMenuNameList;
    List<MenuInfo> allDishNameList;//全部菜式
    Map<String, Integer> subMenu2Int;//每个首菜单对应的首个dish的Index
    Map<String, Integer> dist2Int;//每个菜式对应的子菜单

    LinearLayout lastSelectLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_menu_info);

        storeInfo = (StoreInfo) getIntent().getSerializableExtra("storeInfo");
        menuList = storeInfo.getMenuList();

        lastSelectLayout = null;

        getSupportActionBar().setTitle(storeInfo.getName());

        subMenuinfoListView = (ListView) findViewById(R.id.sub_menu_info_listView);
        distInfoListView = (ListView) findViewById(R.id.dist_info_listView);
        subMenuNameTextView = (TextView) findViewById(R.id.sub_menu_name);
        callPhoneButton = (FloatingActionButton) findViewById(R.id.call_phone_button);

        subMenuNameList = new ArrayList<>();
        final List<String> subMenuNameList = new ArrayList<>();
        for (String name : menuList.keySet()) {
            subMenuNameList.add(name);
        }
        subMenuNameTextView.setText(subMenuNameList.get(0));

        subMenuinfoListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return subMenuNameList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                final LinearLayout layout = (LinearLayout) LinearLayout.inflate(ShowMenuInfoActivity.this,
                        R.layout.sub_menu_item, null);

                final TextView subMenuItemTextView = (TextView) layout.findViewById(R.id.sub_menu_item_name);

                Log.d("nowSubMenuPos", nowSubMenuPos + "");


                layout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                subMenuItemTextView.setTextColor(Color.parseColor("#787878"));


                subMenuItemTextView.setText(subMenuNameList.get(i));

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nowSubMenuPos = i;
                        distInfoListView.setSelection(0);
                        distInfoListView.setSelection(subMenu2Int.get(subMenuNameList.get(i)));
                        subMenuNameTextView.setText(subMenuNameList.get(i));
                    }
                });

                return layout;
            }
        });

        subMenu2Int = new HashMap<>();
        dist2Int = new HashMap<>();
        allDishNameList = new ArrayList<>();
        int allIndex = 0;
        for (int i = 0; i < subMenuNameList.size(); i++) {
            String subMenuName = subMenuNameList.get(i);
            List<MenuInfo> menuInfoList = menuList.get(subMenuName);
            subMenu2Int.put(subMenuName, allIndex);
            for (int j = 0; j < menuInfoList.size(); j++) {
                allDishNameList.add(menuInfoList.get(j));
                dist2Int.put(menuInfoList.get(j).getDish(), i);
                ++allIndex;
            }
        }

        distInfoListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return allDishNameList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                MenuInfo menuInfo = allDishNameList.get(i);

                LinearLayout layout = (LinearLayout) LinearLayout.inflate(ShowMenuInfoActivity.this,
                        R.layout.dish_item_layout, null);
                TextView dishTextView = (TextView) layout.findViewById(R.id.dishTextView);
                TextView priceTextView = (TextView) layout.findViewById(R.id.priceTextView);
                dishTextView.setText(menuInfo.getDish());
                priceTextView.setText(menuInfo.getPrice());
                return layout;
            }
        });

        distInfoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                LinearLayout ll = (LinearLayout) distInfoListView.getChildAt(0);
                if (ll == null) return;
                TextView tt = (TextView) ll.getChildAt(0);
                if (tt == null) return;
                nowSubMenuPos = dist2Int.get(tt.getText());

//                Log.d("onScrollStateChanged", tt.getText() + "");
//                Log.d("onScrollStateChanged", subMenuNameList.get(nowSubMenuPos));
//                subMenuinfoListView.setSelection(nowSubMenuPos);

                subMenuNameTextView.setText(subMenuNameList.get(nowSubMenuPos));
            }
        });

        callPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] lphone;
                if (storeInfo.getLong_number().trim().isEmpty()) {
                    lphone = new String[0];
                } else {
                    lphone = storeInfo.getLong_number().split("/");
                }
                String[] sphone;
                if (storeInfo.getShort_number().trim().isEmpty()) {
                    sphone = new String[0];
                } else {
                    sphone = storeInfo.getShort_number().split("/");
                }

                final String[] phoneNumber = new String[lphone.length + sphone.length];
                int index = 0;
                for (String phone : lphone) {
                    phoneNumber[index++] = phone.trim();
                }
                for (String phone : sphone) {
                    phoneNumber[index++] = phone.trim();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMenuInfoActivity.this)
                        .setTitle("选择要拨打的号码");

                builder.setItems(phoneNumber, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialPhoneNumber(phoneNumber[i]);
                    }
                });

                builder.show();
            }
        });
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

