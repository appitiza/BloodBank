package net.appitiza.android.lifedrop.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.constants.Constants;
import net.appitiza.android.lifedrop.model.NewsListModel;
import net.appitiza.android.lifedrop.ui.adapter.NewsListAdapter;
import net.appitiza.android.lifedrop.ui.callbacks.HomeActivityActions;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class NewsFragment extends BaseFragment {
    private ArrayList<String> titleList;
    private ArrayList<String> desList;
    private ArrayList<String> urlList;
    private ArrayList<String> pubList;
    private Handler handler;
    private ArrayList<NewsListModel> mList = new ArrayList<>();
    private RecyclerView mRvList;
    private CustomTextview mTvMessage;
    private NewsListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressDialog pd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        mTvMessage = getActivity().findViewById(R.id.tv_message);
        mRvList = getActivity().findViewById(R.id.rv_list);
        mAdapter = new NewsListAdapter(getActivity(), mList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvList.setLayoutManager(mLayoutManager);
        mRvList.setAdapter(mAdapter);
        mTvMessage.setVisibility(View.GONE);
        if (NetWorkUtil.isNetworkAvailable(getActivity())) {
            StreamXML(Constants.sRSSLink);
        } else {
            showNetworkAlert();
        }


        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    ((HomeActivityActions) getActivity()).listScrolling(false);
                } else {
                    ((HomeActivityActions) getActivity()).listScrolling(true);
                }
            }


        });
    }

    public void StreamXML(final String streamURL) {
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Fetching news...");
        pd.show();
        this.titleList = new ArrayList<>();
        this.urlList = new ArrayList<>();
        this.desList = new ArrayList<>();
        this.pubList = new ArrayList<>();

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Thread task = new Thread() {
                @Override
                public void run() {
                    try {

                        URL url = new URL(streamURL);
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(false);
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(getInputStream(url), "UTF_8");
                        boolean insideItem = false;
                        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
                            if (eventType == 2) {
                                if (xpp.getName().equalsIgnoreCase("item")) {
                                    insideItem = true;
                                } else if (xpp.getName().equalsIgnoreCase("title")) {
                                    if (insideItem) {
                                        titleList.add(xpp.nextText());
                                    }
                                } else if (xpp.getName().equalsIgnoreCase("link")) {
                                    if (insideItem) {
                                        urlList.add(xpp.nextText());
                                    }
                                } else if (xpp.getName().equalsIgnoreCase("description")) {
                                    if (insideItem) {
                                        desList.add(xpp.nextText());
                                    }
                                } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                    if (insideItem) {
                                        pubList.add(xpp.nextText());
                                    }
                                }
                            } else if (eventType == 3 && xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e2) {
                        e2.printStackTrace();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                mList.clear();
                                setListData();
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                                if (mList != null && mList.size() > 0) {
                                    mTvMessage.setVisibility(View.GONE);
                                    mAdapter = new NewsListAdapter(getActivity(), mList);
                                    mRvList.setAdapter(mAdapter);
                                } else {
                                    mTvMessage.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e4) {
                            }
                        }
                    });
                }
            };

            task.start();
        }


    }

    public void setListData() {
        for (int i = 0; i < this.titleList.size() - 1; i++) {
            NewsListModel sched = new NewsListModel();
            sched.setTitle(this.titleList.get(i));
            sched.setDesc(this.desList.get(i));
            sched.setLink(this.urlList.get(i));
            sched.setMdate(this.pubList.get(i));
            this.mList.add(sched);
        }
    }

    public InputStream getInputStream(URL url) {

        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
