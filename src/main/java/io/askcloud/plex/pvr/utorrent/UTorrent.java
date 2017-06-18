/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.askcloud.plex.pvr.utorrent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.askcloud.plex.pvr.utorrent.domain.ChangedTorrentList;
import io.askcloud.plex.pvr.utorrent.domain.FilesRequestResult;
import io.askcloud.plex.pvr.utorrent.domain.TorrentList;

//import ru.finkel.utorrentaccess.domain.TorrentFiles;
/**
 * @author finkel
 */
public class UTorrent {

    public static final String CHARSET = "UTF-8";
    public static final String HTTP = "http://";
    private UTorrentServer server;
    private static final Gson GSON = new GsonBuilder().create();
    private static DefaultHttpClient httpClient = new DefaultHttpClient();
    private String token;
    private static final Map<UTorrentServer, UTorrent> MAP = new HashMap<>();

    private UTorrent() {
    }

    public static UTorrent getInstance(String host, int port, String login, String password) throws IOException {
        UTorrentServer uTorrentServer = new UTorrentServer(host, port, login, password);
        if (MAP.get(uTorrentServer) != null) {
            return MAP.get(uTorrentServer);
        }
        UTorrent instance = new UTorrent();
        instance.server = uTorrentServer;
        instance.token = instance.getToken(host, port, login, password);
        MAP.put(uTorrentServer, instance);
        return instance;
    }

    private String getToken(String host, int port, String userName, String password) throws IOException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);
        httpClient.setCredentialsProvider(provider);
        String tokenUrl = HTTP + host + ":" + port + "/gui/token.html";
        String s = Http.get(tokenUrl).followRedirects(true).use(httpClient).charset(CHARSET).asString();
        Document parse = Jsoup.parse(s);
        s = parse.body().child(0).text();

        return s;
    }

    /**
     * Utility method to get initial list of torrents
     *
     * @return
     * @throws IOException
     */
    public TorrentList getTorrentList() throws IOException {
        return GSON.fromJson(getTorrentsListJson(), TorrentList.class);
    }

    public ChangedTorrentList getChangedTorrentList(String cacheId) throws IOException {
        return GSON.fromJson(getChangedTorrentListJson(cacheId), ChangedTorrentList.class);
    }

    /**
     * List files in torrent (in JSON format)
     *
     * @param hash
     * @return
     * @throws IOException
     */
    String getFilesByTorrentHashJson(String hash) throws IOException {
        String url = generateFirstPartOfRequestUrl() + generateActionPart(Action.GET_FILES) + "&hash=" + hash;
        final String text = generateJsonReturningRequest(url);
        return text;
    }

    public FilesRequestResult getFilesByTorrentHash(String hash) throws IOException {

        final FilesRequestResult result = GSON.fromJson(getFilesByTorrentHashJson(hash), FilesRequestResult.class);
        return result;
    }

    /**
     * Utility method to get list of only changed torrents (JSON)
     *
     * @param cacheId
     * @return
     * @throws IOException
     */
    private String getChangedTorrentListJson(String cacheId) throws IOException {
        return generateJsonReturningRequest(generateChangedListUrl(cacheId));
    }

    private String getTorrentsListJson() throws IOException {
        return generateJsonReturningRequest(generateListUrl());
    }

    private String generateListUrl() {
        return generateFirstPartOfRequestUrl() + "&list=1";
    }

    private String generateChangedListUrl(String cacheId) {
        return generateFirstPartOfRequestUrl() + "&list=1&cid=" + cacheId;
    }

    private String generateJsonReturningRequest(String url) throws IOException {
        return Http.get(url).followRedirects(true).use(httpClient).charset("UTF-8").asString();
    }

    /**
     * Utility method to generate &action part by Action
     *
     * @param action
     * @return e.g. &action=getfiles
     */
    public String generateActionPart(Action action) {
        return "&action=" + action.getActionText();
    }

    private String generateFirstPartOfRequestUrl() {
        return HTTP + server.getHostName() + ":" + server.getPort() + "/gui/?token=" + token;
    }

    /**
     * Enumeration of actions
     */
    public enum Action {

        GET_FILES("getfiles");
        private final String actionText;

        Action(String actionText) {
            this.actionText = actionText;
        }

        public String getActionText() {
            return actionText;
        }
    }
}
