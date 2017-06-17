package io.askcloud.plex.pvr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
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
 */

/**
 * The PirateBay site stores its data in 4 columns
 * Type | Name | Seeds | Leach
 * 
 * <tbody>
 *   <tr>
 *     <td class="vertTh"><center><a href="/browse/200" title="More from this category">Video</a><br> (<a href="/browse/205" title="More from this category">TV shows</a>)</center> </td>
 *     <td><div class="detName"><a href="/torrent/17555310/Switched.at.Birth.S05E10.HDTV.x264-FLEET" class="detLink" title="Details for Switched.at.Birth.S05E10.HDTV.x264-FLEET">Switched.at.Birth.S05E10.HDTV.x264-FLEET</a>
 *       </div> <a href="magnet:?xt=urn:btih:280b5eb48a8e2e8f5e2e44adc1f61ac0d302693e&amp;dn=Switched.at.Birth.S05E10.HDTV.x264-FLEET&amp;tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&amp;tr=udp%3A%2F%2Fzer0day.ch%3A1337&amp;tr=udp%3A%2F%2Fopen.demonii.com%3A1337&amp;tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&amp;tr=udp%3A%2F%2Fexodus.desync.com%3A6969" title="Download this torrent using magnet"><img src="//thepiratebay.org/static/img/icon-magnet.gif" alt="Magnet link"></a><a href="/user/TvTeam"><img src="//thepiratebay.org/static/img/vip.gif" alt="VIP" title="VIP" style="width:11px;" border="0"></a><img src="//thepiratebay.org/static/img/11x11p.png"> <font class="detDesc">Uploaded 04-12&nbsp;04:10, Size 313.7&nbsp;MiB, ULed by <a class="detDesc" href="/user/TvTeam/" title="Browse TvTeam">TvTeam</a></font> </td>
 *     <td align="right">37</td>
 *     <td align="right">12</td>
 *   </tr>
 *   ... 
*/
public class PlexPVR {

	/**
	 * Pirate Bay has 4 main colums in its table
	 * Type | Name | Seeds | Leach
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			//String html = "<html><head><title>First parse</title></head><body><p>Parsed HTML into a doc.</p></body></html>";
			//Document doc = Jsoup.parse(html);
//			Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
//			Elements newsHeadlines = doc.select("#mp-itn b a");
//			System.out.println(newsHeadlines);
			
			//URLEncoder.encode(s);
			//URLDecoder.decode(s);
			
			String url = "https://thepiratebay.org/search/switched%20at%20birth/0/7/0";
			Document doc = Jsoup.connect("https://thepiratebay.org/search/switched%20at%20birth/0/7/0").get();
			
//			Elements tBody = doc.getElementsByTag("tbody");
//			for (Element element : tBody) {
//				Elements tdElements = element.getElementsByTag("td");
//				
//				for (Element td : tdElements) {
//					System.out.println(td);
//				}
//			}
			
			// <div class="detName"> 
	        //  <a href="/torrent/17483326/Switched.at.Birth.S05E09.HDTV.x264-SVA" class="detLink" title="Details for Switched.at.Birth.S05E09.HDTV.x264-SVA">Switched.at.Birth.S05E09.HDTV.x264-SVA</a> 
	        //  </div> <a href="magnet:?xt=urn:btih:1e27c77694a9dc3ac5c66671cb94a847097ef907&amp;dn=Switched.at.Birth.S05E09.HDTV.x264-SVA&amp;tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&amp;tr=udp%3A%2F%2Fzer0day.ch%3A1337&amp;tr=udp%3A%2F%2Fopen.demonii.com%3A1337&amp;tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&amp;tr=udp%3A%2F%2Fexodus.desync.com%3A6969" title="Download this torrent using magnet"><img src="//thepiratebay.org/static/img/icon-magnet.gif" alt="Magnet link"></a><a href="/user/TvTeam"><img src="//thepiratebay.org/static/img/vip.gif" alt="VIP" title="VIP" style="width:11px;" border="0"></a><img src="//thepiratebay.org/static/img/11x11p.png"> <font class="detDesc">Uploaded 04-05&nbsp;04:13, Size 232.02&nbsp;MiB, ULed by <a class="detDesc" href="/user/TvTeam/" title="Browse TvTeam">TvTeam</a></font> </td>
			
			
			Elements torrents = doc.select("[title=Download this torrent using magnet]");
			
			geturl(null,null,null);
			//Elements newsHeadlines = doc.select("#mp-itn b a");
			//System.out.println(torrents);			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	  static String geturl(String urlreq,String username,String password) throws Exception
	  {
		  urlreq="http://192.168.0.20:8052/gui/token.html";
		  username="admin";
		  password="wid24esb";
	        String filname = "filname";
	                 try {
	            URL url = new URL (urlreq);
	            String userPassword = username+ ":" + password;
	            String encoding = new sun.misc.BASE64Encoder().encode (userPassword.getBytes());
	            URLConnection uc = url.openConnection();
	            uc.setRequestProperty  ("Authorization", "Basic " + encoding);
	            InputStream content = (InputStream)uc.getInputStream();
	            BufferedReader in   = new BufferedReader (new InputStreamReader (content));
	            BufferedWriter ou = new BufferedWriter(new FileWriter("request.txt"));
	            String line;
	            int i = 1;
	            while ((line = in.readLine()) != null) {
	                System.out.println (line);
	                ou.write(line);
	                ou.newLine();
	                i++;
	            }
	            ou.close();
	            in.close();
	        } catch (MalformedURLException e) {
	            System.out.println ("Invalid URL");
	        } catch (IOException e) {
	            System.out.println ("Error reading URL");
	        }
	    return filname;    
	  }	
}
