package cn.promptness.calculus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CiaResponse<T> {

    @SerializedName("msg")
    private String message;

    @SerializedName("code")
    private Integer code;

    @SerializedName("data")
    private T data;

    public boolean isSuccess() {
        return this.code == 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CiaResponse<?> response = (CiaResponse<?>) o;
        return Objects.equals(message, response.message) && Objects.equals(code, response.code) && Objects.equals(data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code, data);
    }
}
