package cn.promptness.calculus.pojo;

public class TaskEvent {

    private final Integer target;
    private final Boolean result;

    public TaskEvent(Integer target, Boolean result) {
        this.target = target;
        this.result = result;
    }

    public Integer getTarget() {
        return target;
    }

    public Boolean getResult() {
        return result;
    }
}
