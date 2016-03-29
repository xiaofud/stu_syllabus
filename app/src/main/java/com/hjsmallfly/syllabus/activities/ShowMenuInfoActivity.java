package com.hjsmallfly.syllabus.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.syllabus.MenuInfo;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.StoreInfo;
import com.hjsmallfly.syllabus.syllabus.StudentInfo;

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
    CardView show_poke_card;
    TextView show_my_poke_linear;
    ImageButton close_poke_button;


    TextView sum_poke_price_text;
    ListView pokeListView;

    int nowSubMenuPos = 0;

    List<String> subMenuNameList;
    List<MenuInfo> allDishNameList;//全部菜式
    Map<String, Integer> subMenu2Int;//每个首菜单对应的首个dish的Index
    Map<String, Integer> dist2Int;//每个菜式对应的子菜单

    LinearLayout lastSelectLayout;

    HashMap<MenuInfo, Integer> buyMenuInfoMap;//购买的菜单已经对应的数量
    List<MenuInfo> buyMenuInfoList;//购买的菜单

    private LinearLayout mDictQueryLinearLayout;
    private EditText mDictQueryEditText;
    private Button mDictQueryButton;
    private Button mFindForwardDictButton;
    private Button mFindNextDictButton;

    int lastqueryPositon = 0;

    private void assignFindViews() {
        mDictQueryLinearLayout = (LinearLayout) findViewById(R.id.dictQueryLinearLayout);
        mDictQueryEditText = (EditText) findViewById(R.id.dictQueryEditText);
        mDictQueryButton = (Button) findViewById(R.id.dictQueryButton);
        mFindForwardDictButton = (Button) findViewById(R.id.findForwardDictButton);
        mFindNextDictButton = (Button) findViewById(R.id.findNextDictButton);


        mDictQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryDict = mDictQueryEditText.getText().toString();
                if (queryDict.isEmpty()) {
                    Toast.makeText(ShowMenuInfoActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                for (int i = 0; i < allDishNameList.size(); i++) {
                    MenuInfo menuInfo = allDishNameList.get(i);
                    if (menuInfo.getDish().indexOf(queryDict) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowMenuInfoActivity.this, "查找不到选项", Toast.LENGTH_SHORT).show();
                } else {
//                    distInfoListView.setSelection(0);
                    distInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }
            }
        });

        mFindNextDictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryDict = mDictQueryEditText.getText().toString();
                if (queryDict.isEmpty()) {
                    Toast.makeText(ShowMenuInfoActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                for (int i = lastqueryPositon + 1; i < allDishNameList.size(); i++) {
                    MenuInfo menuInfo = allDishNameList.get(i);
                    if (menuInfo.getDish().indexOf(queryDict) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowMenuInfoActivity.this, "已经是最后一个选项", Toast.LENGTH_SHORT).show();
                } else {
//                    distInfoListView.setSelection(0);
                    distInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }
            }
        });

        mDictQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryDict = mDictQueryEditText.getText().toString();
                if (queryDict.isEmpty()) {
                    Toast.makeText(ShowMenuInfoActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                if (lastqueryPositon == -1) lastqueryPositon = allDishNameList.size();

                for (int i = lastqueryPositon - 1; i > 0; --i) {
                    MenuInfo menuInfo = allDishNameList.get(i);
                    if (menuInfo.getDish().indexOf(queryDict) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowMenuInfoActivity.this, "已经是第一个选项", Toast.LENGTH_SHORT).show();
                } else {
//                    distInfoListView.setSelection(0);
                    distInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }

            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_menu_info);

        storeInfo = (StoreInfo) getIntent().getSerializableExtra("storeInfo");
        menuList = storeInfo.getMenuList();

        lastSelectLayout = null;

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(storeInfo.getName());


        assignFindViews();

        subMenuinfoListView = (ListView) findViewById(R.id.sub_menu_info_listView);
        distInfoListView = (ListView) findViewById(R.id.dist_info_listView);
        pokeListView = (ListView) findViewById(R.id.poke_list_view);
        subMenuNameTextView = (TextView) findViewById(R.id.sub_menu_name);
        sum_poke_price_text = (TextView) findViewById(R.id.sum_poke_price_text);
        callPhoneButton = (FloatingActionButton) findViewById(R.id.call_phone_button);
        show_poke_card = (CardView) findViewById(R.id.show_poke_card);
        showPoke(false);
        show_my_poke_linear = (TextView) findViewById(R.id.show_my_poke_linear);
        show_my_poke_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPoke(true);
            }
        });
        close_poke_button = (ImageButton) findViewById(R.id.close_poke_button);
        close_poke_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPoke(false);
            }
        });

        buyMenuInfoMap = new HashMap<>();
        buyMenuInfoList = new ArrayList<>();
        updateSumPrice();

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

                if (i == nowSubMenuPos) {
                    subMenuItemTextView.setBackgroundColor(Color.parseColor("#99CC00"));
                }
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

            class ViewHolder {
                private TextView dishTextView;
                private TextView priceTextView;
                private TextView buyItemNum;

            }

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
            public View getView(int i, View convertView, ViewGroup viewGroup) {
                final MenuInfo menuInfo = allDishNameList.get(i);

                LinearLayout layout;
                ViewHolder viewHolder;

                if (convertView == null) {
                    layout = (LinearLayout) LinearLayout.inflate(ShowMenuInfoActivity.this,
                            R.layout.dish_item_layout, null);
                    viewHolder = new ViewHolder();
                    viewHolder.dishTextView = (TextView) layout.findViewById(R.id.dishTextView);
                    viewHolder.priceTextView = (TextView) layout.findViewById(R.id.priceTextView);
                    viewHolder.buyItemNum = (TextView) layout.findViewById(R.id.buy_item_num);

                    layout.setTag(viewHolder);
                } else {  // 之前缓存过的view
                    layout = (LinearLayout) convertView;
                    viewHolder = (ViewHolder) layout.getTag();
                }


                viewHolder.dishTextView.setText(menuInfo.getDish());
                viewHolder.priceTextView.setText("价格: " + menuInfo.getPrice());

                if (buyMenuInfoMap.get(menuInfo) != null) {
                    viewHolder.buyItemNum.setVisibility(View.VISIBLE);
                    viewHolder.buyItemNum.setText(buyMenuInfoMap.get(menuInfo) + "份");
                } else {
                    viewHolder.buyItemNum.setVisibility(View.GONE);
                }


                //点击当做是购买
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buyMenuInfoMap.get(menuInfo) == null) {
                            buyMenuInfoMap.put(menuInfo, 1);
                            buyMenuInfoList.add(menuInfo);
                        } else {
                            Integer num = buyMenuInfoMap.get(menuInfo);
                            buyMenuInfoMap.put(menuInfo, num + 1);
                        }
                        Log.d("BUY_THING", menuInfo.getDish() + " " + buyMenuInfoMap.get(menuInfo));
                        if (pokeListView.getAdapter() != null) {
                            BaseAdapter adapter = (BaseAdapter) pokeListView.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                        updateSumPrice();
                    }
                });

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
                if (subMenuinfoListView.getAdapter() != null) {
                    BaseAdapter adapter = (BaseAdapter) subMenuinfoListView.getAdapter();
                    adapter.notifyDataSetChanged();

                }
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


        //口袋选项
        pokeListView.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return buyMenuInfoList.size();
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
                LinearLayout linear = (LinearLayout) LinearLayout.inflate(getApplicationContext(),
                        R.layout.buy_poke_item, null);

                TextView buy_poke_item_name = (TextView) linear.findViewById(R.id.buy_poke_item_name);
                buy_poke_item_name.setText(buyMenuInfoList.get(i).getDish());

                final MenuInfo menuInfo = buyMenuInfoList.get(i);
                Log.d("BUY_THING", menuInfo.getDish() + " " + buyMenuInfoMap.get(menuInfo));

                final Integer num = buyMenuInfoMap.get(menuInfo);

                TextView buy_poke_item_num = (TextView) linear.findViewById(R.id.buy_poke_item_num);
                buy_poke_item_num.setText(num + "");

                TextView buy_poke_item_price = (TextView) linear.findViewById(R.id.buy_poke_item_price);
                String priceString = buyMenuInfoList.get(i).getPrice();

                if (isNumberic(priceString)) {
                    buy_poke_item_price.setText("¥" + num * Integer.parseInt(priceString) + "");
                } else {
                    buy_poke_item_price.setText("无");
                }

                Button dict_add_button = (Button) linear.findViewById(R.id.dict_add_button);
                dict_add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buyMenuInfoMap.put(menuInfo, num + 1);
                        if (pokeListView.getAdapter() != null) {
                            BaseAdapter adapter = (BaseAdapter) pokeListView.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                        updateSumPrice();
                    }
                });

                Button dict_sub_button = (Button) linear.findViewById(R.id.dict_sub_button);
                dict_sub_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (num == 1) {
                            buyMenuInfoList.remove(menuInfo);
                            buyMenuInfoMap.remove(menuInfo);
                        } else {
                            buyMenuInfoMap.put(menuInfo, num - 1);
                        }
                        if (pokeListView.getAdapter() != null) {
                            BaseAdapter adapter = (BaseAdapter) pokeListView.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                        updateSumPrice();
                    }
                });
                return linear;
            }
        });

    }

    public final static boolean isNumberic(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (show_poke_card.getVisibility() == View.VISIBLE) {
                showPoke(false);
            } else {
                exit();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出本菜单", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void showPoke(boolean isShow) {
        if (isShow) {
            subMenuinfoListView.setEnabled(false);
            distInfoListView.setEnabled(false);
            show_poke_card.setVisibility(View.VISIBLE);
        } else {
            subMenuinfoListView.setEnabled(true);
            distInfoListView.setEnabled(true);
            show_poke_card.setVisibility(View.GONE);

        }
    }

    public void updateSumPrice() {
        int sumPrice = 0;
        StringBuilder canNotCalcPrice = new StringBuilder();
        for (int i = 0; i < buyMenuInfoList.size(); i++) {
            MenuInfo menuInfo = buyMenuInfoList.get(i);
            int num = buyMenuInfoMap.get(menuInfo);
            String priceString = menuInfo.getPrice();
            if (isNumberic(priceString)) {
                sumPrice += Integer.parseInt(priceString) * num;
            } else {
                canNotCalcPrice.append(menuInfo.getDish() + " ( " + priceString + " ) * " + num + "\n");
            }
        }
        String resultPrice = "可计算的总价为: ¥" + sumPrice + "\n";
        if (!canNotCalcPrice.toString().trim().isEmpty()) {
            resultPrice += "不可计算的总价为: \n" + canNotCalcPrice.toString();
        }
        sum_poke_price_text.setText(resultPrice);
        Log.d("Sum_price", sumPrice + " ");
        show_my_poke_linear.setText("查看口袋( " + buyMenuInfoList.size() + " )");
        if (distInfoListView.getAdapter() != null) {
            BaseAdapter adapter = (BaseAdapter) distInfoListView.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_dict_find, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.find_dict:
                if (mDictQueryLinearLayout.getVisibility() == View.GONE) {
                    mDictQueryLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mDictQueryLinearLayout.setVisibility(View.GONE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

