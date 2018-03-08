package net.appitiza.android.lifedrop.ui.adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.model.SearchItem;
import net.appitiza.android.lifedrop.utils.DateTimeUtils;

import java.io.File;
import java.util.ArrayList;


public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SearchItem> mList;
    private final LayoutInflater inflater;
    private int lastPosition = -1;
    private Dialog mDialogRespond;

    public SearchListAdapter(Context context, ArrayList<SearchItem> mList) {
        this.context = context;
        this.mList = mList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final SearchItem mData = mList.get(position);

        holder.mTvName.setText(mData.getFirstName() + " " + mData.getLastName());
        holder.mTvEmail.setText(mData.getEmail());
        holder.mTvNumber.setText(mData.getNumber());
        if (mData.getLastDonation() != null && !mData.getLastDonation().equalsIgnoreCase("")) {
            holder.mTvdate.setText(DateTimeUtils.dateDifferenceCalculation(mData.getLastDonation(), context) + "(" + DateTimeUtils.formatDate(mData.getLastDonation()) + ")");

        }
        if (mData.getGender().equalsIgnoreCase("M")) {
            holder.mIvProfile.setImageResource(R.drawable.man);
        } else {
            holder.mIvProfile.setImageResource(R.drawable.girl);
        }
        setAnimation(holder.itemView, position);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverDialog(mData);
            }
        });
        holder.mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mData.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            }
        });

    }

    private void showOverDialog(final SearchItem mData) {
        if (mDialogRespond != null && mDialogRespond.isShowing()) {
            mDialogRespond.dismiss();
        }
        mDialogRespond = new Dialog(context);
        mDialogRespond.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRespond.setCancelable(true);
        mDialogRespond.setContentView(R.layout.dialog_item_search_list);
        if (mDialogRespond.getWindow() != null && mDialogRespond.getWindow().getAttributes() != null) {
            mDialogRespond.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDialogRespond.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationGrow;
        }
        ImageView mIvProfile = mDialogRespond.findViewById(R.id.mIvProfile);
        CustomTextview mTvName = mDialogRespond.findViewById(R.id.tv_name);
        CustomTextview mTvEmail = mDialogRespond.findViewById(R.id.tv_email);
        CustomTextview mTvNumber = mDialogRespond.findViewById(R.id.tv_number);
        CustomTextview mTvGender = mDialogRespond.findViewById(R.id.tv_gender);
        CustomTextview mTvBlood = mDialogRespond.findViewById(R.id.tv_blood);
        CustomTextview mTvAddress = mDialogRespond.findViewById(R.id.tv_address);
        CustomTextview mTvMedical = mDialogRespond.findViewById(R.id.tv_medical);
        CustomTextview mTvdate = mDialogRespond.findViewById(R.id.tv_date);
        mTvName.setText(mData.getFirstName() + " " + mData.getLastName());
        mTvEmail.setText(mData.getEmail());
        mTvNumber.setText(mData.getNumber());
        mTvAddress.setText(mData.getAddress());

        if (mData.getMedical() != null && !mData.getMedical().equalsIgnoreCase("")) {
            mTvMedical.setText(DateTimeUtils.ageCalculation(mData.getMedical(), context) + "(" + DateTimeUtils.formatDate(mData.getMedical()) + ")");

        }
        else
        {
            mTvMedical.setText("Unknown");
        }
        if (mData.getLastDonation() != null && !mData.getLastDonation().equalsIgnoreCase("")) {
            mTvdate.setText(DateTimeUtils.dateDifferenceCalculation(mData.getLastDonation(), context) + "(" + DateTimeUtils.formatDate(mData.getLastDonation()) + ")");

        }
        else
        {
            mTvdate.setText("Unknown");
        }
        mTvBlood.setText(mData.getBlood());
        if (mData.getGender().equalsIgnoreCase("M")) {
            mIvProfile.setImageResource(R.drawable.man);
            mTvGender.setText("Male");
        } else {
            mIvProfile.setImageResource(R.drawable.girl);
            mTvGender.setText("Female");
        }

        mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mData.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            }
        });

        mDialogRespond.show();
    }
    private void share(String title,String message) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(share, title));
        if (mDialogRespond != null && mDialogRespond.isShowing())
            mDialogRespond.dismiss();
    }

    private void shareWhatsApp(String title,String message) {

        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.setType("text/plain");
            context.startActivity(shareIntent);
            if (mDialogRespond != null && mDialogRespond.isShowing())
                mDialogRespond.dismiss();
        } else {
            try {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.whatsapp"));
                context.startActivity(intent);
                if (mDialogRespond != null && mDialogRespond.isShowing())
                    mDialogRespond.dismiss();
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                context.startActivity(intent);
                if (mDialogRespond != null && mDialogRespond.isShowing())
                    mDialogRespond.dismiss();

            }
        }
    }

    private void shareFb(String title,String message) {

        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.facebook.katana");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.setType("text/plain");
            context.startActivity(shareIntent);
            if (mDialogRespond != null && mDialogRespond.isShowing())
                mDialogRespond.dismiss();
        } else {
            try {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
                context.startActivity(intent);
                if (mDialogRespond != null && mDialogRespond.isShowing())
                    mDialogRespond.dismiss();
            } catch (ActivityNotFoundException e) {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
                context.startActivity(intent);
                if (mDialogRespond != null && mDialogRespond.isShowing())
                    mDialogRespond.dismiss();

            }
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextview mTvName;
        private CustomTextview mTvEmail;
        private CustomTextview mTvNumber;
        private CustomTextview mTvdate;
        private CardView mCardView;
        private ImageView mIvProfile;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.mIvProfile = itemView.findViewById(R.id.mIvProfile);
            this.mTvName = itemView.findViewById(R.id.tv_name);
            this.mTvEmail = itemView.findViewById(R.id.tv_email);
            this.mTvNumber = itemView.findViewById(R.id.tv_number);
            this.mTvdate = itemView.findViewById(R.id.tv_date);
            this.mCardView = itemView.findViewById(R.id.card_view);

        }


    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
