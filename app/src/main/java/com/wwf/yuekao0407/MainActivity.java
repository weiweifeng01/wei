package com.wwf.yuekao0407;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wwf.mlibrary.NetStateUtil;
import com.wwf.mlibrary.NetWordUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String baseUrl = "http://api.tianapi.com/huabian/?key=71e58b5b2f930eaf1f937407acde08fe&num=";
    int page = 10;
    ArrayList<Phrea.NewslistBean> list = new ArrayList<>();
    private PullToRefreshListView plv;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String s = (String) msg.obj;
                //查到的json加入数据库
                dao.insert(mUrl,s);
                //解析数据
                jiexi(s);
            }
        }
    };
    private dbDao dao;
    private String mUrl;

    private void jiexi(String s) {
        Gson gson = new Gson();
        Phrea phrea = gson.fromJson(s, Phrea.class);
        List<Phrea.NewslistBean> newslist = phrea.getNewslist();
        list.addAll(newslist);
        adapter.notifyDataSetChanged();
        plv.onRefreshComplete();
    }

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //声明id
        plv = (PullToRefreshListView) findViewById(R.id.plv);
        plv.setMode(PullToRefreshBase.Mode.BOTH);//设置显示模式
        dao = new dbDao(this);
        //设置适配器
        adapter = new MyAdapter();
        plv.setAdapter(adapter);
        //请求数据
        getData(10);
        //设置监听
        plv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                //下拉刷新
                list.clear();
                getData(10);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                //上啦加载
                page++;
                getData(page);
            }
        });
    }

    private void getData(int page) {
        mUrl = baseUrl + page;
        //开启子线程请求数据

        if (NetStateUtil.isConn(MainActivity.this)) {
            //有网络
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    //请求数据
                    String netjson = NetWordUtils.getNetjson(mUrl);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = netjson;
                    handler.sendMessage(msg);
                }
            }.start();
        } else {
            //无网络
            String query = dao.query(mUrl);
            if (!query.isEmpty()){//不为空开始解析
                jiexi(query);
            }
        }

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view=View.inflate(MainActivity.this,R.layout.item_list,null);
                holder=new ViewHolder();
                holder.iv=view.findViewById(R.id.iv);
                holder.tv1=view.findViewById(R.id.tv1);
                holder.tv2=view.findViewById(R.id.tv2);
                view.setTag(holder);
            }else {
                holder= (ViewHolder) view.getTag();
            }
            holder.tv1.setText(list.get(i).getTitle());
            holder.tv2.setText(list.get(i).getCtime());
            ImageLoader.getInstance().displayImage(list.get(i).getPicUrl(),holder.iv);
            return view;
        }

        class ViewHolder {
            private ImageView iv;
            private TextView tv1;
            private TextView tv2;
        }
    }
}
