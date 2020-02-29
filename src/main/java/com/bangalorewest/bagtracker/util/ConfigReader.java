/**
 * Created as part of Sabre hackathon 2019.
 */
package com.bangalorewest.bagtracker.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * PropertiesUtil.java Created On: Nov 13, 2019 Created By: M1041768
 */
@ConfigurationProperties("bag-tracker")
public class ConfigReader {

	private static String pwdEncryption;
	
	private static String statusApiUrl;
	private static String baseUrl;
	private static String loginUrl;


	/**
	 * @return the pwdEncryption
	 */
	public static String getPwdEncryption() {
		return pwdEncryption;
	}

	/**
	 * @param pwdEncryption the pwdEncryption to set
	 */
	public void setPwdEncryption(String pwdEncryption) {
		ConfigReader.pwdEncryption = pwdEncryption;
	}

	/**
	 * @return the statusApiUrl
	 */
	public static String getStatusApiUrl() {
		return statusApiUrl;
	}

	/**
	 * @param statusApiUrl the statusApiUrl to set
	 */
	public void setStatusApiUrl(String statusApiUrl) {
		ConfigReader.statusApiUrl = statusApiUrl;
	}

	/**
	 * @return the baseUrl
	 */
	public static String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		ConfigReader.baseUrl = baseUrl;
	}

	/**
	 * @return the loginUrl
	 */
	public static String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * @param loginUrl the loginUrl to set
	 */
	public void setLoginUrl(String loginUrl) {
		ConfigReader.loginUrl = loginUrl;
	}

}
