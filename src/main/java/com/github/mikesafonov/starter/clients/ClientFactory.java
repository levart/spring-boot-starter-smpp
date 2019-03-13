package com.github.mikesafonov.starter.clients;

import com.github.mikesafonov.starter.SmppProperties;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ReceiverConfiguration;
import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.sender.DefaultSenderClient;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.TransmitterConfiguration;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * Helper class for building {@link SenderClient} and {@link ResponseClient}
 */
@UtilityClass
public class ClientFactory {

    public static SenderClient mockSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator) {
        validateName(name);
        requireNonNull(smppResultGenerator);

        return new MockSenderClient(smppResultGenerator, name);
    }

    public static SenderClient testSender(@NotNull SenderClient senderClient, @NotNull SmppProperties.Defaults defaults,
                                          @NotNull SmppResultGenerator smppResultGenerator,
                                          @NotNull SmppProperties.SMSC smsc) {
        requireNonNull(senderClient);
        requireNonNull(defaults);
        requireNonNull(smppResultGenerator);
        requireNonNull(smsc);

        String[] phones = getOrDefault(smsc.getAllowedPhones(), defaults.getAllowedPhones());
        List<String> allowedPhones = (phones == null) ? emptyList() : Arrays.asList(phones);
        return new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);
    }

    public static SenderClient defaultSender(@NotBlank String name, @NotNull SmppProperties.Defaults defaults, @NotNull SmppProperties.SMSC smsc,
                                             @NotNull TypeOfAddressParser typeOfAddressParser) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);
        requireNonNull(typeOfAddressParser);

        boolean loggingBytes = getOrDefault(smsc.getLoggingBytes(), defaults.isLoggingBytes());
        boolean loggingPdu = getOrDefault(smsc.getLoggingPdu(), defaults.isLoggingPdu());
        int windowsSize = getOrDefault(smsc.getWindowSize(), defaults.getWindowSize());
        boolean ucs2Only = getOrDefault(smsc.getUcs2Only(), defaults.isUcs2Only());
        long requestTimeout = getOrDefault(smsc.getRequestTimeout(), defaults.getRequestTimeout()).toMillis();

        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(name, smsc.getCredentials(), loggingBytes, loggingPdu, windowsSize);
        return new DefaultSenderClient(transmitterConfiguration, smsc.getMaxTry(),
                ucs2Only, requestTimeout, typeOfAddressParser);
    }

    public static ResponseClient defaultResponse(@NotBlank String name, @NotNull SmppProperties.Defaults defaults, @NotNull SmppProperties.SMSC smsc) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);

        boolean loggingBytes = getOrDefault(smsc.getLoggingBytes(), defaults.isLoggingBytes());
        boolean loggingPdu = getOrDefault(smsc.getLoggingPdu(), defaults.isLoggingPdu());
        long rebindPeriod = getOrDefault(smsc.getRebindPeriod(), defaults.getRebindPeriod()).getSeconds();

        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(name, smsc.getCredentials(), loggingBytes, loggingPdu);
        return new DefaultResponseClient(receiverConfiguration, rebindPeriod);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name must not be empty!");
        }
    }

    private  <T> T getOrDefault(T obj, T defaultObj) {
        if (obj == null) {
            return defaultObj;
        }
        return obj;
    }
}
