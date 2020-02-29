/**
 * 
 */
package com.bangalorewest.bagtracker.dto;

import java.util.Date;

/**
 * @author sudhanshu.singh
 *
 */
public class ConfirmMessageGeneration {

	private String message;
	private String bagTagID;
	private Date timeStamp;
	private String airportCode;
	private String from;
	private String to;
	private Date bagDate;
	private String loggedInAgent;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the bagTagID
	 */
	public String getBagTagID() {
		return bagTagID;
	}

	/**
	 * @param bagTagID the bagTagID to set
	 */
	public void setBagTagID(String bagTagID) {
		this.bagTagID = bagTagID;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the airportCode
	 */
	public String getAirportCode() {
		return airportCode;
	}

	/**
	 * @param airportCode the airportCode to set
	 */
	public void setAirportCode(String airportCode) {
		this.airportCode = airportCode;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the bagDate
	 */
	public Date getBagDate() {
		return bagDate;
	}

	/**
	 * @param bagDate the bagDate to set
	 */
	public void setBagDate(Date bagDate) {
		this.bagDate = bagDate;
	}

	/**
	 * @return the loggedInAgent
	 */
	public String getLoggedInAgent() {
		return loggedInAgent;
	}

	/**
	 * @param loggedInAgent the loggedInAgent to set
	 */
	public void setLoggedInAgent(String loggedInAgent) {
		this.loggedInAgent = loggedInAgent;
	}

}
