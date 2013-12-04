package org.keyser.anr.web.dto;

/**
 * Une réponse
 * @author PAF
 *
 */
public class ResponseDTO {
	private int rid;

	private int qid;

	private Object content;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getQid() {
		return qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}