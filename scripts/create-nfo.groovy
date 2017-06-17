/*
 * Create a tvshow.nfo file
 */

import net.filebot.format.*
import net.filebot.media.*
include('htpc')

// Define first what is now.
log.fine("Run script [$_args.script] at [$now]")
args.withIndex().each{ f, i -> if (f.exists()) { log.finest "Argument[$i]: $f" } else { log.warning "Argument[$i]: File does not exist: $f" } }

//Don't use the Calendar date since that won't work as it starts from 0 .. 11
//https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html#MONTH
def now = Calendar.instance
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM");

//def simpleNow=new SimpleDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
def simpleNow=new SimpleDate(now.get(Calendar.YEAR),Integer.parseInt(dateFormat.format(now.getTime())),now.get(Calendar.DAY_OF_MONTH))
log.info "Todays Date: " + simpleNow; 

seriesDirectory = tryQuietly{ def f = seriesDirectory as File; f.isAbsolute() ? f : outputFolder.resolve(f.path) }

log.info "Processing files..."
log.info "seriesDirectory: " + seriesDirectory.toString();

nfoFile=seriesDirectory.toPath().resolve('tvshow.nfo').toFile();
posterFile=seriesDirectory.toPath().resolve('poster.jpg').toFile();

//load the nfo file
tvshowNFO = loadTVShowNFO(nfoFile);
int seriesId=0;
String seriesName="";

log.fine "nfoFile: $nfoFile";
log.fine "posterFile: $posterFile";

if(tvshowNFO == null)
{
    //C:\Plex\AMC\TV Shows\American Horror Story
    log.fine "Could not find $nfoFile";
    //def seriesName = detectSeriesName(seriesDirectory);
    seriesName = seriesDirectory.getName();

    log.fine "seriesName: $seriesName"    
    options=TheTVDB.search(seriesName,Locale.ENGLISH)
	// sort by relevance
    log.fine "Sorting searched series..."
	options = options.sortBySimilarity(seriesName, { it.name })

	// auto-select series
	def series = options[0];
    log.fine "Found Series: ${series.id} id: ${series.name}";
    if(series.id)
    {
        seriesId=series.id;
    }
}
else{
    log.info "Found tvshow.nfo for ${seriesDirectory}";
    seriesId=tvshowNFO.id.text().toInteger();
    seriesName=tvshowNFO.title.text();
}

if(!seriesId){
    log.warning "Could not find series: $seriesName";
    return;
}

//Check to see if we should search by id or name.  We only search by name when we don't have an nfo
log.fine "Searching for TV Show seriesName: $seriesName id: $seriesId";
def seriesInfo = TheTVDB.getSeriesInfo(seriesId, Locale.ENGLISH);

cleanNFOAndPoster(seriesDirectory);

fetchSeriesNfo(nfoFile, seriesInfo, Locale.ENGLISH);
fetchSeriesBanner(posterFile, seriesId, 'poster', '680x1000', null, false, Locale.ENGLISH);
