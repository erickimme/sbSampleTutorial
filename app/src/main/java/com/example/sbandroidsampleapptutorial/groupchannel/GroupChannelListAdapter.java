package com.example.sbandroidsampleapptutorial.groupchannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sbandroidsampleapptutorial.R;
import com.example.sbandroidsampleapptutorial.utils.FileUtils;
import com.example.sbandroidsampleapptutorial.utils.TypingIndicator;
import com.example.sbandroidsampleapptutorial.utils.DateUtils;
import com.example.sbandroidsampleapptutorial.utils.TextUtils;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;
import com.stfalcon.multiimageview.MultiImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
* Displaying group channel list
 */
public class GroupChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroupChannel> mChannelList;
    private Context mContext;
    private final ConcurrentHashMap<SimpleTarget<Bitmap>, Integer> mSimpleTargetIndexMap;
    private final ConcurrentHashMap<SimpleTarget<Bitmap>, GroupChannel> mSimpleTargetGroupChannelMap;
    private final ConcurrentHashMap<String, Integer> mChannelImageNumMap;
    private final ConcurrentHashMap<String, ImageView> mChannelImageViewMap;
    private final ConcurrentHashMap<String, SparseArray<Bitmap>> mChannelBitmapMap;


    // itemclicklisteners
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    interface OnItemClickListener {
        void onItemClick(GroupChannel channel);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(GroupChannel channel);
    }

    // constructor
    public GroupChannelListAdapter(Context context) {
        mContext = context;

        mSimpleTargetIndexMap = new ConcurrentHashMap<>();
        mSimpleTargetGroupChannelMap = new ConcurrentHashMap<>();
        mChannelImageNumMap = new ConcurrentHashMap<>();
        mChannelImageViewMap = new ConcurrentHashMap<>();
        mChannelBitmapMap = new ConcurrentHashMap<>();

        mChannelList = new ArrayList<>();
    }

    void clearMap(){
        mSimpleTargetIndexMap.clear();
        mSimpleTargetGroupChannelMap.clear();
        mChannelImageNumMap.clear();
        mChannelImageViewMap.clear();
        mChannelBitmapMap.clear();
    }


    // TODO: fild load
    public void load(){
        try {
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            String content = FileUtils.loadFromFile(dataFile);
            String[] dataArray = content.split("\n");

            // Reset channel list, then add cached data.
            mChannelList.clear();
            for (int i = 0; i < dataArray.length; i++) {
                mChannelList.add((GroupChannel) BaseChannel.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
            }

            notifyDataSetChanged();
        } catch(Exception e) {
            // Nothing to load.
        }
    }

    public void save(){
        try {
            StringBuilder sb = new StringBuilder();

            // Save the data into file.
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File hashFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".hash");
            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            if (mChannelList != null && mChannelList.size() > 0) {
                // Convert current data into string.
                GroupChannel channel = null;
                for (int i = 0; i < Math.min(mChannelList.size(), 100); i++) {
                    channel = mChannelList.get(i);
                    sb.append("\n");
                    sb.append(Base64.encodeToString(channel.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                }
                // Remove first newline.
                sb.delete(0, 1);

                String data = sb.toString();
                String md5 = TextUtils.generateMD5(data);

                try {
                    String content = FileUtils.loadFromFile(hashFile);
                    // If data has not been changed, do not save.
                    if(md5.equals(content)) {
                        return;
                    }
                } catch(IOException e) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data);
                FileUtils.saveToFile(hashFile, md5);
            } else {
                FileUtils.deleteFile(dataFile);
                FileUtils.deleteFile(hashFile);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_channel, parent, false);
        return new ChannelHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((ChannelHolder) viewHolder).bind(mContext, i, mChannelList.get(i), mItemClickListener, mItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    void setGroupChannelList(List<GroupChannel> channelList){
        mChannelList = channelList;
        notifyDataSetChanged();
    }

    void addLast(GroupChannel channel){
        mChannelList.add(channel);
        notifyItemInserted(mChannelList.size()-1);
    }


    // If the channel is not in the list yet, adds it.
    // If it is, finds the channel in current list, and replaces it.
    // Moves the updated channel to the front of the list.
    void updateOrInsert(BaseChannel channel){
        if (!(channel instanceof GroupChannel)){
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;

        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())){
                mChannelList.remove(mChannelList.get(i));
                mChannelList.add(0, groupChannel);
                notifyDataSetChanged();
                Log.v(GroupChannelListAdapter.class.getSimpleName(), "Channel Replaced.");
                return;
            }
        }

        mChannelList.add(0, groupChannel);
        notifyDataSetChanged();
    }


    void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }


    /**
     * A ViewHolder that contains UI to display information about a GroupChannel.
     */
    private class ChannelHolder extends RecyclerView.ViewHolder{

        TextView topicText, lastMessageText, unreadCountText, dateText, memberCountText;
        MultiImageView coverImage;
        LinearLayout typingIndicatorContainer;

        ChannelHolder(View itemView){
            super(itemView);

            topicText = (TextView) itemView.findViewById(R.id.text_group_channel_list_topic);
            lastMessageText = (TextView) itemView.findViewById(R.id.text_group_channel_list_message);
            unreadCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_unread_count);
            dateText = (TextView) itemView.findViewById(R.id.text_group_channel_list_date);
            memberCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_member_count);
            coverImage = (MultiImageView) itemView.findViewById(R.id.image_group_channel_list_cover);
            coverImage.setShape(MultiImageView.Shape.CIRCLE);

            typingIndicatorContainer = (LinearLayout) itemView.findViewById(R.id.container_group_channel_list_typing_indicator);
        }

        void bind(final Context context, int position, final GroupChannel channel, @Nullable  final OnItemClickListener clickListener, @Nullable final OnItemLongClickListener longClickListener){
            topicText.setText(TextUtils.getGroupChannelTitle(channel));
            memberCountText.setText(String.valueOf(channel.getMemberCount()));

            setChannelImage(context, position, channel, coverImage);


            /* Unread count handling */
            int unreadCount = channel.getUnreadMessageCount();
            // no unreadcount ->  everyone read it -> hide the unread count badge
            if (unreadCount == 0){
                unreadCountText.setVisibility(View.INVISIBLE);
            } else {
                unreadCountText.setVisibility(View.VISIBLE);
                unreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
            }

            /*Displaying the last maeesge*/
            BaseMessage lastMessage = channel.getLastMessage();
            if (lastMessage != null){
                dateText.setVisibility(View.VISIBLE);
                lastMessageText.setVisibility(View.VISIBLE);

                // Display information about the most recently sent message in the channel
                dateText.setText(DateUtils.formatDateTime(lastMessage.getCreatedAt()));

                // Bind last message text according to the type of message. Specifically, if
                // the last message is a File Message, there must be special formatting.
                if (lastMessage instanceof UserMessage){
                    lastMessageText.setText(((UserMessage) lastMessage).getMessage());
                } else if (lastMessage instanceof AdminMessage){
                    lastMessageText.setText(((AdminMessage) lastMessage).getMessage());
                } else {
                    String lastMsgString = String.format(
                            context.getString(R.string.group_channel_list_file_message_text), ((FileMessage) lastMessage).getSender().getNickname());
                    lastMessageText.setText(lastMsgString);
                }
            } else {
                dateText.setVisibility(View.INVISIBLE);
                lastMessageText.setVisibility(View.INVISIBLE);
            }

            /* Typing Indicator */
            ArrayList<ImageView> indicatorImages = new ArrayList<>();
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));

            TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
            indicator.animate();

            // displaying typing indicator
            if (channel.isTyping()){
                typingIndicatorContainer.setVisibility(View.VISIBLE);
                lastMessageText.setText("Someone is typing");
            } else {
                typingIndicatorContainer.setVisibility(View.GONE);
            }



            // Todo : onClickListener
            // Set an OnClickListener to this item.
            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(channel);
                    }
                });
            }

            // Set an OnLongClickListener to this item.
                if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongClick(channel);

                        // return true if the callback consumed the long click
                        return true;
                    }
                });
            }

        }
    }

    private void setChannelImage(Context context, int position, GroupChannel channel, MultiImageView multiImageView){
        List<Member> members = channel.getMembers();
        int size = members.size();
        if (size >= 1){
            int imageNum = size;
            if (size >= 4){
                imageNum = 4;
            }

            if  (!mChannelImageNumMap.containsKey(channel.getUrl())){
                mChannelImageNumMap.put(channel.getUrl(), imageNum);
                mChannelImageViewMap.put(channel.getUrl(), multiImageView);

                multiImageView.clear();

                for (int index = 0; index < imageNum; index++){
                    SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {
                            GroupChannel channel = mSimpleTargetGroupChannelMap.get(this);
                            Integer index = mSimpleTargetIndexMap.get(this);
                            if (channel != null && index != null){
                                SparseArray<Bitmap> bitmapSparseArray = mChannelBitmapMap.get(channel.getUrl());
                                if (bitmapSparseArray == null){
                                    bitmapSparseArray = new SparseArray<>();
                                    mChannelBitmapMap.put(channel.getUrl(), bitmapSparseArray);
                                }
                                bitmapSparseArray.put(index, bitmap);

                                Integer num = mChannelImageNumMap.get(channel.getUrl());
                                if (num != null && num == bitmapSparseArray.size()){
                                    MultiImageView multiImageView = (MultiImageView) mChannelImageViewMap.get(channel.getUrl());
                                    if (multiImageView != null){
                                        for (int i = 0; i < bitmapSparseArray.size() ; i++) {
                                            multiImageView.addImage(bitmapSparseArray.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    };

                    mSimpleTargetIndexMap.put(simpleTarget, index);
                    mSimpleTargetGroupChannelMap.put(simpleTarget, channel);

                    RequestOptions myOptions = new RequestOptions()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    Glide.with(context)
                            .asBitmap()
                            .load(members.get(index).getProfileUrl())
                            .apply(myOptions)
                            .into(simpleTarget);
                }
            }
            else {
                SparseArray<Bitmap> bitmapSparseArray = mChannelBitmapMap.get(channel.getUrl());
                if (bitmapSparseArray != null){
                    Integer num = mChannelImageNumMap.get(channel.getUrl());
                    if (num != null && num == bitmapSparseArray.size()){
                        multiImageView.clear();

                        for (int i = 0; i < bitmapSparseArray.size(); i++) {
                            multiImageView.addImage(bitmapSparseArray.get(i));
                        }
                    }
                }
            }
        }
    }


}
