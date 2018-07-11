package com.vijay.jsonwizard.utils;

import java.io.File;
import java.util.List;

import com.bumptech.glide.Glide;
import com.vijay.jsonwizard.R;

import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xcarmona on 21/06/18.
 */
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder>{

    private List<CarouselItem> data;
    private RecyclerView parentRecycler;

    public CarouselAdapter(List<CarouselItem> data) {
        this.data = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_carousel_element, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = parentRecycler.getChildLayoutPosition(v);
                String imagePath =  data.get(position).getImage();
                boolean exists = (!TextUtils.isEmpty(imagePath)) && (new File(imagePath).exists());
                if(exists){
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    View popupView = factory.inflate(R.layout.item_carousel_popup, null);
                    ImageView imageView = popupView.findViewById(R.id.image);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
                    builder.setView(popupView);
                    builder.create().show();
                }
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imagePath = data.get(position).getImage();
        int imageResourceId = isInteger(imagePath);
        if(!TextUtils.isEmpty(imagePath)) {
            if(imageResourceId>0){
                Glide.with(holder.itemView.getContext())
                        .load(imageResourceId)
                        .into(holder.image);
            }else {
                Glide.with(holder.itemView.getContext())
                        .load(new File(imagePath))
                        .into(holder.image);
            }
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.mipmap.error_icon)
                    .into(holder.image);
        }
        holder.name.setText(data.get(position).getName());
        holder.value.setText(data.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private int isInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return -1;
        }
        // only got here if we didn't return false
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name;
        private TextView value;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            value = (TextView) itemView.findViewById(R.id.value);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
