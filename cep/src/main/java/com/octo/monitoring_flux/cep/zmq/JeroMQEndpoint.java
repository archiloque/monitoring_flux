package com.octo.monitoring_flux.cep.zmq;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * EndPoint representing a ZERO MQ Endpoint through JeroMQ.
 */
public class JeroMQEndpoint extends DefaultEndpoint {

    /**
     * Available METHODS.
     */
    private enum SOCKETS {
        PUSH, PULL, SUBSCRIBE
    }

    /**
     * Socket ZeroMQ.
     */
    private ZContext jeromqContext;

    /**
     * Reading socket.
     */
    private ZMQ.Socket zContextSocket;

    /**
     * Available are PUSH, PULL, SUBSCRIBE.
     */
    private String socketType = String.valueOf(SOCKETS.PULL);

    /**
     * Linger.
     */
    private int linger = 0;

    /**
     * zMQ URL.
     */
    private String url;

    /**
     * Label of endpoint for logging.
     */
    private String label = url;

    /**
     * Initialize endpoint from consumer.
     *
     * @param uri       current URI
     */
    public JeroMQEndpoint(String uri, JeroMQComponent component) {
        super(uri, component);
        jeromqContext = new ZContext(1);
        // Component got other properties not used immesiately but inherit like 
    }

    public ZContext getJeromqContext() {
        return jeromqContext;
    }

    public void setJeromqContext(ZContext jeromqContext) {
        this.jeromqContext = jeromqContext;
    }

    public Producer createProducer() throws Exception {
        return new JeroMQProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new JeroMQConsumer(this, processor);
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public int getLinger() {
        return linger;
    }

    public void setLinger(int linger) {
        this.linger = linger;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSingleton() {
        return true;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ZMQ.Socket getzContextSocket() {
        return zContextSocket;
    }

    public void setzContextSocket(ZMQ.Socket zContextSocket) {
        this.zContextSocket = zContextSocket;
    }
}
