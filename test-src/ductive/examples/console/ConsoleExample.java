package ductive.examples.console;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConsoleExample {

	public static void main(String[] args) throws InterruptedException {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ConsoleExampleConfig.class);
		Thread.currentThread().join();
	}

}
