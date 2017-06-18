package io.askcloud.plex.pvr;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * https://thepiratebay.org/search/The%20Ranch%20S01E01/0/7/0
 * 
 * opts:
 *  category
 *    0   - all
 *    101 - 699
 *  page
 *    0 - 99
 *  orderBy
 *     1  - name desc
 *     2  - name asc
 *     3  - date desc
 *     4  - date asc
 *     5  - size desc
 *     6  - size asc
 *     7  - seeds desc
 *     8  - seeds asc
 *     9  - leeches desc
 *     10 - leeches asc
 * @author apps
 *
 */
public class UTorrentManager {

	private static String UTORRENT_IP="192.168.0.22";
	private static int UTORRENT_PORT_INT = 8052;
	private static String UTORRENT_PORT = "8052";
	private static String UTORRENT_USER="admin";
	private static String UTORRENT_PASSWORD="wid24esb";
	
	public static final String CLASS_NAME = HTPC.class.getName();	
	public static final Logger LOG = HTPC.LOG;	
	
    public static void main(String[] args) throws Exception {

    	List<Torrent> torrents = HTPC.getInstance().loadDownloads();
    	//UTorrentManager manager = new UTorrentManager();
    }
    
    public UTorrentManager(){
    	super();
    	

    	CloseableHttpClient httpClient = null;
        try {
        	
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(UTORRENT_IP, UTORRENT_PORT_INT),
            		new UsernamePasswordCredentials(UTORRENT_USER, UTORRENT_PASSWORD));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
            
        	checkUTorrentClientConnection(httpClient);

        	//Get the UTorrent Authentication Token
        	String authToken = getUTorrentAuthToken(httpClient);
        	//startUTorrentDownload(httpClient, authToken);
        	
        	List<Torrent> torrents = getTorrentFiles(httpClient,authToken);
        	
        	LOG.info("========== Found Torrents ==========");
        	for (Torrent torrent : torrents) {
				LOG.info(torrent.toString());
			}
        	
        	
            
        } finally {
        	IOUtils.closeQuietly(httpClient);
        }
    }
    
    /**
     * @param httpClient
     * @param authToken
     */
    protected void startUTorrentDownload(CloseableHttpClient httpClient,String authToken)
    {       
    	LOG.entering(CLASS_NAME, "startUTorrentDownload",new Object[]{httpClient,authToken});
        CloseableHttpResponse response = null;
    	try {
    		HttpHost targetHost = new HttpHost(UTORRENT_IP, UTORRENT_PORT_INT, "http");
        	//Start UTorrent magnet link
            String add = URLEncoder.encode("http://www.torrentportal.com/download/6066218/True.Blood.S06E01.Who.Are.You.Really.XviD-MGD%5Bettv%5D.torrent","UTF-8");
            //add= "magnet:?xt=urn:btih:1e27c77694a9dc3ac5c66671cb94a847097ef907&dn=Switched.at.Birth.S05E09.HDTV.x264-SVA&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
            //add="magnet:?xt=urn:btih:3f3ebfba71081257f9ffb5fd2a63e58679551da9&dn=The+Doors+-+The+Best+Of+The+Doors&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
            add="magnet:?xt=urn:btih:0abf3b768ec2dd489c54dba34c0877f793cf792b&dn=The.Doors.1991.1080p.BluRay.x264.%5BExYu-Subs+HC%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
            HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/?action=add-url&s="+add+"&token="+authToken);
            
            response = httpClient.execute(targetHost, httpget);
           
            HttpEntity e = response.getEntity();
            InputStream is = e.getContent();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw);
            sw.flush();
            sw.close();
            is.close();
            LOG.fine(sw.toString());    	
		} catch (Exception e) {
			// TODO: handle exception
		}
    	finally {
    		IOUtils.closeQuietly(response);
		}
    	LOG.exiting(CLASS_NAME, "startUTorrentDownload");
    }
    
    /**
     * @param httpClient
     * @param authToken
     * @return
     */
    protected List<Torrent> getTorrentFiles(CloseableHttpClient httpClient,String authToken)
    {
    	LOG.entering(CLASS_NAME, "getUTorrentFiles",new Object[]{httpClient,authToken});
    	List<Torrent> torrents = new ArrayList<Torrent>();
    	HttpHost targetHost = new HttpHost(UTORRENT_IP, UTORRENT_PORT_INT, "http");
    	
    	//Get the UTorrent URL GUI To test the connection
    	HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/?list=1&token="+authToken);

        LOG.info("Executing request " + httpget.getRequestLine());
        
        CloseableHttpResponse response = null;
        try {
        	response = httpClient.execute(targetHost, httpget);
            LOG.info("UTorrent Files JSON");
            //System.out.println(response.getStatusLine());
            //EntityUtils.consumeQuietly(response.getEntity());
            String jsonString=EntityUtils.toString(response.getEntity());
            LOG.info(jsonString);
            
            torrents = getTorrentList(jsonString);
        }catch(Exception e)
        {
        	e.printStackTrace();
        	throw new RuntimeException("Error getting uTorrent URL: " + httpget.getURI());
        }
        finally {
        	IOUtils.closeQuietly(response);
        }    
        LOG.exiting(CLASS_NAME, "getUTorrentFiles",torrents);
        return torrents;
    }
    
    /**
     * @param httpClient
     */
    protected void checkUTorrentClientConnection(CloseableHttpClient httpClient) 
    {
    	LOG.entering(CLASS_NAME, "checkUTorrentClientConnection",httpClient);
    	//Get the UTorrent URL GUI To test the connection
        HttpGet httpget = new HttpGet("http://" + UTORRENT_IP + ":" + UTORRENT_PORT + "/gui/");

        LOG.info("Executing request " + httpget.getRequestLine());
        
        CloseableHttpResponse response = null;
        try {
        	response = httpClient.execute(httpget);
            LOG.fine("Checking UTorrent Connection");
            //LOG.info(response.getStatusLine().toString());
            EntityUtils.consumeQuietly(response.getEntity());
            //LOG.info(EntityUtils.toString(response.getEntity()));
        }catch(Exception e)
        {
        	e.printStackTrace();
        	throw new RuntimeException("Error getting uTorrent URL: " + httpget.getURI());
        }
        finally {
        	IOUtils.closeQuietly(response);
        }   
        LOG.exiting(CLASS_NAME, "checkUTorrentClientConnection");
    }
    

    /**
     * @param httpClient
     * @return
     */
    private String getUTorrentAuthToken(CloseableHttpClient httpClient)
    {
    	LOG.entering(CLASS_NAME, "getUTorrentAuthToken",httpClient);
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
        LOG.exiting(CLASS_NAME, "getUTorrentAuthToken",token);
        return token;	
    }


    /**
     * @param jsonString
     * @return
     */
    private List<Torrent> getTorrentList(String jsonString)
    {
    	LOG.entering(CLASS_NAME, "getTorrentList",jsonString);
//        InputStream is = 
//                JsonParsing.class.getResourceAsStream( "sample-json.txt");
//        String jsonString = IOUtils.toString( is );
    	
    	List<Torrent> torrents = new ArrayList<Torrent>();

        JSONObject json = new JSONObject(jsonString);  
        
        JSONArray torrentsJSON = (JSONArray)json.get("torrents");
        
        for (int i = 0; i < torrentsJSON.length(); i++) {
        	JSONArray jsonArray = (JSONArray)torrentsJSON.get(i);
        	Torrent torrent = new Torrent();
        	torrents.add(torrent);
        	for (int j = 0; j < jsonArray.length(); j++) {
				Object jsonObject = (Object)jsonArray.get(j);

		        switch (j) {
		            case 0:  
		            	torrent.setHash((String)jsonObject);
		                break;
		            case 1:  
		            	torrent.setStatus(((Integer)jsonObject));
		                break;
		            case 2:  
		            	torrent.setName((String)jsonObject);
		                break;
		            case 3:  
		            	torrent.setSize((Integer)jsonObject);
		                break;
		            case 4:  
		            	torrent.setPercentProgress((Integer)jsonObject);
		                break;
		            case 5:  
		            	torrent.setDownloaded((Integer)jsonObject);
		                break;
		            case 6:  
		            	torrent.setUploaded((Integer)jsonObject);
		                break;
		            case 7:  
		            	torrent.setRatio((Integer)jsonObject);
		                break;
		            case 8:  
		            	torrent.setUploadSpeed((Integer)jsonObject);
		                break;
		            case 9:  
		            	torrent.setDownloadSpeed((Integer)jsonObject);
		                break;
		            case 10:  
		            	torrent.setEta((Integer)jsonObject);
		                break;
		            case 11:  
		            	torrent.setLabel((String)jsonObject);
		                break;
		            case 12:  
		            	torrent.setPeersConnected((Integer)jsonObject);
		                break;
		            case 13:  
		            	torrent.setPeersInSwarm((Integer)jsonObject);
		                break;
		            case 14:  
		            	torrent.setSeedsConnected((Integer)jsonObject);
		                break;
		            case 15:  
		            	torrent.setSeedsInSwarm((Integer)jsonObject);
		                break;
		            case 16:  
		            	torrent.setAvailability((Integer)jsonObject);
		                break;		                
		            case 17: 
		            	torrent.setOrder((Integer)jsonObject);		            	
		                break;
		            case 18: 
		            	torrent.setRemaining((Integer)jsonObject);
		                     break;
		            default: 
		                     break;
		        }
			}
        	
        	LOG.finest("Torrent: " + torrent);
		}
        
//        File f = new File("file.json");
//        if (f.exists()){
//            InputStream is = new FileInputStream("file.json");
//            String jsonTxt = IOUtils.toString(is);
//            System.out.println(jsonTxt);
//            JSONObject json = new JSONObject(jsonTxt);       
//            String a = json.getString("1000");
//            System.out.println(a);   
//        }  
        LOG.exiting(CLASS_NAME, "getTorrentList",torrents);
        return torrents;
    }
}