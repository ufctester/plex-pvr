

C:\gitbash\opt\filebot\filebot.exe -script C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\scripts\\create-nfo.groovy --def seriesDirectory="C:\tmp\TVShows" --log info

C:\gitbash\opt\filebot\filebot.exe -script C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\scripts\\find-series-episodes-missing.groovy C:\\Plex\\SeriesSearch\\TVShows --output C:\\gitbash\\opt\\eclipse\\workspace\\plex-pvr\\downloads\\download.json --log info

C:\gitbash\opt\filebot\filebot.exe -script fn:amc --output C:\\Plex\\AMC --action copy -non-strict C:\\gitbash\\opt\\kodi\\completed --conflict override --def movieFormat="C:\\Plex\\AMC\\Movies\\{fn}" subtitles=en music=y artwork=n --log-file c:\\gitbash\\opt\\eclipse\\workspace\\askcloud-pvr\\scripts\\filebot\\amc.log --def excludeList=c:\\gitbash\\opt\\eclipse\\workspace\\askcloud-pvr\\scripts\\filebot\\amc-exclude.txt --def minFileSize=40 --log info