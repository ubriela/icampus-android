package edu.usc.imsc.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.usc.imsc.R;

import edu.usc.imsc.util.JavaScriptInterface;
/**
 * @author linghu
 *
 */
public class WebViewActivity extends AbstractActivity {
	WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
	    mWebView.loadUrl("http://mobile.usc.edu");
	    mWebView.setWebViewClient(new MyWebViewClient());
	}
	
	@Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
	            mWebView.goBack();
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	 
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	   
	}

	@Override
	public void refresh(boolean fromCache) {

	}
}
