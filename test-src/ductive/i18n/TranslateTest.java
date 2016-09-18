package ductive.i18n;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ductive.i18n.api.context.InContextTemplate;
import ductive.i18n.api.context.TranslationContext;
import ductive.i18n.api.plaintext.Tr;

public class TranslateTest {

	@Test
	public void test1() {
		TranslationContext translationContext = new TranslationContext("ductive-tests","en",new LoopbackTranslationService());
		String actual = new InContextTemplate<String>() {
			@Override protected String inContext() {
				return Tr.text("Hallo {username}!","welcome text",Tr.var("username","christian","name of user"));
			}
		}.execute(translationContext);
		assertEquals("Hallo christian!",actual);
	}

}
