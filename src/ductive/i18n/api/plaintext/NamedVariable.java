package ductive.i18n.api.plaintext;

public class NamedVariable {

	private final String name;

	private final Object value;

	private final String description;

	public NamedVariable(String name, Object value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public static NamedVariable var(String name, Object value,
			String description) {
		return new NamedVariable(name, value, description);
	}

	public String name() {
		return name;
	}

	public Object value() {
		return value;
	}

	public String description() {
		return description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<NamedVariable ");
		sb.append(" name=").append(name()).append(" ");
		sb.append(" value").append(value()).append("");
		sb.append(">");
		return sb.toString();
	}

}
