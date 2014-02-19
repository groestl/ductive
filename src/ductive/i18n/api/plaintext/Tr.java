package ductive.i18n.api.plaintext;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ductive.i18n.api.context.TranslationContext;
import ductive.i18n.api.context.TranslationContextHolder;

public class Tr {

	private final static Log logger = LogFactory.getLog(Tr.class);

	/**
	 * This is a convenience function to use for a single TranslateableText
	 * parameter. This is useful to prepare immutable, unparameterized text to
	 * be used on more than one location.
	 * 
	 * @param text
	 *            text dto containing the key and a description
	 * @return translated text
	 */
	public static String text(TranslateableText text) {
		return text(text.key(), text.description());
	}

	/**
	 * Translate a key containing variables (e.g. "{name-of-variable}") to a
	 * translated version
	 * 
	 * @param key
	 *            the key to translate, containing the default text
	 * @param description
	 *            a description of the key, for the translator
	 * @param variables
	 *            variables, containing specific values and a description, also
	 *            targeted at a translator
	 * @return translated text
	 */
	public static String text(String key, String description, NamedVariable... variables) {
		final TranslationContext ctx = TranslationContextHolder.getCurrentContext();

		if (logger.isTraceEnabled())
			logger.trace(String.format("Translating (%s/%s) %s (%s)",ctx.applicationName(),ctx.requestedLanguageCode(),key,description));

		String tmp = ctx.translationService().translateDefault(ctx.applicationName(),ctx.requestedLanguageCode(),key,description);

		for (NamedVariable var : variables)
			tmp = tmp.replace(String.format("{%s}",var.name()), var.value()==null?"null":var.value().toString());
		
		return tmp;
	}

	public static NamedVariable var(String variableName, Object variableValue, String variableDescription) {
		return NamedVariable.var(variableName,variableValue,variableDescription);
	}

}
