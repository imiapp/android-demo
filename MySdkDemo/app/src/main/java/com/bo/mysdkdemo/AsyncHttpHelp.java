package com.bo.mysdkdemo;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wifire.vport_third_sdk.constants.Constants;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;

/**
 * 获取一个httpClient
 */
public class AsyncHttpHelp {

    public final static int TIMEOUT_CONNECTION = 20000;// 连接超时时间
    public final static int TIMEOUT_SOCKET = 50000;// socket超时

    private static volatile AsyncHttpClient asyncHttpClient;

    public static AsyncHttpClient getHttpClient(Context context) {

        if (asyncHttpClient == null) {
            synchronized (AsyncHttpHelp.class) {
                if (asyncHttpClient == null) {
                    asyncHttpClient = new AsyncHttpClient();
                    asyncHttpClient.setTimeout(TIMEOUT_CONNECTION);
                    asyncHttpClient.setResponseTimeout(TIMEOUT_SOCKET);
                    asyncHttpClient.setMaxRetriesAndTimeout(3, 500);
                }
            }
        }

        return asyncHttpClient;
    }

    public static void get(Context context, String url, AsyncHttpResponseHandler handler) {
        getHttpClient(context).get(url, handler);
        log(new StringBuilder("GET ").append(url).toString());
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        getHttpClient(context).get(url, params, handler);
        log(new StringBuilder("GET ").append(url).append("?").append(params).toString());
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        getHttpClient(context).post(url, params, handler);
        log(new StringBuilder("POST ").append(url).append("?").append(params).toString());
    }

    public static void post(Context context, String url, HttpEntity params, String contentType, AsyncHttpResponseHandler handler) {
        getHttpClient(context).post(context, url, params, contentType, handler);
        log(new StringBuilder("POST ").append(url).append("?").append(params).toString());
    }

    public static void post(Context context, String url, AsyncHttpResponseHandler handler) {
        getHttpClient(context).post(url, handler);
        log(new StringBuilder("POST ").append(url).append("?").toString());
    }

    public static void put(Context context, String url, HttpEntity params, String contentType, AsyncHttpResponseHandler handler) {
        getHttpClient(context).put(context, url, params, contentType, handler);
        log(new StringBuilder("PUT ").append(url).append("?").toString());
    }

    public static void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        getHttpClient(context).put(context, url, params, handler);
        log(new StringBuilder("PUT ").append(url).append("?").toString());
    }

    public static void delete(Context context, String url, HttpEntity params, String contentType, AsyncHttpResponseHandler handler) {
        getHttpClient(context).delete(context, url, params, contentType, handler);
        log(new StringBuilder("DELETE ").append(url).append("?").toString());
    }

    public static void delete(Context context, String url, AsyncHttpResponseHandler handler) {
        getHttpClient(context).delete(context, url, handler);
        log(new StringBuilder("DELETE ").append(url).append("?").toString());
    }

    private static void log(String log) {
        Log.d("http", log);
    }

    private static SchemeRegistry getSchemeRegistry(Context context) {

        SchemeRegistry schReg = null;

        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            // 设置 https支持
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore, context);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证
            schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", sf, 443));
//            schReg.register(new Scheme("https", sf, 8443));

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return schReg;
    }

    static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore, Context context)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = null;
            try {
                tm = new MyTrustManager(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    static class MyTrustManager implements X509TrustManager {
        /*
         * The default X509TrustManager returned by SunX509.  We'll delegate
         * decisions to it, and fall back to the logic in this class if the
         * default X509TrustManager doesn't trust it.
         */
        X509TrustManager trustManager;

        MyTrustManager() {
        }

        MyTrustManager(Context context) throws Exception {
            // create a "default" JSSE X509TrustManager.
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//          KeyStore ks = KeyStore.getInstance("BKS");
//            ks.load(context.getResources().openRawResource(Constants.KEYSTORE_VERIFY_RAW_RESOURCE), Constants.KEYSTORE_VERIFY_PASSWORD.toCharArray());
//          TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            TrustManager tms[] = tmf.getTrustManagers();
        /*
         * Iterate over the returned trustmanagers, look
         * for an instance of X509TrustManager.  If found,
         * use that as our "default" trust manager.
         */
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tms[i];
                    return;
                }
            }
        /*
         * Find some other way to initialize, or else we have to fail the
         * constructor.
         */
            throw new Exception("Couldn't initialize");
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                trustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException excep) {
                // do any special handling here, or rethrow exception.
            }
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                chain[0].checkValidity();
            } catch (Exception e) {
                throw new CertificateException("Certificate server not valid or trusted.");
            }
//            try {
//                trustManager.checkServerTrusted(chain, authType);
//            } catch (CertificateException excep) {
//            /*
//             * Possibly pop up a dialog box asking whether to trust the
//             * cert chain.
//             */
//            }
        }

        /*
         * Merely pass this through.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return trustManager.getAcceptedIssuers();
        }
    }

}
