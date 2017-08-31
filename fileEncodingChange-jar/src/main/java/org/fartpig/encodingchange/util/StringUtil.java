package org.fartpig.encodingchange.util;

public class StringUtil {

	public static String join(CharSequence delimiter, CharSequence... elements) {
		if (delimiter == null) {
			return null;
		}

		if (elements == null) {
			return null;
		}
		boolean isFist = true;
		StringBuilder sb = new StringBuilder();
		for (CharSequence cs : elements) {
			if (!isFist) {
				sb.append(delimiter);
			}
			sb.append(cs);
			isFist = false;
		}
		return sb.toString();
	}

	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		if (delimiter == null) {
			return null;
		}

		if (elements == null) {
			return null;
		}
		boolean isFist = true;
		StringBuilder sb = new StringBuilder();
		for (CharSequence cs : elements) {
			if (!isFist) {
				sb.append(delimiter);
			}
			sb.append(cs);
			isFist = false;
		}
		return sb.toString();
	}
}
