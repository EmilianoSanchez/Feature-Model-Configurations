package com.emiliano.fmframework.core;

import java.io.Serializable;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The class Feature represents a selectable element of a model. A feature is
 * compound by an identifier name, an optional set of attributes (each attribute
 * with a name and Object value), and an optional FeatureAction. element.
 *
 * @param <SoftElement>
 *            the generic type
 */
public class Feature implements Cloneable, Serializable {

	/** The name. */
	private String name;

	/** The attributes. */
	private double attributes[];
	
	private HashMap<String, Object> properties;

	public Feature() {
		this(null,new double[]{});
	}

	public Feature(String name) {
		this(name,new double[]{});
	}
	
	public Feature(String name,HashMap<String,Object> properties) {
		this(name,properties,new double[]{});
	}
	
	public Feature(double... attributos) {
		this(null,attributos);
	}
	
	public Feature(String name,double... attributos) {
		this(name, new HashMap<>(),attributos);
	}
	
	public Feature(String name,HashMap<String,Object> properties,double... attributos) {
		this.name = name;
		this.attributes = attributos;
		this.properties = properties;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the attribute.
	 *
	 * @param name
	 *            the name
	 * @return the attribute
	 */
	public double getAttribute(int idAttribute) {
		return this.attributes[idAttribute];
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public double[] getAttributes() {
		return this.attributes;
	}
	
	public int getNumAttributes(){
		return this.attributes.length;
	}

	public void setAttributes(double[] attributes) {
		this.attributes=attributes;
	}
	
	public void setAttribute(int idAttribute, double value) {
		this.attributes[idAttribute]=value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		builder.append(this.name+"={");
		for(double attribute:this.attributes)
			builder.append(attribute+";");
		builder.append("}");
		return builder.toString();
	}

	/**
	 * Copy.
	 *
	 * @param other
	 *            the configuration to copy
	 */
	@SuppressWarnings("unchecked")
	public void copy(Feature other) {
		this.name = other.name;
		this.attributes = other.attributes.clone();
		this.properties = (HashMap<String, Object>) other.properties.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Feature clone() {
		Feature clone = new Feature();
		clone.copy(this);
		return clone;
	}
	
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	
	public Object getProperty(String key) {
		return this.properties.get(key);
	};
}
