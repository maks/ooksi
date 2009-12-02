package org.kobjects.utils4me;

import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;

public final class Tools {

	public static final Boolean TRUE = new Boolean(true);
	public static final Boolean FALSE = new Boolean(false);

	/** Can be used for all integer variables when -1 means no, false, null */
	public static final int NOTHING = -1;

	private Tools() {
		/* Nothing */
	}

	public static String decodeJavaEscape(final String encoded) {

		if( encoded.indexOf('\\') == -1 ) {
			return encoded;
		}

		final StringBuffer buf = new StringBuffer();
		for( int i = 0; i < encoded.length(); i++ ) {
			if( encoded.charAt(i) == '\\' ) {
				final char c = encoded.charAt(++i);
				switch( c ) {
				case 't':
					buf.append('\t');
					break;
				case 'n':
					buf.append('\r');
					break;
				case 'r':
					buf.append('\n');
					break;
				case '\\':
					buf.append('\\');
					break;
				case 'u':
					buf.append((char) Integer.parseInt(encoded.substring(
							i + 1, i + 5), 16));
					i += 4;
					break;
				default:
					buf.append('\\');
					buf.append(c);
				}
			}
			else {
				buf.append(encoded.charAt(i));
			}
		}
		return buf.toString();
	}

	public static Calendar parseIsoDate(final String str) {
		final Calendar c = Calendar.getInstance();
		try {
			if( str != null && str.length() == 10 ) {
				c.set(Calendar.YEAR, Integer.parseInt(str.substring(0, 4)));
				c.set(Calendar.MONTH, Integer.parseInt((str.substring(5, 7)))
						- 1 + Calendar.JANUARY);
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt((str.substring(
						8, 10))));
			}
		} catch (final Exception e) {
			//#mdebug fatal
			System.out.println(e);
			//#enddebug
		}
		return c;
	}

	public static boolean isEmptyString(final String str) {
		for( int i = 0; i < str.length(); i++ ) {
			if( str.charAt(i) != ' ' ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Converts the given integer to a string. Leading zeros are added until
	 * to fill up the string length to digits.
	 */

	public static String formatInteger(final int i, final int digits) {
		final StringBuffer sb = new StringBuffer(String.valueOf(i)); // ""+i);
		while( sb.length() < digits ) {
			sb.insert(0, '0');
		}
		return sb.toString();
	}

	/**
	 * The method make sure that the given date is valid. If the date is not
	 * valid a {@link DateFormatException} is thrown.
	 * 
	 * @param day
	 *            the day of the month of the date to check.
	 * @param month
	 *            the month of the year of the date to check.
	 * @param year
	 *            the year of the date to check.
	 * @throws DateFormatException
	 *             if the date is invalid.
	 * 
	 */
	public static void checkDate(final int day, final int month,
			final int year) {

		/* year is wrong if year < 0 */
		if( year < 0 ) {
			throw new DateFormatException("year < 0");
		}

		/* Validation of month */
		if( (month < 1) || (month > 12) ) {
			throw new DateFormatException("month < 1 || month > 12");
		}

		/* Validation of day */
		if( day < 1 ) {
			throw new DateFormatException("day < 1");
		}

		/* Validation leap-year / february / day */
		boolean isLeap = false;
		if( (year % 4 == 0) || (year % 100 == 0) || (year % 400 == 0) ) {
			isLeap = true;
		}

		if( (month == 2) && (isLeap) && (day > 29) ) {
			throw new DateFormatException("feb, leap and day > 29");
		}

		if( (month == 2) && (!isLeap) && (day > 28) ) {
			throw new DateFormatException("feb, no leap and day > 28");
		}

		/* Validation of other months */
		if( (day > 31)
				&& ((month == 1) || (month == 3) || (month == 5)
						|| (month == 7) || (month == 8) || (month == 10) || (month == 12)) ) {
			throw new DateFormatException("day > 31");
		}

		if( (day > 30)
				&& ((month == 4) || (month == 6) || (month == 9) || (month == 11)) ) {
			throw new DateFormatException("day > 30");
		}

		/* The Date is correct. */
	}

	public static String toIsoDate(final Calendar c) {

		final int year = c.get(Calendar.YEAR);
		final int month = c.get(Calendar.MONTH) + 1;
		final int day = c.get(Calendar.DAY_OF_MONTH);

		/* Make sure the given date is valid. */
		checkDate(day, month, year);

		return formatInteger(year, 4) + '-' + formatInteger(month, 2) + '-'
				+ formatInteger(day, 2);
	}

	public static String readLine(final Reader reader) throws IOException {
		final StringBuffer buf = new StringBuffer();
		while( true ) {
			final int c = reader.read();
			if( c == -1 ) {
				if( buf.length() == 0 ) {
					return null;
				}
				break;
			}
			if( c == '\n' )
				break;
			if( c != '\r' )
				buf.append((char) c);
		}
		return buf.toString();
	}

	public static String[] split(final String str, final char c) {

		final StringBuffer buf = new StringBuffer();
		int pos = 0;
		while( true ) {
			final int cut = str.indexOf(c, pos);
			if( cut == -1 ) {
				break;
			}
			buf.append((char) cut);
			pos = cut + 1;
		}

		final String[] result = new String[buf.length() + 1];
		pos = 0;
		for( int i = 0; i < buf.length(); i++ ) {
			result[i] = str.substring(pos, buf.charAt(i));
			pos = buf.charAt(i) + 1;
		}
		result[buf.length()] = str.substring(pos);
		return result;
	}

	public static String replace(final String haystack, final String needle,
			final String replacement) {

		if( needle == null || needle.length() == 0 ) {
			return haystack;
		}

		StringBuffer result = null;
		int searchPos = 0;

		while( true ) {
			final int pos = haystack.indexOf(needle, searchPos);
			if( pos == -1 ) {
				break;
			}

			if( result == null ) {
				result = new StringBuffer();
			}

			result.append(haystack.substring(searchPos, pos));
			result.append(replacement);

			searchPos = pos + needle.length();
		}

		if( result == null ) {
			return haystack;
		}

		result.append(haystack.substring(searchPos));

		return result.toString();
	}
}
