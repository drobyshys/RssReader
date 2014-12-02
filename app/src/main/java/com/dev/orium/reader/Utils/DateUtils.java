package com.dev.orium.reader.Utils;

import android.content.Context;

import com.dev.orium.reader.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	
	final static SimpleDateFormat dateFormats[] = new SimpleDateFormat[] {
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US),
			new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z", Locale.US),
			new SimpleDateFormat("EEE, d MMM yy HH:mm z", Locale.US),
			new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US),
			new SimpleDateFormat("EEE, d MMM yyyy HH:mm z", Locale.US),
			new SimpleDateFormat("EEE d MMM yy HH:mm:ss z", Locale.US),
			new SimpleDateFormat("EEE d MMM yy HH:mm z", Locale.US),
			new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss z", Locale.US),
			new SimpleDateFormat("EEE d MMM yyyy HH:mm z", Locale.US),
			new SimpleDateFormat("d MMM yy HH:mm z", Locale.US),
			new SimpleDateFormat("d MMM yy HH:mm:ss z", Locale.US),
			new SimpleDateFormat("d MMM yyyy HH:mm z", Locale.US),
			new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.US),

			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
			new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z", Locale.getDefault()),
			new SimpleDateFormat("EEE, d MMM yy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.getDefault()),
			new SimpleDateFormat("EEE, d MMM yyyy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("EEE d MMM yy HH:mm:ss z", Locale.getDefault()),
			new SimpleDateFormat("EEE d MMM yy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss z", Locale.getDefault()),
			new SimpleDateFormat("EEE d MMM yyyy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("d MMM yy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("d MMM yy HH:mm:ss z", Locale.getDefault()),
			new SimpleDateFormat("d MMM yyyy HH:mm z", Locale.getDefault()),
			new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.getDefault()),
	};

    private static SimpleDateFormat dateF;
    private static SimpleDateFormat timeF;
    private static int year;
    private static String yesterday;
    private static Calendar calendar;
    private static int todayDay;
    private static String today;

    private static Context context;



    public static void init(Context ctx) {
        context = ctx;

        calendar = Calendar.getInstance();

        dateF = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        timeF = new SimpleDateFormat(", hh.mm", Locale.getDefault());


        today = context.getString(R.string.today);
        yesterday = context.getString(R.string.yesterday);

        calendar.setTime(new Date());
        todayDay = calendar.get(Calendar.DAY_OF_YEAR);
        year = calendar.get(Calendar.YEAR);
    }

    public static String getDateString(Date date) {
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_YEAR);
        if (calendar.get(Calendar.YEAR) == year) {
            if (day == todayDay) {
                return today + timeF.format(date);
            }
            if (day == todayDay - 1) {
                return yesterday + timeF.format(date);
            }
        }
        return dateF.format(date);
    }

	public static Date parseDate(String date) {
		for (SimpleDateFormat format : dateFormats) {
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				return format.parse(date);
			} catch (ParseException e) {
			}
	
			// try it again in english
			try {
				SimpleDateFormat enUSFormat = new SimpleDateFormat(format.toPattern(), Locale.US);
				return enUSFormat.parse(date);
			} catch (ParseException e) {
			}
		}
	
		return null;
	}

}
