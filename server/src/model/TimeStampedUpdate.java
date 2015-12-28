/**
 * 
 */
package model;

/**
 * @author Sandeep
 *
 */
public class TimeStampedUpdate {
	String message;
	long timeStamp;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * 
	 */
	public TimeStampedUpdate(String message, long timestamp) {
		this.message = message;
		this.timeStamp = timestamp;
	}

	public TimeStampedUpdate(String message) {
		this.message = message;
		timeStamp = System.nanoTime();
	}

	public boolean isNewerThan(TimeStampedUpdate update){
		return (timeStamp<update.getTimeStamp());
	}
}
