package net.appitiza.android.lifedrop.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.model.Requestlist;
import net.appitiza.android.lifedrop.ui.activities.MessageDetailsActivity;
import net.appitiza.android.lifedrop.utils.DateTimeUtils;

import java.util.ArrayList;


public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Requestlist> mList;
    private final LayoutInflater inflater;
    private int lastPosition = -1;
    private Dialog mDialogRespond;

    public RequestListAdapter(Context context, ArrayList<Requestlist> mList) {
        this.context = context;
        this.mList = mList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_message_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Requestlist mData = mList.get(position);

        if (mData != null) {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mCardView.setVisibility(View.VISIBLE);
            holder.mTvName.setText(mData.getFirstName());
            holder.mTvBlood.setText(mData.getBlood());
            holder.mTvNumber.setText(mData.getNumber());
            if (mData.getRequiredDate() != null && !mData.getRequiredDate().equalsIgnoreCase("")) {
                StringBuilder datedata = new StringBuilder();
                datedata.append(DateTimeUtils.dateDifferenceCalculation(mData.getRequiredDate(), context));
                datedata.append("(");
                datedata.append(mData.getRequiredDate());
                datedata.append(")");
                holder.mTvdate.setText(datedata);

            }
            setAnimation(holder.itemView, position);
        } else {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mCardView.setVisibility(View.GONE);
        }

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData != null) {
                    Intent intent = new Intent(context, MessageDetailsActivity.class);
                    intent.putExtra("title", mData.getBlood());
                    intent.putExtra("email_id", mData.getEmailId());
                    intent.putExtra("blood", mData.getBlood());
                    intent.putExtra("address", mData.getAddress());
                    intent.putExtra("lat", mData.getLat());
                    intent.putExtra("lon", mData.getLon());
                    intent.putExtra("required_date", mData.getRequiredDate());
                    intent.putExtra("first_name", mData.getFirstName());
                    intent.putExtra("last_name", mData.getLastName());
                    intent.putExtra("message", mData.getMessage());
                    intent.putExtra("number", mData.getNumber());

                    Pair<View, String> p1 = Pair.create((View) (holder.mCardView), context.getString(R.string.message_open_transistion));
                    Pair<View, String> p2 = Pair.create((View) (holder.mTvBlood), context.getString(R.string.blood_group_transistion));
                    Pair<View, String> p3 = Pair.create((View) (holder.mTvName), context.getString(R.string.name_transistion));
                    Pair<View, String> p4 = Pair.create((View) (holder.mTvNumber), context.getString(R.string.number_transistion));
                    Pair<View, String> p5 = Pair.create((View) (holder.mTvdate), context.getString(R.string.date_transistion));
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                    context.startActivity(intent, options.toBundle());
                }
            }
        });
        holder.mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData != null && mData.getNumber() != null) {
                    String phone = mData.getNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    context.startActivity(intent);
                }
            }
        });
    }

    private void showOverDialog(final Requestlist mData) {
        if (mDialogRespond != null && mDialogRespond.isShowing()) {
            mDialogRespond.dismiss();
        }

        mDialogRespond = new Dialog(context);
        mDialogRespond.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRespond.setCancelable(true);
        mDialogRespond.setContentView(R.layout.dialog_message_list);
        if (mDialogRespond.getWindow() != null && mDialogRespond.getWindow().getAttributes() != null) {
            mDialogRespond.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDialogRespond.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationGrow;
        }
        ImageView mIvProfile = mDialogRespond.findViewById(R.id.mIvProfile);
        CustomTextview mTvName = mDialogRespond.findViewById(R.id.tv_name);
        CustomTextview mTvEmail = mDialogRespond.findViewById(R.id.tv_email);
        CustomTextview mTvNumber = mDialogRespond.findViewById(R.id.tv_number);
        CustomTextview mTvBlood = mDialogRespond.findViewById(R.id.tv_blood);
        CustomTextview mTvAddress = mDialogRespond.findViewById(R.id.tv_address);
        CustomTextview mTvMessage = mDialogRespond.findViewById(R.id.tv_message);
        CustomTextview mTvdate = mDialogRespond.findViewById(R.id.tv_date);
        ImageView mIvShare = mDialogRespond.findViewById(R.id.iv_share);
        ImageView mIvWhatsapp = mDialogRespond.findViewById(R.id.iv_whatsapp);
        ImageView mIvFb = mDialogRespond.findViewById(R.id.iv_fb);
        LinearLayout mLlShare = mDialogRespond.findViewById(R.id.ll_share);
        mLlShare.setVisibility(View.INVISIBLE);
        slideUp(mLlShare);
        mTvName.setText(mData.getFirstName() + " " + mData.getLastName());
        mTvEmail.setText(mData.getEmailId());
        mTvNumber.setText(mData.getNumber());
        mTvAddress.setText(mData.getAddress());
        mTvMessage.setText(mData.getMessage());
        if (mData.getRequiredDate() != null && !mData.getRequiredDate().equalsIgnoreCase("")) {
            mTvdate.setText(DateTimeUtils.dateDifferenceCalculation(mData.getRequiredDate(), context) + "(" + DateTimeUtils.formatDate(mData.getRequiredDate()) + ")");

        } else {
            mTvdate.setText("Unknown");
        }
        mTvBlood.setText(mData.getBlood());

        mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mData.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            }
        });
        mTvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share("Message From Blood", mData.getFirstName() + " requires " + mData.getBlood() + "  blood by " + DateTimeUtils.formatDate(mData.getRequiredDate()));
            }
        });
        mIvWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWhatsApp("Message From Blood", mData.getFirstName() + " requires " + mData.getBlood() + "  blood by " + DateTimeUtils.formatDate(mData.getRequiredDate()));

            }
        });
        mIvFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFb("Message From Blood", mData.getFirstName() + " requires " + mData.getBlood() + "  blood by " + DateTimeUtils.formatDate(mData.getRequiredDate()));

            }
        });

        mDialogRespond.show();

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextview mTvName;
        private CustomTextview mTvBlood;
        private CustomTextview mTvNumber;
        private CustomTextview mTvdate;
        private ProgressBar mProgressBar;
        private CardView mCardView;
        private LinearLayout mLlRoot;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.mTvName = itemView.findViewById(R.id.tv_name);
            this.mTvBlood = itemView.findViewById(R.id.tv_blood);
            this.mTvNumber = itemView.findViewById(R.id.tv_number);
            this.mTvdate = itemView.findViewById(R.id.tv_date);
            this.mCardView = itemView.findViewById(R.id.card_view);
            this.mLlRoot = itemView.findViewById(R.id.ll_root);
            this.mProgressBar = itemView.findViewById(R.id.progress);


        }


    }

    private void share(String title, String message) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(share, title));
        if (mDialogRespond != null && mDialogRespond.isShowing())
            mDialogRespond.dismiss();
    }

    private void shareWhatsApp(String title, String message) {

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

    private void shareFb(String title, String message) {

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

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
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
