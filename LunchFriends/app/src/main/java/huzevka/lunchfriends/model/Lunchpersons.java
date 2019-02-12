package huzevka.lunchfriends.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="lunchpersons")
public class Lunchpersons {

	@ElementList(name="lunchperson", inline=true, required = false)
	private List<Lunchperson> lunchperson;

	public List<Lunchperson> getLunchperson() {
		return lunchperson;
	}

	public void setLunchperson(List<Lunchperson> lunchperson) {
		this.lunchperson = lunchperson;
	}
}
