package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class OpenOnline extends WordpressProxy {

    public OpenOnline() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.open.online";
        this.pageSize = 6;
        this.screenRows = 18;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
            18, 5, 32, 32, 32, 32, -94, -94, 32, 32, -94, -94, -69, 32, -94, -94,
            -94, 32, -94, 32, -84, -69, 32, 32, 32, -110, 13, 18, -95, 32, 32, -110,
            -66, 18, -66, -68, -110, -68, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
            -110, 32, 18, 32, 32, 32, -110, 32, -68, -95, 18, -95, 32, 32, -110, -95,
            13, 32, 18, 32, 32, -110, 32, 18, 32, 32, -110, 32, 18, 32, -110, 32,
            -94, 18, -66, 32, -110, 32, -94, 18, -66, 32, -110, 32, 18, -68, -110, 32,
            18, -95, 32, 32, -110, 13, 32, 18, -95, 32, 32, -94, -110, -66, 18, -66,
            32, -110, 32, 18, 32, 32, 32, -110, 32, 18, -94, -94, 32, -110, 32, 18,
            32, -110, -95, 18, -95, 32, -110, -95, 13, 32, 32, 18, -94, -94, -94, -94,
            -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -110, 13
    };

}