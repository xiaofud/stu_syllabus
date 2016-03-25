package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.StudentInfo;

import java.util.List;

public class ShowStudentInfoListActivity extends AppCompatActivity {

    List<StudentInfo> studentInfoList;
    ListView studentInfoListView;

    FloatingActionButton showStudentQuery;
    LinearLayout studentQueryLinearLayout;

    TextView students_title;


    EditText studentQueryEditText;
    Button studentQueryButton;
    Button findForwardButton;
    Button findNextButton;

    int lastqueryPositon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_info_list);

        getSupportActionBar().hide();

        studentInfoList = getStudentInfoList();
        initView();
    }


    public List<StudentInfo> getStudentInfoList() {
        Intent intent = getIntent();

        List<StudentInfo> studentInfoList = (List<StudentInfo>) intent.getSerializableExtra("studentInfoList");

        return studentInfoList;
    }

    public void initView() {

        lastqueryPositon = -1;

        studentQueryEditText = (EditText) findViewById(R.id.studentQueryEditText);
        studentQueryButton = (Button) findViewById(R.id.studentQueryButton);
        findForwardButton = (Button) findViewById(R.id.findForwardButton);
        findNextButton = (Button) findViewById(R.id.findNextButton);

        students_title = (TextView) findViewById(R.id.students_title);
        students_title.setBackgroundColor(SyllabusActivity.clicked_lesson.colorID);
        students_title.setText("共同参加(" + studentInfoList.size() + ")");

        studentInfoListView = (ListView) findViewById(R.id.studentInfoListView);
        showStudentQuery = (FloatingActionButton) findViewById(R.id.showStudentQuery);
        studentQueryLinearLayout = (LinearLayout) findViewById(R.id.studentQueryLinearLayout);

        showStudentQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentQueryLinearLayout.getVisibility() == View.GONE) {
                    studentQueryLinearLayout.setVisibility(View.VISIBLE);
                    lastqueryPositon = -1;
                    studentQueryEditText.setText("");
                } else {
                    studentQueryLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        studentInfoListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return studentInfoList.size();
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
                View studentItem = View.inflate(ShowStudentInfoListActivity.this, R.layout.student_item, null);
                TextView studentNameTextView = (TextView) studentItem.findViewById(R.id.studentNameTextView);
                TextView studentNumberTextView = (TextView) studentItem.findViewById(R.id.studentNumberTextView);
                TextView studentMajorTextView = (TextView) studentItem.findViewById(R.id.studentMajorTextView);
                ImageView genderImageView = (ImageView) studentItem.findViewById(R.id.genderImageView);

                StudentInfo studentInfo = studentInfoList.get(position);

                studentNameTextView.setText(studentInfo.getName());
                studentNumberTextView.setText(studentInfo.getNumber());
                studentMajorTextView.setText(studentInfo.getMajor());

                genderImageView.setImageResource(studentInfo.getGender().equals("男") ? R.drawable.male : R.drawable.female);

                return studentItem;

            }
        });

        studentQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryStudent = studentQueryEditText.getText().toString();
                if (queryStudent.isEmpty()) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                for (int i = 0; i < studentInfoList.size(); i++) {
                    StudentInfo studentInfo = studentInfoList.get(i);
                    if (studentInfo.getName().indexOf(queryStudent) != -1
                            || studentInfo.getNumber().indexOf(queryStudent) != -1
                            || studentInfo.getMajor().indexOf(queryStudent) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "查找不到选项", Toast.LENGTH_SHORT).show();
                } else {
                    studentInfoListView.setSelection(0);
                    studentInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }
            }
        });

        findNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryStudent = studentQueryEditText.getText().toString();
                if (queryStudent.isEmpty()) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                for (int i = lastqueryPositon + 1; i < studentInfoList.size(); i++) {
                    StudentInfo studentInfo = studentInfoList.get(i);
                    if (studentInfo.getName().indexOf(queryStudent) != -1
                            || studentInfo.getNumber().indexOf(queryStudent) != -1
                            || studentInfo.getMajor().indexOf(queryStudent) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "已经是最后一个选项", Toast.LENGTH_SHORT).show();
                } else {
                    studentInfoListView.setSelection(0);
                    studentInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }
            }
        });

        findForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryStudent = studentQueryEditText.getText().toString();
                if (queryStudent.isEmpty()) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int queryPositon = -1;

                if (lastqueryPositon == -1) lastqueryPositon = studentInfoList.size();

                for (int i = lastqueryPositon - 1; i > 0; --i) {
                    StudentInfo studentInfo = studentInfoList.get(i);
                    if (studentInfo.getName().indexOf(queryStudent) != -1
                            || studentInfo.getNumber().indexOf(queryStudent) != -1
                            || studentInfo.getMajor().indexOf(queryStudent) != -1) {
                        queryPositon = i;
                        break;
                    }
                }
                if (queryPositon == -1) {
                    Toast.makeText(ShowStudentInfoListActivity.this, "已经是第一个选项", Toast.LENGTH_SHORT).show();
                } else {
                    studentInfoListView.setSelection(0);
                    studentInfoListView.setSelection(queryPositon);
                    lastqueryPositon = queryPositon;
                }

            }
        });

    }

}
