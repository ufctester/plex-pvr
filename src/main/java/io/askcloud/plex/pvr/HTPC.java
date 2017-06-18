package io.askcloud.plex.pvr;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author git@askcloud.io
 *
 */
public class HTPC {
	private static HTPC eINSTANCE = null;
	//PVR Configuration Options
	private Configuration options = null;
	
	private final static String CONFIG_FILE = "config.properties";
	
	public static final String CLASS_NAME = HTPC.class.getName();	
	public static final Logger LOG = Logger.getLogger(CLASS_NAME);		
	
	//Logger Details
	public static final Level LOG_LEVEL = Level.FINEST;	

	//default is everything except the exlude list.  This can be overwritten
	private static Set<String> ENABLED_LOGGERS = new LinkedHashSet<String>();
	
    static
    {
    	ENABLED_LOGGERS = new LinkedHashSet<String>();
    	ENABLED_LOGGERS.add(HTPC.CLASS_NAME);
    }
    
	public class HTPCLogFilter implements Filter {

		/**
		 * 
		 */
		public HTPCLogFilter() {
			super();
		}    

		/* (non-Javadoc)
		 * @see java.util.logging.Filter#isLoggable(java.util.logging.LogRecord)
		 */
		@Override
		public boolean isLoggable(LogRecord record) {
			if (record == null)
				return false;
			
			Level level = record.getLevel();
			String loggerName=record.getLoggerName();
			String message = record.getMessage() == null ? "" : record.getMessage();
			
			if(ENABLED_LOGGERS.isEmpty())
			{
				return true;
			}
			else
			{
				for (String pattern : ENABLED_LOGGERS) {
					if (loggerName.contains(pattern))
						return true;				
				}
			}
			return false;
		}

	}
	
	
	/**
	 * @author git@askcloud.io
	 *
	 */
	protected class OneLineFormatter extends SimpleFormatter {

		// format string for printing the log record
		private final String format = "[%1$tc] %4$s: %5$s %n";//LoggingSupport.getSimpleFormat();
		private final Date dat = new Date();

		public OneLineFormatter() {
			super();
		}

		@Override
		public synchronized String format(LogRecord record) {
			dat.setTime(record.getMillis());
			String source;
			if (record.getSourceClassName() != null) {
				source = record.getSourceClassName();
				if (record.getSourceMethodName() != null) {
					source += " " + record.getSourceMethodName();
				}
			}
			else {
				source = record.getLoggerName();
			}
			String message = formatMessage(record);
			String throwable = "";
			if (record.getThrown() != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println();
				record.getThrown().printStackTrace(pw);
				pw.close();
				throwable = sw.toString();
			}
			return String.format(format, dat, source, record.getLoggerName(), record.getLevel().getName(), message, throwable);
		}
	}
	
	private void initLogger() {

		//  //Handler handler = new ConsoleHandler();
		//  SimpleFormatter formatter = new SimpleFormatter();
		//  
		//  Handler handler = new StreamHandler(System.out,formatter);
		//  handler.setLevel(LOG_LEVEL);
		//  LOG.setLevel(LOG_LEVEL);
		//  //LOG.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
		//  LOG.addHandler(handler);

		//get the top Logger
		Logger system = Logger.getLogger("");

		// Handler for console (reuse it if it already exists)
		Handler consoleHandler = null;
		//see if there is already a console handler
		for (Handler handler : system.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				//found the console handler
				consoleHandler = handler;
				break;
			}
		}
		if (consoleHandler == null) {
			//there was no console handler found, create a new one
			consoleHandler = new ConsoleHandler();
			system.addHandler(consoleHandler);
		}
		OneLineFormatter formatter = new OneLineFormatter();
		consoleHandler.setFormatter(formatter);
		consoleHandler.setFilter(new HTPCLogFilter());
		LOG.setLevel(LOG_LEVEL);

		//Logger.getLogger("").setLevel( Level.OFF ); // Solution 2

		//set the console handler to fine:
		consoleHandler.setLevel(LOG_LEVEL);
	}
	
	private HTPC() {

		super();
		initLogger();
	}
	
	
	public static HTPC getInstance() {
		if(eINSTANCE == null)
		{
			eINSTANCE = new HTPC();
		}
		return eINSTANCE;
	}
	
	/**
	 * @param file
	 */
	public void deleteQuietly(String file)
	{
		if(file != null)
		{
			File fileObj = new File(file);
			FileUtils.deleteQuietly(fileObj);
		}
	}
	
	/*
	 * load pvr-config.properties file
	 */
	private Configuration getOptions() {
		if(options == null){
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
			    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
			    .configure(params.properties()
			        .setFileName(CONFIG_FILE));
			try
			{
				options = builder.getConfiguration();
			    
			}
			catch(ConfigurationException cex)
			{
			    // loading of the configuration file failed
				cex.printStackTrace();
			}
		}	
		return options;
	}	
	
	public static String getFILE_BOT_EXE() {
		return getInstance().getOptions().getString("FILE_BOT_EXE");
	}	
	
	public static String getFILEBOT_FIND_MISSING_EPISODES() {
		return getInstance().getOptions().getString("FILEBOT_FIND_MISSING_EPISODES");
	}	
	
	public static String getDOWNLOAD_QUEUE_FILE() {
		return getInstance().getOptions().getString("DOWNLOAD_QUEUE_FILE");
	}	
	
	/**
	 * @return
	 */
	public static String getPLEX_TVSHOWS_DIR() {
		return getInstance().getOptions().getString("PLEX_TVSHOWS_DIR");
	}
	
	/**
	 * @return
	 */
	public static String getUTORRENT_IP() {
		return getInstance().getOptions().getString("UTORRENT_IP");
	}	
	
	/**
	 * @return
	 */
	public static String getUTORRENT_PORT() {
		return getInstance().getOptions().getString("UTORRENT_PORT");
	}	
	
	/**
	 * @return
	 */
	public static String getUTORRENT_USER() {
		return getInstance().getOptions().getString("UTORRENT_USER");
	}	
	
	/**
	 * @return
	 */
	public static String getUTORRENT_PASSWORD() {
		return getInstance().getOptions().getString("UTORRENT_PASSWORD");
	}	
	
	/**
	 * @return
	 */
	public List<Torrent> loadDownloads()
	{
		LOG.entering(CLASS_NAME, "loadDownloads");
	
		List<Torrent> torrents = new ArrayList<Torrent>();

		File f = new File(getDOWNLOAD_QUEUE_FILE());

		if (f.exists()) {
			try {
				InputStream is = new FileInputStream(getDOWNLOAD_QUEUE_FILE());
				String jsonTxt = IOUtils.toString(is);
				System.out.println(jsonTxt);
				JSONObject json = new JSONObject(jsonTxt);
				
				JSONArray downloadsJson = (JSONArray)json.get("download");
				for (int i = 0; i < downloadsJson.length(); i++) {
					JSONObject downloadJson = (JSONObject)downloadsJson.get(i);
					
					//{"DOWNLOADSIZE":"","EPISODE":"1","DOWNLOAD_PERCENT":"","ENDED":"","SEASON":"3","NAME":"The Blacklist","PB_URL":"https://thepiratebay.org/search/The Blacklist - S03E01/0/7/0","IMDB_ID":"","STATUS":"QUEUED","TVDB_ID":"266189","SHOWTYPE":"SERIES","STATUS_LABEL":"The Blacklist - S03E01","TOTALSIZE":"","FILE":""}
					String name = (String)downloadJson.get("NAME");
					String season = (String)downloadJson.get("SEASON");
					String episode = (String)downloadJson.get("EPISODE");
					String statusLabel = (String)downloadJson.get("STATUS_LABEL");
					statusLabel=statusLabel.replaceAll(" - ", " ");
					
					String piratebaySearchName = URLEncoder.encode(statusLabel,"UTF-8").replaceAll("\\+", "%20");
					String piratebayURL = "https://thepiratebay.org/search/" + piratebaySearchName + "/0/7/0";
					
					 
					
					
					season=(season.length() == 1)?"0" + season:season;
					episode=(episode.length() == 1)?"0" + episode:episode;
					Torrent torrent = new Torrent();
					
					torrents.add(torrent);
					torrent.setName(name + " S" + season + "E" + episode);
					torrent.setPirvatebayURL(piratebayURL);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
    	LOG.info("========== Found Torrents ==========");
    	for (Torrent torrent : torrents) {
			LOG.info(torrent.toString());
		}
		
		return torrents;
//		
    	
//      InputStream is = 
//              JsonParsing.class.getResourceAsStream( "sample-json.txt");
//      String jsonString = IOUtils.toString( is );
  	

//      JSONObject json = new JSONObject(jsonString);  
//      
//      JSONArray torrentsJSON = (JSONArray)json.get("torrents");
//      
//      for (int i = 0; i < torrentsJSON.length(); i++) {
//      	JSONArray jsonArray = (JSONArray)torrentsJSON.get(i);
//      	Torrent torrent = new Torrent();
//      	torrents.add(torrent);
//      	for (int j = 0; j < jsonArray.length(); j++) {
//				Object jsonObject = (Object)jsonArray.get(j);
//
//		        switch (j) {
//		            case 0:  
//		            	torrent.setHash((String)jsonObject);
//		                break;
//		            case 1:  		
//		
//		return torrents;
//	}
	}
}

