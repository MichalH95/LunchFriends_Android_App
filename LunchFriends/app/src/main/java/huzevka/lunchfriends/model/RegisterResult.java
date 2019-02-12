package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="registerresult")
public class RegisterResult {

	@Element(name = "success")
	boolean success;
	@Element(name = "useridUniqueError")
	boolean useridUniqueError;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isUseridUniqueError() {
		return useridUniqueError;
	}

	public void setUseridUniqueError(boolean useridUniqueError) {
		this.useridUniqueError = useridUniqueError;
	}

}
