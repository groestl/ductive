package ductive.i18n.api.plaintext;

public class TranslateableText {

	private final String key;
	private final String description;

	public TranslateableText(String key, String description) {
		this.key = key;
		this.description = description;
	}

	public String key() {
		return key;
	}

	public String description() {
		return description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<TranslateableText ");
		sb.append(" key=").append(key()).append(" ");
		sb.append(" description=").append(description()).append("");
		sb.append(">");
		return sb.toString();
	}

}
