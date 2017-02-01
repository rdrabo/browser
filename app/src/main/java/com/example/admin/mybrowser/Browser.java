package com.example.admin.mybrowser;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//Note: this activity was based on the embedding browser module wit the addition of
//the nonsense counter, back button, the URL filter, and addition of the necessary "https://"

@SuppressLint("SetJavaScriptEnabled")
public class Browser extends AppCompatActivity {
    private EditText field;
    private WebView browser;
    int badcounter = 0, index = 0;
    private String url = "", message = "";
    private ArrayList<String> back = new ArrayList<String>();

    Button buttonStar;
    Button buttonShowStar;
    MyBrowser mybrowser =new MyBrowser();
    MyAppSettings settings;
    Browser thisclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        thisclass = this;
        settings = new MyAppSettings(getApplicationContext());
        buttonStar = (Button)findViewById(R.id.buttonStar);
        buttonShowStar = (Button)findViewById(R.id.buttonShowStar);
        buttonShowStar.setOnClickListener(buttonShowStar_OnClickListener);


        //Create an object of the class WebView
        browser = (WebView)findViewById(R.id.webView);
        browser.setWebViewClient(mybrowser);

        //Hide buttonStar
        setStarButtonsVisibility();

    }

    public void open(View view){
        //Set the variable to the textedit view
        field = (EditText)(findViewById(R.id.txtWebbar));


        //Check the url if valid then set back button and
        //update the index before updating url if it is
        if(Patterns.WEB_URL.matcher(url).matches()) {
            back.add(url);
            index++;
        }

        //Default the URL to start with "https://" to be secure
        url = "https://" + field.getText().toString();

        //Wet the web browser settings
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //Check to see if the user entered a valid URL
        if(Patterns.WEB_URL.matcher(url).matches()==false){
            if(badcounter <4) {
                message = "Please enter a valid URL";

            }
            else if (badcounter < 9){
                //Check if it is an invalid URL
                message = "URL is invalid. Please retry";
            }
            else{
                //on the 10th consecutive attempt close the app and send a notification
                CloseNotification.notify(getBaseContext(),"end",0);
                finish();
            }
            //Create a toast to the user to enter a valid URL
            Toast.makeText(Browser.this, message, Toast.LENGTH_SHORT).show();

            //Set default as google search engine
            url = "https://google.com";

            //Clear the URL entry bar
            field.setText("");

            badcounter++;

         //If the user enters a proper URL then reset badcounter
        } else{
            badcounter = 0;
        }

        //Load the page
        if (badcounter < 5) {
            browser.loadUrl(url);
            //When the URL goes to default reset to blank string so that the history array
            //will not be filled with repeated google domains
            if(url == "https://google.com")
                url = "";
        }
    }

    //Go back one URL within the current session
    public void goback(View view){
        if(index>0) {
            browser.loadUrl(back.get(index - 1));
            index--;
        }else
            Toast.makeText(Browser.this, "There are no previous URLs", Toast.LENGTH_SHORT).show();
    }

    private class MyBrowser extends WebViewClient {
        // The web page loaded right (spero si dica così)
        public String currentPage = "";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            currentPage = "";
            thisclass.setStarButtonsVisibility();
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            currentPage = url;
            thisclass.setStarButtonsVisibility();
        }
    }

    private void hideButtonStar() {
        buttonStar.setVisibility(View.GONE);
    }

    private void showButtonStar() {
        buttonStar.setVisibility(View.VISIBLE);
    }

    public void setStarButtonsVisibility(){
        int visibility = settings.get_starSites().size()==0 ? View.GONE : View.VISIBLE;
        buttonShowStar.setVisibility(visibility);
        checkUrl(mybrowser.currentPage);
    }

    //Check if url is in stars and change the star button text with remove or add
    private void checkUrl(String pUrl) {
        if(pUrl.equals("")){
            buttonStar.setVisibility(View.GONE);
        }else {
            buttonStar.setVisibility(View.VISIBLE);
            boolean isStar = settings.get_starSites().contains(pUrl);
            buttonStar.setText(getResources().getString(isStar ? R.string.button_star_remove : R.string.button_star_add));
        }
    }

    //Add or remove from preferred sites
    public void setStar(View view){
        if(!mybrowser.currentPage.equals("")) {
            if (settings.get_starSites().contains(mybrowser.currentPage)) {
                settings.removeSiteFromStar(mybrowser.currentPage);
            } else {
                settings.addSiteToStar(mybrowser.currentPage);
            }
        }
        setStarButtonsVisibility();
    }


    //Show dialog with preferred web sites
    View.OnClickListener buttonShowStar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    thisclass,
                    android.R.layout.simple_list_item_1,
                    settings.get_starSites() );

            final Dialog dialog = new Dialog(thisclass);
            dialog.setContentView(R.layout.dialog_stars);
            dialog.setTitle("Preferred web sites");
            ListView listview = (ListView)dialog.findViewById(R.id.listStar);
            listview.setAdapter(arrayAdapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ((EditText)(findViewById(R.id.txtWebbar))).setText(arrayAdapter.getItem(i).toString().replace("http://","").replace("https://",""));
                    open(thisclass.findViewById(R.id.webView));
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

}