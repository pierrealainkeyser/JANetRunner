package org.keyser.anr.web.dto;

/**
 * Une réponse
 * 
 * @author PAF
 *
 */
public class ResponseDTO {
	private int rid;

	private Object object;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object content) {
		this.object = content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseDTO [rid=");
		builder.append(rid);
		builder.append(", ");
		if (object != null) {
			builder.append("content=");
			builder.append(object);
		}
		builder.append("]");
		return builder.toString();
	}

}