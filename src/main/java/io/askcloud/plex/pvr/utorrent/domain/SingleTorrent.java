package io.askcloud.plex.pvr.utorrent.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated( "com.googlecode.jsonschema2pojo" )
public class SingleTorrent {

	private Integer build;
	@Valid
	private List<Prop> props = new ArrayList<Prop>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Integer getBuild() {
		return build;
	}

	public void setBuild( Integer build ) {
		this.build = build;
	}

	public SingleTorrent withBuild( Integer build ) {
		this.build = build;
		return this;
	}

	public List<Prop> getProps() {
		return props;
	}

	
	public void setProps( List<Prop> props ) {
		this.props = props;
	}

	public SingleTorrent withProps( List<Prop> props ) {
		this.props = props;
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

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperties( String name, Object value ) {
		this.additionalProperties.put( name, value );
	}

}
