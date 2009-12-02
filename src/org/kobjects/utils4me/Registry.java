package org.kobjects.utils4me;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Stores data persistently. A singleton instance should be referenced from
 * the MIDlet. All data is stored as strings.
 * 
 * @author stefan.haustein
 */
public final class Registry {

	private static Hashtable regData = new Hashtable();
	private static Hashtable rmsData = new Hashtable();
	private static Hashtable tmpData = new Hashtable();

	private static RecordStore recordStore;
	private static boolean isUncommitted;

	private static void init() {

		if( recordStore != null ) {
			return;
		}
		
		//Try to get data that should be pre-populated into the registry
		try {
			final InputStream is = new Registry().getClass().getResourceAsStream(
					"/registry.ini");
			if( is == null ) {
				//
				// Some debug output
				//#mdebug fatal
				System.out.println("Cannot access resource!?!");
				//#enddebug
			}
			else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final byte[] buf = new byte[4096];
				while( true ) {
					final int count = is.read(buf);
					if( count <= 0 )
						break;
					baos.write(buf, 0, count);
				}
				baos.close();
				final ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				baos = null;

				final InputStreamReader r = new InputStreamReader(bais,
						"ISO-8859-1");
				while( true ) {
					String s = Tools.readLine(r);

					//System.out.println("Reading registry line: "+s);

					if( s == null )
						break;
					s = s.trim();
					if( s.length() > 0 && s.charAt(0) == ';' )
						continue;
					final int cut = s.indexOf('=');
					if( cut == -1 )
						continue;
					regData.put(
							s.substring(0, cut).trim(),
							Tools.decodeJavaEscape(s.substring(cut + 1).trim()));
				}
				bais.close();
				is.close();
			}

			recordStore = RecordStore.openRecordStore("registry", true);

			if( recordStore.getNumRecords() != 0 ) {
				final byte[] record = recordStore.getRecord(1);
				final ByteArrayInputStream bais = new ByteArrayInputStream(
						record);
				final DataInputStream dis = new DataInputStream(bais);
				final int count = dis.readInt();
				for( int i = 0; i < count; i++ ) {
					final String key = dis.readUTF();
					final String value = dis.readUTF();
					rmsData.put(key, value);
				}
			}
		} catch (final Exception e) {
			//#mdebug fatal
			e.printStackTrace();
			//#enddebug

			throw new RuntimeException(e.toString() + ":" + e.getMessage()
					+ ":" + recordStore);
		}
	}

	/** 
	 * Commit pending changes to backing store
	 */
	public synchronized static void flush() {
		if( recordStore == null || !isUncommitted ) {
			return;
		}

		isUncommitted = false;

		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final DataOutputStream dos = new DataOutputStream(baos);

			dos.writeInt(rmsData.size());

			for( final Enumeration e = rmsData.keys(); e.hasMoreElements(); ) {
				final String key = (String) e.nextElement();
				dos.writeUTF(key);
				dos.writeUTF((String) rmsData.get(key));
			}
			dos.close();

			final byte[] record = baos.toByteArray();
			baos.close();

			if( recordStore.getNumRecords() == 0 ) {
				recordStore.addRecord(record, 0, record.length);
			}
			else {
				recordStore.setRecord(1, record, 0, record.length);
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Close backing store
	 */
	public synchronized static void close() {
		if( recordStore != null ) {
			try {
				flush();
				recordStore.closeRecordStore();
				recordStore = null;

			} catch (final RecordStoreException e) {
				//#mdebug fatal
				e.printStackTrace();
				//#enddebug
			}
		}
	}

	/**
	 * Set an entry pair of key and value in the registry.
	 * 
	 * @param key
	 *            a string with the name of key.
	 * @param value
	 *            a string with the value.
	 * @param bPersistent
	 *            flag which indicate if this pair should be made persistent;
	 *            stored into the registry file.
	 */
	public synchronized static void set(final String key, final String value,
			final boolean bPersistent) {

		//
		// Make sure the registry system is initialized
		init();

		//
		// Give us some debug information
		//#mdebug debug
		//@		System.out.println("Registry.set(Key:\"" + key + "\", Value:\""
		//@				+ value + "\", Persistent:\"" + bPersistent + "\")");
		//#enddebug

		//
		// Lookup the current value for this key
		final String old = get(key, null);

		//
		// Check if we should set a value or remove a existing one
		if( value == null ) {
			if( old != null ) {
				//
				// Remove the existing entry and mark the registry as dirty
				tmpData.remove(key);
				rmsData.remove(key);
				isUncommitted = true;
			}
		}
		else if( !value.equals(old) ) {
			//
			// If this key value pair should not be persistent then store it only in the
			// tmpData hashtable
			if( !bPersistent ) {
				tmpData.put(key, value);
				return;
			}

			//
			// Ok, we need to make this entry persistent. So remove any value from
			// the temp table, store it into the persistent table and mark the registry
			// as dirty.
			tmpData.remove(key);
			rmsData.put(key, value);
			isUncommitted = true;
		}
	}

	/**
	 * Set an entry pair of key and value in the registry.
	 * 
	 * @param key  The key to retrieve a value for
	 * @param dflt A default value if key does not exist
	 * @return The value stored in the registry for the key. 
	 */
	public static String get(final String key, final String dflt) {

		init();

		//
		// First try to read from the temp data, then from rms and then from reg
		String v = null;
		try {
			v = (String) tmpData.get(key);
			if( v == null ) {
				v = (String) rmsData.get(key);
			}
			if( v == null ) {
				v = (String) regData.get(key);
			}
		} catch (final Exception e) {
			//#mdebug fatal
			// TODO Auto-generated catch block
			e.printStackTrace();
			//#enddebug
		}

		//
		// If we still don't have a valid value then return the given default value.
		return v == null ? dflt : v;
	}

	/**
	 * Remove all entries from the registry. Depending on the given parameter
	 * either the complete registry is cleared or only the temporary registry
	 * entries.
	 * 
	 * @param bAll
	 *            true to clear all entries, false to only clear temporary
	 *            entries.
	 */
	public static void clear(final boolean bAll) {

		//#mdebug info
		//@		System.out.println("Registry.clear(): bAll:"+ bAll);
		//#enddebug

		//
		// Clear the temp entries in any case, then check if there is more to do
		tmpData = new Hashtable();
		if( !bAll )
			return;

		rmsData = new Hashtable();
		isUncommitted = true;
		flush();
	}

}
