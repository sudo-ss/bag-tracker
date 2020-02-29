package com.bangalorewest.bagtracker.constants;

/**
 * @author sudhanshu.singh
 *
 */
public enum MessageBuilderConstants {

	START_BSM("BSM"),

	START_BPM("BPM"),

	FWD_SLASH("/"),

	LOCAL_BAG("1L"),

	DOT_V(".V/"),

	DOT_N(".N/"),

	DOT_P(".P/"),

	DOT_O(".O/"),

	DOT_F(".F/"),

	DOT_U(".U/"),

	DOT_B(".B/"),

	END_BSM("ENDBSM"),

	END_BPM("ENDBPM"),

	HASH("#"),

	CABIN_CLASS_Y("Y");

	private String msg;

	private MessageBuilderConstants(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return this.msg;
	}

}
