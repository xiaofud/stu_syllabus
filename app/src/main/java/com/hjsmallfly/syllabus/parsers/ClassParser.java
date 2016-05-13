package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.hjsmallfly.syllabus.activities.MainActivity;
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
import java.util.List;
import java.util.Set;


public class ClassParser {

    private ArrayList<Lesson> all_classes;

    public static final String EMPTY_CLASS_STRING = "";
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
    public static final int COLUMNS = 8;    // 包含了 一个 空单元 以及 星期一到星期日
    public Object[] syllabusGrid;  // 用于适配 课表的 view 的数据

    private Context context;

    private TokenGetter tokenGetter;

    public ClassParser(Context context, TokenGetter tokenGetter) {

        syllabusGrid = new Object[ROWS * (COLUMNS + 1)];
        all_classes = new ArrayList<>();
        this.context = context;
        this.tokenGetter = tokenGetter;
    }


    /**
     * 解析json数据
     *
     * @param json_data 从服务器返回的代表课程信息的 json 数据
     * @return 解析是否成功
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

            // 更新本地的用户id
            if (curriculum.has("user_id")) {
                int uid = curriculum.getInt("user_id");
                MainActivity.user_id = uid;
                MainActivity.save_uid(context, MainActivity.user_id);
                Log.d("syllabus", "用户id: " + uid);
            }

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


//    public static int change_into_number(char c) {
//        int num = -1;
//        switch (c) {
//            case '0':
//                num = 10;
//                break;
//            case 'A':
//            case 'B':
//            case 'C':
//                num = (c - 'A') + 11;
//                break;
//            default:
//                num = c - '0';
//                break;
//        }
//        return num;
//    }

    /**
     * 计算现在距离设定过的周数是第几周
     * @param target_calendar
     * @return
     */
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
        // 计算出星期一的日期
        calendar.add(Calendar.DAY_OF_MONTH, - (day_of_week - 1 ));
        long diff_day = ( target_calendar.getTime().getTime() - calendar.getTime().getTime() ) / (60 * 60 * 24 * 1000);
//                    Toast.makeText(SyllabusActivity.this, "日期相差了" + diff, Toast.LENGTH_SHORT).show();
        //            Log.d("this_week",  "初始周数是 " + MainActivity.initial_week +  " 现在的周数是" + this_week);
        Log.d("week", "记录中的日期是: "
                        + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1 )
                + "-" + calendar.get(Calendar.DAY_OF_MONTH)
        );
        Log.d("week", "现在的日期是: "
                        + target_calendar.get(Calendar.YEAR) + "-" + (target_calendar.get(Calendar.MONTH) + 1 )
                        + "-" + target_calendar.get(Calendar.DAY_OF_MONTH)
        );
        Log.d("week", "计算得出的差是: " + diff_day);
        return (int) diff_day / 7 + MainActivity.initial_week;
    }


    /**
     * 计算lesson 应该在哪个位置上
     * @param lesson    需要计算的课程
     * @param column_count  表格的列数
     * @param this_week 当前的周数
     * @return position 这节课需要添加到的所有位置, 返回的 List size 为0的话表明不需要添加
     */
    public static List<Integer> calcPosition(Lesson lesson, int column_count, int this_week) {

        List<Integer> positions = new ArrayList<>();

        // 遍历key set, 所以应该上相同课程的格子，实际上添加的是同一个 Lesson 对象
        for (String day_key : lesson.days.keySet()) {
            // key 的值是  w1 w2 这种格式
            String class_time = lesson.days.get(day_key);
//                Log.d(MainActivity.TAG, "class_time " + class_time);
            if (!class_time.equals(EMPTY_CLASS_STRING)) {
                // w0指的是周日
                // day_of_week 是这节课所在的星期几
                int day_of_week = Integer.parseInt(day_key.substring(1));   // 得到 w1 中的数字部分

//                    boolean hasBeenAdded = false;
                // 遍历时间字符串
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
                            break;
                        default:
                            row = c - '0';
                            break;
                    }
                    if (row == -1)   // 说明是单双周的情况
                        continue;

                    // 计算出在表格中的位置, 压缩为一维数组
                    int index = row * column_count + day_of_week;

                    boolean week_match = true;

                    // 在单周不显示双周的课程, 在双周不显示单周的课程

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
                        if (w == day_of_week) {
//                            lesson_str = "[" + has_single_or_double + "]" + lesson_str;
//                                int week = MainActivity.initial_week;
                            if (this_week % 2 == 0 && has_single_or_double.equals("单")) {
//                                    do_not_show = true;
                                week_match = false;
                            } else if (this_week % 2 == 1 && has_single_or_double.equals("双")) {
//                                    do_not_show = true;
                                week_match = false;
                            }
                        }
                    }

                    // -------------判断是否有单双周的情况-------------

                    if (!week_match)
                        continue;

                    positions.add(index);
                }
            }
        }
        return positions;
    }



    /**
     * 这堂课是否已经上完了
     * @param lesson 课程
     * @return true | false
     */
    public static boolean class_already_finished(Lesson lesson, int this_week){
        int[] range = lesson.get_duration();

        if (this_week < range[0] || this_week > range[1])
            return true;
        else
            return false;
    }

    /**
     * 用解析得到的课程填充 syllabusGrid
     * 计算每节课在格子中的位置
     */
    public void calcClassPosition() {
        // 填充课表数据
        // 计算出现在是第几周
        int this_week = calculate_week(Calendar.getInstance());

        for (int i = 0; i < all_classes.size(); ++i) {
            // 遍历每一堂课
            Lesson lesson = all_classes.get(i);

            // ------------这里可以踢掉已经上完了的课程-------------

            // -------------判断是否课程已经上完了-------------

            // 如果这节课已经完结
            if (class_already_finished(lesson, this_week))
                continue;

            // -------------判断是否课程已经上完了-------------

            // ------------这里可以踢掉已经上完了的课程-------------


            List<Integer> positions = calcPosition(lesson, COLUMNS, this_week);
            for(int loop = 0 ; loop < positions.size() ; ++loop)
                syllabusGrid[positions.get(loop)] = lesson;

        }
    }

}
