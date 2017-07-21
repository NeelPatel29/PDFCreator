package com.pdfkns.pdfcreator.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.pdfkns.pdfcreator.Adapter.MediaCellAdapter;
import com.pdfkns.pdfcreator.Model.MediaCell;
import com.pdfkns.pdfcreator.Model.MediaCellEnum;
import com.pdfkns.pdfcreator.Tools.PrefManager;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class Guide extends AppCompatActivity {

    private static final int MY_MENU_1 = Menu.FIRST;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams;
    private PrefManager prefManager;
    private Paint p = new Paint();
    public List<MediaCell> sanDSList = new ArrayList<>();
    MediaCellAdapter dsRecyclerviewAdapter;
    TourGuide mTutorialHandler;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        recyclerView = new RecyclerView(getApplicationContext());
        linearLayout = new LinearLayout(getApplicationContext());

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        linearLayout.setBackgroundColor(Color.parseColor("#08557E"));


        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
       if (prefManager.isFirstTimeLaunch()) {


            getData();
           mTutorialHandler = TourGuide.init(mActivity).with(TourGuide.Technique.HORIZONTAL_LEFT)
                   .setPointer(new Pointer())
                   .setToolTip(new ToolTip()
                           .setTitle("Delete Item ")
                           .setDescription("Swipe LEFT")
                           .setGravity(Gravity.BOTTOM))
                   .playOn(recyclerView)
                   .setOverlay(new Overlay()
                           .setEnterAnimation(enterAnimation)
                           .setExitAnimation(exitAnimation));


           // recyclerView = (RecyclerView) findViewById(R.id.sampleRecyclerview);
            dsRecyclerviewAdapter = new MediaCellAdapter(sanDSList, getApplicationContext());

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Guide.this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(dsRecyclerviewAdapter);
             linearLayout.addView(recyclerView);
           setContentView(linearLayout,layoutParams);

        } else {
            prefManager.setFirstTimeLaunch(false);
            Intent intent = new Intent(Guide.this, MainActivity.class);
            startActivity(intent);
           finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem menuItem = menu.add(0, MY_MENU_1, 0, "ADD").setShortcut('3', 'c');
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(android.R.drawable.ic_menu_add);
        return true;
    }


    public void getData() {
       prefManager.setFirstTimeLaunch(false);
        MediaCell mediaCell1 = new MediaCell("Sample 1", "Sample1 ", 1, MediaCellEnum.TEXT);
        sanDSList.add(mediaCell1);
       /* MediaCell mediaCell2 = new MediaCell("Sample 2", "Sample2 ", 2, MediaCellEnum.TEXT);
        mediaCellList.add(mediaCell2);*/

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            return false;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Bitmap icon;
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;
                if(dX > 0){

                    p.setColor(Color.parseColor("#388E3C"));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_edit);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);

                } else {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                    mTutorialHandler.cleanUp();
                    Intent intent = new Intent(Guide.this, GuideSecond.class);
                    startActivity(intent);
                    finish();

                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


}
