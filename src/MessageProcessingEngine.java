import exception.InvalidConfigException;

public class MessageProcessingEngine extends Engine implements Configurable {


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setConfig(Configuration config) throws InvalidConfigException {
        if(!config.getClass().isAssignableFrom(MessagingEngineConfiguration.class)) {
            throw new InvalidConfigException("Invalid config");
        }
    }
}
