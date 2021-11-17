package cn.promptness.calculus.pojo;

import java.util.Objects;

public class Session {

    private String min;
    private String name;
    private Integer mid;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equals(min, session.min) && Objects.equals(name, session.name) && Objects.equals(mid, session.mid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), min, name, mid);
    }
}
