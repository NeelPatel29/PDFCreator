package com.pdfkns.pdfcreator.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.LinearLayout;

import com.pdfkns.pdfcreator.Adapter.MediaCellAdapter;
import com.pdfkns.pdfcreator.Model.MediaCell;
import com.pdfkns.pdfcreator.Model.MediaCellEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by krushitpatel on 2017-06-29.
 */

public class GuideThird extends AppCompatActivity {

    private static final int MY_MENU_1 = Menu.FIRST;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams;

    private Paint p = new Paint();
    public List<MediaCell> sanDSList = new ArrayList<>();
    MediaCellAdapter dsRecyclerviewAdapter;
    TourGuide mTutorialHandler;
    Activity mActivity;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        recyclerView = new RecyclerView(getApplicationContext());
        linearLayout = new LinearLayout(getApplicationContext());
        btn = new Button(getApplicationContext());

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        linearLayout.setBackgroundColor(Color.parseColor("#08557E"));

        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        // Checking for first time launch - before calling setContentView()



        getData();
        mTutorialHandler = TourGuide.init(mActivity).with(TourGuide.Technique.VERTICAL_UPWARD)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip()
                        .setTitle("Change order of Item ")
                        .setDescription("Hold and Drag Item to Up side")
                        .setGravity(Gravity.BOTTOM))
                .playOn(recyclerView)
                .setOverlay(new Overlay()
                        .setEnterAnimation(enterAnimation)
                        .setExitAnimation(exitAnimation)
                );


        // recyclerView = (RecyclerView) findViewById(R.id.sampleRecyclerview);
        dsRecyclerviewAdapter = new MediaCellAdapter(sanDSList, getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(GuideThird.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dsRecyclerviewAdapter);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(500, 500, 0, 0);
        btn.setText("Let's Get Started");
        btn.setLayoutParams(buttonLayoutParams);

        linearLayout.addView(recyclerView);
        linearLayout.addView(btn);
        setContentView(linearLayout,layoutParams);

btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(GuideThird.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
});
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

        MediaCell mediaCell1 = new MediaCell("Sample 1", "Sample1 ", 1, MediaCellEnum.TEXT);
        sanDSList.add(mediaCell1);
        MediaCell mediaCell2 = new MediaCell("Sample 2", "Sample2 ", 2, MediaCellEnum.TEXT);
        sanDSList.add(mediaCell2);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.UP | ItemTouchHelper.DOWN){
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

        }
       public boolean onItemMove(int fromPosition, int toPosition) {
            mTutorialHandler.cleanUp();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(sanDSList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(sanDSList, i, i - 1);
                }
            }
            dsRecyclerviewAdapter.notifyItemMoved(fromPosition, toPosition);

            return true;

        }


    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
