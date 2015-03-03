package org.keyser.anr.web.dto;

/**
 * Une réponse
 * 
 * @author PAF
 *
 */
public class ResponseDTO {
	private int rid;

	private Object content;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseDTO [rid=");
		builder.append(rid);
		builder.append(", ");
		if (content != null) {
			builder.append("content=");
			builder.append(content);
		}
		builder.append("]");
		return builder.toString();
	}

}