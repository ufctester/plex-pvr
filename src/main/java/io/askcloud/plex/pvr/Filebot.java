package io.askcloud.plex.pvr;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import net.filebot.Main;

/**
 * 
 * @author git@askcloud.io
 *
 */
public class Filebot {
	private static Filebot eINSTANCE = null;
	
	public static final String CLASS_NAME = Filebot.class.getName();	
	public static final Logger LOG = Logger.getLogger(CLASS_NAME);		
	
	
	public static void main(String[] args) {
		//Filebot.getInstance().filebotCreateNFO("C:\\tmp\\TVShows\\The Blacklist");
		Filebot.getInstance().filebotMissingTVShowEpisodes(HTPC.getPLEX_TVSHOWS_DIR());
		//Filebot.getInstance().filebotSeriesEnded("C:\\Plex\\SeriesSearch\\TVShows");
		//Filebot.getInstance().filebotCreateNFOs();
	}
	
	private Filebot() {

		super();
	}
	
	
	public static Filebot getInstance() {
		if(eINSTANCE == null)
		{
			eINSTANCE = new Filebot();
		}
		return eINSTANCE;
	}
	
	/**
	 * Main.main(new String[]{"-list", "--db", "thetvdb", "--q", "Dexter","--format", "{plex}"});
	 * @param args
	 */
	private void callFileBot(String[] args)
	{
		LOG.entering(CLASS_NAME, "callFileBot",args);
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
            String line = HTPC.getFILE_BOT_EXE() + commandArgs;
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
	 * C:\gitbash\opt\filebot\filebot.exe -script C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\scripts\\find-series-ended.groovy C:\tmp\TVShows --log info
	 * @param directory
	 */
	public void filebotSeriesEnded(String directory) {
		LOG.entering(CLASS_NAME, "createSeriesEnded", new Object[] {directory});
		
		try {		
			Main.main(new String[] { "-script", HTPC.getFILEBOT_SERIES_ENDED(), directory, "--log", "info"});
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "createSeriesEnded");
	}
	
	public void filebotCreateNFOs()
	{
		LOG.entering(CLASS_NAME, "filebotCreateNFOs");
		
		File directory = new File(HTPC.getPLEX_TVSHOWS_DIR());
		File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (File dir : subdirs) {
			LOG.info("Creating NFO For: " + dir.toString());
			filebotCreateNFO(dir.toString());
		}
		
		LOG.exiting(CLASS_NAME, "filebotCreateNFOs");
	}
	
	/**
	 * C:\gitbash\opt\filebot\filebot.exe -script C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\scripts\\create-nfo.groovy --def seriesDirectory="C:\tmp\TVShows" --log info
	 * @param directory
	 */
	public void filebotCreateNFO(String directory) {
		LOG.entering(CLASS_NAME, "filebotCreateNFO", new Object[] {directory});
		
		try {		
			Main.main(new String[] { "-script", HTPC.getFILEBOT_CREATE_SERIES_NFO(),"--def", "seriesDirectory=" + directory, "--log", "all"});
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "filebotCreateNFO");
	}
	
	/**
	 * C:\gitbash\opt\filebot\filebot.exe -script C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\scripts\\find-series-episodes-missing.groovy C:\\Plex\\SeriesSearch\\TVShows --output C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\downloads\\download.json --log info
	 * @param directory
	 */
	public void filebotMissingTVShowEpisodes(String directory) {
		LOG.entering(CLASS_NAME, "filebotMissingTVShowEpisodes", new Object[] {directory});
		HTPC.getInstance().deleteQuietly(HTPC.getDOWNLOAD_QUEUE_FILE());
		
		try {		
			
			Main.main(new String[] { "-script", HTPC.getFILEBOT_FIND_MISSING_EPISODES(), directory, "--output", HTPC.getDOWNLOAD_QUEUE_FILE() , "--log", "all"});
			//Main.main(new String[] { "-script", PlexPVRManager.FILE_BOT_FIND_MISSING_EPISODES, directory,"--output",PlexPVRManager.FILEBOT_SERIES_EPISODES_MISSING_FILE , "--def", "excludeList=" + FILE_BOT_FIND_MISSING_EPISODES_EXCLUDES,"--log", "all"});
			//callFileBot(new String[] { "-script", PlexPVRManager.FILE_BOT_FIND_MISSING_EPISODES, directory,"--output",PlexPVRManager.FILEBOT_SERIES_EPISODES_MISSING_FILE , "--def", "excludeList=" + FILE_BOT_FIND_MISSING_EPISODES_EXCLUDES,"--log", "info"});
			//Main.main(new String[] { "-list", "--db", "thetvdb", "--q", "Dexter", "--format", "{plex}" });
			//directory="\"" + directory + "\"";
			//callFileBot(new String[] { "-script", getFILEBOT_FIND_MISSING_EPISODES(), directory, "--output",HTPC.getDOWNLOAD_QUEUE_FILE() , "--log", "info"});
		}
		catch(SecurityException e)
		{
			LOG.severe("Error getting missing episodes: " + e.getMessage());			
			e.printStackTrace();
		}
		finally {
			
		}
		
		LOG.exiting(CLASS_NAME, "filebotMissingTVShowEpisodes");
	}
	
//	/**
//	 * C:\gitbash\opt\filebot\filebot.exe -script fn:amc --output C:\\Plex\\AMC --action copy -non-strict C:\\gitbash\\opt\\kodi\\completed --conflict override --def movieFormat="C:\\Plex\\AMC\\Movies\\{fn}" subtitles=en music=y artwork=n --log-file c:\\gitbash\\opt\\eclipse\\workspace\\askcloud-pvr\\scripts\\filebot\\amc.log --def excludeList=c:\\gitbash\\opt\\eclipse\\workspace\\askcloud-pvr\\scripts\\filebot\\amc-exclude.txt --def minFileSize=40 --log info
//	 */
//	public void runAutomatedMediaCenter(boolean publishToPlex) {
//		LOG.entering(CLASS_NAME, "automatedMediaCenter");
//		 
//		File targetDirectoryFile = new File(HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR());
//		if(!targetDirectoryFile.exists())
//		{
//			try {
//				//FIXME Add the mkdir to tvshows and movies
//				FileUtils.forceMkdir(targetDirectoryFile);
//			}
//			catch (Exception e) {
//				LOG.severe("Error trying to create directory: " + HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR());
//			}
//		}
//				
//		String fileBotDestForwardSlashes=HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR().replace("\\\\", "/");
//		try {
//			//Main.main(new String[] { "-script", "fn:amc", "--output", targetDirectory, "--action", "copy", "-non-strict", sourceDirectory, "--conflict", "override", "--def","movieFormat=\"" + fileBotDestForwardSlashes + "/Movies/{fn}\"",
//			//		"subtitles", "en", "music", "y", "artwork", "n", "--log-file", "amc.log", "--def", "ecludeList", "amc-exclude.txt", "--def", "--log", "all" });
//			
//			String[] args = new String[] { "-script", "fn:amc", "--output", HTPC.getKODI_DOWNLOAD_COMPLETED_AMC_DIR(), "--action", "copy", "-non-strict", HTPC.getKODI_DOWNLOAD_COMPLETED_DIR(), 
//					"--conflict", "override", "--def","movieFormat=\"" + fileBotDestForwardSlashes + "/Movies/{fn}\"",
//					"subtitles", "en", "music", "y", "artwork", "n", "--log-file", getFILEBOT_AMC_LOG(), "--def", "excludeList=" + getFILEBOT_AMC_EXCLUDE_LIST(), "--log", "all" };					
//					
//			callFileBotAMC(args,publishToPlex);
//		}
//		catch(SecurityException e)
//		{
//			LOG.severe("Error Handling Filebot AMC: " + e.getMessage());			
//			e.printStackTrace();
//		}
//		finally {
//			
//		}
//		
//		LOG.exiting(CLASS_NAME, "automatedMediaCenter");		
//	}
//
}

