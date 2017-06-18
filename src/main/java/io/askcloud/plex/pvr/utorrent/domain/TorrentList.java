
package io.askcloud.plex.pvr.utorrent.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class TorrentList {

    private Integer build;
    private List<Object> label = new ArrayList<>();
    private List<SingleListTorrent> torrents = new ArrayList<>();
    private String torrentc;
    private List<Object> rssfeeds = new ArrayList<>();
    private List<Object> rssfilters = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }

    public TorrentList withBuild(Integer build) {
        this.build = build;
        return this;
    }

    
    public List<Object> getLabel() {
        return label;
    }

    
    public void setLabel(List<Object> label) {
        this.label = label;
    }

    public TorrentList withLabel(List<Object> label) {
        this.label = label;
        return this;
    }

    
    public List<SingleListTorrent> getTorrents() {
        return torrents;
    }

    public void setTorrents(List<SingleListTorrent> torrents) {
        this.torrents = torrents;
    }

    public TorrentList withTorrents(List<SingleListTorrent> torrents) {
        this.torrents = torrents;
        return this;
    }

    public String getTorrentc() {
        return torrentc;
    }

    public void setTorrentc(String torrentc) {
        this.torrentc = torrentc;
    }

    public TorrentList withTorrentc(String torrentc) {
        this.torrentc = torrentc;
        return this;
    }

    
    public List<Object> getRssfeeds() {
        return rssfeeds;
    }

    public void setRssfeeds(List<Object> rssfeeds) {
        this.rssfeeds = rssfeeds;
    }

    public TorrentList withRssfeeds(List<Object> rssfeeds) {
        this.rssfeeds = rssfeeds;
        return this;
    }

    public List<Object> getRssfilters() {
        return rssfilters;
    }

    public void setRssfilters(List<Object> rssfilters) {
        this.rssfilters = rssfilters;
    }

    public TorrentList withRssfilters(List<Object> rssfilters) {
        this.rssfilters = rssfilters;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
