package io.askcloud.plex.pvr;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class UTorrentManager2 {

	private static String UTORRENT_IP="192.168.0.22";
	private static int UTORRENT_PORT_INT = 8052;
	private static String UTORRENT_PORT = "8052";
	private static String UTORRENT_USER="admin";
	private static String UTORRENT_PASSWORD="wid24esb";
	
    public static void main(String[] args) throws Exception {

        HttpHost targetHost = new HttpHost(UTORRENT_IP, UTORRENT_PORT_INT, "http");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(UTORRENT_IP, UTORRENT_PORT_INT),
        		new UsernamePasswordCredentials(UTORRENT_USER, UTORRENT_PASSWORD));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        
        try {
        	checkUTorrentClientConnection(httpclient);
//        	//Get the UTorrent URL GUI To test the connection
//            HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/");
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//            CloseableHttpResponse response = httpclient.execute(httpget);
//            try {
//                System.out.println("----------------------------------------");
//                //System.out.println(response.getStatusLine());
//                EntityUtils.consumeQuietly(response.getEntity());
//                //System.out.println(EntityUtils.toString(response.getEntity()));
//            }catch(Exception e)
//            {
//            	e.printStackTrace();
//            }
//            finally {
//                response.close();
//            }
        
            //Get the UTorrent Authentication Token
        	HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/token.html");
        	
            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity e = response.getEntity();
                InputStream is = e.getContent();
                StringWriter sw = new StringWriter();
                IOUtils.copy(is, sw);
                sw.flush();
                sw.close();
                is.close();
                //<html><div id='token' style='display:none;'>gzB9zbMru3JJlBf2TbmwwklESgXW2hD_caJfFLvNBjmaRbLZ3kNGnSHrFlIAAAAA</div></html>
                String t = sw.toString();
                int start = "<html><div id='token' style='display:none;'>".length();
                int end = t.indexOf("</div></html>");
                String token = t.substring(start,end);
                System.out.println("Authentication Token= " + token);
                EntityUtils.consumeQuietly(response.getEntity());
                
                String add = URLEncoder.encode("http://www.torrentportal.com/download/6066218/True.Blood.S06E01.Who.Are.You.Really.XviD-MGD%5Bettv%5D.torrent","UTF-8");
                add= "magnet:?xt=urn:btih:1e27c77694a9dc3ac5c66671cb94a847097ef907&dn=Switched.at.Birth.S05E09.HDTV.x264-SVA&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
                httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/?action=add-url&s="+add+"&token="+token);
                response = httpclient.execute(targetHost, httpget);
               
                e = response.getEntity();
                is = e.getContent();
                sw = new StringWriter();
                IOUtils.copy(is, sw);
                sw.flush();
                sw.close();
                is.close();
                System.out.println(sw.toString());
                
            }catch(Exception e)
            {
            	e.printStackTrace();
            }
            finally {
                response.close();
            }
            
            
        } finally {
            httpclient.close();
        }
    }
    
    
    static protected void checkUTorrentClientConnection(CloseableHttpClient httpClient) 
    {
    	//Get the UTorrent URL GUI To test the connection
        HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/");

        System.out.println("Executing request " + httpget.getRequestLine());
        
        CloseableHttpResponse response = null;
        try {
        	response = httpClient.execute(httpget);
            System.out.println("----------------------------------------");
            //System.out.println(response.getStatusLine());
            EntityUtils.consumeQuietly(response.getEntity());
            //System.out.println(EntityUtils.toString(response.getEntity()));
        }catch(Exception e)
        {
        	e.printStackTrace();
        	throw new RuntimeException("Error getting uTorrent URL: " + httpget.getURI());
        }
        finally {
        	IOUtils.closeQuietly(response);
        }    	
    }
    
    public static void main2(String[] args) throws Exception {

        HttpHost targetHost = new HttpHost(UTORRENT_IP, 8052, "http");

        
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials("admin", "wid24esb"));

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            // Add AuthCache to the execution context
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

            HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":8052/gui/");
            HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
            EntityUtils.consumeQuietly(response.getEntity());
           
            httpget = new HttpGet("http://" + UTORRENT_IP + ":8052/gui/token.html");
            response = httpclient.execute(targetHost, httpget, localcontext);
           
            HttpEntity e = response.getEntity();
            InputStream is = e.getContent();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw);
            sw.flush();
            sw.close();
            is.close();
            //<html><div id='token' style='display:none;'>gzB9zbMru3JJlBf2TbmwwklESgXW2hD_caJfFLvNBjmaRbLZ3kNGnSHrFlIAAAAA</div></html>
            String t = sw.toString();
            int start = "<html><div id='token' style='display:none;'>".length();
            int end = t.indexOf("</div></html>");
            String token = t.substring(start,end);
            System.out.println(token);
            EntityUtils.consumeQuietly(response.getEntity());
           
            String add = URLEncoder.encode("http://www.torrentportal.com/download/6066218/True.Blood.S06E01.Who.Are.You.Really.XviD-MGD%5Bettv%5D.torrent","UTF-8");
            add= "magnet:?xt=urn:btih:1e27c77694a9dc3ac5c66671cb94a847097ef907&dn=Switched.at.Birth.S05E09.HDTV.x264-SVA&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
            httpget = new HttpGet("http://192.168.0.20:8052/gui/?action=add-url&s="+add+"&token="+token);
            response = httpclient.execute(targetHost, httpget, localcontext);
           
            e = response.getEntity();
            is = e.getContent();
            sw = new StringWriter();
            IOUtils.copy(is, sw);
            sw.flush();
            sw.close();
            is.close();
            System.out.println(sw.toString());

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }    
}