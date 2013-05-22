package org.openmrs.module.programOver.advice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Request;
import org.openmrs.api.context.Context;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class UsageStatsUtils {
	
	protected static final Log log = LogFactory.getLog(UsageStatsUtils.class);
	
	/**
	 * Utility method to get a parsed date parameter
	 * 
	 * @param request the HTTP request object
	 * @param name the name of the date parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the date
	 * @throws java.text.ParseException
	 */
	public static Date getDateParameter(HttpServletRequest request, String name, Date def) throws java.text.ParseException {
		String strDate = request.getParameter(name);
		
		if (strDate != null) {
			try {
				return Context.getDateFormat().parse(strDate);
			}
			catch (Exception e) {
				// TODO: handle exception
				log.info("invalid  date format" + strDate);
			}
			
		}
		
		return def;
	}
	
	/**
	 * Utility method to add days to an existing date
	 * 
	 * @param date (may be null to use today's date)
	 * @param days the number of days to add (negative to subtract days)
	 * @return the new date
	 */
	public static Date addDaysToDate(Date date, int months) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);
		
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	
	/**
	 * Calculates the time of the last midnight before an existing date
	 * 
	 * @param date (may be null to use today's date)
	 * @return the new date
	 */
	public static Date getPreviousMidnight(Date date) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static int calculateDelay(Date startDate, Date endDate) {
		log.info("inside CalculateDelay  startDate=" + startDate + " endDate=" + endDate);
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		int delay = 0;
		while (startCal.before(endCal)) // iterate until reaching end
		{
			log.info("startCalDate=>>>" + startCal.getTime());
			int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
			
			if (dayOfWeek != 1 && dayOfWeek != 7) {
				delay++;
				log.info("day of week>>>" + dayOfWeek);
				log.info("delay>>>" + delay);
			}
			startCal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return --delay;
	}
	
	public static Date calculateAge(int age) {
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -age);
		return cal.getTime();
		
	}
	
	public static long calculateDiffDays(Date date1, Date date2) {
		
		long diff = date1.getTime() - date2.getTime();
		//		           long   diffDays = miff / (24 * 60 * 60 * 1000);
		
		return diff / (24 * 60 * 60 * 1000);
		
	}
	
	public static int getNumberOfMonths(String numberOfMonths) {
		int numberMonths = 0;
		try {
			numberMonths = Integer.parseInt(numberOfMonths);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return numberMonths;
		
	}
	
	public static Date getTheLastDayOfThemonth(int year, int month) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		// Setup a Calendar instance.
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		// Set the year as 2009
		cal.set(Calendar.YEAR, year);
		// Set the month as February (can be set as 1 or Calendar.FEBRUARY)
		cal.set(Calendar.MONTH, month);
		// Set the date as 1st - optional
		cal.set(Calendar.DATE, 1);
		System.out.println("Input date: " + cal.getTime());
		int lastDateOfMonth = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, lastDateOfMonth);
		System.out.println("Output Date: " + cal.getTime());
		return cal.getTime();
		
	}
	
	public static Date getTheFirstDayOftheMonth(int year, int month) {
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		// Setup a Calendar instance.
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		// Set the year as year
		cal.set(Calendar.YEAR, year);
		// Set the month as month(can be set as 1 or Calendar.FEBRUARY)
		cal.set(Calendar.MONTH, month);
		// Set the date as 1st - optional
		cal.set(Calendar.DATE, 1);
		System.out.println("Input date: " + cal.getTime());
		int lastDateOfMonth = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, lastDateOfMonth);
		System.out.println("Output Date: " + cal.getTime());
		return cal.getTime();
		
	}
	
}
