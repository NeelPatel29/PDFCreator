package com.pdfkns.pdfcreator.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.pdfkns.pdfcreator.Activity.AddEditActivity;
import com.pdfkns.pdfcreator.Model.MediaCell;
import com.pdfkns.pdfcreator.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

//import android.support.v7.app.AlertDialog;

/**
 * Created by sanjaypatel on 2017-06-02.
 */

public class MediaCellAdapter extends RecyclerView.Adapter<MediaCellAdapter.DSViewHolder>  {
    private List<MediaCell> sanDSList;

    private Context context;

    public MediaCellAdapter(List<MediaCell> sanPersonList, Context context) {
        this.sanDSList = sanPersonList;
        this.context = context;
    }

    @Override
    public DSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new DSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DSViewHolder holder, final int position) {

        try{

            holder.setIsRecyclable(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.linearLayout.setLayoutParams(layoutParams);
            holder.cardView.setLayoutParams(layoutParams);
            holder.linearLayout.addView(holder.cardView);


//            holder.cardView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
           // holder.displayLinear.setOrientation(LinearLayout.VERTICAL);
            holder.displayLinear.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            holder.cardView.addView(holder.displayLinear);

            RelativeLayout.LayoutParams rlLayoutParamsSeq = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.txtTextSeq.setText(String.valueOf(sanDSList.get(position).getSequence()));
            rlLayoutParamsSeq.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            holder.txtTextSeq.setLayoutParams(rlLayoutParamsSeq);

            RelativeLayout.LayoutParams rlLayoutParamsCon = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            holder.txtTextCon.setText(sanDSList.get(position).getTxtContent());
            rlLayoutParamsCon.addRule(RelativeLayout.BELOW,R.id.txtTextSeq);
            holder.txtTextCon.setLayoutParams(rlLayoutParamsCon);

            holder.viVideo.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams rlLayoutParamsMenu = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlLayoutParamsMenu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,R.id.txtTextSeq);
            holder.optMenu.setLayoutParams(rlLayoutParamsMenu);
            holder.displayLinear.addView(holder.optMenu);


            RelativeLayout.LayoutParams rlLayoutParamsLink = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlLayoutParamsLink.addRule(RelativeLayout.BELOW, R.id.txtText);
            rlLayoutParamsLink.setMargins(0,20,0,0);
            holder.txtTextLink.setLayoutParams(rlLayoutParamsLink);

            RelativeLayout.LayoutParams rlLayoutParamsImage = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,500);
            rlLayoutParamsImage.addRule(RelativeLayout.BELOW , R.id.txtText);
            holder.image.setLayoutParams(rlLayoutParamsImage);


            holder.displayLinear.setPadding(40,40,40,40);

            if (sanDSList.get(position).getMediaCellEnum().toString() .equals("TEXT")){


                holder.txtTextLink.setTextColor(Color.BLACK);
                holder.txtTextLink.setClickable(false);

                //--restore from string--
                Spanned s3 = Html.fromHtml(sanDSList.get(position).getUrl());
                holder.txtTextLink.setText(s3);
                holder.displayLinear.addView(holder.txtTextCon);
                holder.displayLinear.addView(holder.txtTextLink);

            }
            if (sanDSList.get(position).getMediaCellEnum().toString().equals("LINK")){
                Log.d("Data: " ,"LINK" + sanDSList.get(position).getMediaCellEnum().toString() );
                holder.txtTextLink.setText(sanDSList.get(position).getUrl());
                holder.txtTextLink.setTextColor(Color.BLUE);
                holder.txtTextLink.setClickable(true);
                holder.txtTextLink.setLayoutParams(rlLayoutParamsLink);
                holder.displayLinear.addView(holder.txtTextCon);
                holder.displayLinear.addView(holder.txtTextLink);

            }
            if (sanDSList.get(position).getMediaCellEnum().toString() .equals( "IMAGE")){
                Log.d("Data: " ,"IMAGE" + sanDSList.get(position).getMediaCellEnum().toString() );

                Uri uri = Uri.parse(sanDSList.get(position).getUrl());
                Log.d("URI",""+uri);

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytearrayoutputstream);

                    byte[] bytearray = bytearrayoutputstream.toByteArray();

                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytearray,0,bytearray.length);
                    holder.image.setImageBitmap(bitmap2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

               // holder.image.setImageURI(uri);
              //  holder.image.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 500));

                holder.txtTextCon.setText(sanDSList.get(position).getTxtContent());
                holder.displayLinear.addView(holder.txtTextCon);
                holder.displayLinear.addView(holder.image);


            }
            if (sanDSList.get(position).getMediaCellEnum().toString() .equals( "VIDEO")){
                Log.d("Data: " ,"VIDEO" +  sanDSList.get(position).getMediaCellEnum().toString() );


                /*holder.linearLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 800));*/
                RelativeLayout.LayoutParams rlLayoutParamsVideo = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 800);
                rlLayoutParamsVideo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlLayoutParamsVideo.setMargins(0,100,0,0);
                holder.viVideo.setLayoutParams(rlLayoutParamsVideo);
                Uri uri = Uri.parse(sanDSList.get(position).getUrl());
                Log.d("URI",""+uri);

                holder.viVideo.setVideoURI(uri);
                // holder.viVideo.seekTo(100);
                holder.viVideo.start();
                holder.displayLinear.addView(holder.txtTextCon);
                holder.displayLinear.addView(holder.viVideo);
            }

           holder.optMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context,holder.optMenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.cardviewoption_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    //handle menu1 click

                                    Intent intent = new Intent(context,AddEditActivity.class);
                                    intent.putExtra("type", "Edit");
                                    intent.putExtra("idData",sanDSList.get(position).getId());
                                    intent.putExtra("url",sanDSList.get(position).getUrl());
                                    intent.putExtra("sequence",sanDSList.get(position).getSequence());
                                    intent.putExtra("content",sanDSList.get(position).getTxtContent());
                                    intent.putExtra("mediaType",sanDSList.get(position).getMediaCellEnum());
                                   context. startActivity(intent);

                                    break;
                                case R.id.delete:
                                    //handle menu2 click
                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                                    builder.setMessage("Are you sure you want to delete ?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    long data = sanDSList.get(position).getId();
                                                    MediaCell mediaCell;
                                                    mediaCell= MediaCell.findById(MediaCell.class, data);
                                                    mediaCell.delete();
                                                    sanDSList.remove(position);
                                                    notifyDataSetChanged();


//                                                    Intent intent = new Intent(context,MainActivity.class);
//                                                    context. startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setTitle("Alert Dialog");
                                    alertDialog.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });
        }catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }


    @Override
    public int getItemCount() {
        return sanDSList.size();
    }



    public static class DSViewHolder extends RecyclerView.ViewHolder {


        public TextView txtTextCon,txtTextLink,txtTextSeq;
        public ImageView image,optMenu;
        public static VideoView viVideo;
        CardView cardView;


        LinearLayout linearLayout;
        RelativeLayout displayLinear;

        public DSViewHolder(final View itemView) {
            super(itemView);

            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);

            // code
            cardView = new CardView(itemView.getContext());
            displayLinear = new RelativeLayout(itemView.getContext());
            displayLinear.setId(R.id.displayLinear);
            optMenu = new ImageView(itemView.getContext());
            optMenu.setId(R.id.optMenu);
            optMenu.setImageResource(R.drawable.ic_more_vert_black_24dp);
            txtTextSeq = new TextView(itemView.getContext());
            txtTextSeq.setId(R.id.txtTextSeq);
            txtTextCon = new TextView(itemView.getContext());
            txtTextCon.setId(R.id.txtText);
            txtTextCon.setTextSize(30);
            txtTextLink = new TextView(itemView.getContext());
            txtTextLink.setId(R.id.txtLink);
            txtTextLink.setLinksClickable(true);
            txtTextLink.setTextColor(Color.BLUE);
            txtTextLink.setTextSize(30);

            image = new ImageView(itemView.getRootView().getContext());
            image.setId(R.id.viewImage);
            viVideo = new VideoView(itemView.getRootView().getContext());
            viVideo.setId(R.id.viewVideo);

            txtTextLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // String url = "http://google.com";
                    String url = txtTextLink.getText().toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    itemView.getRootView().getContext().startActivity(i);
                }
            });

        }
    }

}

