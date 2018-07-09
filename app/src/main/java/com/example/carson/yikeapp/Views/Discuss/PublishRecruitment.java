package com.example.carson.yikeapp.Views.Discuss;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.FormBody;

public class PublishRecruitment extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back_pub_recruit)
    ImageView ivBack;
    @BindView(R.id.pub_recruitment)
    TextView publish;
    @BindView(R.id.et_recruit_name)
    EditText etName;
    @BindView(R.id.et_recruit_location)
    EditText etLocation;
    @BindView(R.id.et_recruit_number)
    EditText etNumber;
    @BindView(R.id.tv_recruit_date)
    TextView tvDate;
    @BindView(R.id.ll_recruit_date)
    LinearLayout llDate;
    @BindView(R.id.et_recruit_last)
    EditText etLast;
    @BindView(R.id.et_recruit_work)
    EditText etWork;
    @BindView(R.id.et_recruit_requirement)
    EditText etRequirement;
    @BindView(R.id.et_recruit_introduction)
    EditText etIntroduction;
    @BindView(R.id.et_recruit_other)
    EditText etOther;

//    private EditText[] edits = new EditText[] {etName, etLocation, etNumber, etLast, etWork, etRequirement, etIntroduction, etOther};
    private String token;
    private boolean isComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_recruitment);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        token = ConstantValues.getCachedToken(this);
        setPublishStatus(false);
//        for (EditText editText : edits) {
//            checkEditableStatus(editText);
//        }
        checkEditableStatus(etName);
        checkEditableStatus(etLocation);
        checkEditableStatus(etNumber);
        checkEditableStatus(etLast);
        checkEditableStatus(etWork);
        checkEditableStatus(etRequirement);
        checkEditableStatus(etIntroduction);
        checkEditableStatus(etOther);
    }

    private void checkEditableStatus(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    isComplete = false;
                } else {
                    isComplete = true;
                }
            }
        });
        if (isComplete) {
          //如果当前判断为真 证明此项不为空 但不保证其他项
            if (etName.getText().toString().isEmpty() || etLocation.getText().toString().isEmpty() ||
                    etNumber.getText().toString().isEmpty() || etLast.getText().toString().isEmpty() ||
                    etWork.getText().toString().isEmpty() || etRequirement.getText().toString().isEmpty()||
                    etIntroduction.getText().toString().isEmpty() || etOther.getText().toString().isEmpty() ||
                    tvDate.getText().toString().isEmpty()) {
                isComplete = false;
                return;
            } else {
                setPublishStatus(isComplete);
            }
        } else {
            setPublishStatus(isComplete);
        }
    }

    private void setPublishStatus(boolean isComplete) {
        if (isComplete) {
            publish.setEnabled(true);
            publish.setClickable(true);
            publish.setTextColor(getResources().getColor(R.color.bg_bnb_loca));
            publish.setOnClickListener(this);
            return;
        }
        publish.setTextColor(Color.DKGRAY);
        publish.setClickable(false);
        publish.setEnabled(false);
    }

    @OnClick({R.id.iv_back_pub_recruit, R.id.ll_recruit_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_pub_recruit:
                onBackPressed();
                break;
            case R.id.ll_recruit_date:
                showDatePicker(tvDate);
                break;
        }
    }

    private void showDatePicker(final TextView textView) {
        final DatePicker picker = new DatePicker(PublishRecruitment.this);
        //获取当前日期
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        //活动最多可以持续两年
        picker.setRangeEnd(year + 2, month, day);
        //从当前日期开始
        picker.setRangeStart(year, month, day);
        picker.setSelectedItem(year, month, day);
        int color = getResources().getColor(R.color.bg_bnb_loca);
        picker.setLabelTextColor(color);
        picker.setTextColor(color);
        picker.setDividerVisible(false);
        picker.setCancelTextColor(Color.GRAY);
        picker.setTopLineColor(Color.WHITE);
        picker.setSubmitTextColor(color);
        picker.setResetWhileWheel(false);

        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                textView.setText(year + "/" + month + "/" + day);
            }
        });

        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "/" + picker.getSelectedMonth() + "/" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "/" + month + "/" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "/" + picker.getSelectedMonth() + "/" +day);
            }
        });

        picker.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_recruitment:
                String name = etName.getText().toString();
                String location = etLocation.getText().toString();
                String number = etNumber.getText().toString();
                String date = tvDate.getText().toString();
                String last = etLast.getText().toString();
                String work = etWork.getText().toString();
                String requirement = etRequirement.getText().toString();
                String introduction = etIntroduction.getText().toString();
                String other = etOther.getText().toString();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(ConstantValues.KEY_TOKEN, token);
                builder.add("name", name);
                builder.add("location", location);
                builder.add("time", date);
                builder.add("last", last);
                builder.add("number", number);
                builder.add("introduction", introduction);
                builder.add("requirement", requirement);
                builder.add("work", work);
                builder.add("other", other);
                //欠缺照片
                break;
        }
    }
}
