package com.ooksi.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

public class ThinParser {
	
	/**
	 * Creates a component from the given stream and event handler
	 *
	 * @param inputstream read xml from this stream
	 * @param handler event handlers are implemented in this object
	 * @return the parsed components' root
	 * @throws java.io.IOException
	 */
	/*
	public Object parse(InputStream inputstream, Object handler, GUIBuilder gui) throws IOException {
		return parse(inputstream, true, false, handler, gui);
	}
	*/
	/**
	 *
	 * @param inputstream
	 * @param validate parse GUI from xml if true
	 * @param dom parse an xml resoource
	 * @param handler
	 * @return
	 * @throws java.io.IOException
	 * @throws java.lang.IllegalArgumentException
	 */
	/*
	private Object parse(InputStream inputstream,
			boolean validate, boolean dom, Object handler, GUIBuilder gui) throws IOException {
		Reader reader = new InputStreamReader(inputstream);
		try {
			Object[] parentlist = null;
			Object current = null;
			Hashtable attributelist = null;
			Vector methods = (validate && !dom) ? new Vector() : null;
			StringBuffer text = new StringBuffer();
			for (int c = reader.read(); c != -1;) {
				if (c == '<') {
					if ((c = reader.read()) == '/') { //endtag
						if (text.length() > 0) {
							if (text.charAt(text.length() - 1) == ' ') {
								text.setLength(text.length() - 1);
							}
							if (!validate) {
								if (dom) {
									set(current, ":text", text.toString());
								} else {
									characters(text.toString());
								}
							}
							// else {
								//addContent(current, text.toString());
							//}
							text.setLength(0);
						}
						String tagname = (String) parentlist[2]; //getClass(current);
						for (int i = 0; i < tagname.length(); i++) { // current-tag
							if ((c = reader.read()) != tagname.charAt(i)) {
								throw new IllegalArgumentException(tagname);
							}
						}
						while (" \t\n\r".indexOf(c = reader.read()) != -1); // whitespace
						if (c != '>') throw new IllegalArgumentException(); // '>'
						c = reader.read();
						if (!validate && !dom) { endElement(); }
						if (parentlist[0] == null) {
							reader.close();
							finishParse(methods, current, handler);
							return current;
						}
						current = parentlist[0];
						parentlist = (Object[]) parentlist[1];
					}
					else if (c == '!') { // DOCTYPE
						while ((c = reader.read()) != '>'); //+(-1)
					}
					else if (c == '?') { // Processing Instructions
						boolean question = false; // read until '?>'
						while (((c = reader.read()) != '>') || !question) { question = (c == '?'); }
					}
					else { //start or standalone tag
						text.setLength(0);
						boolean iscomment = false;
						while (">/ \t\n\r".indexOf(c) == -1) {
							text.append((char) c);
							if ((text.length() == 3) && (text.charAt(0) == '!') &&
									(text.charAt(1) == '-') && (text.charAt(2) == '-')) {
								int m = 0;
								while (true) {
									c = reader.read();
									if (c == '-') { m++; }
									else if ((c == '>') && (m >= 2)) { break; }
									else { m = 0; }
								}
								iscomment = true;
							}
							c = reader.read();
						}
						if (iscomment) { continue; }
						String tagname = text.toString();
						parentlist = new Object[] { current, parentlist, tagname };
						if (validate) {
							current = (current != null) ?
								addElement(current, tagname) : create(tagname);
						} else {
							if (dom) {
								Object parent = current;
								current = createImpl(tagname = tagname.intern());
								if (parent != null) {
									insertItem(parent, tagname, current, -1);
									//set(current, ":parent", parent);
								}
							} else {
								current = tagname;
							}
						}
						text.setLength(0);
						while (true) {
							boolean whitespace = false;
							while (" \t\n\r".indexOf(c) != -1) {
								c = reader.read();
								whitespace = true;
							}
							if (c == '>') {
								if (!validate && !dom) {
									startElement((String) current, attributelist); attributelist = null;
								}
								c = reader.read();
								break;
							}
							else if (c == '/') {
								if ((c = reader.read()) != '>') {
									throw new IllegalArgumentException(); // '>'
								}
								if (!validate && !dom) {
									startElement((String) current, attributelist); attributelist = null;
									endElement();
								}
								if (parentlist[0] == null) {
									reader.close();
									finishParse(methods, current, handler);
									return current;
								}
								current = parentlist[0];
								parentlist = (Object[]) parentlist[1];
								c = reader.read();
								break;
							}
							else if (whitespace) {
								while ("= \t\n\r".indexOf(c) == -1) {
									text.append((char) c);
									c = reader.read();
								}
								String key = text.toString();
								text.setLength(0);
								while (" \t\n\r".indexOf(c) != -1) c = reader.read();
								if (c != '=') throw new IllegalArgumentException();
								while (" \t\n\r".indexOf(c = reader.read()) != -1);
								char quote = (char) c;
								if ((c != '\"') && (c != '\'')) throw new IllegalArgumentException();
								while (quote != (c = reader.read())) {
									if (c == '&') {
										StringBuffer eb = new StringBuffer();
										while (';' != (c = reader.read())) { eb.append((char) c); }
										String entity = eb.toString();
										if ("lt".equals(entity)) { text.append('<'); }
										else if ("gt".equals(entity)) { text.append('>'); }
										else if ("amp".equals(entity)) { text.append('&'); }
										else if ("quot".equals(entity)) { text.append('"'); }
										else if ("apos".equals(entity)) { text.append('\''); }
										else if (entity.startsWith("#")) {
											boolean hexa = (entity.charAt(1) == 'x');
											text.append((char) Integer.parseInt(entity.substring(hexa ? 2 : 1), hexa ? 16 : 10));
										}
										else throw new IllegalArgumentException("unknown " + "entity " + entity);
									}
									else text.append((char) c);
								}
								if (validate) {
									addAttribute(current, key, text.toString(), methods);
								} else {
									if (dom) {
										set(current, key.intern(), text.toString());
									} else {
										if (attributelist == null) { attributelist = new Hashtable(); }
										attributelist.put(key, text.toString());
									}
								}
								//'<![CDATA[' ']]>'
								text.setLength(0);
								c = reader.read();
							}
							else throw new IllegalArgumentException();
						}
					}
				}
				else {
					if (" \t\n\r".indexOf(c) != -1) {
						if ((text.length() > 0) && (text.charAt(text.length() - 1) != ' ')) {
							text.append(' ');
						}
					}
					else {
						text.append((char) c);
					}
					c = reader.read();
				} 
			}
			throw new IllegalArgumentException();
		}
		finally {
			if (reader != null) { reader.close(); }
		}
	}
	*/
}
