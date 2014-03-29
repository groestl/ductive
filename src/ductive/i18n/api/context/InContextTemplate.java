package ductive.i18n.api.context;

abstract public class InContextTemplate<T> {

	public T execute(TranslationContext translationContext) {
		TranslationContextHolder.pushContext(translationContext);
		try {
			return inContext();
		} finally {
			TranslationContextHolder.popContext();
		}
	}

	protected abstract T inContext();

}
