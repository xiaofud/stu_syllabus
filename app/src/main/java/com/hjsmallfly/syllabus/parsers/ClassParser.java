package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;


public class ClassParser {

    private ArrayList<Lesson> all_classes;
    public ArrayList<Lesson> weekend_classes;  // 存放周末的课程

    public static final String EMPTY_CLASS_STRING = "";
//    public static final String[] LABELS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
public static final String[] LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    public static final HashMap<String, String> time_table;
    public static final Set<String> class_table;
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
    public static final int COLUMNS = 6;    // 包含了 一个 空单元 以及 星期一到星期五
    public Object[] weekdays_syllabus_data;  // 用于适配 课表的 view 的数据

    private Context context;

    private TokenGetter tokenGetter;

    public ClassParser(Context context, TokenGetter tokenGetter){
        weekdays_syllabus_data = new Object[ROWS * COLUMNS];
        all_classes = new ArrayList<>();
        weekend_classes = new ArrayList<>();
        this.context = context;
        this.tokenGetter = tokenGetter;

        init();     // 生成初始化的数据，在特定位置上填上日期信息之类的

    }


    /**
     * 解析json数据
     * @param json_data 从服务器返回的代表课程信息的 json 数据
     * @return
     */
    public boolean parseJSON(String json_data, boolean update_local_token){
        // 用response作为json传给 JSONTOkener
        // 检查是否外网流量已经用完
        JSONTokener jsonParser = new JSONTokener(json_data);
        all_classes.clear();
        try {
            JSONObject curriculum = (JSONObject) jsonParser.nextValue();
            // 判断有没有错误先
            if (curriculum.has(ERROR)){
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
            int color_counts = SyllabusAdapter.class_cell_drawable.length;
            // 颜色 指针
            int color_index = -1;
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

                // 会重复使用之前的色彩
                color_index = (color_index + 1) % color_counts;
                cls.color_code = SyllabusAdapter.class_cell_drawable[color_index];

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
            return true;
        }catch (JSONException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
    }

    private void init(){
//        Log.d(MainActivity.TAG, "start init()");

        for(int i = 0 ; i < weekdays_syllabus_data.length ; ++i)
            weekdays_syllabus_data[i] = EMPTY_CLASS_STRING;   // 初始化数据

        // 处理非课程的数据
        for (int i = 0 ; i < weekdays_syllabus_data.length ; ++i){
            // 处理星期几这些日期
            if (i <= 5){    // 一个空白格子，外加 周一到周五
                if (i == 0)
                    weekdays_syllabus_data[i] = "";   // 空白的一个格子
                else {
                    weekdays_syllabus_data[i] = LABELS[i - 1];    // 转化为英文表示的星期数
                }

            }else if (i % COLUMNS == 0){
                // 处理第一列的 课的节数
                // 表明目前第i个元素位于 i / COLUMNS 行的第一个位置
                if (i / COLUMNS <= 9) {   // 这里还是用数字表示
                    int num = i / COLUMNS;
                    weekdays_syllabus_data[i] = num + "";  // i.e. 123..ABC
                }else{
                    // 用ABC代替
                    String label = "";
                    switch (i / COLUMNS){
                        case 10:
                            label = "0";
                            break;
                        case 11:
                            label = "A";
                            break;
                        case 12:
                            label = "B";
                            break;
                        case 13:
                            label = "C";
                            break;
                        default:
                            break;
                    }
                    weekdays_syllabus_data[i] = label;
                }
            }//else{
//                weekdays_syllabus_data[i] = EMPTY_CLASS_STRING; // 置为空
//            }
        }
//        Log.d(MainActivity.TAG, "end init()");
        // 为星期几添加日期
        Calendar day_helper = Calendar.getInstance();
        // 第一天是周日 1 第七天是 周日 7
        int today_in_week = day_helper.get(Calendar.DAY_OF_WEEK);
        // 先算出周一(2)的日期
        day_helper.add(Calendar.DAY_OF_WEEK, - (today_in_week - 2 ));
        // 只显示周一到周五
        for(int i = 1 ; i < 6 ; ++i){
            int month =  day_helper.get(Calendar.MONTH) + 1;
            int day = day_helper.get(Calendar.DAY_OF_MONTH);
            weekdays_syllabus_data[i] = month + "-" + day + "\n" + weekdays_syllabus_data[i].toString();
            day_helper.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public static int change_into_number(char c){
        int num;
        switch (c){
            case '0':
                num = 10;
                break;
            case 'A':
            case 'B':
            case 'C':
                num = ( c - 'A' ) + 11;
                break;
            default:
                num = c - '0';
                break;
        }
        return num;
    }

    /**
     * 用解析得到的课程填充 weekdays_syllabus_data
     */
    public void inflateTable(){
//        Log.d(MainActivity.TAG, "before inflate class_table");
        weekend_classes.clear();
        // 填充课表数据
        for(int i = 0 ; i < all_classes.size() ; ++i){
            // 遍历每一堂课
            Lesson lesson = all_classes.get(i);
            // 遍历key set, 所以应该上相同课程的格子，实际上添加的是同一个 Lesson 对象
            for (String key : lesson.days.keySet()){
                // key 的值是  w1 w2 这种格式
                String class_time = lesson.days.get(key);
//                Log.d(MainActivity.TAG, "class_time " + class_time);
                if (!class_time.equals(EMPTY_CLASS_STRING)){
                    // 添加到obj数组中
                    int offset = Integer.parseInt( key.substring(1));   // 得到 w1 中的数字部分
                    if (offset == 0 || offset == 6) {     // 忽略周六周日的课
//                        offset = 7;     // 因为web api返回的数据 w0 是代表周日
                        weekend_classes.add(all_classes.get(i));    // 添加周末的课程到此
                        continue;
                    }
                    boolean hasBeenAdded = false;
                    for(int count = 0 ; count < class_time.length() ; ++count){

                        char c = class_time.charAt(count);  // 得到数据
                        int row = -1;
                        switch (c){
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
                            case '双':
//                                lesson.comment = c + "";
                                hasBeenAdded = false;
                                break;
                            default:
                                row = c - '0';
                                break;
                        }
                        if (row == -1)   // 说明是单双周的情况
                            continue;

                        int index = row * COLUMNS + offset;

                        if (!hasBeenAdded) {     // 一节课添加一次即可
                            weekdays_syllabus_data[index] = lesson;   // 将这节课添加到合适的位置
                            hasBeenAdded = true;
                        }else{
                            // 找前一个位置
                            char pre_char = count == 0 ? 0 : class_time.charAt(count - 1);
                            if (pre_char > 0){
                                // 这里也要注意 90 这种情况 要把 0 转化为 10 , A 11 B 12 C 13
                                int pre_row = change_into_number(pre_char);
                                int diff = row - pre_row;
                                if (diff != 1) {  // 不相邻，如整合思维 一天三节课但是 大班课 不相邻
//                                    Toast.makeText(context, "the difference is" + diff, Toast.LENGTH_SHORT).show();
                                    weekdays_syllabus_data[index] = lesson;
                                    continue;
                                }
                                // 说明这节课和上面的课是连着的而且是同一节课
                                weekdays_syllabus_data[index] = "同上";
//                                weekdays_syllabus_data[index] = lesson;
                            }

                        }

                    }
                }
            }
        }
    }

}
