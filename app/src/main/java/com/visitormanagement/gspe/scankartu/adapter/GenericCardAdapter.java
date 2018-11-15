package com.visitormanagement.gspe.scankartu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visitormanagement.gspe.scankartu.R;
import com.visitormanagement.gspe.scankartu.model.GenericCard;

import java.util.List;

public class GenericCardAdapter extends RecyclerView.Adapter<GenericCardAdapter.ViewHolder> {

    private Context context;
    private List<GenericCard> cardList;

    public GenericCardAdapter(Context context, List<GenericCard> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GenericCard card = cardList.get(position);
        holder.tvCardType.setText(String.valueOf(card.getType()));
        holder.tvContent.setText(String.valueOf(card.getContent()));
        holder.tvId.setText(String.valueOf(card.getImagePath()));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCardType;
        TextView tvContent;
        TextView tvId;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCardType = itemView.findViewById(R.id.tv_card_type);
            tvId = itemView.findViewById(R.id.tv_nik);
            tvContent = itemView.findViewById(R.id.tv_nama);
        }
    }
}
