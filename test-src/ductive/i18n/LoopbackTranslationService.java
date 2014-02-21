package ductive.i18n;

import java.util.Map;

import ductive.i18n.api.TranslationService;

public class LoopbackTranslationService implements TranslationService {

	@Override
	public String translateDefault(String applicationName, String languageCode, String key, String description) {
		return key;
	}

	@Override
	public String translate(String applicationName, String languageCode, String key, String description, Map<String, String> variation) {
		return key;
	}

	@Override
	public Map<String,String> translateAll(String applicationName, String languageCode, String key, String description) {
		throw new UnsupportedOperationException();
	}

}
