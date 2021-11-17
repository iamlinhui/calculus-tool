package cn.promptness.calculus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Login {

    @SerializedName("login_success")
    private Integer success;

    public boolean isSuccess() {
        return Objects.equals(success, 1);
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Login login = (Login) o;
        return Objects.equals(success, login.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success);
    }
}
