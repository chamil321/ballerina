package netty.message;

import netty.contract.Constants;
import netty.contract.config.SenderConfiguration;
import netty.contract.config.ServerBootstrapConfiguration;
import netty.contract.config.TransportProperty;
import netty.contract.config.TransportsConfiguration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Util class that provides common functionality.
 */
public class HttpConnectorUtil {

    /**
     * Extract sender configuration from transport configuration.
     *
     * @param transportsConfiguration {@link netty.contract.config.TransportsConfiguration} which sender configurations should be extracted.
     * @param scheme scheme of the transport.
     * @return extracted {@link netty.contract.config.SenderConfiguration}.
     */
    public static netty.contract.config.SenderConfiguration getSenderConfiguration(
            netty.contract.config.TransportsConfiguration transportsConfiguration,
            String scheme) {
        Map<String, SenderConfiguration> senderConfigurations =
                transportsConfiguration.getSenderConfigurations().stream().collect(Collectors
                        .toMap(senderConf ->
                                senderConf.getScheme().toLowerCase(Locale.getDefault()), config -> config));

        return netty.contract.Constants.HTTPS_SCHEME.equals(scheme) ?
                senderConfigurations.get(netty.contract.Constants.HTTPS_SCHEME) : senderConfigurations.get(Constants.HTTP_SCHEME);
    }

    /**
     * Extract transport properties from transport configurations.
     *
     * @param transportsConfiguration transportsConfiguration {@link netty.contract.config.TransportsConfiguration} which transport
     *                                properties should be extracted.
     * @return Map of transport properties.
     */
    public static Map<String, Object> getTransportProperties(TransportsConfiguration transportsConfiguration) {
        Map<String, Object> transportProperties = new HashMap<>();
        Set<netty.contract.config.TransportProperty> transportPropertiesSet = transportsConfiguration.getTransportProperties();
        if (transportPropertiesSet != null && !transportPropertiesSet.isEmpty()) {
            transportProperties = transportPropertiesSet.stream().collect(
                    Collectors.toMap(netty.contract.config.TransportProperty::getName, netty.contract.config.TransportProperty::getValue));

        }
        return transportProperties;
    }

    /**
     * Create server bootstrap configuration from given transport property set.
     *
     * @param transportPropertiesSet Set of transport properties which should be converted
     *                               to {@link netty.contract.config.ServerBootstrapConfiguration}.
     * @return ServerBootstrapConfiguration which is created from given Set of transport properties.
     */
    public static netty.contract.config.ServerBootstrapConfiguration getServerBootstrapConfiguration(Set<netty.contract.config.TransportProperty>
            transportPropertiesSet) {
        Map<String, Object> transportProperties = new HashMap<>();

        if (transportPropertiesSet != null && !transportPropertiesSet.isEmpty()) {
            transportProperties = transportPropertiesSet.stream().collect(
                    Collectors.toMap(netty.contract.config.TransportProperty::getName, TransportProperty::getValue));
        }
        // Create Bootstrap Configuration from listener parameters
        return new ServerBootstrapConfiguration(transportProperties);
    }

    private HttpConnectorUtil() {
    }
}
