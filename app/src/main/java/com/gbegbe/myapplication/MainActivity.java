package com.gbegbe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog prg;
    private WebView webView;
    private int counter = 0;
    SwipeRefreshLayout refreshLayout;


    String remarks, androidId, address1;
    LocationManager locationManager;
    private final int FINE_LOCATION_PERMISSION = 9999;
    Double postlatitude = 0.00;
    Double postlongitude = 0.00;
    String locationname = "";
    /*  private FirebaseRemoteConfig mFirebaseRemoteConfig;*/


    String English_URL = "https://www.gbegbe.com/en";
    String Bangla_URL = "https://www.gbegbe.com/bg";
    public static final String Login_Post_En_URL = "https://www.gbegbe.com/en";
    public static final String Login_Post_Bn_URL = "https://www.gbegbe.com/bg";

    ProgressDialog progressDialog;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        androidId = getIntent().getStringExtra("AndroidId");
        address1 = getIntent().getStringExtra("Location");

        remarks="GbeGbe.com";
        postlatitude = getIntent().getDoubleExtra("postlatitude",0);
        postlongitude = getIntent().getDoubleExtra("postlongitude",0);

        webView = (WebView) findViewById(R.id.mainWebView);
        refreshLayout = findViewById(R.id.swipe);
        webView.requestFocus();
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.flush();


        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getPath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);


        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
          webView.getSettings().setDomStorageEnabled(true);
        /*webView.getSettings().setBlockNetworkLoads(true);*/
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setUseWideViewPort(true);
     /*   webView.getSettings().setSavePassword(true);*/
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setEnableSmoothTransition(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }

        });


        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_network_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            Button btTryAgain = dialog.findViewById(R.id.bt_try_again);

            btTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            dialog.show();
        } else {

            //URL gir "....."
            getWebview("https://www.gbegbe.com/bg");
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                webView.reload();
                refreshLayout.setRefreshing(false);
            }
        });


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        HashMap<String, Object> defaultsRate = new HashMap<>();
        defaultsRate.put("new_GbeGbe_version_code", String.valueOf(getVersionCode()));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10) // change to 3600 on published app
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultsRate);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    final String new_GbeGbe_version_code = mFirebaseRemoteConfig.getString("new_GbeGbe_version_code");

                    if(Integer.parseInt(new_GbeGbe_version_code) > getVersionCode())
                        showTheDialog("com.gbegbe.myapplication", new_GbeGbe_version_code );
                }
                else Log.e("MYLOG", "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful");

            }
        });

    }

    private void showTheDialog(final String appPackageName, String versionFromRemoteConfig){
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("আপডেট!")
                .setMessage("নতুন আপডেট ভার্সনটি ইনস্টল করুন ।")
                .setPositiveButton("আপডেট করুন", null)
                .setNegativeButton("না", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .show();

        dialog.setCancelable(false);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }
    private PackageInfo pInfo;
    public int getVersionCode() {
        pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("MYLOG", "NameNotFoundException: "+e.getMessage());
        }
        return pInfo.versionCode;
    }

    protected boolean loginURL_BN_Matching(String url) {
        return url.toLowerCase().contains(Login_Post_Bn_URL.toLowerCase());
    }
    protected boolean loginURL_EN_Matching(String url) {
        return url.toLowerCase().contains(Login_Post_En_URL.toLowerCase());
    }

    public void getWebview(String myurl) {
        progressDialog = new ProgressDialog(MainActivity.this);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
                view.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
                webView.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        view.loadData("<html>SOMETHING WENT WRONG!,Please Check your Internet Connection</html>", "", "");
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        CookieSyncManager.getInstance().sync();
                    }

                });


                CookieSyncManager.createInstance(webView.getContext());
                CookieSyncManager.getInstance().sync();
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (loginURL_BN_Matching(url)) {
                    try {
                        postInfo();
                    } catch (Exception e) {
                        Log.e("Fail 2", e.toString());
                        //At the level Exception Class handle the error in Exception Table
                        // Exception Create That Error  Object and throw it
                        //E.g: FileNotFoundException ,etc
                        e.printStackTrace();
                    }
                }else if (loginURL_EN_Matching(url)) {
                    try {
                        postInfo();
                    } catch (Exception e) {
                        Log.e("Fail 2", e.toString());
                        //At the level Exception Class handle the error in Exception Table
                        // Exception Create That Error  Object and throw it
                        //E.g: FileNotFoundException ,etc
                        e.printStackTrace();
                    }
                }
                super.onPageStarted(view, url, favicon);
            }
        });


        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        webView.loadUrl(myurl);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        String url = new String(English_URL);
        String webUrl = new String(webView.getUrl());

        String BanglaUrl = new String(Bangla_URL);
        /*String webUrl = new String(webView.getUrl());*/
        if (url.equals(webUrl)) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.alert_dark_frame)
                    .setTitle("Exit")
                    .setMessage("Do you want to Exit ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No ", null)
                    .show();
        } else if (BanglaUrl.equals(webUrl)) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.alert_dark_frame)
                    .setTitle("বাহির")
                    .setMessage("আপনি কি বন্ধ করতে চান ?")
                    .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("না ", null)
                    .show();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }

    }


    private void postInfo(){

        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pqstec.com/Easyreg/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        API api = retrofit.create(API.class);

        JsonObject jsonObjectFinal = new JsonObject();

        JSONObject jsonObjectName = new JSONObject();


        try {

            /*Toast.makeText(MainActivity.this,"PhoneNumber"+phoneNumber+
                    "\nAndroidID"+androidId+"\naddress"+address1+
                    "\npostlatitude"+postlatitude+"\npostlongitude"+postlongitude,Toast.LENGTH_LONG).show();*/

            jsonObjectName.put("Remarks", remarks);
            jsonObjectName.put("macAddress", androidId);
            jsonObjectName.put("Latitude", postlatitude);
            jsonObjectName.put("longitude", postlongitude);
            jsonObjectName.put("LocationName", address1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        jsonObjectFinal = (JsonObject) jsonParser.parse(jsonObjectName.toString());
        Call<Information> obj = api.postURLInfo(jsonObjectFinal);

        obj.enqueue(new Callback<Information>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<Information> obj, Response<Information> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    int counter = 0;

                    Information information= response.body();

                    Information isSuccessful=response.body();

                    if (isSuccessful != null) {
                        if (isSuccessful.equals("")) {
                            Toast.makeText(MainActivity.this, "আবার চেষ্টা করুন", Toast.LENGTH_LONG).show();
                        } else {
                            /*Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG).show();*/
                            Toast.makeText(MainActivity.this, "AndroidID"+androidId+"\nRemarks"+remarks+"\n"+"\naddress"+address1+
                                    "\npostlatitude"+postlatitude+"\npostlongitude"+postlongitude,Toast.LENGTH_LONG).show();

                            /*preferenceConfig.writeLoginStatus(true);*/
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "আবার চেষ্টা করুন", Toast.LENGTH_LONG).show();


                    }

                } else {
                    progressDialog.dismiss();
                    Log.d("", "onResponse: ");
                    Toast.makeText(MainActivity.this,"আবার চেষ্টা করুন", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<Information> obj, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,  "Failure", Toast.LENGTH_LONG).show();

            }
        });

    }
}