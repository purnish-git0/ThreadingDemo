import exception.InvalidConfigException;

public interface Configurable {

    public void setConfig(Configuration config) throws InvalidConfigException;
}
