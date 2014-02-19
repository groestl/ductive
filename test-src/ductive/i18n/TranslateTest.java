package ductive.i18n;

import org.junit.Test;

import ductive.i18n.api.context.InContextTemplate;
import ductive.i18n.api.context.TranslationContext;
import ductive.i18n.api.plaintext.Tr;

public class TranslateTest {
	
	@Test
	public void test1() {
		TranslationContext translationContext = new TranslationContext("ductive-tests","en",new DefaultTranslationService());
		new InContextTemplate<Void>() {
			@Override protected Void inContext() {
				
				String text = Tr.text("Hallo {username}!","welcome text",Tr.var("username","christian","name of user"));
				System.out.println(String.format("%s",text));
				
				return null;
			}
		}.execute(translationContext);
	}

}
