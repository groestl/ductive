package ductive.i18n.api.context;

import ductive.i18n.api.TranslationService;

public class TranslationContext {

	private final String applicationName;
	private final String requestedLanguageCode;
	private final TranslationService translationService;

	public TranslationContext(final String applicationName,final String requestedLanguageCode,final TranslationService translationService) {
		this.requestedLanguageCode = requestedLanguageCode;
		this.applicationName = applicationName;
		this.translationService = translationService;
	}

	public String requestedLanguageCode() {
		return requestedLanguageCode;
	}

	public String applicationName() {
		return applicationName;
	}

	public TranslationService translationService() {
		return translationService;
	}

	public String translateDefault(String key, String description) {
		return translationService.translateDefault(applicationName,requestedLanguageCode,key,description);
	}
}
