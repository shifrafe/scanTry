package com.example.myapplication.book_library;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.BookObject;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.LiRecyclerViewHolder> {

    private List<BookObject> mItems;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private OnBookListener monBookListener;

    BookAdapter(Context context, List<BookObject> items, OnBookListener onBookListener) {
        mContext = context;
        mItems = items;
        this.monBookListener = onBookListener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public LiRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recommanded_list, parent, false);
        return new LiRecyclerViewHolder(view, monBookListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final LiRecyclerViewHolder holder, int position) {
        BookObject bookModel = mItems.get(position);
        try {
            StorageReference store = FirebaseStorage.getInstance().getReferenceFromUrl("gs://booklibrary-f24cd.appspot.com" + bookModel.getPageObjects().get(0).getImage());

            store.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.name.setText(bookModel.getName());
        holder.author.setText(bookModel.getAuthor());

       // holder.card.setCardBackgroundColor();


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class LiRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView name;
        private TextView author;
        private CardView card;
        private OnBookListener onBookListener;

        public LiRecyclerViewHolder(View itemView, final OnBookListener onBookListener) {
            super(itemView);
            image = itemView.findViewById(R.id.cover_page);
            name = itemView.findViewById(R.id.library_name_book);
            author = itemView.findViewById(R.id.authot_library);
           // card = itemView.findViewById(R.id.card_library);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBookListener.onBookClick(getAdapterPosition());
                }
            });
            this.onBookListener = onBookListener;

        }

        @Override
        public void onClick(View view) {
            onBookListener.onBookClick(getAdapterPosition());

        }

    }

    public interface OnBookListener {
        void onBookClick(int position);

    }

}
