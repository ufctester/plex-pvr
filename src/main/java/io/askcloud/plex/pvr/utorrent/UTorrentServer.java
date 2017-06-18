/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.askcloud.plex.pvr.utorrent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author finkel
 */
public class UTorrentServer {

	private String hostName;
	private int port;
	private String login;
	private String password;

	public UTorrentServer( String hostName, int port, String login, String password ) {
		this.hostName = hostName;
		this.port = port;
		this.login = login;
		this.password = password;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort( int port ) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode( this );
	}

	@Override
	public boolean equals( Object obj ) {
		return EqualsBuilder.reflectionEquals( this, obj );
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString( this, ToStringStyle.SHORT_PREFIX_STYLE );
	}

	public String getLogin() {
		return login;
	}

	public void setLogin( String login ) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}
}
