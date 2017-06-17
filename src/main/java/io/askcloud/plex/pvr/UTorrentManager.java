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

public class UTorrentManager {

	private static String UTORRENT_IP="192.168.0.22";
	private static int UTORRENT_PORT_INT = 8052;
	private static String UTORRENT_PORT = "8052";
	private static String UTORRENT_USER="admin";
	private static String UTORRENT_PASSWORD="wid24esb";
	
    public static void main(String[] args) throws Exception {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(UTORRENT_IP, UTORRENT_PORT_INT),
        		new UsernamePasswordCredentials(UTORRENT_USER, UTORRENT_PASSWORD));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        
        try {
        	checkUTorrentClientConnection(httpClient);

        	//Get the UTorrent Authentication Token
        	String authToken = getUTorrentAuthenticationToken(httpClient);
        	startUTorrentDownload(httpClient, authToken);
            
        } finally {
        	httpClient.close();
        }
    }
    
    
    static protected void startUTorrentDownload(CloseableHttpClient httpClient,String authToken)
    {       
        CloseableHttpResponse response = null;
    	try {
    		HttpHost targetHost = new HttpHost(UTORRENT_IP, UTORRENT_PORT_INT, "http");
        	//Start UTorrent magnet link
            String add = URLEncoder.encode("http://www.torrentportal.com/download/6066218/True.Blood.S06E01.Who.Are.You.Really.XviD-MGD%5Bettv%5D.torrent","UTF-8");
            add= "magnet:?xt=urn:btih:1e27c77694a9dc3ac5c66671cb94a847097ef907&dn=Switched.at.Birth.S05E09.HDTV.x264-SVA&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";    	
            HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/?action=add-url&s="+add+"&token="+authToken);
            
            response = httpClient.execute(targetHost, httpget);
           
            HttpEntity e = response.getEntity();
            InputStream is = e.getContent();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw);
            sw.flush();
            sw.close();
            is.close();
            System.out.println(sw.toString());    	
		} catch (Exception e) {
			// TODO: handle exception
		}
    	finally {
    		IOUtils.closeQuietly(response);
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
    
    static protected String getUTorrentAuthenticationToken(CloseableHttpClient httpClient)
    {
    	String token = null;
        //Get the UTorrent Authentication Token
    	HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/token.html");
    	
        System.out.println("Executing request " + httpget.getRequestLine());
        CloseableHttpResponse response = null;
        try {
        	response = httpClient.execute(httpget);
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
            token = t.substring(start,end);
            System.out.println("Authentication Token= " + token);
            EntityUtils.consumeQuietly(response.getEntity());
                       
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally {
        	IOUtils.closeQuietly(response);
        }
        return token;	
    }
  
}