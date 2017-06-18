package io.askcloud.plex.pvr.utorrent;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import fj.Effect;
import fj.F;
import io.askcloud.plex.pvr.utorrent.domain.ChangedTorrentList;
import io.askcloud.plex.pvr.utorrent.domain.FilesRequestResult;
import io.askcloud.plex.pvr.utorrent.domain.SingleListTorrent;
import io.askcloud.plex.pvr.utorrent.domain.TorrentList;

/**
 * Created with IntelliJ IDEA. User: finkel Date: 28.12.12 Time: 11:15 To change
 * this template use File | Settings | File Templates.
 */
public class TestConnecter {

    public static void main(String[] agrs) throws IOException, InterruptedException {
        UTorrent instance = UTorrent.getInstance("192.168.0.22", 8052, "admin", "wid24esb");
        final TorrentList torrentList = instance.getTorrentList();

        
        for (SingleListTorrent torrent : torrentList.getTorrents()) {
        	System.out.println("========" + torrent.getName() + "========");
        	System.out.println("Hash: " + torrent.getHash());
        	System.out.println("Status: " + torrent.getStatuses());
        	System.out.println("Name: " + torrent.getName());
        	System.out.println("Size: " + torrent.getTorrentSize());
        	System.out.println("Percent: " + torrent.getPercentReady());
        	System.out.println("Download: " + torrent.getDownloaded());
        	System.out.println("Uploaded: " + torrent.getUploaded());
        	System.out.println("Ratio: " + torrent.getCoef());
        	System.out.println("Upload Speed: " + torrent.getUploadSpeed());
        	System.out.println("Download Speed: " + torrent.getDownloadSpeed());
        	System.out.println("ETA: " + torrent.getEta());
        	System.out.println("Label: " + torrent.getLabel());
        	System.out.println("Peers Connected: " + torrent.getPeersConnected());
        	System.out.println("Peers In Swarm: " + torrent.getPeersInSwarm());
        	
        	System.out.println("Seeds Connected : " + torrent.getSeedsConnected());
        	System.out.println("Seeds In Swarm: " + torrent.getSeedsInSwarm());
        	System.out.println("Availability: " + torrent.getAvailability());
        	System.out.println("Torrent Queue Order: " + torrent.getQueueNum());
        	System.out.println("Remaining: " + (torrent.getLeftToDownload() / 1024)/1024);
        	
        	
		}
        
//        for (SingleListTorrent singleListTorrent : singleListTorrents) {
//            FilesRequestResult filesByTorrentHash = instance.getFilesByTorrentHash(singleListTorrent.getHash());
//            System.out.println(filesByTorrentHash);
//        }

    }
}
