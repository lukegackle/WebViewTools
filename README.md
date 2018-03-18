# WebViewTools v5.5

This is a Java extension for App Inventor based platforms. This extension provides access to a number of methods for the WebViewer component which are not available in App Inventor itself.

Now in version 5 the extension is able to handle popups which are usually needed in OAuth login flow. Popups are especially important in OAuth login used in a lot of websites (e.g., www.feedly.com). The popups in this project open in a dialog and can be dismissed by a close button or pressing Back or if the popup window closes itself (like what happens on most login authentication flow).

Example Block diagram:

![Blocks](https://github.com/hwasiti/WebViewTools/raw/master/Blocks.png)

![aia file](https://github.com/hwasiti/WebViewTools/raw/master/test9.aia)

![apk file](https://github.com/hwasiti/WebViewTools/raw/master/test9.apk)

Version history:
v5.5: Added WebView.loadDataWithBaseURL Method:
More details:
https://developer.android.com/reference/android/webkit/WebView.html#loadDataWithBaseURL%28java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String%29
What can this method do:
https://stackoverflow.com/questions/4950729/rendering-html-in-a-webview-with-custom-css



For more details how to use the extension:

http://thunkableblocks.blogspot.my/2017/06/webviewtools-extension-for-app-inventor.html

Created by Luke Gackle 

Version5.5 by Haider Alwasiti
