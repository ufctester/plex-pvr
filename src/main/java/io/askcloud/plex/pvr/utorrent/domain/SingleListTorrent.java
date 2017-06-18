package io.askcloud.plex.pvr.utorrent.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Created with IntelliJ IDEA. User: finkel Date: 28.12.12 Time: 13:04 To change
 * this template use File | Settings | File Templates.
 */
public class SingleListTorrent extends ArrayList<String> implements Comparable {

    private String hash;
    private List<Status> statuses;
    private String name;
    private long torrentSize;
    private BigDecimal percentReady;
    private long downloaded;
    private long uploaded;
    private BigDecimal coef;
    private long uploadSpeed;
    private long leftToDownload;
    private long downloadSpeed;
    private long eta;
    private long queueNum;
    private String label;
    private long peersConnected;
    private long seedsConnected;
    private long peersInSwarm;
    private long seedsInSwarm;
    private int availability;

    @Override
    public boolean add(String s) {
        boolean add = true;
        if (size() <= 18) {
            add = super.add(s);
        } else {
            return add;
        }
        makeOverrideAction(s, size());

        return add;
    }

    @Override
    public String set(int index, String element) {
        String set = super.set(index, element);
        makeOverrideAction(element, index + 1);
        return set;
    }

    private void makeOverrideAction(String s, int size) {
        switch (size) {
            case 1:
                hash = s;
                break;
            case 2:
                int status = parseInt(s);
                statuses = Status.byInt(status);
                break;
            case 3:
                name = s;
                break;
            case 4:
                torrentSize = parseLong(s);
                break;
            case 5:
                percentReady = new BigDecimal(s).divide(BigDecimal.TEN);
                break;
            case 6:
                downloaded = parseLong(s);
                break;
            case 7:
                uploaded = parseLong(s);
                break;
            case 8:
                coef = new BigDecimal(s).divide(BigDecimal.TEN);
                break;
            case 9:
                uploadSpeed = parseLong(s);
                break;
            case 10:
                downloadSpeed = parseLong(s);
                break;
            case 11:
                eta = parseLong(s);
                break;
            case 12:
                label = s;
                break;
            case 13:
                peersConnected = parseLong(s);
                break;
            case 14:
                peersInSwarm = parseLong(s);
                break;
            case 15:
                seedsConnected = parseLong(s);
                break;
            case 16:
                seedsInSwarm = parseLong(s);
                break;
            case 17:
                availability = parseInt(s);
                break;
            case 18:
                queueNum = parseLong(s);
                break;
            case 19:
                try {
                    leftToDownload = "".equals(s) ? 0 : parseLong(s);
                } catch (Exception e) {
                    System.out.println("Unable to parse value " + s);
                }
                break;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, false, Object.class);
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public BigDecimal getCoef() {
        return coef;
    }

    public void setCoef(BigDecimal coef) {
        this.coef = coef;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getLeftToDownload() {
        return leftToDownload;
    }

    public void setLeftToDownload(long leftToDownload) {
        this.leftToDownload = leftToDownload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPeersConnected() {
        return peersConnected;
    }

    public void setPeersConnected(long peersConnected) {
        this.peersConnected = peersConnected;
    }

    public long getPeersInSwarm() {
        return peersInSwarm;
    }

    public void setPeersInSwarm(long peersInSwarm) {
        this.peersInSwarm = peersInSwarm;
    }

    public BigDecimal getPercentReady() {
        return percentReady;
    }

    public void setPercentReady(BigDecimal percentReady) {
        this.percentReady = percentReady;
    }

    public long getQueueNum() {
        return queueNum;
    }

    public void setQueueNum(long queueNum) {
        this.queueNum = queueNum;
    }

    public long getSeedsConnected() {
        return seedsConnected;
    }

    public void setSeedsConnected(long seedsConnected) {
        this.seedsConnected = seedsConnected;
    }

    public long getSeedsInSwarm() {
        return seedsInSwarm;
    }

    public void setSeedsInSwarm(long seedsInSwarm) {
        this.seedsInSwarm = seedsInSwarm;
    }

    public long getTorrentSize() {
        return torrentSize;
    }

    public void setTorrentSize(long torrentSize) {
        this.torrentSize = torrentSize;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(long uploaded) {
        this.uploaded = uploaded;
    }

    public long getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(long uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false, Object.class);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(33, 31, this, false, Object.class);
    }

    @Override
    public int compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

    public enum Status {

        LAUNCHED(1), CHECKING(2), LAUNCHING_AFTER_CHECK(4), CHECKED(8), ERROR(16), PAUSED(32), QUEUED(64), DOWNLOADED(128);
        private static final List<Integer> statusInts = Arrays.asList(128, 64, 32, 16, 8, 4, 2, 1);
        private int myStatusInt;

        Status(int i) {
            myStatusInt = i;
        }

        public static List<Status> byInt(int statusInt) {
            List<Status> statuses = new ArrayList<>();
            for (Integer integer : statusInts) {
                if (statusInt >= integer) {
                    statuses.add(getStatus(integer));
                    statusInt -= integer;
                }
            }
            Collections.sort(statuses, new Comparator<Status>() {
                @Override
                public int compare(Status o1, Status o2) {
                    return o1.getStatusInt() - o2.getStatusInt();
                }
            });
            return statuses;
        }

        private static Status getStatus(int statusInt) {
            for (Status status : Status.values()) {
                if (status.getStatusInt() == statusInt) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unsupported status. Supported statuses are " + statusInts);
        }

        public int getStatusInt() {
            return myStatusInt;
        }
    }

    public void cloneWithoutHash(SingleListTorrent changed) {
        List<Status> statuses1 = changed.getStatuses();
        int finalStatus = 0;
        for (Status status : statuses1) {
            finalStatus += status.getStatusInt();
        }
        set(1, valueOf(finalStatus));
        set(2, changed.name);
        set(3, valueOf(changed.torrentSize));
        set(4, changed.percentReady.toPlainString());
        set(5, valueOf(changed.downloaded));
        set(6, valueOf(changed.uploaded));
        set(7, changed.coef.toPlainString());
        set(8, valueOf(changed.uploadSpeed));
        set(9, valueOf(changed.downloadSpeed));
        set(10, valueOf(changed.eta));
        set(11, changed.label);
        set(12, valueOf(changed.peersConnected));
        set(13, valueOf(changed.peersInSwarm));
        set(14, valueOf(changed.seedsConnected));
        set(15, valueOf(changed.seedsInSwarm));
        set(16, valueOf(changed.availability));
        set(17, valueOf(changed.queueNum));
    }
}
