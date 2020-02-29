/**
 * 
 */
package com.bangalorewest.bagtracker.dto;

import java.util.Date;

/**
 * @author sudhanshu.singh
 *
 */
public class ConfirmSave {

	private String message;
	private String bagTagID;
	private Date bagDate;

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

}
