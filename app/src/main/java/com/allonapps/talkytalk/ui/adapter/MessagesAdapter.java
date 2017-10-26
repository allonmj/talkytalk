package com.allonapps.talkytalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allonapps.talkytalk.R;
import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.ui.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 10/14/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList = new ArrayList<>();
    private Context context;

    public MessagesAdapter(Context context) {
        this.context = context;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        messageViewHolder.tvUserName.setText(message.userName);
        messageViewHolder.tvMessage.setText(message.messageText);
        messageViewHolder.tvDate.setText(
                StringUtils.ago(new Date(message.messageDate), context.getResources())
        );
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    protected static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @BindView(R.id.tvMessage)
        TextView tvMessage;

        @BindView(R.id.tvDate)
        TextView tvDate;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
