package com.pdfkns.pdfcreator.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.pdfkns.pdfcreator.R;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class PreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    private static final String TAG = PreviewActivity.class.getSimpleName();

     File file = new File(Environment.getExternalStorageDirectory() + "/PDFCreator/Sample.pdf");
    PDFView pdfView;
    private static final int MY_MENU_1 = Menu.FIRST;
    private static final int MY_MENU_2 = Menu.FIRST + 1;
    private static final int MY_MENU_3 = Menu.FIRST + 3;
    Integer pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewactivity);
        getSupportActionBar().setTitle("PDF View");

        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

@Override
    public void onPageChanged(int page, int pageCount) {
    pageNumber = page;
    setTitle(String.format("%s %s / %s", file, page + 1, pageCount));
}
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//            PDFView.Configurator.onRender(new OnRenderListener() {
//                @Override
//                public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
//                    pdfView.fitToWidth(); // optionally pass page number
//                }
//            });
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuItem menuItemSave = menu.add(0, MY_MENU_1 , 1, "Save").setShortcut('3', 'c');
        menuItemSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemSave.setIcon(android.R.drawable.ic_menu_save);

        MenuItem menuItemPreview = menu.add(0, MY_MENU_2 , 2, "Cancle").setShortcut('3', 'c');
        menuItemPreview.setShowAsAction(menuItemPreview.SHOW_AS_ACTION_ALWAYS);
        menuItemPreview.setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        MenuItem menuItemShare = menu.add(0, MY_MENU_3 , 3, "Share").setShortcut('3', 'c');
        menuItemShare.setShowAsAction(menuItemShare.SHOW_AS_ACTION_ALWAYS);
        menuItemShare.setIcon(android.R.drawable.ic_menu_share);

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
            Toast.makeText(getApplicationContext(),"Files Saved....",Toast.LENGTH_SHORT).show();

            File dir = new File(Environment.getExternalStorageDirectory() + "/PDFCreator/");
            if(dir.exists()){
                File from = new File(dir,"Sample.pdf");
                File to = new File(dir,"newname.pdf");
                if(from.exists())
                    from.renameTo(to);
            }

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == MY_MENU_2) {
            Toast.makeText(getApplicationContext(),"Cancle",Toast.LENGTH_SHORT).show();
            file.delete();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == MY_MENU_3) {
            Toast.makeText(getApplicationContext(),"Share",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
