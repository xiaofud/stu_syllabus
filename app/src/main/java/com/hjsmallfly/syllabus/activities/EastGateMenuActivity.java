package com.hjsmallfly.syllabus.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.parsers.TakeOutParser;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.StoreInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EastGateMenuActivity extends AppCompatActivity {

    List<StoreInfo> storeInfoList;
    ListView storeListView;

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_east_gate_menu);

        getSupportActionBar().setTitle("东门外卖");

        storeInfoList = TakeOutParser.parser(this);

        storeListView = (ListView) findViewById(R.id.menu_list);

        if (storeInfoList == null) {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        } else {
            storeListView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return storeInfoList.size();
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
                    LinearLayout storeItemLayout = (LinearLayout) LinearLayout.inflate(EastGateMenuActivity.this,
                            R.layout.store_item, null);
                    TextView storeNameTextView = (TextView) storeItemLayout.findViewById(R.id.store_name);
                    TextView conditionTextView = (TextView) storeItemLayout.findViewById(R.id.condition);
                    TextView longNumberTextView = (TextView) storeItemLayout.findViewById(R.id.long_number);
                    TextView shortNumberTextView = (TextView) storeItemLayout.findViewById(R.id.short_number);

                    final StoreInfo storeInfo = storeInfoList.get(i);

                    storeNameTextView.setText(storeInfo.getName());
                    conditionTextView.setText("起送条件: " + storeInfo.getCondition());
                    longNumberTextView.setText("长号: " + storeInfo.getLong_number());
                    if (!storeInfo.getShort_number().trim().isEmpty())
                        shortNumberTextView.setText("短号: " + storeInfo.getShort_number());
                    else
                        shortNumberTextView.setText("短号: 无");

                    storeItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EastGateMenuActivity.this,
                                    ShowMenuInfoActivity.class);
                            intent.putExtra("storeInfo", storeInfo);
                            startActivity(intent);
                        }
                    });

                    storeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {


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

                            AlertDialog.Builder builder = new AlertDialog.Builder(EastGateMenuActivity.this)
                                    .setTitle("选择要拨打的号码");

                            builder.setItems(phoneNumber, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialPhoneNumber(phoneNumber[i]);
                                }
                            });

                            builder.show();
                            return true;
                        }
                    });

                    return storeItemLayout;

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(EastGateMenuActivity.this);
        inflater.inflate(R.menu.show_tip_of_east_gate, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String tips = "        在外卖首界面长按进行拨号,直接点击进入外卖菜单栏界面\n" +
                "        菜单栏界面可以点击左边的子菜单快速定位，也可以用右上角的菜单栏中的放大镜进行搜索\n" +
                "        点击每一个菜品可以添加到口袋中,口袋用于记录要点的菜品\n" +
                "        在口袋中可以增加删除,还能提供价格的估算\n" +
                "        (注意本应用不能直接点外卖,可以先把要点的东西准备在口袋中\n" +
                "        然后点击拨号按钮(菜单栏和悬浮按钮都可以)\n" +
                "        菜单可能会有极个别错误,请以实际菜单为准\n";


        switch (item.getItemId()) {
            case R.id.show_east_gate_tips:

                TextView textView = new TextView(EastGateMenuActivity.this);
                textView.setText(tips);
                textView.setTextSize(15);
                textView.setPadding(30,30,30,30);

                AlertDialog.Builder builder = new AlertDialog.Builder(EastGateMenuActivity.this)
                        .setTitle("小Tips")
                        .setView(textView);
                builder.show();


                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
