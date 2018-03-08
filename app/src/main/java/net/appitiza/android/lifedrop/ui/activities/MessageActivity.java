package net.appitiza.android.lifedrop.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.utils.DateTimeUtils;

public class MessageActivity extends AppCompatActivity {

    ImageView mIvProfile;
    CustomTextview mTvName;
    CustomTextview mTvEmail;
    CustomTextview mTvNumber;
    CustomTextview mTvTitle;
    CustomTextview mTvBlood;
    CustomTextview mTvAddress;
    CustomTextview mTvMessage;
    CustomTextview mTvdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initialize();
    }

    private void initialize() {
        mIvProfile = findViewById(R.id.mIvProfile);
        mTvTitle = findViewById(R.id.tv_title);
        mTvName = findViewById(R.id.tv_name);
        mTvEmail = findViewById(R.id.tv_email);
        mTvNumber = findViewById(R.id.tv_number);
        mTvBlood = findViewById(R.id.tv_blood);
        mTvAddress = findViewById(R.id.tv_address);
        mTvMessage = findViewById(R.id.tv_message);
        mTvdate = findViewById(R.id.tv_date);


        if (getIntent() != null) {
            if (getIntent().getStringExtra("first_name") != null && getIntent().getStringExtra("last_name") != null) {
                mTvName.setText(getIntent().getStringExtra("first_name") + " " + getIntent().getStringExtra("last_name"));
            }
            if (getIntent().getStringExtra("email_id") != null) {
                mTvEmail.setText(getIntent().getStringExtra("email_id"));
            }
            if (getIntent().getStringExtra("number") != null) {
                mTvNumber.setText(getIntent().getStringExtra("number"));
            }
            if (getIntent().getStringExtra("address") != null) {
                mTvAddress.setText(getIntent().getStringExtra("address"));
            }
            if (getIntent().getStringExtra("message") != null) {
                mTvMessage.setText(getIntent().getStringExtra("message"));
            }
            if (getIntent().getStringExtra("blood") != null) {
                mTvTitle.setText(getIntent().getStringExtra("blood"));
            }
            if (getIntent().getStringExtra("title") != null) {
                mTvBlood.setText(getIntent().getStringExtra("title"));
            }
            if (getIntent().getStringExtra("required_date") != null && !getIntent().getStringExtra("required_date").equalsIgnoreCase("")) {
                mTvdate.setText(DateTimeUtils.dateDifferenceCalculation(getIntent().getStringExtra("required_date"), MessageActivity.this) + "(" + DateTimeUtils.formatDate(getIntent().getStringExtra("required_date")) + ")");

            } else {
                mTvdate.setText("Unknown");
            }
        }
    }
}
