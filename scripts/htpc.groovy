
/**
 * Kodi helper functions
 */
def scanVideoLibrary(host, port) {
	def json = [jsonrpc: '2.0', method: 'VideoLibrary.Scan', id: 1]
	def url = "http://$host:$port/jsonrpc?request=" + URLEncoder.encode(JsonOutput.toJson(json), 'UTF-8')

	log.finest "GET: $url"
	new URL(url).get()
}

def showNotification(host, port, title, message, image) {
	def json = [jsonrpc:'2.0', method:'GUI.ShowNotification', params: [title: title, message: message, image: image], id: 1]
	def url = "http://$host:$port/jsonrpc?request=" + URLEncoder.encode(JsonOutput.toJson(json), 'UTF-8')

	log.finest "GET: $url"
	new URL(url).get()
}



/**
 * Plex helpers
 */
def refreshPlexLibrary(server, port, token) {
	// use HTTPS if hostname is specified, use HTTP if IP is specified
	def protocol = server ==~ /localhost|[0-9.:]+/ ? 'http' : 'https'
	def url = "$protocol://$server:$port/library/sections/all/refresh"
	if (token) {
		url += "?X-Plex-Token=$token"
	}
	log.finest "GET: $url"
	new URL(url).get()
}



/**
 * Emby helpers
 */
def refreshEmbyLibrary(server, port, token) {
	// use HTTPS if hostname is specified, use HTTP if IP is specified
	def protocol = server ==~ /localhost|[0-9.:]+/ ? 'http' : 'https'
	def url = "$protocol://$server:$port/Library/Refresh"
	if (token) {
		url += "?api_key=$token"
	}
	log.finest "GET: $url"
	new URL(url).post([:], [:])
}



/**
 * TheTVDB artwork/nfo helpers
 */
def fetchSeriesBanner(outputFile, seriesId, bannerType, bannerType2, season, override, locale) {
    log.fine "bannerType: " + bannerType
	if (outputFile.exists() && !override) {
		log.finest "Banner already exists: $outputFile"
		return outputFile
	}

	// select and fetch banner
	def artwork = TheTVDB.getArtwork(seriesId, bannerType, locale)
    log.fine "artwork: " + artwork
	def banner = [locale.language, null].findResult { lang -> artwork.find{ it.matches(bannerType2, season, lang) } }
	if (banner == null) {
		log.finest "Banner not found: $outputFile / $bannerType:$bannerType2"
		return null
	}
	log.finest "Fetching $outputFile => $banner"
	return banner.url.saveAs(outputFile)
}

def fetchSeriesFanart(outputFile, seriesId, type, season, override, locale) {
	if (outputFile.exists() && !override) {
		log.finest "Fanart already exists: $outputFile"
		return outputFile
	}

	def artwork = FanartTV.getArtwork(seriesId, "tv", locale)
	def fanart = [locale.language, null].findResult{ lang -> artwork.find{ it.matches(type, season, lang) } }
	if (fanart == null) {
		log.finest "Fanart not found: $outputFile / $type"
		return null
	}
	log.finest "Fetching $outputFile => $fanart"
	return fanart.url.saveAs(outputFile)
}

def fetchSeriesNfo(outputFile, i, locale) {
	log.finest "Generate Series NFO: $i.name [$i.id] outputFile: $outputFile imdbId: $i.imdbId"
	def xml = XML {
		tvshow {
			title(i.name)
			sorttitle([i.name, i.startDate as String].findAll{ it?.length() > 0 }.findResults{ it.sortName('$2') }.join(' :: '))
			year(i.startDate?.year)
			rating(i.rating)
			votes(i.ratingCount)
			plot(i.overview)
			runtime(i.runtime)
			mpaa(i.certification)
			id(i.id)
            imdbId(i.imdbId)
			i.genres.each{
				genre(it)
			}
			thumb(i.bannerUrl)
			premiered(i.startDate)
			status(i.status)
			studio(i.network)
			tvdb(id:i.id, "http://www.thetvdb.com/?tab=series&id=${i.id}")

			/** Kodi requires an <episodeguide> element with a TheTVDB API (v1) Series Record XML URL **/
			episodeguide {
				url(cache:"${i.id}.xml", "http://www.thetvdb.com/api/1D62F2F90030C444/series/${i.id}/all/${locale.language}.zip")
			}
		}
	}
	xml.saveAs(outputFile)
}

def fetchSeriesArtworkAndNfo(seriesDir, seasonDir, seriesId, season, override = false, locale = Locale.ENGLISH) {
	tryLogCatch {
		// fetch nfo
		def seriesInfo = TheTVDB.getSeriesInfo(seriesId, locale)
		fetchSeriesNfo(seriesDir.resolve('tvshow.nfo'), seriesInfo, locale)

		// fetch series banner, fanart, posters, etc
		['680x1000', null].findResult{ fetchSeriesBanner(seriesDir.resolve('poster.jpg'), seriesId, 'poster', it, null, override, locale) }
		['graphical', null].findResult{ fetchSeriesBanner(seriesDir.resolve('banner.jpg'), seriesId, 'series', it, null, override, locale) }

		// fetch highest resolution fanart
		['1920x1080', '1280x720', null].findResult{ fetchSeriesBanner(seriesDir.resolve('fanart.jpg'), seriesId, 'fanart', it, null, override, locale) }

		// fetch season banners
		if (seasonDir != seriesDir) {
			fetchSeriesBanner(seasonDir.resolve('poster.jpg'), seriesId, 'season', 'season', season, override, locale)
			fetchSeriesBanner(seasonDir.resolve('banner.jpg'), seriesId, 'seasonwide', 'seasonwide', season, override, locale)

			// folder image (resuse series poster if possible)
			copyIfPossible(seasonDir.resolve('poster.jpg'), seasonDir.resolve('folder.jpg'))
		}

		// fetch fanart
		['hdclearart', 'clearart'].findResult{ type -> fetchSeriesFanart(seriesDir.resolve('clearart.png'), seriesId, type, null, override, locale) }
		['hdtvlogo', 'clearlogo'].findResult{ type -> fetchSeriesFanart(seriesDir.resolve('logo.png'), seriesId, type, null, override, locale) }
		fetchSeriesFanart(seriesDir.resolve('landscape.jpg'), seriesId, 'tvthumb', null, override, locale)

		// fetch season fanart
		if (seasonDir != seriesDir) {
			fetchSeriesFanart(seasonDir.resolve('landscape.jpg'), seriesId, 'seasonthumb', season, override, locale)
		}

		// folder image (resuse series poster if possible)
		copyIfPossible(seriesDir.resolve('poster.jpg'), seriesDir.resolve('folder.jpg'))
	}
}



/**
 * TheMovieDB artwork/nfo helpers
 */
def fetchMovieArtwork(outputFile, movieInfo, category, override, locale) {
	if (outputFile.exists() && !override) {
		log.finest "Artwork already exists: $outputFile"
		return outputFile
	}

	// select and fetch artwork
	def artwork = TheMovieDB.getArtwork(movieInfo.id, category, locale)
	def selection = [locale.language, 'en', null].findResult{ lang -> artwork.find{ it.matches(lang) } }
	if (selection == null) {
		log.finest "Artwork not found: $outputFile"
		return null
	}
	log.finest "Fetching $outputFile => $selection"
	return selection.url.saveAs(outputFile)
}

def fetchAllMovieArtwork(outputFolder, prefix, movieInfo, category, override, locale) {
	// select and fetch artwork
	def artwork = TheMovieDB.getArtwork(movieInfo.id, category, locale)
	def selection = [locale.language, 'en', null].findResults{ lang -> artwork.findAll{ it.matches(lang) } }.flatten().unique()
	if (selection == null) {
		log.finest "Artwork not found: $outputFolder"
		return null
	}
	selection.eachWithIndex{ s, i ->
		def outputFile = new File(outputFolder, "${prefix}${i+1}.jpg")
		if (outputFile.exists() && !override) {
			log.finest "Artwork already exists: $outputFile"
		} else {
			log.finest "Fetching $outputFile => $s"
			s.url.saveAs(outputFile)
		}
	}
}

def fetchMovieFanart(outputFile, movieInfo, type, diskType, override, locale) {
	if (outputFile.exists() && !override) {
		log.finest "Fanart already exists: $outputFile"
		return outputFile
	}

	def artwork = FanartTV.getArtwork(movieInfo.id, "movies", locale)
	def fanart = [locale, null].findResult{ lang -> artwork.find{ it.matches(type, diskType, lang) } }
	if (fanart == null) {
		log.finest "Fanart not found: $outputFile / $type"
		return null
	}
	log.finest "Fetching $outputFile => $fanart"
	return fanart.url.saveAs(outputFile)
}

def fetchMovieNfo(outputFile, i, movieFile) {
	log.finest "Generate Movie NFO: $i.name [$i.id]"
	def mi = tryLogCatch{ movieFile ? MediaInfo.snapshot(movieFile) : null }
	def xml = XML {
		movie {
			title(i.name)
			originaltitle(i.originalName)
			sorttitle([i.collection, i.name, i.released as String].findAll{ it?.length() > 0 }.findResults{ it.sortName('$2') }.join(' :: '))
			set(i.collection)
			year(i.released?.year)
			rating(i.rating)
			votes(i.votes)
			mpaa(i.certification)
			id('tt' + (i.imdbId ?: 0).pad(7))
			plot(i.overview)
			tagline(i.tagline)
			runtime(i.runtime)
			i.genres.each{
				genre(it)
			}
			i.productionCountries.each{
				country(it)
			}
			i.productionCompanies.each{
				studio(it)
			}
			i.people.each{ p ->
				if (p.director) {
					director(p.name)
				} else if (p.writer) {
					credits(p.name)
				} else if (p.actor) { 
					actor {
						name(p.name)
						role(p.character)
					}
				} else if (p.department == 'Writing') {
					credits("$p.name ($p.job)")
				}
			}
			fileinfo {
				streamdetails {
					mi?.each { kind, streams ->
						def section = kind.toString().toLowerCase()
						streams.each { s ->
							if (section == 'video') {
								video {
									codec((s.'Encoded_Library/Name' ?: s.'CodecID/Hint' ?: s.'Format').replaceAll(/[ ].+/, '').trim())
									aspect(s.'DisplayAspectRatio')
									width(s.'Width')
									height(s.'Height')
								}
							}
							if (section == 'audio') {
								audio {
									codec((s.'CodecID/Hint' ?: s.'Format').replaceAll(/\p{Punct}/, '').trim())
									language(s.'Language/String3')
									channels(s.'Channel(s)_Original' ?: s.'Channel(s)')
								}
							}
							if (section == 'text') {
								subtitle { language(s.'Language/String3') }
							}
						}
					}
				}
			}
			imdb(id:"tt" + (i.imdbId ?: 0).pad(7), "http://www.imdb.com/title/tt" + (i.imdbId ?: 0).pad(7))
			tmdb(id:i.id, "http://www.themoviedb.org/movie/${i.id}")

			/** <trailer> element not supported due to lack of specification on acceptable values for both Plex and Kodi
			i.trailers.each{ t ->
				t.sources.each { s, v ->
					trailer(type:t.type, name:t.name, size:s, v)
				}
			}
			**/
		}
	}
	xml.saveAs(outputFile)
}

def fetchMovieArtworkAndNfo(movieDir, movie, movieFile = null, extras = false, override = false, locale = Locale.ENGLISH) {
	tryLogCatch {
		def movieInfo = TheMovieDB.getMovieInfo(movie, locale, true)

		// fetch nfo
		fetchMovieNfo(movieDir.resolve('movie.nfo'), movieInfo, movieFile)

		// generate url files
		if (extras) {
			[[db:'imdb', id:movieInfo.imdbId, url:'http://www.imdb.com/title/tt' + (movieInfo.imdbId ?: 0).pad(7)], [db:'tmdb', id:movieInfo.id, url:"http://www.themoviedb.org/movie/${movieInfo.id}"]].each{
				if (it.id > 0) {
					def content = "[InternetShortcut]\nURL=${it.url}\n"
					content.saveAs(movieDir.resolve("${it.db}.url"))
				}
			}
		}

		// fetch series banner, fanart, posters, etc
		fetchMovieArtwork(movieDir.resolve('poster.jpg'), movieInfo, 'posters', override, locale)
		fetchMovieArtwork(movieDir.resolve('fanart.jpg'), movieInfo, 'backdrops', override, locale)

		['hdmovieclearart', 'movieart'].findResult { type -> fetchMovieFanart(movieDir.resolve('clearart.png'), movieInfo, type, null, override, locale) }
		['hdmovielogo', 'movielogo'].findResult { type -> fetchMovieFanart(movieDir.resolve('logo.png'), movieInfo, type, null, override, locale) }
		['bluray', 'dvd', null].findResult { diskType -> fetchMovieFanart(movieDir.resolve('disc.png'), movieInfo, 'moviedisc', diskType, override, locale) }

		if (extras) {
			fetchAllMovieArtwork(movieDir.resolve('extrafanart'), 'fanart', movieInfo, 'backdrops', override, locale)
		}

		// folder image (reuse movie poster if possible)
		copyIfPossible(movieDir.resolve('poster.jpg'), movieDir.resolve('folder.jpg'))
	}
}

def copyIfPossible(File src, File dst) {
	if (src.exists() && !dst.exists()) {
		src.copyAs(dst)
	}
}


/*
 * git@askcloud.io
 */

/*
 * NOTE: This will determine if a specific season and episode should be ignored (this can happen if tvdb is wrong or we can not find a download for it
 * The directory coming into this is in the form 
 *    - C:\Plex\SeriesSearch\TVShows\Bates Motel
 *
 */
def cleanNFOAndPoster(seriesDirectory) {
	log.fine "Checking if NFO and poster.jpg need to be cleaned";

	if(!seriesDirectory)
	{
		return;
	}

    def nfoFile=seriesDirectory.toPath().resolve('tvshow.nfo').toFile();
    log.fine "NFO File: " + nfoFile.toString();

    def posterFile=seriesDirectory.toPath().resolve('poster.jpg').toFile();
    log.fine "Poster File: " + posterFile.toString();

    log.fine "Cleaning NFO and Poster Data $seriesDirectory"
    if(nfoFile.exists()){
        log.fine "Found tvshow.nfo: " + nfoFile.toString();
        tryLogCatch {
            nfoFile.delete();
        }  
    }

    if(posterFile.exists()){
        log.fine "Found poster.jpg: " + posterFile.toString();
        tryLogCatch {
            posterFile.delete();
        }  
    }        
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
 *
 * C:\Plex\AMC\TV Shows\American Horror Story
 * or
 * C:\Plex\SeriesSearch\TVShows\Bates Motel
 */
def loadTVShowNFO(nfoFile) {
	log.fine "loadTVShowNFO() ... Load the $nfoFile";

	tvshowNFO=null;
	if(!nfoFile)
	{
		return tvshowNFO;
	}

	log.fine "tvshow.nfo: ${nfoFile.toString()}"

	if(nfoFile.exists()){
		log.fine "Found tvshow.nfo: " + nfoFile.toString();
		tryQuietly {
			tvshowNFO = new XmlSlurper().parse(nfoFile);
			log.fine "Adding ${nfoFile.toString()}";			
		}  
	}

	return tvshowNFO;
}


/*
 *
 */
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