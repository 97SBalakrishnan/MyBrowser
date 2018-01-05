package com.example.balakrishnan.mybrowser;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {
    int backFlag = 0,duplFlag=1,replFlag=0;
    public static SwipeRefreshLayout mySwipeRefreshLayout;
    TextView txt;
    ImageView imageView;
    WebView wv;
    BitmapDrawable drawable;
    AutoCompleteTextView et;
    boolean isOpen = false, isOpen1 = false;
    boolean fl;
    Animation FabOpen, FabClose, FabRC, FabRAC;
    Animation FabOpen1, FabClose1, FabRC1, FabRAC1;
    TextView t, w;
    ArrayList<String> al;
    public static ArrayList<String> hist = new ArrayList<>();

    public static ArrayAdapter<String> adapter;
    public static Context cont;
    public static String dpath;
    public ArrayList<String> FileList;
    public ArrayList<String> DownloadList;
    public void FileListFunction()
    {

        FileList=new ArrayList<>();
        FileList.clear();
        System.out.println("files from "+dpath);
        File f = Environment.getExternalStoragePublicDirectory(dpath);
        if(f.listFiles()==null)
            return;
        System.out.println(dpath);
        File[] g = f.listFiles();

        for(File x:g)
        {

            System.out.println("filer "+x.getAbsoluteFile().getName());
            String fname=x.getAbsoluteFile().getName();
            if(FileList.contains(fname)==false)
            FileList.add(fname);

        }
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        isStoragePermissionGranted();
        backFlag = 0;duplFlag=1;replFlag=0;
        DownloadList = new ArrayList<>();

        dpath="/Download";
        cont = this.getApplicationContext();
        txt = (TextView) findViewById(R.id.txt);
        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        wv.reload();

                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

/*
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, WEBSITES);
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.editText2);
        textView.setAdapter(adapter);
*/
        //new BackgroundTask().execute(getApplicationContext());

        final FloatingActionButton fab_option, fab_refresh, fab_forward, fab_back, fab_more;
        fab_option = (FloatingActionButton) findViewById(R.id.fab_option);
        fab_refresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fab_forward = (FloatingActionButton) findViewById(R.id.fab_forward);
        fab_back = (FloatingActionButton) findViewById(R.id.fab_back);
        fab_more = (FloatingActionButton) findViewById(R.id.fab_more);
        //fab_option.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55000000")));


        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRC = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRAC = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        fab_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.setBackgroundColor(Color.parseColor("#77000000"));
                mySwipeRefreshLayout.bringToFront();
                mySwipeRefreshLayout.invalidate();
                wv.bringToFront();
                wv.invalidate();
                t.setVisibility(View.INVISIBLE);
                w.setVisibility(View.INVISIBLE);

                String url = et.getText().toString().trim();
                if (url.length() == 0)
                    Toast.makeText(getApplicationContext(), "Please Enter URL", Toast.LENGTH_SHORT).show();
                else if (url.contains("http://") || url.contains("https://"))
                    wv.loadUrl(url);
                else if (url.contains("."))
                    wv.loadUrl("http://" + url);
                else
                    wv.loadUrl("http://google.com/search?q=" + url);
                hideSoftKeyboard();
                et.setSelectAllOnFocus(true);


            }
        });
        fab_option.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isOpen) {
                    fab_back.startAnimation(FabClose);
                    fab_forward.startAnimation(FabClose);
                    fab_refresh.startAnimation(FabClose);
                    fab_option.startAnimation(FabRAC);
                    fab_back.setClickable(false);
                    fab_forward.setClickable(false);
                    fab_refresh.setClickable(false);
                    isOpen = false;
                } else if (isOpen == false) {
                    fab_back.startAnimation(FabOpen);
                    fab_forward.startAnimation(FabOpen);
                    fab_refresh.startAnimation(FabOpen);
                    fab_option.startAnimation(FabRC);
                    fab_back.setClickable(true);
                    fab_forward.setClickable(true);
                    fab_refresh.setClickable(true);
                    isOpen = true;
                }
                return true;
            }
        });
        final TextView about = (TextView) findViewById(R.id.about);
        final TextView clear = (TextView) findViewById(R.id.clear);
        FabOpen1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRC1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRAC1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"App made by S Balakrishnan ",Toast.LENGTH_SHORT).show();
            }
        });
        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen1) {
                    about.startAnimation(FabClose1);
                    clear.startAnimation(FabClose1);
                    fab_more.startAnimation(FabRAC1);
                    about.setClickable(false);
                    clear.setClickable(false);
                    isOpen1 = false;
                } else if (isOpen1 == false) {
                    about.startAnimation(FabOpen1);
                    clear.startAnimation(FabOpen1);
                    fab_more.startAnimation(FabRC1);
                    about.bringToFront();
                    about.invalidate();
                    about.setClickable(true);
                    clear.bringToFront();
                    clear.invalidate();
                    clear.setClickable(true);
                    isOpen1 = true;
                }
            }
        });

        fl = false;
        wv = (WebView) findViewById(R.id.main_web_view);
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setMinimumFontSize(10);
        //wv.loadUrl("https://source.unsplash.com/random");
        //wv.loadDataWithBaseURL(null,"<!DOCTYPE html><html><body style = \"text-align:center\"><img style=\"border-style:solid;border-width:5px;border-color:black;width:99%;\" src= https://source.unsplash.com/random alt=\"page Not Found\"></body></html>","text/html", "UTF-8", null);
        imageView = (ImageView) findViewById(R.id.imageView);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                System.out.println("greener1");
                fab_more.setBackgroundColor(getDominantColor(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };


        Picasso.with(this)
                .load("https://source.unsplash.com/random").error(R.drawable.nointernet).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                imageView.getDrawable();
            }

            @Override
            public void onError() {
                imageView.setBackgroundColor(0);System.out.println("error");
            }
        });


        //imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
        // wv.setWebViewClient(new WebViewClient());
        wv.setWebViewClient(new NewWebViewClient());

        et = (AutoCompleteTextView) findViewById(R.id.editText2);


        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {
                    txt.setBackgroundColor(Color.parseColor("#000000"));
                    mySwipeRefreshLayout.bringToFront();
                    mySwipeRefreshLayout.invalidate();
                    wv.bringToFront();
                    wv.invalidate();
                    t.setVisibility(View.INVISIBLE);
                    w.setVisibility(View.INVISIBLE);
                    String url = et.getText().toString().trim();
                    if (url.length() == 0)
                        Toast.makeText(getApplicationContext(), "Please Enter URL", Toast.LENGTH_SHORT).show();
                    else if (url.contains("http://") || url.contains("https://"))
                        wv.loadUrl(url);
                    else if (url.contains("."))
                        wv.loadUrl("http://" + url);
                    else
                        wv.loadUrl("http://google.com/search?q=" + url);
                    hideSoftKeyboard();
                    et.setSelectAllOnFocus(true);

                    handled = true;
                }
                return handled;
            }
        });

        //imageView.setVisibility(View.INVISIBLE);

        w = (TextView) findViewById(R.id.welcome);
        t = (TextView) findViewById(R.id.clock);
        DigitalClock clock = (DigitalClock) findViewById(R.id.dclock);
        clock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                t.setText(editable.toString());
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()) {
                    alertBoxWindow();

                }
                else
                {

                }
            }
        });
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wv.canGoBack())
                    wv.goBack();
            }
        });
        fab_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wv.canGoForward())
                    wv.goForward();
            }
        });
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wv.reload();
            }
        });
        /*et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSoftKeyboard(view);
                if(!et.getText().toString().equals("http://"))
                et.setSelectAllOnFocus(true);
                ArrayList<String> al = new ArrayList<String>();
                for(int x=0;x<WEBSITES1.length;x++)
                {
                    if(WEBSITES1[x].contains(et.getText().toString().trim()))
                    {
                        al.add(WEBSITES1[x]);
                    }
                }
                WEBSITES = al.toArray(new String[0]);
                adapter.notifyDataSetChanged();
                et.showDropDown();
            }
        });


      et.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {

              ArrayList<String> al = new ArrayList<String>();
              for(int x=0;x<WEBSITES1.length;x++)
              {
                  if(WEBSITES1[x].contains(et.getText().toString().trim()))
                  {
                      al.add(WEBSITES1[x]);
                  }
              }
              WEBSITES = al.toArray(new String[0]);
              adapter.notifyDataSetChanged();
              et.showDropDown();
          }
      });
*/
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();

        if (wv.canGoBack()) {
            wv.goBack();
            backFlag = 0;
        } else if (backFlag == 0) {
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            backFlag = 1;
        } else {
            this.onDestroy();
        }
    }

    public void buttonClick(View v) {
        AutoCompleteTextView et = (AutoCompleteTextView) findViewById(R.id.editText2);
        String s = et.getText().toString();
        if (fl == false && s.length() == 0) {

            Picasso.with(this).load("https://source.unsplash.com/random").skipMemoryCache().error(R.drawable.nointernet).into(imageView);
            //imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
        } else if (s.length() != 0) {
            fl = true;
            WebView wv = (WebView) findViewById(R.id.main_web_view);
            imageView.setVisibility(View.INVISIBLE);
            t.setVisibility(View.INVISIBLE);
            w.setVisibility(View.INVISIBLE);
            wv.loadUrl(s);

        }

    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


    }


    public static int getDominantColor(Bitmap bitmap) {

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        System.out.println("greener2" + color);
        return color;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission is granted");
                return true;
            } else {

                System.out.println("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.println("PERMISSION GRANTED!");
            return true;
        }
    }
    class BackgroundTask extends AsyncTask<String, Void, String> {
        int cnt;
        int dcnt;
        int rcnt;
        String urlString;
        String msg;


        @Override
        protected String doInBackground(String... args) {
            urlString = args[0];

            DHandler();
            try {

                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        DIRECTORY_DOWNLOADS).getAbsolutePath());

                getFilesFromDir(downloadDir);
                if(duplFlag==1 ) {
                    if (cnt > 0 && dcnt == 0)
                        msg = "Downloading " + cnt + " files ..";
                    else if (cnt == 0 && dcnt == 0)
                        msg = "No files to download ! ";
                    else if (cnt > 0 && dcnt > 0)
                        msg = "Downloading " + cnt + " files .." + dcnt + " duplicate files excluded ..";
                    else if (dcnt > 0 && cnt == 0)
                        msg = dcnt + "duplicate files .. not downloaded";
                }
                if(duplFlag==0 && replFlag==0)
                {
                    msg= "Downloading "+cnt+" files ..";
                }
                else if(duplFlag==0 && replFlag ==1)
                {
                    if(rcnt>0 && cnt>0)
                    msg = "Downloading "+cnt+" files .."+"Replacing "+rcnt+" files .. ";
                    else if(rcnt>0 && cnt==0)
                        msg="Replacing "+rcnt+" files ..";
                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        public void downloader(String durl, String name, String ext) {


            String url = durl;
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Some description");
            request.setTitle(name);
// in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            CreateDir(dpath);
            request.setDestinationInExternalPublicDir(dpath, name + ext);


// get download service and enqueue file
            DownloadManager manager = (DownloadManager) MainActivity.cont.getSystemService(Context.DOWNLOAD_SERVICE);

            manager.enqueue(request);


        }
        String line;
        public void DHandler() {

            try {
                //Toast.makeText(getApplication(), "HELLO1", Toast.LENGTH_SHORT).show();
                URL url = new URL(urlString);
                BufferedReader reader = null;
                System.out.println("starting");

                try {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    cnt = 0;
                    dcnt=0;
                    rcnt=0;
                    String[] aExt = exts.split(" ");

                    for (; (line = reader.readLine()) != null; ) {


                        for(int z=0;z<aExt.length;z++) {
                            if (line.contains(aExt[z]+"\"") ){

                                int locn = -1;
                                locn = (line.indexOf(aExt[z]));

                                int i = locn;
                                while (line.charAt(i) != '\"' && i >= 0) i--;
                                i++;
                                int j=locn;
                                while(line.charAt(j)!='\"' && j<line.length()) j++;

                                String fileLink=line.substring(i,j);
                                System.out.println(line);

                                if (fileLink.startsWith("http://")||fileLink.startsWith("https://")) {

                                    System.out.println(".!!." + fileLink);
                                    String name = fileLink.substring(fileLink.lastIndexOf("/") + 1, fileLink.length());

                                    System.out.println("filename:" + name);
                                    boolean dflag=false;
                                    if(FileList.contains(name) )
                                    {
                                        if(duplFlag==1 || replFlag==0)
                                            dcnt++;
                                        else if(duplFlag==0 && replFlag==1)
                                            rcnt++;
                                    }
                                    if(FileList.contains(name)&&DownloadList.contains(name)==true)
                                    {
                                        rcnt++;
                                    }
                                    if(duplFlag==1)
                                    {
                                        if(!FileList.contains(name))
                                            dflag=true;
                                        else
                                            dflag=false;
                                    }
                                    else if(duplFlag==0) {

                                        if (replFlag == 0)
                                            dflag = true;
                                        else if(replFlag==1) {
                                            File file = new File(dpath + "/" + name);
                                            System.out.println("!/"+file.getAbsolutePath());
                                            if(FileList.contains(name)&&file.exists()==false)
                                                dflag=false;
                                            else {
                                                System.out.println("" + file.exists() + ".");
                                                file.delete();
                                                if (file.exists()) {
                                                    file.getCanonicalFile().delete();
                                                    if (file.exists()) {
                                                        getApplicationContext().deleteFile(file.getName());
                                                    }
                                                }

                                                System.out.println("" + file.exists());
                                                dflag = true;
                                            }
                                        }
                                    }
                                    if(dflag){
                                    cnt++;

                                    try {
                                        downloader(fileLink, name.substring(0,name.indexOf(".")), aExt[z]);
                                        FileList.add(name);
                                        DownloadList.add(name);
                                    }
                                    catch(Exception e)
                                    {
                                        System.out.println("1!"+e);
                                    }
                                    }

                                }
                            }
                        }
                    }
                    //System.out.println("greener " + cnt);
                } finally {
                    if (reader != null) try {
                        reader.close();
                    } catch (IOException logOrIgnore) {
                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }


    }
    public static String exts = ".pdf .ppt .pptx .PDF .doc .docx";
    EditText vDpath;
    public void alertBoxWindow()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_layout, null);
        dialogBuilder.setView(dialogView);
        FileListFunction();
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        vDpath=(EditText)dialogView.findViewById(R.id.edit2);
        vDpath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dpath=vDpath.getText().toString();

            }
        });
        final Switch cb = (Switch)dialogView.findViewById(R.id.checkBox);
        final Switch cb2 = (Switch)dialogView.findViewById(R.id.checkBox2);

        System.out.println("invoked");
        cb.setChecked(true);
        cb2.setChecked(false);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb.isChecked()==true)
                { duplFlag=1;System.out.println("Checkbox checked!!!");cb2.setEnabled(false);}
                else
                {duplFlag=0;cb2.setEnabled(true);}
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb2.isChecked())
                {
                    replFlag=1;
                }
                else
                {
                    replFlag=0;
                }
            }
        });
        edt.setText(exts);
        dialogBuilder.setTitle("DownloadAll Menu");

        vDpath.setText(dpath);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FileListFunction();
                CreateDir(dpath);
                exts=edt.getText().toString().trim();
                System.out.println("bluer "+exts);
                new BackgroundTask().execute(et.getText().toString().trim());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public void getFilesFromDir(File filesFromSD)
    {

        File listAllFiles[] = filesFromSD.listFiles();

        if (listAllFiles != null && listAllFiles.length > 0) {
            for (File currentFile : listAllFiles) {
                if (currentFile.isDirectory()) {
                    getFilesFromDir(currentFile);
                } else {
                    if (currentFile.getName().endsWith("")) {
                        // File absolute path
                        Log.e("File path", currentFile.getAbsolutePath());
                        // File Name
                        Log.e("File path", currentFile.getName());

                    }
                }
            }
        }
    }
    public void CreateDir(String s)
    {
        File dir = new File(s);
        if(dir.exists())
        {
            System.out.println("Directory "+s+" exists");
        }
        else {
            try {
                if (dir.mkdir()) {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory is not created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dpath=dir.getPath();
    }


}