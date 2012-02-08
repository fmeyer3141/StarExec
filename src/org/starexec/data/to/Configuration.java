package org.starexec.data.to;

import org.starexec.util.Util;

import com.google.gson.annotations.Expose;

/**
 * Represents a configuration, which is a run script to execute a solver with specific parameters
 * 
 * @author Tyler Jensen
 */
public class Configuration extends Identifiable {		
	private int solverId = -1;
	@Expose private String name;
	@Expose private String description = "no description";
	
	/**
	 * @return the id of the solver the configuration beints to
	 */
	public int getSolverId() {
		return solverId;
	}
	/**
	 * @param solverId the id of the solver to set
	 */
	public void setSolverId(int solverId) {
		this.solverId = solverId;
	}
	/**
	 * @return the name of the configuration
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set for the configuration
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the user defined description for the configuration
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set for the configuration
	 */
	public void setDescription(String description) {
		if(!Util.isNullOrEmpty(description)) {
			this.description = description;
		}
	}	
}
