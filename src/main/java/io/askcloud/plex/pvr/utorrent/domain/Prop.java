package io.askcloud.plex.pvr.utorrent.domain;

import javax.annotation.Generated;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated( "com.googlecode.jsonschema2pojo" )
public class Prop {

	private String hash;
	private String trackers;
	private Integer ulrate;
	private Integer dlrate;
	private Integer superseed;
	private Integer dht;
	private Integer pex;
	private Integer seed_override;
	private Integer seed_ratio;
	private Integer seed_time;
	private Integer ulslots;
	private Integer seed_num;

	public String getHash() {
		return hash;
	}

	public void setHash( String hash ) {
		this.hash = hash;
	}

	public Prop withHash( String hash ) {
		this.hash = hash;
		return this;
	}

	public String getTrackers() {
		return trackers;
	}

	public void setTrackers( String trackers ) {
		this.trackers = trackers;
	}

	public Prop withTrackers( String trackers ) {
		this.trackers = trackers;
		return this;
	}

	public Integer getUlrate() {
		return ulrate;
	}

	public void setUlrate( Integer ulrate ) {
		this.ulrate = ulrate;
	}

	public Prop withUlrate( Integer ulrate ) {
		this.ulrate = ulrate;
		return this;
	}

	public Integer getDlrate() {
		return dlrate;
	}

	public void setDlrate( Integer dlrate ) {
		this.dlrate = dlrate;
	}

	public Prop withDlrate( Integer dlrate ) {
		this.dlrate = dlrate;
		return this;
	}

	public Integer getSuperseed() {
		return superseed;
	}

	public void setSuperseed( Integer superseed ) {
		this.superseed = superseed;
	}

	public Prop withSuperseed( Integer superseed ) {
		this.superseed = superseed;
		return this;
	}

	public Integer getDht() {
		return dht;
	}

	public void setDht( Integer dht ) {
		this.dht = dht;
	}

	public Prop withDht( Integer dht ) {
		this.dht = dht;
		return this;
	}

	public Integer getPex() {
		return pex;
	}

	public void setPex( Integer pex ) {
		this.pex = pex;
	}

	public Prop withPex( Integer pex ) {
		this.pex = pex;
		return this;
	}

	public Integer getSeed_override() {
		return seed_override;
	}

	public void setSeed_override( Integer seed_override ) {
		this.seed_override = seed_override;
	}

	public Prop withSeed_override( Integer seed_override ) {
		this.seed_override = seed_override;
		return this;
	}

	public Integer getSeed_ratio() {
		return seed_ratio;
	}

	public void setSeed_ratio( Integer seed_ratio ) {
		this.seed_ratio = seed_ratio;
	}

	public Prop withSeed_ratio( Integer seed_ratio ) {
		this.seed_ratio = seed_ratio;
		return this;
	}

	public Integer getSeed_time() {
		return seed_time;
	}

	public void setSeed_time( Integer seed_time ) {
		this.seed_time = seed_time;
	}

	public Prop withSeed_time( Integer seed_time ) {
		this.seed_time = seed_time;
		return this;
	}

	public Integer getUlslots() {
		return ulslots;
	}

	public void setUlslots( Integer ulslots ) {
		this.ulslots = ulslots;
	}

	public Prop withUlslots( Integer ulslots ) {
		this.ulslots = ulslots;
		return this;
	}

	public Integer getSeed_num() {
		return seed_num;
	}

	public void setSeed_num( Integer seed_num ) {
		this.seed_num = seed_num;
	}

	public Prop withSeed_num( Integer seed_num ) {
		this.seed_num = seed_num;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString( this );
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode( this );
	}

	@Override
	public boolean equals( Object other ) {
		return EqualsBuilder.reflectionEquals( this, other );
	}


}
