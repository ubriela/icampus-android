

package edu.usc.imsc.activities;

import edu.usc.imsc.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class AboutActivity extends Activity {
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.about);
        setTitle("iCampus 1.0");
        
        final String mimeType = "text/html";
        final String encoding = "utf-8";
        
        WebView wv;
        
        wv = (WebView) findViewById(R.id.wv1);
        wv.loadData("iCampus is a geo-spacial social networking portal for USC community, which integrates various types of datasets of USC, including public data (e.g., flicker, facebook), user data (pictures, video, location-data), USC's proprietary data (i.g., events, alerts) and IMSC's proprietary (e.g., 3D models, traffic).", mimeType, encoding);
        
        wv = (WebView) findViewById(R.id.wv2);
        wv.loadData("@Copyright <a href='http://imsc.usc.edu/'> Integrated Media System Center</a> 2011.", mimeType, encoding);
    }
}
