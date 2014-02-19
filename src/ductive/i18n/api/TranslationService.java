package ductive.i18n.api;

import java.util.Map;

public interface TranslationService {
	
	String translateDefault(String applicationName, String languageCode, String key, String description);

	String translate(String applicationName, String languageCode, String key, String description, Map<String,String> variation); // not implemented yet

	Map<String,String> translateAll(String applicationName, String languageCode, String key, String description); // not implemented yet

}