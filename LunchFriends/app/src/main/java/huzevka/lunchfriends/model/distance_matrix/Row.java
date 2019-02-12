package huzevka.lunchfriends.model.distance_matrix;

import org.simpleframework.xml.Root;

@Root(name="row")
public class Row {

	@org.simpleframework.xml.Element(name = "element")
	Element element;

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
}
