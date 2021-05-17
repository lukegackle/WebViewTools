/*------------------------------------------------
 ----------- WebViewTools Extension -----------

This is a Java extension for App Inventor based platforms.
This extension provides access to a number of methods for the WebViewer component which are not available in App Inventor itself.

Created by: Luke Gackle

Other Contributors:
-Version 5 by Haider Alwasiti
-Version 7 by ady_irawan
Website: thunkableblocks.blogspot.com

Find this extension at: 
http://thunkableblocks.blogspot.com.au/2017/06/webviewtools-extension-for-app-inventor.html

------------------------------------------------*/

package com.LukeGackle.WebViewTools;

import android.content.Context;
import android.net.http.SslError;
import android.annotation.TargetApi;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.collect.Sets;
import com.google.appinventor.components.runtime.util.BoundingBox;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.FileUtil;
import com.google.appinventor.components.runtime.util.MediaUtil;
import com.google.appinventor.components.runtime.util.PaintUtil;
import com.google.appinventor.components.runtime.WebViewer;
import com.google.appinventor.components.common.ComponentConstants;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;

import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.os.Message;

import android.webkit.ValueCallback;
import android.os.Build;
import android.util.Log;
import android.app.Activity;
import android.widget.Toast;  
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
@DesignerComponent(version = WebViewTools.VERSION,
description = "This extension provides a range of methods for changing the settings for a WebView component. For further documentation and updates check out <a href=\"http://thunkableblocks.blogspot.com/\">thunkableblocks.blogspot.com</a>",
category = ComponentCategory.EXTENSION,
nonVisible = true,
iconName = "https://1.bp.blogspot.com/-d-xyqbKFyAY/WSDvpMEG-tI/AAAAAAABYTk/I9gjYEgABZYxjwi2pzmlqbvQg6eMJhSeQCLcB/s1600/ExtensionsIcons.png")
@SimpleObject(external = true)


public class WebViewTools extends AndroidNonvisibleComponent implements Component{
public static final int VERSION = 9;
private ComponentContainer container;

private Context context;
private Activity activity;

public static final String DEVELOPER = "Luke Gackle";

private static final String LOG_TAG = "WebViewToolsExtension" + "-" + VERSION + "-" + DEVELOPER;

private WebView webview;
private WebView mWebviewPop;
private WebSettings settings;

private String useragentstringPopup;

private WebViewToolsClient wvtc;
private WebChromeToolsClient wctc;

private boolean OverrideURLChange;
private String OverrideURL;

private String OutputHTML;


private AlertDialog builder;
private Toast mToast;

//constructor
public WebViewTools(ComponentContainer container){
super(container.$form());
this.container = container;
context = (Context) container.$context();
activity = (Activity) container.$context();
wvtc = new WebViewToolsClient();
wctc = new WebChromeToolsClient();



OverrideURLChange = false;
}

@SimpleFunction(description="SetWebViewer takes a WebViewer and internally stores the object, enabling you to change a variety of settings on the given WebViewer.")
public void SetWebViewer(WebViewer webviewer){
	webview = (WebView) webviewer.getView();

	CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.setAcceptCookie(true);
	
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webview,true);
        }

	settings = webview.getSettings();
	
	webview.setWebViewClient(wvtc);
	webview.setWebChromeClient(wctc);
	webview.getSettings().setSavePassword(true);
	/* - This will only work on API 17 and above https://developer.android.com/reference/android/webkit/JavascriptInterface.html
	webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");*/
}

@SimpleFunction(description="RemoveWebViewer removes a previous WebViewer from the internal store of this extension. Using this method will also stop the \"OnPageFinished\" event firing for the given WebViewer.")
public void RemoveWebViewer(WebViewer webviewer){
	
	WebView webviewerWebView = (WebView) webviewer.getView();
	
	if(webviewerWebView == this.webview){
		this.webview = null;
		this.settings = null;
	}
	else{
		Log.d("RemoveWebViewer", "Does not Match");
	}
	//webview.removeJavascriptInterface("HtmlViewer");
	//webviewer.FollowLinks(webviewer.FollowLinks());
	
	//webviewerWebView.setWebViewClient(new WebViewClient());
}



@SimpleFunction(description="Gets the current useragent string being used by the WebView.")
public String GetUserAgentString(){
	return settings.getUserAgentString();
}

@SimpleFunction(description="Gets whether Javascript is enabled in the current WebView.")
public boolean GetJavaScriptEnabled(){
	return settings.getJavaScriptEnabled();
}

@SimpleFunction(description="Gets whether the WebView has access to the device file system.")
public boolean GetDomStorageEnabled(){
	return settings.getDomStorageEnabled();
}
@SimpleFunction(description="Gets whether the database storage API is enabled.")
public boolean GetDatabaseEnabled(){
	return settings.getDatabaseEnabled();
}

@SimpleFunction(description="Gets the text zoom of the page in percent.")
public int GetTextZoom(){
	return settings.getTextZoom();
}

@SimpleFunction(description="Gets whether media playback requires user gesture in the current WebView.")
public boolean GetMediaPlaybackRequiresUserGesture(){
	return settings.getMediaPlaybackRequiresUserGesture();
}

@SimpleFunction(description="Executes the given JavaScript in the current web viewer.")
public void RunJavaScript(String js){
	if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
		webview.evaluateJavascript(js, new ValueCallback<String>() {
			   @Override
			   public void onReceiveValue(String output) {
				   OnJavaScriptOutput(output);
			   }
			});
	}
	else{
		webview.loadUrl("javascript:" + js);
	}
}

@SimpleEvent (description="Returns the output of the executed JavaScript. This event will only fire on Android KitKat or newer.")
public void OnJavaScriptOutput(String output){
	EventDispatcher.dispatchEvent(this, "OnJavaScriptOutput", output);
}

@SimpleFunction(description="Gets whether the builtin zoom mechanisms are being used, includes pinch to zoom gestures.")
public boolean GetBuiltInZoomControls(){
	return settings.getBuiltInZoomControls();
}

@SimpleFunction(description="Sets the useragent string for the WebViewer to use.")
public void SetUserAgentString(String useragentstring){
	settings.setUserAgentString(useragentstring);
	useragentstringPopup = useragentstring;

}

@SimpleFunction(description="Sets the WebViewer to request Desktop Site if true, else requests Mobile site.")
public void SetDesktopMode(boolean enabled) {

	String newUserAgent;

	if (enabled) {
		newUserAgent = settings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
	}
	else {
		newUserAgent = settings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
	}

	settings.setUserAgentString(newUserAgent);
	settings.setUseWideViewPort(enabled);
	settings.setLoadWithOverviewMode(enabled);
	settings.setSupportZoom(enabled);
	settings.setBuiltInZoomControls(enabled);
}

@SimpleFunction(description="Requests the raw HTML source code for the loaded page. Only works on Android KitKat and newer. Use the GotHTMLSource event to retrieve the HTML source.")
public void GetSourceHTML(){
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
		webview.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML;", new ValueCallback<String>() {
			   @Override
			   public void onReceiveValue(String output) {
				   GotHTMLSource(output);
			   }
			});
	}
}

@SimpleEvent (description="Returns the HTML Source of a website. This event will only fire on Android KitKat or newer.")
public void GotHTMLSource(String output){
	String text = "";
    try{
		byte[] utf8Bytes = output.getBytes("UTF8");
		text = new String(utf8Bytes,"UTF8");
	}
	catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	if(text != ""){
		EventDispatcher.dispatchEvent(this, "GotHTMLSource", text);
	}
	else{
		EventDispatcher.dispatchEvent(this, "GotHTMLSource", output);
	}
}

@SimpleFunction(description="Enables or disables file access within WebView.")
public void SetAllowFileAccess(boolean allow){
	settings.setAllowFileAccess(allow);
}

@SimpleFunction(description="Sets whether media playback requires user gesture within WebView.")
public void SetMediaPlaybackRequiresUserGesture(boolean require){
	settings.setMediaPlaybackRequiresUserGesture(require);
}

@SimpleFunction(description="Sets whether the URL should be checked prior to loading, enabling this will stop the a page from loading if it does not contain the specified URL in the destination URL.")
public void SetOverrideURLChange(boolean enabled, String url){
	OverrideURL = url;
	OverrideURLChange = enabled;
	
}


@SimpleFunction(description="Enables or disables access to the database storage API in the current WebView.")
public void SetDatabaseEnabled(boolean enabled){
	settings.setDatabaseEnabled(enabled);
}

@SimpleFunction(description="Enables the AppCache API and sets the AppCache path to the apps cache directory.")
public void SetAppCacheEnabled(boolean allow){
	settings.setAppCacheEnabled(allow);
	String appCachePath = activity.getCacheDir().getAbsolutePath();
	settings.setAppCachePath(appCachePath);
}

@SimpleFunction(description="Sets whether WebView should support multiple windows.")
public void SetSupportMultipleWindows(boolean enabled){
	settings.setSupportMultipleWindows(enabled);
}

@SimpleFunction(description="Sets whether WebView should block resources from network locations (http and https).")
public void SetBlockNetworkLoads(boolean block){
	settings.setBlockNetworkLoads(block);
}

@SimpleFunction(description="Sets whether the WebView should use it's builtin zoom mechanisms. This enables or disables zoom gestures such as pinch to zoom.")
public void SetBuiltInZoomControls(boolean enabled){
	settings.setBuiltInZoomControls(enabled);
}

@SimpleFunction(description="Sets whether the WebView loads pages in overview mode, that is, zooms out the content to fit on screen by width.")
public void SetLoadWithOverviewMode(boolean enabled){
	settings.setLoadWithOverviewMode(enabled);
}

@SimpleFunction(description="Sets whether the current WebView should enable support for the \"viewport\" HTML meta tag or should use a wide viewport. If set to true and the page contains a Viewport meta tag then the Viewport meta tag is used.")
public void SetUseWideViewPort(boolean enabled){
	settings.setUseWideViewPort(enabled);
}

@SimpleFunction(description="Can be used to set the initial scale if the webpage doesnt make use of a viewport meta tag, scale is a percentage e.g for 100% enter 100.")
public void SetInitialScale(int scale){
	webview.setInitialScale(scale);
}

@SimpleFunction(description="Sets whether geolocation is enabled in the WebView.")
public void SetGeolocationEnabled(boolean enabled){
	settings.setGeolocationEnabled(enabled);
}

@SimpleFunction(description="Sets whether javascript is enabled in the WebView.")
public void SetJavaScriptEnabled(boolean enabled){
	settings.setJavaScriptEnabled(enabled);
}

@SimpleFunction(description="Sets whether javascript can open windows automatically via window.open() javascript methods.")
public void SetJavaScriptCanOpenWindowsAutomatically(boolean enabled){
	settings.setJavaScriptCanOpenWindowsAutomatically(enabled);
}

@SimpleFunction(description="Sets the text zoom of the page in percent.")
public void SetTextZoom(int textZoom){
	settings.setTextZoom(textZoom);
}

@SimpleFunction(description="Sets the cachemode the WebView should use, refer to Googles WebSettings documentation for further information on the different cache modes and their constant values: https://developer.android.com/reference/android/webkit/WebSettings.html#LOAD_DEFAULT.")
public void SetCacheMode(int cacheMode){
	settings.setCacheMode(cacheMode);
}

@SimpleFunction(description="force destroy popup")
public void DismissPopup(){
	if(builder != null && mWebviewPop !=null){
	mWebviewPop.destroy();
	builder.dismiss();
	}
}

@SimpleEvent(description="Fires once the page has finished loading.")
	public void OnPageFinished(String URL){
		EventDispatcher.dispatchEvent(this, "OnPageFinished", URL);
	}
	
@SimpleEvent(description="Fires when an error occurs loading page, note that API 23 and above this will trigger for onpage elements .e.g iframes, refer to Android documentation at http://developer.android.com/reference/android/webkit/WebViewClient.html.")
	public void OnReceivedError(String URL, int errorCode, String errorDescription){
		EventDispatcher.dispatchEvent(this, "OnReceivedError", URL, errorCode, errorDescription);
	}
	
@SimpleEvent(description="Fires when an error occurs loading page, note that API 23 and above this will trigger for onpage elements .e.g iframes, refer to Android documentation at http://developer.android.com/reference/android/webkit/WebViewClient.html.")
	public void OnReceivedHttpError(String URL, int errorCode, String errorDescription){
		EventDispatcher.dispatchEvent(this, "OnReceivedHttpError", URL, errorCode, errorDescription);
	}
	
@SimpleEvent(description="Fires when back button pressed.")
	public void OnPopupBackPressed(){
		EventDispatcher.dispatchEvent(this, "OnPopupBackPressed");
	}
	
@SimpleEvent(description="Fires when Popup Dismissed.")
	public void OnPopupDismissed(){
		EventDispatcher.dispatchEvent(this, "OnPopupDismissed");
	}
	
	@SimpleEvent(description="Fires when Progress changed.")
	public void OnProgressChanged(int newProgress){
		EventDispatcher.dispatchEvent(this, "OnProgressChanged", newProgress);
	}

private class WebViewToolsClient extends WebViewClient {
	
    @Override
    public void onPageFinished(WebView view, String url) {
		if(view == webview){
			OnPageFinished(url);
		}
		else{
			super.onPageFinished(view, url);
		}
		
    }
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url){
		
		if(view == webview){
			if(OverrideURLChange){
				if(url.contains(OverrideURL) ){ 
					return false;
				}
				else{
					
					return true;
					
				}
			}
			else{
				return false;
			}
		}
		else{
			return super.shouldOverrideUrlLoading(view, url);
		}
		
		
		
	}

	@Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        
		if(view == webview){
			Log.d("onReceivedSslError", "onReceivedSslError");
		}
		else{
			super.onReceivedSslError(view, handler, error);
		}
		
    }
	
	@Override
	public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
		if(view == webview){
			OnReceivedError(req.getUrl().toString(),rerr.getErrorCode(),rerr.getDescription().toString());
		}
		else{
			super.onReceivedError(view, req, rerr);
		}
		
	}
	
	@Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
		if(view == webview){
			OnReceivedHttpError(request.getUrl().toString(), errorResponse.getStatusCode(), errorResponse.getReasonPhrase());
		}
		else{
			super.onReceivedHttpError(view, request, errorResponse);
		}
    }
	
  }



private class WebChromeToolsClient extends WebChromeClient {

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		if(view == webview){
			OnProgressChanged(newProgress);
		}
		else{
			super.onProgressChanged(view, newProgress);
		}
	}


    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
            boolean isUserGesture, Message resultMsg) {
		if(view == webview){
			mWebviewPop = new WebView(context);
			mWebviewPop.setVerticalScrollBarEnabled(false);
			mWebviewPop.setHorizontalScrollBarEnabled(false);
			mWebviewPop.setWebViewClient(new WebViewToolsClient());
			mWebviewPop.setWebChromeClient(new WebChromeToolsClient());
			mWebviewPop.getSettings().setJavaScriptEnabled(true);
			mWebviewPop.getSettings().setSavePassword(true);
			mWebviewPop.getSettings().setSaveFormData(true);
			mWebviewPop.getSettings().setUserAgentString(useragentstringPopup);
			
			builder = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
					.setTitle("")
					.setView(mWebviewPop)
					.setCancelable(false)
					.setPositiveButton("CLOSE", null)
					.setNegativeButton("<", null)
					.create();
					
			builder.setOnShowListener(new DialogInterface.OnShowListener(){
				@Override
				public void onShow(DialogInterface dialogInterface) {
					Button buttonPositive = ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE);
					buttonPositive.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
						OnPopupDismissed();
						mWebviewPop.destroy();
						builder.dismiss();
						}
					});
					Button buttonNegative = ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE);
					buttonNegative.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
						if(mWebviewPop.canGoBack()){
						mWebviewPop.goBack();
						OnPopupBackPressed();
						} else {
							OnPopupDismissed();
							mWebviewPop.destroy();
							builder.dismiss();
							}
						}
					});
				}
			});
			
			builder.show();
			builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				cookieManager.setAcceptThirdPartyCookies(mWebviewPop,true);
			}
			
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(mWebviewPop);
			resultMsg.sendToTarget();

			//Toast.makeText(context,"Popup handled",Toast.LENGTH_SHORT).show();  
			return true;
		}
		else{
			return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
		}

    }

    @Override
        public void onCloseWindow(WebView window) {

            //Toast.makeText(context,"onCloseWindow called",Toast.LENGTH_SHORT).show();


            try {
                mWebviewPop.destroy();
				
            } catch (Exception e) {

            }

            try {
                builder.dismiss();
            } catch (Exception e) {

            }
			
			try {
				OnPopupDismissed();
			} catch (Exception e){
			
			}
    }

}

 
}