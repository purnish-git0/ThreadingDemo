public class DefaultMessageImpl implements Message {

    private final Endpoint from;

    private final Endpoint to;

    public DefaultMessageImpl(Endpoint from, Endpoint to) {
        this.from = from;
        this.to =to;
    }

    @Override
    public Endpoint from() {
        return this.from;
    }

    @Override
    public Endpoint to() {
        return this.to;
    }
}
