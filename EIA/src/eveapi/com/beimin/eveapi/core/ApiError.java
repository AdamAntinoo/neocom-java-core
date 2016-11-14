package com.beimin.eveapi.core;

import java.io.Serializable;
import java.util.Date;

import com.beimin.eveapi.utils.DateUtils;

public class ApiError implements Serializable {
	private int			code;
	private String	error;
	private Date		retryAfterDate	= null;

	public int getCode() {
		return code;
	}

	public String getError() {
		return error;
	}

	public Date getRetryAfterDate() {
		return retryAfterDate;
	}

	public boolean hasRetryAfterDate() {
		return retryAfterDate != null;
	}

	public void setCode(final int code) {
		this.code = code;
	}

	public void setError(final String error) {
		this.error = error;
		try {
			int retryIndex = error.indexOf("retry after ");
			if (retryIndex > 0) {
				int beginIndex = retryIndex + 12;
				String substring = error.substring(beginIndex, beginIndex + 19);
				retryAfterDate = DateUtils.getGMTConverter().convert(Date.class, substring);
			}
		} catch (Exception e) {
			// ignore.
		}
	}

	@Override
	public String toString() {
		return code + ": " + error;
	}
}