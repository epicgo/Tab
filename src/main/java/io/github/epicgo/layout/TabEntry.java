package io.github.epicgo.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class TabEntry {

    private UUID id;
    private String name;

    private int tabSlot;
    private int ping = 0;

    private String textLine = " ";
    private String value;
    private String signature;
}
