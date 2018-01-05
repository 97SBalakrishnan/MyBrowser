package com.example.balakrishnan.mybrowser;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.balakrishnan.mybrowser.MainActivity.adapter;
import static com.example.balakrishnan.mybrowser.MainActivity.dpath;
import static com.example.balakrishnan.mybrowser.MainActivity.hist;
import static com.example.balakrishnan.mybrowser.MainActivity.mySwipeRefreshLayout;

/**
 * Created by balakrishnan on 24/11/17.
 */

public class NewWebViewClient extends WebViewClient {

    View mainView;
    @Override
    public boolean shouldOverrideUrlLoading(WebView wv, String url) {
        //Toast.makeText(wv.getContext(),url,LENGTH_LONG).show();
        System.out.println("greener " + url);
        mainView = wv.getRootView();
        EditText et = (EditText) mainView.findViewById(R.id.editText2);
       int flag=0;
        String line = url;
       System.out.println("xx"+url);
        String[] aExt = MainActivity.exts.split(" ");

        for (int z = 0; z < aExt.length; z++) {
            if (line.contains(aExt[z])) {

                int locn = -1;
                locn = (line.indexOf(aExt[z]));

                int i;
                i=0;
                String fileLink = null;
                if (line.substring(i,url.length()).startsWith("http://")||line.substring(i,url.length()).startsWith("https://")) {
                    fileLink = line.substring(i,url.length());
                    System.out.println(".!." + fileLink);
                    String name = fileLink.substring(fileLink.lastIndexOf("/") + 1, fileLink.indexOf(".pdf"));
                    System.out.println("filename:" + name);
                    {//Toast.makeText(mainView.getContext(),"Downloading "+name,Toast.LENGTH_SHORT).show();
                        flag = 1;
                        new BackgroundTask().execute(fileLink, name, aExt[z]);
                    }
                }
            }
            if(flag==0)
                et.setText(url);
            else
                return true;
        }
            if (URLUtil.isValidUrl(url)) {
                System.out.println("greener " + url + " is valid");
                return false;
            } else
                System.out.println("greener " + url + " is invalid");
            //Toast.makeText(wv.getContext(),"InvalidURL",LENGTH_LONG).show();
       /* Intent intent = new Intent(Intent.ACTION_

       VIEW,Uri.parse(url));
        wv.getContext().startActivity(intent);*/
            return true;

        }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mySwipeRefreshLayout.setRefreshing(true);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mySwipeRefreshLayout.setRefreshing(false);
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        //Toast.makeText(mainView.getContext(),"Error"+error,Toast.LENGTH_SHORT).show();

        super.onReceivedError(view, request, error);
        System.out.println("greener  error occured");
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
    class BackgroundTask extends AsyncTask<String, Void, String> {
        String dirpath=dpath;
        @Override
        protected String doInBackground(String... strings) {
            String fileLink = strings[0];
            String fileName= strings[1];
            String extension=strings[2];


            String url = fileLink;
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Some description");
            request.setTitle(fileName);
// in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            request.setDestinationInExternalPublicDir(dirpath.substring(dirpath.lastIndexOf("/"),dirpath.length()), fileName + extension);


// get download service and enqueue file
            DownloadManager manager = (DownloadManager) MainActivity.cont.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            return null;
        }
    }
}

