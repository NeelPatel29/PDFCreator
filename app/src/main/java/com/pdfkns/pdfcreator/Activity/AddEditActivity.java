package com.pdfkns.pdfcreator.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pdfkns.pdfcreator.Model.MediaCell;
import com.pdfkns.pdfcreator.Model.MediaCellEnum;
import com.pdfkns.pdfcreator.R;
import com.pdfkns.pdfcreator.Tools.ColorPicker;
import com.pdfkns.pdfcreator.Tools.NumberIndentSpan;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sanjaypatel on 2017-07-20.
 */

public class AddEditActivity extends AppCompatActivity implements
        ColorPicker.OnColorChangedListener {
    TextView txtSeq;
    EditText etCon, etUrl;
    Spinner spType;
    Button btnLoad;
    CardView cardView;
    LinearLayout linearLayout, linearLayoutMain;
    VideoView videoView;
    ImageView imageView;
    TextInputLayout txtInputCon, txtInputUrl;
    private ActionMode actionMode = null;
    LinearLayout.LayoutParams layoutParams;
    private static final int MY_MENU_1 = Menu.FIRST;
    private static final int MY_MENU_2 = Menu.FIRST + 1;
    MediaCell mediaCell;
    Boolean isValidURL = false;
    SpannableStringBuilder stringBuilder;
    private Paint mPaint;
    ImageButton ibCopy,ibPaste,ibCut,ibColor, ibBold, ibItalic, ibUnderline, ibBullets,ibNumber;
    HorizontalScrollView horizontalScrollView;
    LinearLayout bottomPanle;
    MediaCellEnum mediaCellEnum ;
    int mPickedColor = Color.WHITE;
    int selectionStart;
    int selectionEnd;
    int numindex = 0;
    Bundle bundle;
    String funcType;
    String idData,contentData,urlData,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        funcType = bundle.getString("type");
        MainDisplay();




        //Set data Based On Add or Edit
        if (funcType.equals("Add")){
            getSupportActionBar().setTitle("Add");
            txtSeq.setText(String.valueOf(bundle.getInt("seq")));
            txtSeq.setTextSize(25);

        }else if (funcType.equals("Edit")){
            spType.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Edit");
            idData = String.valueOf(bundle.getLong("idData", 0));
            contentData = bundle.getString("content");
            url = bundle.getString("url");
            int sequence = bundle.getInt("sequence", 1);
            mediaCellEnum = (MediaCellEnum) bundle.getSerializable("mediaType");

            txtSeq.setText(String.valueOf(sequence));
            txtSeq.setTextSize(25);
            if (mediaCellEnum.toString() == "TEXT") {
                spType.setSelection(0);
                txtInputCon.setHint("Enter Content");
                txtInputUrl.setHint("Enter Content");
                etCon.setText(contentData);
                etUrl.setVisibility(View.VISIBLE);
                Spanned data = Html.fromHtml(url);
                etUrl.setText(data);
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                btnLoad.setVisibility(View.GONE);
            } else if (mediaCellEnum.toString() == "LINK") {
                spType.setSelection(1);
                txtInputCon.setHint("Enter Content");
                txtInputUrl.setHint("Enter URL");

                etCon.setText(contentData);
                etUrl.setText(url);
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                btnLoad.setVisibility(View.GONE);
            } else if (mediaCellEnum.toString() == "IMAGE") {
                spType.setSelection(2);
                txtInputCon.setHint("Enter Content");
                etCon.setText(contentData);
                etUrl.setText(url);
                btnLoad.setText("Load Image");
                imageView.setImageURI(Uri.parse(url));
                imageView.setVisibility(View.VISIBLE);
                etCon.setVisibility(View.VISIBLE);
                txtInputCon.setVisibility(View.VISIBLE);
                txtInputUrl.setVisibility(View.GONE);
                etUrl.setVisibility(View.GONE);
                etUrl.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
            } else if (mediaCellEnum.toString() == "VIDEO") {
                spType.setSelection(3);
                txtInputCon.setHint("Enter Content");
                etCon.setText(contentData);
                etUrl.setText(url);
                btnLoad.setText("Load Video");
                videoView.setVideoURI(Uri.parse(url));
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
                imageView.setVisibility(View.GONE);
                etUrl.setVisibility(View.GONE);
            }


        }




        //Load Button Click Handler
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (spType.getSelectedItemPosition() == 2) {
                    Toast.makeText(getApplicationContext(), "Image", Toast.LENGTH_SHORT).show();
                    Intent intent_upload = new Intent(Intent.ACTION_PICK);
                    intent_upload.setType("image/*");
                    startActivityForResult(intent_upload, 1);


                } else if (spType.getSelectedItemPosition() == 3) {
                    Toast.makeText(getApplicationContext(), "Video", Toast.LENGTH_SHORT).show();
                    Intent intent_upload = new Intent(Intent.ACTION_PICK);
                    intent_upload.setType("video/*");
                    startActivityForResult(intent_upload, 2);
                }
            }
        });


        //Spinner Item Change Handler
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //horizontalScrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Text", Toast.LENGTH_SHORT).show();
                    txtInputCon.setVisibility(View.GONE);
                    txtInputCon.setHint("Enter Content");
                    txtInputUrl.setHint("Enter Content");
                    etUrl.setVisibility(View.VISIBLE);
                    btnLoad.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.INVISIBLE);

                } else if (position == 1) {
                    horizontalScrollView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Link", Toast.LENGTH_SHORT).show();
                    txtInputCon.setVisibility(View.VISIBLE);
                    txtInputCon.setHint("Enter Content");
                    txtInputUrl.setHint("Enter Link");
                    etCon.setVisibility(View.VISIBLE);
                    etUrl.setVisibility(View.VISIBLE);
                    btnLoad.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.INVISIBLE);

                }
                if (position == 2) {
                    horizontalScrollView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Image", Toast.LENGTH_SHORT).show();
                    txtInputCon.setVisibility(View.VISIBLE);
                    txtInputCon.setHint("Enter Content");
                    etCon.setVisibility(View.VISIBLE);
                    etUrl.setVisibility(View.GONE);
                    btnLoad.setVisibility(View.VISIBLE);
                    btnLoad.setText("Load Image");
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                    imageView.setPadding(50, 50, 50, 50);

                } else if (position == 3) {
                    horizontalScrollView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Video", Toast.LENGTH_SHORT).show();
                    txtInputCon.setVisibility(View.VISIBLE);
                    txtInputCon.setHint("Enter Content");
                    etCon.setVisibility(View.VISIBLE);
                    txtInputUrl.setVisibility(View.GONE);
                    etUrl.setVisibility(View.GONE);
                    btnLoad.setVisibility(View.VISIBLE);
                    btnLoad.setText("Load Video");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //Hide tool Pannel
        horizontalScrollView.setVisibility(View.GONE);


        etUrl.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                horizontalScrollView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        //Handle ToolBar Item Click ...
        ibCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("copy");

            }
        });
        ibPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("paste");
            }
        });
        ibCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("cut");
            }
        });
        ibColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("colorText");
            }
        });
        ibBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("bold");
            }
        });
        ibItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("italic");
            }
        });
        ibUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("underlineText");
            }
        });
        ibBullets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("bullets");
            }
        });
        ibNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextMenuItemClicked("number");
            }
        });

    }

    //Mainlayout Code

    public void MainDisplay(){
        linearLayoutMain = new LinearLayout(getApplicationContext());
        linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        cardView = new CardView(getApplicationContext());
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        cardView.setPadding(50, 50, 50, 50);
        linearLayout.setPadding(25, 25, 25, 25);
        linearLayoutMain.setPadding(25, 25, 25, 25);
        linearLayoutMain.setBackgroundColor(Color.parseColor("#08557E"));
        cardView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        cardView.setCardElevation(25f);

        videoView = new VideoView(getApplicationContext());
        imageView = new ImageView(getApplicationContext());

        linearLayoutMain.setLayoutParams(layoutParams);
        linearLayout.setLayoutParams(layoutParams);
        cardView.setLayoutParams(layoutParams);


        txtSeq = new TextView(getApplicationContext());
        txtInputCon = new TextInputLayout(this);
        txtInputUrl = new TextInputLayout(this);
        etCon = new EditText(getApplicationContext());
        etUrl = new EditText(getApplicationContext());

        btnLoad = new Button(getApplicationContext());
        spType = new Spinner(getApplicationContext());


        txtSeq.setId(R.id.txtTextSeq);
        etCon.setId(R.id.etTEXT);
        etUrl.setId(R.id.etURL);
        btnLoad.setId(R.id.btnLoad);
        spType.setId(R.id.spType);
        imageView.setId(R.id.etIMAGE);
        videoView.setId(R.id.etVIDEO);
        spType.setBackgroundResource(R.drawable.custom_spinner);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);

        spType.setPopupBackgroundResource(R.drawable.custom_popup_spinner);
        spType.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 100));


        String[] Media_Type = {"TEXT", "LINK", "IMAGE", "VIDEO"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, Media_Type);

        spType.setAdapter(arrayAdapter);
        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        editTextView();
        addChild();





    }

    //Panel code
    private void editTextView() {
        //Bottompanle code
        ibCopy = new ImageButton(getApplicationContext());
        ibPaste = new ImageButton(getApplicationContext());
        ibCut = new ImageButton(getApplicationContext());
        ibColor = new ImageButton(getApplicationContext());
        ibBold = new ImageButton(getApplicationContext());
        ibItalic = new ImageButton(getApplicationContext());
        ibUnderline = new ImageButton(getApplicationContext());
        ibBullets = new ImageButton(getApplicationContext());
        ibNumber = new ImageButton(getApplicationContext());

        bottomPanle = new LinearLayout(getApplicationContext());

        horizontalScrollView = new HorizontalScrollView(getApplicationContext());

        horizontalScrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        horizontalScrollView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        bottomPanle.setBackgroundColor(Color.parseColor("#08557E"));

        ibCopy.setId(R.id.ibCopy);
        ibPaste.setId(R.id.ibPaste);
        ibCut.setId(R.id.ibCut);
        ibColor.setId(R.id.ibColor);
        ibBold.setId(R.id.ibBold);
        ibItalic.setId(R.id.ibItalic);
        ibUnderline.setId(R.id.ibUnderline);
        ibBullets.setId(R.id.ibBullets);
        ibNumber.setId(R.id.ibNumber);
        bottomPanle.setId(R.id.bottomPanle);

        ibCopy.setImageResource(R.drawable.ic_content_copy_black_24dp);
        ibPaste.setImageResource(R.drawable.ic_content_paste_black_24dp);
        ibCut.setImageResource(R.drawable.ic_content_cut_black_24dp);
        ibColor.setImageResource(R.drawable.ic_format_color_text_black_24dp);
        ibBold.setImageResource(R.drawable.ic_format_bold_black_24dp);
        ibItalic.setImageResource(R.drawable.ic_format_italic_black_24dp);
        ibUnderline.setImageResource(R.drawable.ic_format_underlined_black_24dp);
        ibBullets.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
        ibNumber.setImageResource(R.drawable.ic_format_list_numbered_black_24dp);


        bottomPanle.setOrientation(LinearLayout.HORIZONTAL);
        bottomPanle.setGravity(Gravity.TOP);

        bottomPanle.addView(ibCut);
        bottomPanle.addView(ibCopy);
        bottomPanle.addView(ibPaste);
        bottomPanle.addView(ibColor);
        bottomPanle.addView(ibBold);
        bottomPanle.addView(ibItalic);
        bottomPanle.addView(ibUnderline);
        bottomPanle.addView(ibBullets);
        bottomPanle.addView(ibNumber);
        horizontalScrollView.addView(bottomPanle);

    }

    private void removeChild() {
        linearLayout.removeAllViews();
    }


    //Add main  and panel to activity
    private void addChild() {
        linearLayout.addView(horizontalScrollView);
        linearLayout.addView(txtSeq);
        linearLayout.addView(spType);
        txtInputCon.addView(etCon);
        linearLayout.addView(txtInputCon);
        txtInputUrl.addView(etUrl);
        linearLayout.addView(txtInputUrl);
        linearLayout.addView(btnLoad);
        linearLayout.addView(imageView);
        linearLayout.addView(videoView);
        cardView.addView(linearLayout);
        linearLayoutMain.addView(cardView);
        setContentView(linearLayoutMain, layoutParams);
    }

    //menuitem create

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (funcType.equals("Add")){
            MenuItem menuItem = menu.add(0, MY_MENU_1, 0, "ADD");
            MenuItem menuItem2 = menu.add(0, MY_MENU_2, 0, "Cancel");
            menuItem.setIcon(android.R.drawable.ic_menu_save);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuItem2.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
            menuItem2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }else if (funcType.equals("Edit")){
            MenuItem menuItem = menu.add(0, MY_MENU_1 , 0, "Update");
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuItem.setIcon(android.R.drawable.ic_menu_save);
            return true;
        }else {

            return true;
        }


    }

    //menu item click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (funcType.equals("Add")){

            switch (item.getItemId()) {
                case MY_MENU_1:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    AddMediaCell();
                    Toast.makeText(getApplicationContext(), "Save..", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    return true;

                case MY_MENU_2:
                    Toast.makeText(getApplicationContext(), "Cancle..", Toast.LENGTH_SHORT).show();
                    Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentMain);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }

        }
        if (funcType.equals("Edit")){
            switch (item.getItemId()){
                case MY_MENU_1:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    updateMediaCell();
                    startActivity(intent);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);

    }


    //loadbutton ok handler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                try {

                    videoView.setVisibility(View.GONE);
                    String st = data.getData().toString();
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(data.getData());
                    etUrl.setText(st);
                } catch (OutOfMemoryError e) {
                    etCon.setError("Please select image");
                } catch (Exception e) {

                }

            }
        } else if (requestCode == 2) {

            if (resultCode == RESULT_OK) {
                try {
                    imageView.setVisibility(View.GONE);
                    String st = data.getData().toString();
                    videoView.setVisibility(View.VISIBLE);
                    //  linearLayout.addView(videoView,layoutParams);
                    videoView.setVideoURI(data.getData());
                    videoView.start();
                    etUrl.setText(st);
                } catch (OutOfMemoryError e) {
                    etCon.setError("Please select image");

                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

        if(funcType.equals("Edit")){
            updateMediaCell();
            startActivity(intent);

        }else {
            startActivity(intent);
        }


    }

    //add data to sugarORM
    public void AddMediaCell(){
        if (spType.getSelectedItem().toString().equals("TEXT")) {
            //--save to string--
            Editable s = etUrl.getText();
            String data = Html.toHtml(s);
            mediaCell = new MediaCell(data, etCon.getText().toString(), Integer.parseInt(txtSeq.getText().toString()), MediaCellEnum.TEXT);
            mediaCell.save();
        } else if (spType.getSelectedItem().toString().equals("LINK")) {
            new Connection().execute();
            mediaCell = new MediaCell(etUrl.getText().toString(), etCon.getText().toString(), Integer.parseInt(txtSeq.getText().toString()), MediaCellEnum.LINK);
        } else if (spType.getSelectedItem().toString().equals("IMAGE")) {
            mediaCell = new MediaCell(etUrl.getText().toString(), etCon.getText().toString(), Integer.parseInt(txtSeq.getText().toString()), MediaCellEnum.IMAGE);
            mediaCell.save();
        } else if (spType.getSelectedItem().toString().equals("VIDEO")) {
            mediaCell = new MediaCell(etUrl.getText().toString(), etCon.getText().toString(), Integer.parseInt(txtSeq.getText().toString()), MediaCellEnum.VIDEO);
            mediaCell.save();
        } else {
            mediaCell = new MediaCell(etUrl.getText().toString(), etCon.getText().toString(), Integer.parseInt(txtSeq.getText().toString()), MediaCellEnum.TEXT);
            mediaCell.save();

        }

    }
    //Update data to sugarORM
    public void updateMediaCell(){
        String editTextData = etCon.getText().toString();
        Editable s = this.etUrl.getText();
        String data = Html.toHtml(s);
        if (mediaCellEnum.toString() == "TEXT"){
            long l = Long.parseLong(idData);
            MediaCell mediaCell = MediaCell.findById(MediaCell.class, l);
            mediaCell.setTxtContent(editTextData);
            mediaCell.setUrl(data);
            mediaCell.save();

        }else if (mediaCellEnum.toString() == "LINK"){
            long l = Long.parseLong(idData);
            MediaCell mediaCell = MediaCell.findById(MediaCell.class, l);
            mediaCell.setUrl(this.etUrl.getText().toString());
            mediaCell.setTxtContent(editTextData);
            mediaCell.save();

        }else if(mediaCellEnum.toString() == "IMAGE"){
            long l = Long.parseLong(idData);
            MediaCell mediaCell = MediaCell.findById(MediaCell.class, l);
            mediaCell.setUrl(this.etUrl.getText().toString());
            mediaCell.setTxtContent(editTextData);
            mediaCell.save();
        }else if(mediaCellEnum.toString() == "VIDEO"){

            long l = Long.parseLong(idData);
            MediaCell mediaCell = MediaCell.findById(MediaCell.class, l);
            mediaCell.setUrl(this.etUrl.getText().toString());
            mediaCell.setTxtContent(editTextData);
            mediaCell.save();
        }

    }


    //Panel effects code
    public boolean EditTextMenuItemClicked(String s) {
        selectionStart = etUrl.getSelectionStart();
        selectionEnd = etUrl.getSelectionEnd();
        int diff = selectionEnd - selectionStart;
        stringBuilder = (SpannableStringBuilder) etUrl.getText();
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        switch (s) {
            case "copy":
                numindex=0;
                Toast.makeText(getApplicationContext(),"copy",Toast.LENGTH_LONG).show();
                ClipData clip = ClipData.newPlainText(etUrl.toString().substring(selectionStart, selectionEnd), etUrl.getText().toString().substring(selectionStart, selectionEnd));
                clipboard.setPrimaryClip(clip);
                break;

            case "paste":
                numindex=0;
                Toast.makeText(getApplicationContext(),"paste",Toast.LENGTH_LONG).show();
                ClipData clipData = clipboard.getPrimaryClip();
                try {
                    int curPos = etUrl.getSelectionStart();
                    String textToPaste = clipData.getItemAt(0).getText().toString();
                    String oldText = etUrl.getText().toString();
                    String textBeforeCursor = oldText.substring(0, curPos);
                    String textAfterCursor = oldText.substring(curPos);
                    String newText = textBeforeCursor + textToPaste + textAfterCursor;
                    etUrl.setText(newText);
                } catch (NullPointerException e) {

                }

                break;
            case "cut":
                numindex=0;
                Toast.makeText(getApplicationContext(),"cut",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                ClipData clip1 = ClipData.newPlainText(etUrl.toString().substring(selectionStart, selectionEnd), etUrl.getText().toString().substring(selectionStart, selectionEnd));
                clipboard.setPrimaryClip(clip1);
                try {
                    int curPos = etUrl.getSelectionStart();
                    String textToPaste = clip1.getItemAt(0).getText().toString();
                    String oldText = etUrl.getText().toString();
                    String textBeforeCursor = oldText.substring(0, selectionStart);
                    String textAfterCursor = oldText.substring(curPos);
                    String newText = textBeforeCursor + textAfterCursor;
                    etUrl.setText(newText);
                } catch (NullPointerException e) {

                }
                break;

            case "bold":
                numindex=0;
                Toast.makeText(getApplicationContext(),"bold",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, 0);
                break;
            case "italic":
                numindex=0;
                Toast.makeText(getApplicationContext(),"italic",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                stringBuilder.setSpan(new StyleSpan(Typeface.ITALIC), selectionStart, selectionEnd, 0);
                break;
            case "underlineText":
                numindex=0;
                Toast.makeText(getApplicationContext(),"underlinetext",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                stringBuilder.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, 0);
                break;
            case "colorText":
                numindex=0;
                Toast.makeText(getApplicationContext(),"color",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                new ColorPicker(this, this, mPaint.getColor()).show();
                break;
            case "bullets":
                numindex=0;
                Toast.makeText(getApplicationContext(),"bullet",Toast.LENGTH_LONG).show();
                stringBuilder.append("\r");
                stringBuilder.setSpan(new BulletSpan(BulletSpan.STANDARD_GAP_WIDTH,Color.BLUE), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                etUrl.setText(stringBuilder);
                break;
            case "number":
                Toast.makeText(getApplicationContext(),"number",Toast.LENGTH_LONG).show();
                numindex++;
                stringBuilder.append("\r");
                stringBuilder.setSpan(new NumberIndentSpan(30,30,numindex), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                etUrl.setText(stringBuilder);
                break;
            default:
                break;
        }
        if (actionMode != null) {
            actionMode.finish();
        }
        return true;
    }
    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        mPickedColor = color;
        stringBuilder.setSpan(new ForegroundColorSpan(mPickedColor), selectionStart, selectionEnd, 0);


    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        actionMode = null;
        super.onActionModeFinished(mode);
    }



    //Check Valid URL...

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            isConnectedToServer();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (isValidURL) {
                mediaCell.save();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                etUrl.setError("Please enter valid URL");
            }
        }
    }

    public boolean isConnectedToServer() {
        try {
            URL myUrl = new URL(etUrl.getText().toString());
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(3500);
            connection.connect();
            isValidURL = true;
            Log.d("HTTPCLIENT", "SUCCCESS");

//            Toast.makeText(getApplicationContext(),"Save..",Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            isValidURL = false;
            Log.d("HTTPCLIENT", e.getLocalizedMessage());
        }
        return true;
    }




}
