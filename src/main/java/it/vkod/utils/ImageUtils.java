package it.vkod.utils;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;

@UtilityClass
public class ImageUtils {

    public StreamResource convertToImage(final byte[] imageData, final String username) {

        return new StreamResource(
                username.concat("_QR.png"),
                (InputStreamFactory) () -> new ByteArrayInputStream(imageData));
    }
}
