package huzevka.lunchfriends.model.distance_matrix;

import org.simpleframework.xml.Root;

@Root(name="distance")
public class Distance {

	@org.simpleframework.xml.Element(name = "value")
	int value;

	@org.simpleframework.xml.Element(name = "text")
	String text;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
