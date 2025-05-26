import java.util.Properties;

public abstract class MainConfig extends Configuration {



    protected int port;

    protected String hostname;

    public MainConfig(Properties props) {
        this.port = (Integer)props.get("port");
        this.hostname = (String) props.get("hostname");
    }
}
