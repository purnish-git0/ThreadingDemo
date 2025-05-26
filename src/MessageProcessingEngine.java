import exception.InvalidConfigException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

public class MessageProcessingEngine extends Engine implements Configurable {

    ServerSocket ss;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setConfig(Configuration config) throws InvalidConfigException, IOException {
        if(!config.getClass().isAssignableFrom(MessagingEngineConfiguration.class)) {
            throw new InvalidConfigException("Invalid config");
        }
        ss.bind(new SocketAddress() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public InetSocketAddress()
        });

    }
}
