package huzevka.lunchfriends.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import huzevka.lunchfriends.model.Invitation;

@Root(name="invitations")
public class Invitations {

	@ElementList(name="invitation", inline=true, required = false)
	private List<Invitation> invitation;

	public List<Invitation> getInvitation() {
		return invitation;
	}

	public void setInvitation(List<Invitation> invitation) {
		this.invitation = invitation;
	}

}
