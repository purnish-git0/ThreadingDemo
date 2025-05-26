import exception.InvalidConfigException;

import java.io.IOException;

public interface Configurable {

    public void setConfig(Configuration config) throws InvalidConfigException, IOException;
}
