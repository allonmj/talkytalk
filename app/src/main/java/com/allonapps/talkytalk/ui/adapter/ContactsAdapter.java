package com.allonapps.talkytalk.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allonapps.talkytalk.R;
import com.allonapps.talkytalk.data.entity.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 10/14/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> userList = new ArrayList<>();

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        User user = userList.get(position);

        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
        contactViewHolder.tvUserName.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    protected static class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUserName)
        TextView tvUserName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}