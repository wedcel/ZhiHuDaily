package io.github.izzyleung.zhihudailypurify.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.bean.DailyNews;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public final class NewsAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private LayoutInflater mInflater;

    private List<DailyNews> newsList;
    private List<String> dateResultList;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.noimage)
            .showImageOnFail(R.drawable.noimage)
            .showImageForEmptyUri(R.drawable.lks_for_blank_url)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public NewsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setNewsList(List<DailyNews> newsList) {
        this.newsList = newsList;
    }

    public void setDateResultList(List<String> dateResultList) {
        this.dateResultList = dateResultList;
    }

    public void updateNewsList(List<DailyNews> newsList) {
        setNewsList(newsList);
        notifyDataSetChanged();
    }

    public void updateDateResultList(List<String> dateResultList) {
        setDateResultList(dateResultList);
        notifyDataSetChanged();
    }

    public void updateContents(List<DailyNews> newsList, List<String> dateResultList) {
        updateNewsList(newsList);
        updateDateResultList(dateResultList);
    }

    @Override
    public int getCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardViewHolder holder;
        if (convertView == null) {
            holder = new CardViewHolder();
            convertView = mInflater.inflate(R.layout.news_list_item, null);

            assert convertView != null;
            holder.newsImage = (ImageView) convertView.findViewById(R.id.thumbnail_image);
            holder.dailyTitle = (TextView) convertView.findViewById(R.id.daily_title);
            holder.questionTitle = (TextView) convertView.findViewById(R.id.question_title);

            convertView.setTag(holder);
        } else {
            holder = (CardViewHolder) convertView.getTag();
        }

        DailyNews dailyNews = new DailyNews(newsList.get(position));

        imageLoader.displayImage(dailyNews.getThumbnailUrl(), holder.newsImage, options, animateFirstListener);

        if (dailyNews.isMulti()) {
            holder.questionTitle.setText(dailyNews.getDailyTitle());
            String simplifiedMultiQuestion = "这里包含多个知乎讨论，请点击后选择";
            holder.dailyTitle.setText(simplifiedMultiQuestion);
        } else {
            holder.questionTitle.setText(dailyNews.getQuestionTitle());
            holder.dailyTitle.setText(dailyNews.getDailyTitle());
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return dateResultList.get(position).hashCode();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.search_result_list_header, null);

            assert convertView != null;
            holder.headerTitle = (TextView)
                    convertView.findViewById(R.id.header_title);

            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.headerTitle.setText(dateResultList.get(position));
        return convertView;
    }

    private final static class CardViewHolder {
        ImageView newsImage;
        TextView questionTitle;
        TextView dailyTitle;
    }

    private final static class HeaderViewHolder {
        TextView headerTitle;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
