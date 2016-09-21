package org.sjr.babel.entity;

import javax.persistence.Embeddable;

@Embeddable
public class MultiLanguageText {

	private String textFr, textEn, textPrs, textAr;

	public String getTextFr() {
		return textFr;
	}

	public void setTextFr(String textFr) {
		this.textFr = textFr;
	}

	public String getTextEn() {
		return textEn;
	}

	public void setTextEn(String textEn) {
		this.textEn = textEn;
	}

	public String getTextPrs() {
		return textPrs;
	}

	public void setTextPrs(String textPrs) {
		this.textPrs = textPrs;
	}

	public String getTextAr() {
		return textAr;
	}

	public void setTextAr(String textAr) {
		this.textAr = textAr;
	}
}
