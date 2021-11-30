package cn.promptness.calculus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class FenqileResponse<T> {

    @SerializedName("retmsg")
    private String message;

    @SerializedName("retcode")
    private Integer code;

    @SerializedName("result_rows")
    private List<T> result;

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

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FenqileResponse<?> response = (FenqileResponse<?>) o;
        return Objects.equals(message, response.message) && Objects.equals(code, response.code) && Objects.equals(result, response.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code, result);
    }
}
