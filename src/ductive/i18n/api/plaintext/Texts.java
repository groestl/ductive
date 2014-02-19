package ductive.i18n.api.plaintext;

/**
 * This is a utils class, as an example for translatable text used at different
 * locations in the project.
 * 
 * Every domain is likely to have other classes like this.
 * 
 */
public class Texts {

	public static class FORM {
		public static final TranslateableText OK_BUTTON = new TranslateableText("OK", "OK Button on forms etc");
		public static final TranslateableText DISCARD_BUTTON = new TranslateableText("Discard changes", "\"Discard changes\" Button on forms etc");
		public static final TranslateableText CANCEL_BUTTON = new TranslateableText("Cancel", "\"Cancel changes\" Button on forms etc");
		public static final TranslateableText APPLY_BUTTON = new TranslateableText("Apply", "\"Apply changes\" Button on forms etc");
	}

}
