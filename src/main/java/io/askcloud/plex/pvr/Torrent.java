/**
 * 
 */
package io.askcloud.plex.pvr;

/**
 * @author apps
 * http://help.utorrent.com/customer/en/portal/articles/1573947-torrent-labels-list---webapi
 *     HASH (string),
 *     STATUS* (integer),
 *     NAME (string),
 *     SIZE (integer in bytes),
 *     PERCENT PROGRESS (integer in per mils),
 *     DOWNLOADED (integer in bytes),
 *     UPLOADED (integer in bytes),
 *     RATIO (integer in per mils),
 *     UPLOAD SPEED (integer in bytes per second),
 *     DOWNLOAD SPEED (integer in bytes per second),
 *     ETA (integer in seconds),
 *     LABEL (string),
 *     PEERS CONNECTED (integer),
 *     PEERS IN SWARM (integer),
 *     SEEDS CONNECTED (integer),
 *     SEEDS IN SWARM (integer),
 *     AVAILABILITY (integer in 1/65535ths),
 *     TORRENT QUEUE ORDER (integer),
 *     REMAINING (integer in bytes)
 *
 */
public class Torrent {

	private String hash; //ASH (string)
	private int status; //STATUS* (integer)
	private String name; //NAME (string)
	private int size; //SIZE (integer in bytes)
	private int percentProgress; //PERCENT PROGRESS (integer in per mils)
	private int downloaded; //DOWNLOADED (integer in bytes)
	private int uploaded; //UPLOADED (integer in bytes)
	private int ratio; //RATIO (integer in per mils)
	private int uploadSpeed; //UPLOAD SPEED (integer in bytes per second)
	private int downloadSpeed; //DOWNLOAD SPEED (integer in bytes per second)
	private int eta; //ETA (integer in seconds)
	private String label; //LABEL (string)
	private int peersConnected; //PEERS CONNECTED (integer)
	private int peersInSwarm; //PEERS IN SWARM (integer)
	private int seedsConnected; //SEEDS CONNECTED (integer)
	private int seedsInSwarm; //SEEDS IN SWARM (integer)
	private int availability; //AVAILABILITY (integer in 1/65535ths)
	private int order; //TORRENT QUEUE ORDER (integer)
	private int remaining; //REMAINING (integer in bytes)	
	private String pirvatebayURL; //Only used for adding new ones
	
	
	/**
	 * 
	 */
	public Torrent() {
		super();
	}


	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}


	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}


	/**
	 * List of statuses:
	 * 128 = Stopped (file is also not fully hash checked) Loaded
	 * 130 = Checking Loaded, Checking
	 * 152 = Error Loaded, Error, Checked
	 * 169 = Paused (torrent is forced) Loaded, Paused, Checked, Started
	 * 233 = Paused (torrent is not forced) Loaded, Queued, Paused, Checked, Started
	 * 
	 * If PERCENT PROGRESS = 1000:
	 * 136 = Finished Loaded, Checked
	 * 137 = [F] Seeding Loaded, Checked, Started
	 * 200 = Queued Seed Loaded, Queued, Checked
	 * 201 = Seeding Loaded, Queued, Checked, Started
	 * 
	 * If PERCENT PROGRESS < 1000:
	 * 136 = Stopped (file is fully hash checked) Loaded, Checked
	 * 137 = [F] Downloading Loaded, Checked, Started
	 * 200 = Queued Loaded, Queued, Checked
	 * 201 = Downloading Loaded, Queued, Checked, Started
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}


	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}


	/**
	 * List of statuses:
	 * 128 = Stopped (file is also not fully hash checked) Loaded
	 * 130 = Checking Loaded, Checking
	 * 152 = Error Loaded, Error, Checked
	 * 169 = Paused (torrent is forced) Loaded, Paused, Checked, Started
	 * 233 = Paused (torrent is not forced) Loaded, Queued, Paused, Checked, Started
	 * 
	 * If PERCENT PROGRESS = 1000:
	 * 136 = Finished Loaded, Checked
	 * 137 = [F] Seeding Loaded, Checked, Started
	 * 200 = Queued Seed Loaded, Queued, Checked
	 * 201 = Seeding Loaded, Queued, Checked, Started
	 * 
	 * If PERCENT PROGRESS < 1000:
	 * 136 = Stopped (file is fully hash checked) Loaded, Checked
	 * 137 = [F] Downloading Loaded, Checked, Started
	 * 200 = Queued Loaded, Queued, Checked
	 * 201 = Downloading Loaded, Queued, Checked, Started
	 * 
	 * 
	 * @return the percentProgress
	 */
	public int getPercentProgress() {
		return percentProgress;
	}

	public boolean isComplete()
	{
		return (getPercentProgress() == 1000);
	}
	/**
	 * @param percentProgress the percentProgress to set
	 */
	public void setPercentProgress(int percentProgress) {
		this.percentProgress = percentProgress;
	}


	/**
	 * @return the downloaded
	 */
	public int getDownloaded() {
		return downloaded;
	}


	/**
	 * @param downloaded the downloaded to set
	 */
	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}


	/**
	 * @return the uploaded
	 */
	public int getUploaded() {
		return uploaded;
	}


	/**
	 * @param uploaded the uploaded to set
	 */
	public void setUploaded(int uploaded) {
		this.uploaded = uploaded;
	}


	/**
	 * @return the ratio
	 */
	public int getRatio() {
		return ratio;
	}


	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}


	/**
	 * @return the uploadSpeed
	 */
	public int getUploadSpeed() {
		return uploadSpeed;
	}


	/**
	 * @param uploadSpeed the uploadSpeed to set
	 */
	public void setUploadSpeed(int uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}


	/**
	 * @return the downloadSpeed
	 */
	public int getDownloadSpeed() {
		return downloadSpeed;
	}


	/**
	 * @param downloadSpeed the downloadSpeed to set
	 */
	public void setDownloadSpeed(int downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}


	/**
	 * @return the eta
	 */
	public int getEta() {
		return eta;
	}


	/**
	 * @param eta the eta to set
	 */
	public void setEta(int eta) {
		this.eta = eta;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * @return the peersConnected
	 */
	public int getPeersConnected() {
		return peersConnected;
	}


	/**
	 * @param peersConnected the peersConnected to set
	 */
	public void setPeersConnected(int peersConnected) {
		this.peersConnected = peersConnected;
	}


	/**
	 * @return the peersInSwarm
	 */
	public int getPeersInSwarm() {
		return peersInSwarm;
	}


	/**
	 * @param peersInSwarm the peersInSwarm to set
	 */
	public void setPeersInSwarm(int peersInSwarm) {
		this.peersInSwarm = peersInSwarm;
	}


	/**
	 * @return the seedsConnected
	 */
	public int getSeedsConnected() {
		return seedsConnected;
	}


	/**
	 * @param seedsConnected the seedsConnected to set
	 */
	public void setSeedsConnected(int seedsConnected) {
		this.seedsConnected = seedsConnected;
	}


	/**
	 * @return the seedsInSwarm
	 */
	public int getSeedsInSwarm() {
		return seedsInSwarm;
	}


	/**
	 * @param seedsInSwarm the seedsInSwarm to set
	 */
	public void setSeedsInSwarm(int seedsInSwarm) {
		this.seedsInSwarm = seedsInSwarm;
	}


	/**
	 * @return the availability
	 */
	public int getAvailability() {
		return availability;
	}


	/**
	 * @param availability the availability to set
	 */
	public void setAvailability(int availability) {
		this.availability = availability;
	}


	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}


	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}


	/**
	 * @return the remaining
	 */
	public int getRemaining() {
		return remaining;
	}


	/**
	 * @param remaining the remaining to set
	 */
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	@Override
	public String toString() {

		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("hash: " + getHash() + " ");
		sBuffer.append("status: " + getStatus() + " ");
		sBuffer.append("name: " + getName() + " ");
		sBuffer.append("size: " + getSize() + " ");
		sBuffer.append("percentProgress: " + getPercentProgress() + " ");
		sBuffer.append("downloaded: " + getDownloaded() + " ");
		sBuffer.append("uploaded: " + getUploaded() + " ");
		sBuffer.append("ratio: " + getRatio() + " ");
		sBuffer.append("uploadSpeed: " + getUploadSpeed() + " ");
		sBuffer.append("downloadSpeed: " + getDownloadSpeed() + " ");
		sBuffer.append("eta: " + getEta() + " ");
		sBuffer.append("label: " + getLabel() + " ");
		sBuffer.append("peersConnected: " + getPeersConnected() + " ");
		sBuffer.append("peersInSwarm: " + getPeersInSwarm() + " ");
		sBuffer.append("seedsConnected: " + getSeedsConnected() + " ");
		sBuffer.append("seedsInSwarm: " + getSeedsInSwarm() + " ");
		sBuffer.append("availability: " + getAvailability() + " ");
		sBuffer.append("order: " + getOrder() + " ");
		sBuffer.append("remaining: " + getRemaining() + " ");
		sBuffer.append("complete: " + isComplete() + " ");
		
		if(getPirvatebayURL()!=null)
		{
			sBuffer.append("piratebayURL: " + getPirvatebayURL() + " ");
		}
		
		return sBuffer.toString();
	}


	/**
	 * @return the pirvatebayURL
	 */
	public String getPirvatebayURL() {
		return pirvatebayURL;
	}


	/**
	 * @param pirvatebayURL the pirvatebayURL to set
	 */
	public void setPirvatebayURL(String pirvatebayURL) {
		this.pirvatebayURL = pirvatebayURL;
	}

}

