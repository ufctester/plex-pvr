//
// Read files in disk and find missing episodes and then put the missing 
// episodes into the following format and into a csv file 
// "TVDB_ID,NAME,SEASON,EPISODE"
//
//

import net.filebot.format.*
import net.filebot.media.*

// Define first what is now.
log.fine("Run script [$_args.script] at [$now]")
args.withIndex().each{ f, i -> if (f.exists()) { log.finest "Argument[$i]: $f" } else { log.warning "Argument[$i]: File does not exist: $f" } }

//Don't use the Calendar date since that won't work as it starts from 0 .. 11
//https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html#MONTH
def now = Calendar.instance
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM");

//def simpleNow=new SimpleDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
simpleNow=new SimpleDate(now.get(Calendar.YEAR),Integer.parseInt(dateFormat.format(now.getTime())),now.get(Calendar.DAY_OF_MONTH))
log.info "Todays Date: " + simpleNow; 

def episodes=[]    // This will keep the list of episodes we have
def episodeList=[] // This will keep the list of all episodes
def showInfo = [:] // This will keep map of show infomation indexed by the TVDB
def seen=[:]       // This will keep a list of the shows already queried.
log.info "Searching for Ended Series";

args.getFiles().each{ f ->
    // start looping through the video files.
    if (f.isVideo()) {
        // Get info from the filename
        def episode = parseEpisodeNumber(f)
        def show = detectSeriesName(f)
    
        if (episode != null & show != null) {
            if (!seen.containsKey(show)) {   
                log.finest "Reading info for "+show+" ... "
                def rs=TheTVDB.search(show,Locale.ENGLISH)
                if (rs !=null) {
                    def s=rs[0]
                    log.finest "found " +s
                    def i=TheTVDB.getSeriesData(s,SortOrder.Airdate,Locale.ENGLISH)
                    i.episodeList.each { j->
                        episodeList << [i.seriesInfo.id,j.season,j.episode]
                    }
                showInfo.put(i.seriesInfo.id,i)
                seen.put(show,i)
                }
            }
            def info=seen.get(show)
            episodes << [info.seriesInfo.id,episode.season,episode.episode]     
        }
        else {
            log.info "** Could not parse "+f.toString()
        }
    }
}

episodeList = episodeList as LinkedHashSet
episodeList.removeAll(episodes)

for( Map.Entry<String,Object> entry : showInfo.entrySet()){
	String key = entry.getKey();
	Object value = (Object)entry.getValue();
    //println "key: " + key + " value: " + value;
  
	AbstractEpisodeListProvider.SeriesData data = (AbstractEpisodeListProvider.SeriesData)value;
  
	if(data.seriesInfo.status == 'Ended'){
		log.info "\n" + data.seriesInfo.id + " - " + data.seriesInfo.name + " Ended=true"
	} 
	else
	{
		//log.info "\n" + data.seriesInfo.id + " - " + data.seriesInfo.name + " Ended=false"
	}
	              
}

