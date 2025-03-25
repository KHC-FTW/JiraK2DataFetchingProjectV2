package org.ha.ckh637.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CachedData {
    private final String emailContent;
    private final String attachmentPath;
    private final byte[] attachment;
}
