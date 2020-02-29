/**
 * 
 */
package com.bangalorewest.bagtracker.dto;

import java.util.Date;

/**
 * @author sudhanshu.singh
 *
 */
public class BagHistoryDTO {

	private String status;
	private Date timestamp;
	private String from;
	private String to;
	private String airportCode;
	private String messageDate;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
	 * @return the messageDate
	 */
	public String getMessageDate() {
		return messageDate;
	}

	/**
	 * @param messageDate the messageDate to set
	 */
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

}
