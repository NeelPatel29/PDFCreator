package com.pdfkns.pdfcreator.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfkns.pdfcreator.Adapter.MediaCellAdapter;
import com.pdfkns.pdfcreator.Model.MediaCell;
import com.pdfkns.pdfcreator.Model.MediaCellEnum;
import com.pdfkns.pdfcreator.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    public List<MediaCell> mediaCellList = new ArrayList<>();
    MediaCellAdapter dsRecyclerviewAdapter;
    private Paint p = new Paint();
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static final int MY_MENU_1 = Menu.FIRST;
    private static final int MY_MENU_2 = Menu.FIRST + 1;
    LinearLayout.LayoutParams layoutParams;
    MediaCell mediaCell;
    private File pdfFile;
    private  String PDFFileName;
    Document document;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = new RecyclerView(getApplicationContext());
        linearLayout = new LinearLayout(getApplicationContext());
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        linearLayout.setBackgroundColor(Color.parseColor("#08557E"));
        linearLayout.setLayoutParams(layoutParams);
        setContentView(linearLayout,layoutParams);
        mediaCellList = MediaCell.listAll(MediaCell.class);


        getSupportActionBar().show();


        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        //         ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false) && (permissionStatus.getBoolean(Manifest.permission.READ_EXTERNAL_STORAGE, false))) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                //   ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);

            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.putBoolean(Manifest.permission.READ_EXTERNAL_STORAGE,true);
            // editor.putBoolean(Manifest.permission.MANAGE_DOCUMENTS,true);

            editor.commit();


        }else {
            proceedAfterPermission();
        }




        if (mediaCellList.size() == 0){
            tempData();
            new BackgroundTask().execute();
//            Toast.makeText(getApplicationContext(),"NO Data ",Toast.LENGTH_SHORT).show();
        }else {

            new BackgroundTask().execute();

        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuItem menuItem = menu.add(0, MY_MENU_1 , 1, "ADD").setShortcut('3', 'c');
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.drawable.ic_add_white_24dp);

        MenuItem menuItemPreview = menu.add(0, MY_MENU_2 , 2, "Preview").setShortcut('3', 'c');
        menuItemPreview.setShowAsAction(menuItemPreview.SHOW_AS_ACTION_ALWAYS);
        menuItemPreview.setIcon(R.drawable.ic_visibility_white_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MY_MENU_1) {
            Intent intent = new Intent(getApplicationContext(),AddEditActivity.class);
            intent.putExtra("seq", mediaCellList.size()+1);
            intent.putExtra("type", "Add");
            startActivity(intent);

            return true;
        }
        if (id == MY_MENU_2) {
            Toast.makeText(getApplicationContext(),"Preview",Toast.LENGTH_SHORT).show();
            preview();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
        // Toast.makeText(getBaseContext(), "We got the Storage Permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN){
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return false;
        }



        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if(direction/4 == ItemTouchHelper.LEFT){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long data = mediaCellList.get(position).getId();
                                mediaCell= MediaCell.findById(MediaCell.class, data);
                                mediaCell.delete();
                                mediaCellList.remove(position);
                                recyclerView.setAdapter(dsRecyclerviewAdapter);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recyclerView.setAdapter(dsRecyclerviewAdapter);
                            }
                        });
               AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Alert Dialog");
                alertDialog.show();


            }else{
                Intent intent = new Intent(getApplicationContext(),AddEditActivity.class);
                intent.putExtra("type", "Edit");
                intent.putExtra("idData", mediaCellList.get(position).getId());
                intent.putExtra("url", mediaCellList.get(position).getUrl());
                intent.putExtra("sequence", mediaCellList.get(position).getSequence());
                intent.putExtra("content", mediaCellList.get(position).getTxtContent());
                intent.putExtra("mediaType", mediaCellList.get(position).getMediaCellEnum());
                startActivity(intent);
            }

        }
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mediaCellList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mediaCellList, i, i - 1);
                }
            }
            dsRecyclerviewAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Bitmap icon;
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;
                if(dX > 0){
                    p.setColor(Color.GREEN);
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_edit);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                } else {
                    p.setColor(Color.RED);
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    private class BackgroundTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Fetching Data, please wait.");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            try {
                //Thread.sleep(5000);


                background();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e)
            {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }


    }

    public void background() {
        dsRecyclerviewAdapter = new MediaCellAdapter(mediaCellList, MainActivity.this);
        try {

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 1);

            //linearLayout.setLayoutParams(layoutParams);
            recyclerView.setLayoutParams(layoutParams);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(dsRecyclerviewAdapter);

            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            linearLayout.addView(recyclerView);
            setContentView(linearLayout, layoutParams);
        } catch(Exception e){

        } catch (OutOfMemoryError e ){

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();// call finish() on click of back button
        }
        return super.onKeyDown(keyCode, event);
    }
    public void tempData(){
        MediaCell m = new MediaCell("Now on Tap can anticipate your needs in the moment. With a simple tap, get an instant shortcut to just the right answers at exactly the right time. \n Now on Tap can anticipate your needs in the moment.  With a simple tap, get an instant shortcut to just the right answers at exactly the right time.\n Now on Tap can anticipate your needs in the moment.  With a simple tap, get an instant shortcut to just the right answers at exactly the right time.","Data",1, MediaCellEnum.TEXT);
        mediaCellList.add(m);
        Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.exampleimage);
        String s = path.toString();
        MediaCell m1 = new MediaCell(s,"Nature",2,MediaCellEnum.IMAGE);
        mediaCellList.add(m1);
    }

    public  void preview(){

        if (mediaCellList.size() == 0){

            Toast.makeText(getApplicationContext(),"Nothing to  Preview. ",Toast.LENGTH_SHORT).show();
        }else {

            new PreviewTask().execute();

        }
    }
    private class PreviewTask extends AsyncTask<String, String, String> {
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Previewing Data, please wait.");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            try {
                //Thread.sleep(5000);

                backgroundpreview();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e)
            {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),PreviewActivity.class);
                startActivity(intent);
            }


        }
    }


        public void backgroundpreview() throws FileNotFoundException, DocumentException {

            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/PDFCreator");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
                Log.i("PDF", "Created a new directory for PDF");
            }
            PDFFileName = "Sample" + ".pdf" ;
            pdfFile = new File(docsFolder.getAbsolutePath(),PDFFileName);
            OutputStream output = new FileOutputStream(pdfFile);
            document = new Document();
            ImageView imageView = new ImageView(getApplicationContext());
            PdfWriter.getInstance(document, output);
            document.open();

            for (int i = 0; i< mediaCellList.size(); i++){

                if (mediaCellList.get(i).getMediaCellEnum() == MediaCellEnum.TEXT){

                    //--restore from string--
                    Spanned s = Html.fromHtml(mediaCellList.get(i).getUrl());
                    String  data = s.toString();
                        addTextToPDF(data);

                }else if (mediaCellList.get(i).getMediaCellEnum() == MediaCellEnum.LINK){

                    if (mediaCellList.get(i).getTxtContent().isEmpty()){
                        addLinkToPDF(mediaCellList.get(i).getUrl(), mediaCellList.get(i).getUrl());
                    }else {
                        addLinkToPDF(mediaCellList.get(i).getTxtContent(), mediaCellList.get(i).getUrl());
                    }

                }
                else if (mediaCellList.get(i).getMediaCellEnum() == MediaCellEnum.IMAGE){
                    if (!mediaCellList.get(i).getTxtContent().isEmpty()){
                        addTextToPDF(mediaCellList.get(i).getTxtContent());
                    }

                    Uri uri = Uri.parse(mediaCellList.get(i).getUrl());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),uri);
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytearrayoutputstream);

                        byte[] bytearray = bytearrayoutputstream.toByteArray();

                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytearray,0,bytearray.length);
                        imageView.setImageBitmap(bitmap2);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addImageToPDF(document,imageView);
                }
            }
            document.close();
        }

        public void addTextToPDF(String txtCon) throws DocumentException{
            document.add(new Paragraph(txtCon));
        }
        public  void addLinkToPDF(String txtCon,String txtlink)throws DocumentException {
            Paragraph paragraph = new Paragraph();
            Font blue = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);
            Anchor anchor = new Anchor(txtCon,blue);
            anchor.setReference(txtlink);
            paragraph.add(anchor);
            document.add(paragraph);
        }
        public void addImageToPDF(Document document, ImageView ivPhoto) throws DocumentException {
            try {
                BitmapDrawable drawable = (BitmapDrawable) ivPhoto.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                byte[] imageInByte = stream.toByteArray();
                Image image = Image.getInstance(imageInByte);
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()) - document.rightMargin() - 0 ) / image.getWidth() * 100 ;
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

                document.add(image);
            }
            catch(IOException ex)
            {
                return;
            }
        }
}
