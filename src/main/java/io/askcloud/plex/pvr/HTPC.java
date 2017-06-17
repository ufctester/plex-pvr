package io.askcloud.plex.pvr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import io.askcloud.pvr.api.HTPC;
import io.askcloud.pvr.api.HTPCOptions;
import io.askcloud.pvr.api.HTPC.HTPCLogFilter;
import io.askcloud.pvr.api.HTPC.OneLineFormatter;
import io.askcloud.pvr.api.kodi.KodiDownloader;
import io.askcloud.pvr.api.kodi.KodiDownloader.KodiDownloaderDetails;
import io.askcloud.pvr.api.kodi.KodiTVShowDownloader;
import io.askcloud.pvr.api.utils.AMCBeyondCompareReportReviewer;
import io.askcloud.pvr.api.utils.GmailEmailClient;
import io.askcloud.pvr.kodi.jsonrpc.api.AbstractCall;
import io.askcloud.pvr.kodi.jsonrpc.api.call.Addons;
import io.askcloud.pvr.kodi.jsonrpc.config.HostConfig;
import io.askcloud.pvr.kodi.jsonrpc.io.ApiCallback;
import io.askcloud.pvr.kodi.jsonrpc.io.ConnectionListener;
import io.askcloud.pvr.kodi.jsonrpc.io.JavaConnectionManager;
import io.askcloud.pvr.kodi.jsonrpc.notification.AbstractEvent;
import io.askcloud.pvr.themoviedb.MovieDbException;
import io.askcloud.pvr.themoviedb.TheMovieDbApi;
import io.askcloud.pvr.tvdb.TheTVDBApi;

/**
 * 
 * @author git@askcloud.io
 *
 */
public class HTPC {
	private static HTPC eINSTANCE = null;
	//PVR Configuration Options
	private Configuration options = null;
	
	private final static String CONFIG_FILE = "pvr-config.properties";
	
	public static final String CLASS_NAME = HTPC.class.getName();	
	public static final Logger LOG = Logger.getLogger(CLASS_NAME);		
	
	//Logger Details
	public static final Level LOG_LEVEL = Level.INFO;	

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
	 * Runs AMC and then publishes to plex if the publishToPlex argument is set to true
	 * @param args
	 */
	private void callFileBotAMC(String[] args,boolean publishToPlex)
	{
//		LOG.entering(CLASS_NAME, "callFileBot",args);
//		try {
//			net.filebot.Main.main(args);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			LOG.severe("Error calling Filebot with args: "  + args);
//		}

		StringBuffer commandArgs = new StringBuffer();
        for (String arg : args)
        	commandArgs.append(" " + arg);
                
        try {
        	DefaultExecuteResultHandler rh = new DefaultExecuteResultHandler() {
        		@Override
        		public void onProcessComplete(int exitValue) {
        			// TODO Auto-generated method stub
        			super.onProcessComplete(exitValue);
        		}
        		
        		@Override
        		public void onProcessFailed(ExecuteException e) {
        			// TODO Auto-generated method stub
        			super.onProcessFailed(e);
        		}
        	};
        	
            String line = getFILE_BOT_EXE() + commandArgs;
        	LOG.info("Calling Filebot: " + line);
            CommandLine cmdLine = CommandLine.parse(line);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine,rh);   
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.exiting(CLASS_NAME, "callFileBot");
	}
	
	/**
	 * Main.main(new String[]{"-list", "--db", "thetvdb", "--q", "Dexter","--format", "{plex}"});
	 * @param args
	 */
	private void callFileBot(String[] args)
	{
//		LOG.entering(CLASS_NAME, "callFileBot",args);
//		try {
//			net.filebot.Main.main(args);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			LOG.severe("Error calling Filebot with args: "  + args);
//		}

		StringBuffer commandArgs = new StringBuffer();
        for (String arg : args)
        	commandArgs.append(" " + arg);
                
        try {
        	DefaultExecuteResultHandler rh = new FileBotExecuteResultHandler(false);
            String line = getFILE_BOT_EXE() + commandArgs;
        	LOG.info("Calling Filebot: " + line);
            CommandLine cmdLine = CommandLine.parse(line);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine,rh);   
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.exiting(CLASS_NAME, "callFileBot");
	}

	/**
	 * @param showName
	 */
	public void getTVEpisodes(String showName) {
		//Main.main(new String[] { "-list", "--db", "thetvdb", "--q", showName, "--format", "{n} - {s00e00} - {t}" });
	}

	/**
	 * @param name
	 */
	public void findTVShowPlex(String name) {
		//Main.main(new String[] { "-list", "--db", "thetvdb", "--q", "Dexter", "--format", "{plex}" });
	}


	/**
	 * @param directory
	 * @return
	 */
	public void findTVShowEpisodesHave(String directory) {
		LOG.entering(CLASS_NAME, "findTVShowEpisodesHave", new Object[] {directory});
		File missingEpisodeFile = new File(HTPC.getFILEBOT_SERIES_EPISODES_HAVE_FILE());
		try {
			LOG.info("Deleting old missing episode file: " + missingEpisodeFile);
			FileUtils.deleteQuietly(missingEpisodeFile);
		}
		catch (Exception e) {
			LOG.severe("ERROR deleting file: " + missingEpisodeFile);
		}
		
		try {		
			directory="\"" + directory + "\"";
			callFileBot(new String[] { "-script", HTPC.getFILEBOT_FIND_SERIES_EPISODES_HAVE(), directory,"--output",getFILEBOT_SERIES_EPISODES_HAVE_FILE() , "--log", "info"});
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "findTVShowEpisodesHave");
	}
	
	/**
	 * @param directory
	 */
	public void findMissingTVShowEpisodes(String directory) {
		LOG.entering(CLASS_NAME, "findMissingEpisodes", new Object[] {directory});
		deleteQuietly(HTPC.getDOWNLOAD_QUEUE_FILE());
		
		try {		
			
			//Main.main(new String[] { "-script", PlexPVRManager.FILE_BOT_FIND_MISSING_EPISODES, directory,"--output",PlexPVRManager.FILEBOT_SERIES_EPISODES_MISSING_FILE , "--def", "excludeList=" + FILE_BOT_FIND_MISSING_EPISODES_EXCLUDES,"--log", "all"});
			//callFileBot(new String[] { "-script", PlexPVRManager.FILE_BOT_FIND_MISSING_EPISODES, directory,"--output",PlexPVRManager.FILEBOT_SERIES_EPISODES_MISSING_FILE , "--def", "excludeList=" + FILE_BOT_FIND_MISSING_EPISODES_EXCLUDES,"--log", "info"});
			//Main.main(new String[] { "-list", "--db", "thetvdb", "--q", "Dexter", "--format", "{plex}" });
			directory="\"" + directory + "\"";
			callFileBot(new String[] { "-script", HTPC.getFILEBOT_FIND_SERIES_MISSING_EPISODES(), directory,"--output",HTPC.getDOWNLOAD_QUEUE_FILE() , "--def", "excludeList=" + getFILEBOT_FIND_SERIES_EPISODES_MISSING_EXCLUDES(),"--log", "info"});
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "findMissingEpisodes");
	}
	
	/**
	 * @param directory
	 * @return
	 */
	public void findCompletedEpisodes(String directory) {
		LOG.entering(CLASS_NAME, "findCompletedEpisodes", new Object[] {directory});
		deleteQuietly(HTPC.getFILEBOT_SERIES_ENDED_EPISODES_FILE());

		try {			
			//Main.main(new String[] { "-script", PlexPVRManager.FILEBOT_FIND_SERIES_ENDED_EPISODES, directory, "--output",PlexPVRManager.FILEBOT_SERIES_ENDED_EPISODES_FILE });
			callFileBot(new String[] { "-script", HTPC.getFILEBOT_FIND_SERIES_ENDED_EPISODES(), directory, "--output",HTPC.getFILEBOT_SERIES_ENDED_EPISODES_FILE() });
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "findCompletedEpisodes");
	}	
	
	/**
	 * 
	 */
	public void runAutomatedMediaCenter(boolean publishToPlex) {
		LOG.entering(CLASS_NAME, "automatedMediaCenter");
		 
		File targetDirectoryFile = new File(HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR());
		if(!targetDirectoryFile.exists())
		{
			try {
				//FIXME Add the mkdir to tvshows and movies
				FileUtils.forceMkdir(targetDirectoryFile);
			}
			catch (Exception e) {
				LOG.severe("Error trying to create directory: " + HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR());
			}
		}
				
		String fileBotDestForwardSlashes=HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR().replace("\\\\", "/");
		try {
			//Main.main(new String[] { "-script", "fn:amc", "--output", targetDirectory, "--action", "copy", "-non-strict", sourceDirectory, "--conflict", "override", "--def","movieFormat=\"" + fileBotDestForwardSlashes + "/Movies/{fn}\"",
			//		"subtitles", "en", "music", "y", "artwork", "n", "--log-file", "amc.log", "--def", "ecludeList", "amc-exclude.txt", "--def", "--log", "all" });
			
			String[] args = new String[] { "-script", "fn:amc", "--output", HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR(), "--action", "copy", "-non-strict", HTPC.getKODI_DOWNLOAD_COMPLETED_DIR(), 
					"--conflict", "override", "--def","movieFormat=\"" + fileBotDestForwardSlashes + "/Movies/{fn}\"",
					"subtitles", "en", "music", "y", "artwork", "n", "--log-file", getFILEBOT_AMC_LOG(), "--def", "excludeList=" + getFILEBOT_AMC_EXCLUDE_LIST(), "--log", "all" };					
					
			callFileBotAMC(args,publishToPlex);
		}
		catch(SecurityException e)
		{
			LOG.severe("Error Handling Filebot AMC: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "automatedMediaCenter");		
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
	
	
	public static String getBC_PUSH_AMC_TO_PLEX_BAT() {
		return getInstance().getOptions().getString("BC_PUSH_AMC_TO_PLEX_BAT");
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
}

