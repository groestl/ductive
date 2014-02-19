package ductive.i18n.api.context;

abstract public class InContextTemplate<T> {

	public void execute(TranslationContext translationContext) {
		TranslationContextHolder.pushContext(translationContext);
		try {
			inContext();
		} finally {
			TranslationContextHolder.popContext();
		}
	}

	protected abstract T inContext();

}
