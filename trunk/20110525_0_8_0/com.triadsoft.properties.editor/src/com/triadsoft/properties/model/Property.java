package com.triadsoft.properties.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Esta clase contiene el valor de una propiedad para todos los locales
 * agregados.
 * </p>
 * <p>
 * Se utiliza para dibujar todas las propiedades en la tabla de properties,
 * donde por cada columna se tendr� un locale diferente.
 * </p>
 * <p>
 * Entonces para cada clave tendremos tantas columnas como locales se decubran
 * en el path
 * </p>
 * 
 * @author Triad (flores.leonardo@gmail.com)
 * 
 */
public class Property {
	public static String VALUES_SEPARATOR = "|";
	private String key;
	private Map<Locale, String> values = new HashMap<Locale, String>();
	private Map<Locale, Error> errors = new HashMap<Locale, Error>();

	public Property(String key) {
		this.key = key;
	}

	/**
	 * Devuleve la clave para la proipiedad
	 * 
	 * @return String con la clave
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Permite establecer el valor de la clave
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Devuelve el valor de la propiedad para el locale solicitado
	 * 
	 * @param locale
	 * @return
	 */
	public String getValue(Locale locale) {
		return values.get(locale);
	}

	public Locale[] getLocales() {
		return values.keySet().toArray(new Locale[values.keySet().size()]);
	}

	/**
	 * Permite establecer el valor para un locale determinado
	 * 
	 * @param locale
	 * @param value
	 */
	public void setValue(Locale locale, String value) {
		if (value == null || value.trim().length() == 0) {
			value = "";
			addError(locale, new PropertyError(PropertyError.VOID_VALUE,
					"No se encontro valor para"));
		}
		this.values.put(locale, value);
	}

	/**
	 * Permite agregar un error para un locale determinado
	 * 
	 * @param locale
	 * @param error
	 */
	protected void addError(Locale locale, Error error) {
		errors.put(locale, error);
	}

	public Map<Locale, Error> getErrors() {
		return errors;
	}

	public Error getError(Locale locale) {
		return errors.get(locale);
	}

	public String toString() {
		String ret = key;
		Iterator<Locale> iter = values.keySet().iterator();
		while (iter.hasNext()) {
			Locale loc = iter.next();
			ret += "|";
			ret += loc.toString();
			ret += "|";
			ret += values.get(loc).toString();
		}
		return ret;
	}
}
