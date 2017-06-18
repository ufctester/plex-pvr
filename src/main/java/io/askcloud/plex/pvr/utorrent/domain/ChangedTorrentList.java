
package io.askcloud.plex.pvr.utorrent.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class ChangedTorrentList {

    private Integer build;
    private List<String> files = new ArrayList<>();
    private List<Object> label = new ArrayList<>();
    private List<SingleListTorrent> torrentp = new ArrayList<>();
    private List<String> torrentm = new ArrayList<>();
    private String torrentc;
    private List<Object> rssfeedp = new ArrayList<>();
    private List<Object> rssfeedm = new ArrayList<>();
    private List<Object> rssfilterp = new ArrayList<>();
    private List<Object> rssfilterm = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }

    public ChangedTorrentList withBuild(Integer build) {
        this.build = build;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public ChangedTorrentList withFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public List<Object> getLabel() {
        return label;
    }

    public void setLabel(List<Object> label) {
        this.label = label;
    }

    public ChangedTorrentList withLabel(List<Object> label) {
        this.label = label;
        return this;
    }

    public List<SingleListTorrent> getTorrentp() {
        return torrentp;
    }

    public void setTorrentp(List<SingleListTorrent> torrentp) {
        this.torrentp = torrentp;
    }

    public ChangedTorrentList withTorrentp(List<SingleListTorrent> torrentp) {
        this.torrentp = torrentp;
        return this;
    }

    public List<String> getTorrentm() {
        return torrentm;
    }

    public void setTorrentm(List<String> torrentm) {
        this.torrentm = torrentm;
    }

    public ChangedTorrentList withTorrentm(List<String> torrentm) {
        this.torrentm = torrentm;
        return this;
    }

    public String getTorrentc() {
        return torrentc;
    }

    public void setTorrentc(String torrentc) {
        this.torrentc = torrentc;
    }

    public ChangedTorrentList withTorrentc(String torrentc) {
        this.torrentc = torrentc;
        return this;
    }

    public List<Object> getRssfeedp() {
        return rssfeedp;
    }

    public void setRssfeedp(List<Object> rssfeedp) {
        this.rssfeedp = rssfeedp;
    }

    public ChangedTorrentList withRssfeedp(List<Object> rssfeedp) {
        this.rssfeedp = rssfeedp;
        return this;
    }

    public List<Object> getRssfeedm() {
        return rssfeedm;
    }

    public void setRssfeedm(List<Object> rssfeedm) {
        this.rssfeedm = rssfeedm;
    }

    public ChangedTorrentList withRssfeedm(List<Object> rssfeedm) {
        this.rssfeedm = rssfeedm;
        return this;
    }

    public List<Object> getRssfilterp() {
        return rssfilterp;
    }

    public void setRssfilterp(List<Object> rssfilterp) {
        this.rssfilterp = rssfilterp;
    }

    public ChangedTorrentList withRssfilterp(List<Object> rssfilterp) {
        this.rssfilterp = rssfilterp;
        return this;
    }

    public List<Object> getRssfilterm() {
        return rssfilterm;
    }

    public void setRssfilterm(List<Object> rssfilterm) {
        this.rssfilterm = rssfilterm;
    }

    public ChangedTorrentList withRssfilterm(List<Object> rssfilterm) {
        this.rssfilterm = rssfilterm;
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
