package ductive.i18n.api.context;

import java.util.EmptyStackException;
import java.util.Stack;

public class TranslationContextHolder {

	private static final ThreadLocal<Stack<TranslationContext>> contextHolder = new ThreadLocal<Stack<TranslationContext>>() {
		@Override
		protected Stack<TranslationContext> initialValue() {
			return new Stack<TranslationContext>();
		}
	};

	public static void pushContext(TranslationContext context) {
		contextHolder.get().push(context);
	}

	public static TranslationContext getCurrentContext() {
		try {
			return contextHolder.get().peek();
		} catch(EmptyStackException e) {
			throw new RuntimeException(String.format("translation context not available!"));
		}
	}

	public static void popContext() {
		contextHolder.get().pop();
	}

}
