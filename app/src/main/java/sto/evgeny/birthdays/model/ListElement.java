package sto.evgeny.birthdays.model;

import android.content.Intent;

public class ListElement {
	private String text;
	private Type type;
	
	public ListElement(String text, Type type) {
		this.text = text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Type getType() {
		return type;
	}

	public enum Type {
		GROUP_HEADER(),
		PHONE(Intent.ACTION_DIAL, "tel:"),
		EMAIL(Intent.ACTION_SENDTO, "mailto:");

		private String action;
		private String prefix;

		Type() {}
		Type(String action, String prefix) {
			this.action = action;
			this.prefix = prefix;
		}

		public String getAction() {
			return action;
		}

		public String getPrefix() {
			return prefix;
		}
	}
}
