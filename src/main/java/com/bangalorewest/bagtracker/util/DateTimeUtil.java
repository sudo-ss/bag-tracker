/**
 * 
 */
package com.bangalorewest.bagtracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sudhanshu.singh
 *
 */
public class DateTimeUtil {

	public static Date parseStringDate(String date) {
		Date utilDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
		try {
			utilDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return utilDate;
	}

	public static String formatDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
		return sdf.format(date);
	}

}
