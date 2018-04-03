package net.appitiza.android.lifedrop.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.utils.DateTimeUtils;

public class MessageDetailsActivity extends AppCompatActivity {

    ImageView mIvProfile;
    CustomTextview mTvName;
    CustomTextview mTvNumber;
    CustomTextview mTvBlood;
    CustomTextview mTvAddress;
    CustomTextview mTvMessage;
    CustomTextview mTvdate;
    ImageView mIvShare;
    ImageView mIvWhatsapp;
    ImageView mIvFb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        initialize();
    }

    private void initialize() {
        mIvProfile = findViewById(R.id.mIvProfile);
        mTvName = findViewById(R.id.tv_name);
        mTvNumber = findViewById(R.id.tv_number);
        mTvBlood = findViewById(R.id.tv_blood);
        mTvAddress = findViewById(R.id.tv_address);
        mTvMessage = findViewById(R.id.tv_message);
        mTvdate = findViewById(R.id.tv_date);
        mIvShare = findViewById(R.id.iv_share);
        mIvWhatsapp = findViewById(R.id.iv_whatsapp);
        mIvFb = findViewById(R.id.iv_fb);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("first_name") != null) {
                mTvName.setText(getIntent().getStringExtra("first_name"));
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

            if (getIntent().getStringExtra("title") != null) {
                mTvBlood.setText(getIntent().getStringExtra("blood"));
            }
            if (getIntent().getStringExtra("required_date") != null && !getIntent().getStringExtra("required_date").equalsIgnoreCase("")) {
                mTvdate.setText(DateTimeUtils.dateDifferenceCalculation(getIntent().getStringExtra("required_date"), MessageDetailsActivity.this) + "(" + DateTimeUtils.formatDate(getIntent().getStringExtra("required_date")) + ")");

            } else {
                mTvdate.setText("Unknown");
            }
        }
        mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = getIntent().getStringExtra("number");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share("Message From Blood", getIntent().getStringExtra("first_name") + " requires " + getIntent().getStringExtra("blood") + "  blood by " + DateTimeUtils.formatDate(getIntent().getStringExtra("required_date")));
            }
        });
        mIvWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWhatsApp("Message From Blood", getIntent().getStringExtra("first_name")  + " requires " + getIntent().getStringExtra("blood") + "  blood by " + DateTimeUtils.formatDate(getIntent().getStringExtra("required_date")));

            }
        });
        mIvFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFb("Message From Blood", getIntent().getStringExtra("first_name") + " requires " + getIntent().getStringExtra("blood") + "  blood by " + DateTimeUtils.formatDate(getIntent().getStringExtra("required_date")));

            }
        });
    }
    private void share(String title, String message) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, title));

    }

    private void shareWhatsApp(String title, String message) {

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);

        } else {
            try {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.whatsapp"));
               startActivity(intent);

            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                startActivity(intent);


            }
        }
    }

    private void shareFb(String title, String message) {

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.facebook.katana");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);

        } else {
            try {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
                startActivity(intent);

            } catch (ActivityNotFoundException e) {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
                startActivity(intent);


            }
        }
    }
}
