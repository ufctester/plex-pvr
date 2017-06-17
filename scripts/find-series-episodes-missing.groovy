//
// Read files in disk and find missing episodes and then put the missing 
// episodes into the following format and into a csv file 
// "TVDB_ID,NAME,SEASON,EPISODE"
// http://thetvdb.com/wiki/index.php/Category:Series
//
include('htpc')
import java.nio.file.Paths

import net.filebot.format.*
import net.filebot.web.*
import net.filebot.media.*
import groovy.util.*

// Define first what is now.
log.info("Run script [$_args.script] at [$now]")

args.withIndex().each{ f, i -> if (f.exists()) { log.finest "Argument[$i]: $f" } else { log.warning "Argument[$i]: File does not exist: $f" } }

//Don't use the Calendar date since that won't work as it starts from 0 .. 11
//https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html#MONTH
def now = Calendar.instance
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM");

//def simpleNow=new SimpleDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
simpleNow=new SimpleDate(now.get(Calendar.YEAR),Integer.parseInt(dateFormat.format(now.getTime())),now.get(Calendar.DAY_OF_MONTH))
log.info "Todays Date: " + simpleNow; 

def episodesMissing=[]    // This will keep the map of missing episodes 
def episodeList=[] // This will keep the list of all episode

missingEpisodes = tryLogCatch{ any{ _args.output }{ '.' }.toFile().getCanonicalFile() }
log.info "Searching for Missing Episodes";

//nfoCache
nfoCache = [:]

ignoreShows= [] as LinkedHashSet
ignoreShowSeriesMap = [:]

def removePunctuation(text){
	if(text != null)
	{
		results = text.replaceAll("\\p{Punct}+", "")
		results = results.replaceAll("  ", " ")
		return results
	}
	else
	{
		return text
	}
} 

//C:\Plex\AMC\TV Shows\Suits\Season 01\Suits - S01E01 - Pilot.mp4
def addExistingEpisode(tvshowMediaFile)
{

	//Typically tvshowMediaPath are like this
	//C:\Plex\AMC\TV Shows\Suits\Season 01\Suits - S01E01 - Pilot.mp4

	if(tvshowMediaFile != null)
	{
		Path tvshowMediaPath=tvshowMediaFile.toPath();
		def sXe = parseEpisodeNumber(tvshowMediaPath);
		//specials return null and we ignore them
		if(sXe != null)
		{
			seriesDirectory=tvshowMediaFile.parentFile.parentFile;
			seriesName=ignoreShowSeriesMap.get(seriesDirectory.toString());

			if(seriesName == null)
			{
				log.fine "seriesName was not found yet for: $seriesDirectory"
				//log.info "done parseEpisodeNumber"
				seriesName = detectSeriesName(tvshowMediaPath);
				ignoreShowSeriesMap.put(seriesDirectory.toString(),seriesName);
			}

			//log.info "done detectSeriesName"
			log.fine "seriesName: $seriesName season: ${sXe.season} episode: ${sXe.episode}"
			def name = getEpisodeName(seriesName,sXe.season,sXe.episode);
			log.fine "name: $name $tvshowMediaPath"
			if(!ignoreShows.contains(name))
			{
				log.fine "Adding Episode to ignore episode list: $name"
				ignoreShows.add(name);
			}
		}
	}	

	log.fine "Ignore Series File is now loaded: ${ignoreShows.size()}";
}

//Check to see if the series should be ignored
/*
 * http://thetvdb.com/wiki/index.php/Category:Series
 * <tvshow>
 *   <title>Suits</title>
 *   <sorttitle>Suits :: 2011-06-23</sorttitle>
 *   <year>2011</year>
 *   <rating>8.9</rating>
 *   <votes>264</votes>
 *   <plot>Suits follows college drop-out Mike Ross, who accidentally lands a job with one of New York's best legal closers, Harvey Specter. They soon become a winning team with Mike's raw talent and photographic memory, and Mike soon reminds Harvey of why he went into the field of law in the first place.</plot>
 *   <runtime>45</runtime>
 *   <mpaa>TV-14</mpaa>
 *   <id>247808</id>
 *   <imdbId>tt1632701</imdbId>
 *   <genre>Drama</genre>
 *   <thumb>http://thetvdb.com/banners/graphical/247808-g17.jpg</thumb>
 *   <premiered>2011-06-23</premiered>
 *   <status>Continuing</status>
 *   <studio>USA Network</studio>
 *   <tvdb id='247808'>http://www.thetvdb.com/?tab=series&amp;id=247808</tvdb>
 *   <episodeguide>
 *   <url cache='247808.xml'>http://www.thetvdb.com/api/1D62F2F90030C444/series/247808/all/en.zip</url>
 *   </episodeguide>
 * </tvshow>
*/
def loadNFO(tvshowMediaFile) {
	log.fine "loadNFO() ... Load the $tvshowMediaFile";

	if(tvshowMediaFile == null)
	{
		return;
	}

	log.fine "nfoCache:";
	nfoCache.each {
		log.fine "MAP Keys ${it.key.toString()}";
	}

	//cache the episode that we have
	addExistingEpisode(tvshowMediaFile);

	//The tvshowMediaFile is C:\Plex\AMC\TV Shows\American Horror Story\Season 01\American Horror Story - S01E01 - Pilot.mp4
	def nfoFile=tvshowMediaFile.parentFile.parentFile.toPath().resolve('tvshow.nfo').toFile();	

	if(nfoCache.containsKey(nfoFile))
	{
		log.fine "tvshow.nfo has already been loaded: " + nfoFile.toString();
	}

	else if(nfoFile.exists()){
		def tvshowNFO=loadTVShowNFO(nfoFile);
		if(tvshowNFO){
			nfoCache.put(nfoFile,tvshowNFO);
		}
	}
}
  
//Check to see if the series should be ignored
def acceptSeries(nfoFile,tvshowNFO) {
	log.fine "acceptSeries() ... Determine if should filter Series from: $nfoFile";

	//Check if this season should be skipped
	if((nfoFile == null) || (tvshowNFO == null))
	{
		log.warning "nfoFile or tvshowNFO or series is null";
		return false;
	}

	//Check the tvshow.nfo for <skip><series id="1"></series></skip>   
	if((tvshowNFO.skip != null) && (tvshowNFO.skip.series != null) && (tvshowNFO.skip.series.@'id' == "1"))
	{
		log.info "Skipping Series: ${tvshowNFO.title.text()}"
		return false;
	}
		
	log.fine "title: ${tvshowNFO.title} id: ${tvshowNFO.id} status: ${tvshowNFO.status}";
	//log.info "==" + tvshow.status.toString() + "==";
	if("Ended" == tvshowNFO.status.text())
	{
		log.warning "TV Series has ended but it is still in the running tv series directory: ${tvshowNFO.title.text()} ${tvshowNFO.status.text()}"
		//The series ended but we might not have the entire series so check to see if the plex flag is set to Continuing (which needs to be done manually)
		if((tvshowNFO.plex != null) && ("Continuing" == tvshowNFO.plex.text())){
			log.warning "TV Series has ended but the plex flag is set to Continuing: ${tvshowNFO.plex}"
			return true;				
		}
		else{
			return true;
		}
	}			
	return true;
}

//Check to see if the season should be ignored
def acceptEpisode(nfoFile,tvshowNFO,episodeObject) {
	log.fine "acceptSeason() ... Determine if should filter Season from series: $nfoFile";
	def acceptSeason=true;
	def season=episodeObject.season;
	def episode= episodeObject.episode;
	def special=episodeObject.special;

	if(special != null){
		log.fine "The current episode is a special.";
		acceptSeason=false;
		return acceptSeason;		
	}

	//Check if this season should be skipped
	if((nfoFile == null) || (tvshowNFO == null) || (season == null) || (episode == null))
	{
		log.fine "nfoFile or tvshowNFO or season is null";
		acceptSeason=false;
		return acceptSeason;
	}

	//Check the date
	if(episodeObject.airdate==null || episodeObject.airdate >= simpleNow)
	{
		acceptSeason=false;
		return acceptSeason;
	}

	log.fine "title: ${tvshowNFO.title} id: ${tvshowNFO.id} status: ${tvshowNFO.status} season: ${season} episode: ${episode}";

	/*
	 *
  	 * <skip>
	 *    <season id="1">
	 *       <episode id="2"/>
	 *    </season>
     * </skip>
	 */
	//log.info "==" + tvshow.status.toString() + "==";
	log.fine "Searching for season to see if it should be skipped: ${tvshowNFO.status}"
	if((tvshowNFO.skip != null) && (tvshowNFO.skip.season != null) && (tvshowNFO.skip.season.size() > 0))
	{
		tvshowNFO.skip.season.each{seasonItem ->
			log.fine "Season: ${season} tvshowNFO Season: ${seasonItem.@'id'}"

			//Check if the tvshow.nfo includes seasons to exclude.
			if(Integer.toString(season) == seasonItem.@'id'.text())
			{
				log.fine "Skipping Season: ${seasonItem.@'id'}";

				//If the season has no episodes under it then skip the entire season
				if((seasonItem.episode == null) || (seasonItem.episode.size() == 0))
				{
					acceptSeason = false;
				}
				//There are episodes under the season to skip so check which one
				else
				{
					seasonItem.episode.each{episodeItem ->
						log.fine "Season: ${seasonItem.@'id'} Episode: : ${episodeItem.@'id'}";

						//Check if the tvshow.nfo includes seasons to exclude.
						if(Integer.toString(episode) == episodeItem.@'id'.text())
						{
							acceptSeason = false;
						}
					}
				}
			}
		}
	}
	
	return acceptSeason;
}

//Create a named episode string from the series name, season, episode
def getEpisodeName(seriesName,season,episode) {
	//log.fine "getEpisodeName() ... return the string representation of an episode";

	if((seriesName == null) || (season == null) || (episode == null))
	{
		return null;
	}

	def seasonString=String.valueOf(season);
	def episodeString=String.valueOf(episode);
	def seasonStr=(seasonString.length() == 1)?"0"+seasonString:seasonString;
	def episodeStr=(episodeString.length() == 1)?"0"+episodeString:episodeString;	

	name = seriesName + " - S" + seasonStr + "E" +  episodeStr;
	//log.fine("Episode name: " + name);
	return name;
}


//Check to see if the episode should be ignored
def getEpisodeNames(episode) {
	log.fine "getEpisodeNames() ... return the string representation of an episode";
	Set episodeNames = [] as LinkedHashSet

	if(episode == null)
	{
		return null;
	}

	name = episode.seriesName;
	def seasonString=String.valueOf(episode.season);
	def episodeString=String.valueOf(episode.episode);
	def seasonStr=(seasonString.length() == 1)?"0"+seasonString:seasonString;
	def episodeStr=(episodeString.length() == 1)?"0"+episodeString:episodeString;	

	name= name + " - S" + seasonStr + "E" +  episodeStr;
	episodeNames.add(name);

	//lets also include the alias name
	if((episode.seriesInfo != null) && (episode.seriesInfo.getAliasNames().size() > 0)){
		episode.seriesInfo.getAliasNames().each{ seriesName ->
			name = getEpisodeName(episode.seriesInfo.name,episode.season,episode.episode);
			episodeNames.add(name);
		}
	}

	return episodeNames;
}

//The name that comes inis in the form The 100 - S01E01
def isMissingEpisode(name) {
	log.fine "isMissingEpisode() ... return the string representation of an episode";
	boolean isMissing=true;
	if(name == null)
	{
		return false;
	}

	if(ignoreShows.contains(name))
	{
		isMissing=false;
	}

	return isMissing;
}


/*
 * Load the TV Series tvshow.nfo file
 */
args.getFiles().each{ f ->
	/*
	 * This will return the directory up to the season
	 * Media Folder C:\Plex\SeriesSearch\TVShows\Tosh.0\Season 05
	 * Media Folder C:\Plex\SeriesSearch\TVShows\Tosh.0\Season 06
	 */
	log.fine "** Media Folder " + f.toString();

	//load the tvshow.nfo file
	loadNFO(f);	
}

log.fine "nfoCache cache size: ${nfoCache.size()}"

/*
 * TODO Need to handle series without a tvshow.xml (one should be generated maybe before this.)
 */
nfoCache.each { nfoFile, tvshowNFO ->
  log.fine "key: ${nfoFile}"
  log.fine "title: ${tvshowNFO.title} id: ${tvshowNFO.id} status: ${tvshowNFO.status}";

  //Check to see if we should search by id or name.  We only search by name when we don't have an nfo
  log.fine "Searching for TV Show ${tvshowNFO.id.text()}"
  String seriesName=tvshowNFO.title.text();
  int seriesId=tvshowNFO.id.text().toInteger();
  log.fine "seriesName: $seriesName seriesId: $seriesId";

	if(acceptSeries(nfoFile,tvshowNFO))
	{
		log.fine "Accepted Series: ${tvshowNFO.title.text()}  id: ${tvshowNFO.id.text()}"

        try{
            def searchResults=new SearchResult(seriesId, seriesName);
            def seriesData=TheTVDB.getSeriesData(searchResults,SortOrder.Airdate,Locale.ENGLISH)
            seriesData.episodeList.each { j->
                log.fine "Series Id: ${seriesData.seriesInfo.id} Season: ${j.season} Episode: ${j.episode} Special: ${j.special}"
                if(acceptEpisode(nfoFile,tvshowNFO,j))
                {
                    log.fine "Accepted Season: ${seriesData.seriesInfo.name}  id: ${seriesData.seriesInfo.id} Season: ${j.season} Episode: ${j.episode}"
                    episodeList.add(j);
                }
                else{
                    log.fine "Ignored Season: ${seriesData.seriesInfo.name}  id: ${seriesData.seriesInfo.id} Season: ${j.season} Episode: ${j.episode} Special: ${j.special}"
                }
            }            
        }
        catch(t)
        {
            log.info("Error Getting show data: ${t.toString()}")
        }
	}	
}

episodeList.each{ e ->
	//log.error "Episodeitem: ${e.toString()}";

	getEpisodeNames(e).each{name ->
		if(isMissingEpisode(name))
		{
			log.fine "Missing: $name";
			episodesMissing.add(e);
		}
		else
		{
			log.fine "Ignore: $name";
		}
	}
}

/*
 *	{
 * 		"TVDB_ID": "266967",
 * 		"IMDB_ID": "", 
 * 		"NAME": "Mom",
 * 		"SEASON": "2", 
 * 		"EPISODE": "4", 
 * 		"ENDED": "",
 * 		"STATUS": "QUEUED",
 * 		"SHOWTYPE": "SERIES",
 * 		"STATUS_LABEL": "",
 * 		"DOWNLOAD_PERCENT": "", 
 * 		"FILE": "",
 * 		"DOWNLOADSIZE": "", 
 * 		"TOTALSIZE": ""
 * }
 */
def builder = new groovy.json.JsonBuilder();
builder {
	download episodesMissing.collect{episode ->
		
		def statusLabel=getEpisodeName(episode.seriesInfo.name,episode.season,episode.episode)
		return {
			"TVDB_ID" episode.seriesInfo.id.toString()
			"IMDB_ID" ""
			"NAME" episode.seriesInfo.name
			"SEASON" episode.season.toString()
			"EPISODE" episode.episode.toString()
			"ENDED" ""
			"STATUS" "QUEUED"
			"SHOWTYPE" "SERIES"
			"STATUS_LABEL" statusLabel
			"DOWNLOAD_PERCENT" ""
			"FILE" ""
			"DOWNLOADSIZE" ""
			"TOTALSIZE" ""
		}
	}
}

log.fine "${builder.toPrettyString()}"
missingEpisodes.append(builder.toPrettyString());

// episodesMissing.each{e -> 
// 	log.info "Accepted Series: ${e.seriesInfo.name}  id: ${e.seriesInfo.id} Season: ${e.season} Episode: ${e.episode}"
// 	def builder = new groovy.json.JsonBuilder();
// 	builder {
// 		items dataList.collect {data ->
// 			return {
// 				my_new_key ''
// 				data.each {key, value ->
// 					"$key" value
// 				}
// 			}
// 		}
// 	}
// 	println builder.toPrettyString()	
// 	log.info ""
// }