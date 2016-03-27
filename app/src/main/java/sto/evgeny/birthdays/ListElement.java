package sto.evgeny.birthdays;

public class ListElement {
	private String text;
	private boolean groupHeader;
	
	public ListElement(String text, boolean groupHeader) {
		this.text = text;
		this.groupHeader = groupHeader;
	}

	public String getText() {
		return text;
	}

	public boolean isGroupHeader() {
		return groupHeader;
	}
	
}
