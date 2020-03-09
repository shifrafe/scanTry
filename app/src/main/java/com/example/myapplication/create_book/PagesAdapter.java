//Adapter of pages for recyclerView of create book
package com.example.myapplication.create_book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;


public class PagesAdapter extends RecyclerView.Adapter<PagesAdapter.CustomRecyclerViewHolder> {

    private List<PageModel> mItems;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private OnPageListener monPageListener;

    PagesAdapter(Context context, List<PageModel> items, OnPageListener onPageListener) {
        mContext = context;
        mItems = items;
        this.monPageListener = onPageListener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CustomRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.page_card, parent, false);
        return new CustomRecyclerViewHolder(view, monPageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerViewHolder holder, int position) {
        PageModel pageModel = mItems.get(position);
        holder.image.setImageBitmap(pageModel.getBit());
        holder.num.setText(String.valueOf(position));
        holder.play.setVisibility(mItems.get(position).getPlay());
        holder.puse.setVisibility(mItems.get(position).getPuse());
        holder.record.setVisibility(mItems.get(position).getRecord());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class CustomRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView num;
        private ImageView delete;
        private ImageButton record;
        private ImageButton puse;
        private ImageButton play;
        private OnPageListener onPageListener;

        public CustomRecyclerViewHolder(View itemView, final OnPageListener onPageListener) {
            super(itemView);
            image = itemView.findViewById(R.id.photoImage);
            delete = itemView.findViewById(R.id.delCard);
            num = itemView.findViewById(R.id.page_card_num);
            record = itemView.findViewById(R.id.recorderContinue);//triangle
            puse = itemView.findViewById(R.id.recorderPuse);//2 kavim
            play = itemView.findViewById(R.id.playRecorder);

            puse.setVisibility(View.INVISIBLE);
            play.setVisibility(View.INVISIBLE);




            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPageListener.onCameraClick(getAdapterPosition());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPageListener.onDeleteClick(getAdapterPosition());
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    record.setVisibility(View.INVISIBLE);
                    puse.setVisibility(View.INVISIBLE);
                    play.setVisibility(View.VISIBLE);

                    onPageListener.onPlayClick(getAdapterPosition());
                }
            });
            puse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setVisibility(View.INVISIBLE);
                    puse.setVisibility(View.INVISIBLE);
                    play.setVisibility(View.VISIBLE);
                    onPageListener.onPuseClick(getAdapterPosition());
                }
            });
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setVisibility(View.INVISIBLE);
                    puse.setVisibility(View.VISIBLE);
                    play.setVisibility(View.INVISIBLE);
                    onPageListener.onRecordClick(getAdapterPosition());
                }
            });

            this.onPageListener = onPageListener;


        }

        @Override
        public void onClick(View view) {
            onPageListener.onCameraClick(getAdapterPosition());

        }

    }

    public interface OnPageListener {
        void onCameraClick(int position);

        void onDeleteClick(int position);

        void onPlayClick(int position);

        void onPuseClick(int position);

        void onRecordClick(int position);

    }
}