package org.sjr.babel.entity;

import javax.persistence.Embeddable;

@Embeddable
public class MultiLanguageText {

	private String defaultText, textEn, textPrs, textAr;

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
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

	public String getText(String language) {
		if("fr".equals(language)){
			return this.defaultText;
		}
		else if("en".equals(language)){
			return this.textEn;
		}
		else if("ar".equals(language)){
			return this.textAr;
		}
		else if("prs".equals(language)){
			return this.textPrs;
		}
		return this.defaultText;
	}
}
