package trio.core;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class Response<T> implements Serializable {
	private final boolean hasError;
	@JsonProperty("errorText")
	private final String  errorText;
	@JsonProperty("data")
	private final T       data;

//	public Response(String errorText) {
//		this.hasError = true;
//		this.errorText = errorText;
//		this.data = null;
//	}
	
	public Response(T data) {
		this.hasError = false;
		this.errorText = null;
		this.data = data;
	}
	
	@JsonCreator
	private Response(@JsonProperty("errorText") String errorText, @JsonProperty("data") T data) {
		this.hasError = errorText != null;
		this.errorText = errorText;
		this.data = data;
	}
	
	public static <T> Response<T> createError(String errorText) {
		return new Response<>(errorText, null);
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	@Override
	public String toString() {
		return "Response{" +
		       "hasError=" + hasError +
		       ", errorText='" + errorText + '\'' +
		       ", data=" + data +
		       '}';
	}
	
	public T getData() {
		return data;
	}
	
	public String getErrorText() {
		return errorText;
	}
}
