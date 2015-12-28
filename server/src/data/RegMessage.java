/**
 * 
 */
package data;

/**
 * @author Sandeep
 *
 */
public class RegMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipv4;

	/**
	 * 
	 */
	public RegMessage() {
		super();
		ipv4 = "";

	}

	public String getIpv4() {
		return ipv4;
	}

	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}
}
