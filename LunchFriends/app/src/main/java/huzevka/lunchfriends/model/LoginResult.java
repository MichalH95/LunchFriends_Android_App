package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="loginresult")
public class LoginResult {

	@Element(name = "success")
	boolean success;

	@Element(name = "noUsernameError")
	boolean noUsernameError;

	@Element(name = "person", required = false)
	Person person;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isNoUsernameError() {
		return noUsernameError;
	}

	public void setNoUsernameError(boolean noUsernameError) {
		this.noUsernameError = noUsernameError;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
