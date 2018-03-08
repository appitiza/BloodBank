package net.appitiza.android.lifedrop.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.model.NewsListModel;

import java.util.ArrayList;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<NewsListModel> mList;
    private final LayoutInflater inflater;
    private int lastPosition = -1;

    public NewsListAdapter(Context context, ArrayList<NewsListModel> mList) {
        this.context = context;
        this.mList = mList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_news_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final NewsListModel mData = mList.get(position);

        holder.mTvTitle.setText(mData.getTitle());
        holder.mTvDesc.setText(mData.getDesc());
        holder.mTvdate.setText(mData.getMdate());
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData.getLink() != null && !TextUtils.isEmpty(mData.getLink())) {
                    String url = mData.getLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            }
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView mRoot;
        private CustomTextview mTvTitle;
        private CustomTextview mTvDesc;
        private CustomTextview mTvdate;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.mRoot = itemView.findViewById(R.id.card_view);
            this.mTvTitle = itemView.findViewById(R.id.tv_title);
            this.mTvDesc = itemView.findViewById(R.id.tv_desc);
            this.mTvdate = itemView.findViewById(R.id.tv_date);

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
