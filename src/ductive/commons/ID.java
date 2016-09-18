package ductive.commons;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.primitives.Longs;

public class ID {

	public static final ID INSTANCE = new ID();

	private static class Holder {
		static final SecureRandom numberGenerator = new SecureRandom();
	}

	public static UUID next() {
		return INSTANCE.generate();
	}

	private long randomPart;
	private AtomicLong sequentialPart = new AtomicLong();

	public ID() {
		SecureRandom random = Holder.numberGenerator;

		byte[] randomBytes = new byte[8];
		random.nextBytes(randomBytes);
		randomBytes[6]  &= 0x0f;  /* clear version        */
		randomBytes[6]  |= 0x40;  /* set to version 4     */

		randomPart = Longs.fromByteArray(randomBytes);
	}

	public UUID generate() {
		return new UUID(randomPart,sequentialPart.incrementAndGet());
	}

}
