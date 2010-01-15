package com.triadsoft.properties.model.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static final String LANGUAGE_REGEX = "[a-z]{2}";
	private static final String COUNTRY_REGEX = "[A-Z]{2}";

	public static Locale getLocale(String languageCountry) {
		String language = extractValue(languageCountry, LANGUAGE_REGEX);
		String country = extractValue(languageCountry, COUNTRY_REGEX);

		if (language != null && country != null) {
			return new Locale(language, country);
		} else if (language != null && country == null) {
			return new Locale(language);
		}
		return null;
	}

	/**
	 * Extrae de un texto el segmento que coincide con la expresion regula
	 * 
	 * @param searchText
	 * @param regex
	 * @return
	 */
	private static String extractValue(String searchText, String regex) {
		String searched = null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(searchText);

		if (m.find()) {
			searched = searchText.substring(m.start(), m.end());
		}
		return searched;
	}

	public static void main(String[] args) {
		Locale locale = StringUtils.getLocale("es_AR");
		Locale locale1 = StringUtils.getLocale("en|US");
		Locale locale2 = StringUtils.getLocale("pt.BR");
		Locale locale3 = StringUtils.getLocale("es.AR");
	}
}