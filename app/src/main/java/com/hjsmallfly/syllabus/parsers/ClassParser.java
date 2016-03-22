package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.adapters.SyllabusAdapter;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.interfaces.TokenGetter;
import com.hjsmallfly.syllabus.syllabus.Lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;


public class ClassParser {

    private ArrayList<Lesson> all_classes;
    // 当前的周数
//    private int week;

    public static final String EMPTY_CLASS_STRING = "";
    //    public static final String[] LABELS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public static final String[] LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    public static final HashMap<String, String> time_table;
    public static final Set<String> class_table;

    final int[] bgColor = {
            Color.argb(178,28,196,179),
            Color.argb(178,80,193,250),
            Color.argb(178,44,177,245),
            Color.argb(178,2,197,151),
            Color.argb(178,254,141,65),
            Color.argb(178,247,125,138),
            Color.argb(178,84,134,234),
            Color.argb(178,200,50,101),
            Color.argb(178,114,204,59),
            Color.argb(178,102,124,177),
            Color.argb(178,43,144,205),
            Color.argb(178,122,166,201),
            Color.argb(178,78,217,27),
            Color.argb(178,226,56,145),
            Color.argb(178,109,55,123),
            Color.argb(178,227,119,195),
    };

    // 静态的初始化过程
    static {
        time_table = new HashMap<>();
        time_table.put("1", "8:00,8:50");
        time_table.put("2", "9:00,9:50");
        time_table.put("3", "10:10,11:00");
        time_table.put("4", "11:10,12:00");
        time_table.put("5", "13:00,13:50");
        time_table.put("6", "14:00,14:50");
        time_table.put("7", "15:00,15:50");
        time_table.put("8", "16:10,17:00");
        time_table.put("9", "17:10,18:00");
        time_table.put("0", "18:10,19:00");
        time_table.put("A", "19:10,20:00");
        time_table.put("B", "20:10,21:00");
        time_table.put("C", "21:10,22:00");

        class_table = time_table.keySet();
    }

    public static final String ERROR = "ERROR";

    public static final int ROWS = 14;
    public static final int COLUMNS = 8;    // 包含了 一个 空单元 以及 星期一到星期五
    public Object[] weekdays_syllabus_data;  // 用于适配 课表的 view 的数据

    private Context context;

    private TokenGetter tokenGetter;

    public ClassParser(Context context, TokenGetter tokenGetter) {
//        this.week = this_week;
        weekdays_syllabus_data = new Object[ROWS * (COLUMNS + 1)];
        all_classes = new ArrayList<>();
//        weekend_classes = new ArrayList<>();
        this.context = context;
        this.tokenGetter = tokenGetter;

        //init();     // 生成初始化的数据，在特定位置上填上日期信息之类的

    }


    /**
     * 解析json数据
     *
     * @param json_data 从服务器返回的代表课程信息的 json 数据
     * @return
     */

    public boolean parseJSON(String json_data, boolean update_local_token) {
        // 用response作为json传给 JSONTOkener
        // 检查是否外网流量已经用完
        JSONTokener jsonParser = new JSONTokener(json_data);
        all_classes.clear();
        try {
            JSONObject curriculum = (JSONObject) jsonParser.nextValue();
            // 判断有没有错误先
            if (curriculum.has(ERROR)) {
                String error = curriculum.getString(ERROR);
//                Toast.makeText(context, "错误代码: " + error, Toast.LENGTH_SHORT).show();
                String err_resp = StringDataHelper.get_error_response(error);
                if (err_resp != null)
                    Toast.makeText(context, err_resp, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "错误: " + error, Toast.LENGTH_SHORT).show();
                return false;
            }
            // 得到所有课程的数组
            JSONArray classes = curriculum.getJSONArray("classes");
            // 因为本地缓存的课表文件里面含有的token文件可能是过期的.
            if (update_local_token) {
                String token = curriculum.getString("token");
                tokenGetter.get_token(token);
            }
//            Toast.makeText(context, "the token is " + token, Toast.LENGTH_SHORT).show();
//            Log.d(MainActivity.TAG, classes.length() + " classes");
            // 得到颜色的种类数
            // 颜色 指针
            int color_index = -1;
            int colorIndex = 0;
            for (int i = 0; i < classes.length(); ++i) {
                // 得到每一节课
                JSONObject lesson = (JSONObject) classes.get(i);
                // 处理每一节课的信息
                String name = lesson.getString("name");
//                Log.d(MainActivity.TAG, name);
                String id = lesson.getString("id");
//                Log.d(MainActivity.TAG, id);
                String teacher = lesson.getString("teacher");
//                Log.d(MainActivity.TAG, teacher);
                String room = lesson.getString("room");
//                Log.d(MainActivity.TAG, room);
                String duration = lesson.getString("duration");
//                Log.d(MainActivity.TAG, duration);

                String credit = lesson.getString("credit");

                Lesson cls = new Lesson();
                cls.name = name;
                cls.id = id;
                cls.teacher = teacher;
                cls.room = room;
                cls.duration = duration;
                cls.credit = credit;
                cls.colorID = bgColor[colorIndex++ % bgColor.length];

//                Log.d(MainActivity.TAG, duration);

                // 额外信息
                cls.semester = MainActivity.cur_semester;
                String[] year_strs = MainActivity.cur_year_string.split("-");
                cls.start_year = Integer.parseInt(year_strs[0]);
                cls.end_year = Integer.parseInt(year_strs[1]);

                // 得到一周之内要上课的日期以及具体上课时间
                JSONObject days = lesson.getJSONObject("days");
                final int weekdays = 7;
                HashMap<String, String> lesson_days = new HashMap<>();
                for (int j = 0; j < weekdays; ++j) {
                    String key = "w" + j;
                    String isNull = days.getString(key);
                    if (!isNull.equals("None"))     // 去除没有的天数
                        lesson_days.put(key, days.getString(key));
//                    Log.d(MainActivity.TAG, key + ":" + days.getString(key));
                }
                cls.days = lesson_days;

                all_classes.add(cls);
            }
            // 记录最新的json_data到MainActivity那里
            MainActivity.syllabus_json_data = json_data;
            return true;
        } catch (JSONException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
    }


    public static int change_into_number(char c) {
        int num;
        switch (c) {
            case '0':
                num = 10;
                break;
            case 'A':
            case 'B':
            case 'C':
                num = (c - 'A') + 11;
                break;
            default:
                num = c - '0';
                break;
        }
        return num;
    }

    public static int calculate_week(Calendar target_calendar){
        Calendar calendar = Calendar.getInstance();
        // 年月日
        String[] date = MainActivity.initial_date.split("/");
        Log.d("week", Arrays.toString(date));
        int [] fields = new int[3];
        for (int loop = 0 ; loop < fields.length ; ++loop)
            fields[loop] = Integer.parseInt(date[loop]);
        // 月份是从0开始切记
        // [2016, 2, 6]
        calendar.set(fields[0], fields[1], fields[2]);
        // day of week 是按照 周日为第一天(返回的结果是0)，所以周三就是第四天，但是是星期三
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//        Log.d("week", "" + day_of_week);
        if (day_of_week == 0){
            // 说明是周日
            day_of_week = 7;
        }
//                    Toast.makeText(SyllabusActivity.this, "今天是" + day_of_week, Toast.LENGTH_SHORT).show();
        // 计算出星期一的日期
        calendar.add(Calendar.DAY_OF_MONTH, - (day_of_week - 1 ));
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    Toast.makeText(SyllabusActivity.this, "这周星期一是" + month + "/" + day , Toast.LENGTH_SHORT).show();
//        Calendar today = Calendar.getInstance();
        long diff_day = ( target_calendar.getTime().getTime() - calendar.getTime().getTime() ) / (60 * 60 * 24 * 1000);
//                    Toast.makeText(SyllabusActivity.this, "日期相差了" + diff, Toast.LENGTH_SHORT).show();
        //            Log.d("this_week",  "初始周数是 " + MainActivity.initial_week +  " 现在的周数是" + this_week);
        Log.d("week", "记录中的日期是: "
                        + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1 )
                + "-" + calendar.get(Calendar.DAY_OF_MONTH)
        );
        Log.d("week", "现在的日期是: "
                        + target_calendar.get(target_calendar.YEAR) + "-" + (target_calendar.get(target_calendar.MONTH) + 1 )
                        + "-" + target_calendar.get(target_calendar.DAY_OF_MONTH)
        );
        Log.d("week", "计算得出的差是: " + diff_day);
        return (int) diff_day / 7 + MainActivity.initial_week;
    }


    /**
     * 用解析得到的课程填充 weekdays_syllabus_data
     */
    public void inflateTable() {
//        Log.d(MainActivity.TAG, "before inflate class_table");
        // 填充课表数据
        int this_week = calculate_week(Calendar.getInstance());
        for (int i = 0; i < all_classes.size(); ++i) {
            // 遍历每一堂课
            Lesson lesson = all_classes.get(i);

            // ------------这里可以踢掉已经上完了的课程-------------
            // -------------判断是否课程已经上完了-------------


//            Log.d("this_week",  "初始周数是 " + MainActivity.initial_week +  " 现在的周数是" + this_week);
//                    Toast.makeText(SyllabusActivity.this, "这周是" + this_week, Toast.LENGTH_SHORT).show();
            int[] range = lesson.get_duration();
//                    Toast.makeText(SyllabusActivity.this, Arrays.toString(range), Toast.LENGTH_SHORT).show();
            if (this_week < range[0] || this_week > range[1]) {
                continue;
//                do_not_show = true;
//                Log.d("pick", lesson.name);
//                need_to_test_single_or_double = false;
            }

            // -------------判断是否课程已经上完了-------------

            // ------------这里可以踢掉已经上完了的课程-------------

            // 遍历key set, 所以应该上相同课程的格子，实际上添加的是同一个 Lesson 对象
            for (String key : lesson.days.keySet()) {
                // key 的值是  w1 w2 这种格式
                String class_time = lesson.days.get(key);
//                Log.d(MainActivity.TAG, "class_time " + class_time);
                if (!class_time.equals(EMPTY_CLASS_STRING)) {
                    // 添加到obj数组中
                    int offset = Integer.parseInt(key.substring(1));   // 得到 w1 中的数字部分
                    // 因为json的w0 指 周日
                    //       if (offset == 0)
                    //          offset = 7;

//                    if (offset == 0 || offset == 6) {     // 忽略周六周日的课
////                        offset = 7;     // 因为web api返回的数据 w0 是代表周日
////                        weekend_classes.add(all_classes.get(i));    // 添加周末的课程到此
////                        continue;11111
//                    }
//                    ++offset;
//                    Log.v("offset", offset + " ");
                    boolean hasBeenAdded = false;
                    for (int count = 0; count < class_time.length(); ++count) {

                        char c = class_time.charAt(count);  // 得到数据
                        int row = -1;
                        switch (c) {
                            case '0':
                                row = 10;
                                break;
                            case 'A':
                                row = 11;
                                break;
                            case 'B':
                                row = 12;
                                break;
                            case 'C':
                                row = 13;
                                break;
                            case '单':   // 跳过这个字符
//                                if (this_week % 2 == 0)
//                                    continue;
                            case '双':
//                                lesson.comment = c + "";
//                                if (this_week % 2 == 1)
//                                    continue;
                                hasBeenAdded = false;
                                break;
                            default:
                                row = c - '0';
                                break;
                        }
                        if (row == -1)   // 说明是单双周的情况
                            continue;

                        int index = row * COLUMNS + offset;

                        boolean really_to_add = true;

                        // 这里需要判断单双周
                        // -------------判断是否有单双周的情况-------------
                        // 额外判断周数
                        // 单双周显示
                        int w = -1;
                        String has_single_or_double = null;
                        for (String day_obj : lesson.days.keySet()) {
                            String time_str = lesson.days.get(day_obj);
                            if (time_str.contains("单")) {
                                has_single_or_double = "单";
                                w = Integer.parseInt(day_obj.substring(1));
                            } else if (time_str.contains("双")) {
                                has_single_or_double = "双";
                                w = Integer.parseInt(day_obj.substring(1));
                            }
                        }

                        if (has_single_or_double != null) {
                            if (w == offset) {
//                            lesson_str = "[" + has_single_or_double + "]" + lesson_str;
//                                int week = MainActivity.initial_week;
                                if (this_week % 2 == 0 && has_single_or_double.equals("单")){
//                                    do_not_show = true;
                                    really_to_add = false;
                                }else if (this_week % 2 == 1 && has_single_or_double.equals("双")){
//                                    do_not_show = true;
                                    really_to_add = false;
                                }
                            }
                        }

                        // -------------判断是否有单双周的情况-------------

                        if (!really_to_add)
                            continue;

                        Log.v("index", index + " ");
                        if (!hasBeenAdded) {     // 一节课添加一次即可
                            weekdays_syllabus_data[index] = lesson;   // 将这节课添加到合适的位置
                            hasBeenAdded = true;
                        } else {
                            // 找前一个位置
                            char pre_char = count == 0 ? 0 : class_time.charAt(count - 1);
                            if (pre_char > 0) {
                                // 这里也要注意 90 这种情况 要把 0 转化为 10 , A 11 B 12 C 13
                                int pre_row = change_into_number(pre_char);
                                int diff = row - pre_row;
                                if (diff != 1) {  // 不相邻，如整合思维 一天三节课但是 大班课 不相邻
//                                    Toast.makeText(context, "the difference is" + diff, Toast.LENGTH_SHORT).show();
                                    weekdays_syllabus_data[index] = lesson;
                                    continue;
                                }
                                // 说明这节课和上面的课是连着的而且是同一节课
//                                weekdays_syllabus_data[index] = "同上";
                                // 取消同上
                                weekdays_syllabus_data[index] = lesson;
                            }

                        }

                    }
                }
            }
        }
    }

}
