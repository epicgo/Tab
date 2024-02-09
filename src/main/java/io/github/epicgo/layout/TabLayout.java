package io.github.epicgo.layout;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TabLayout {

    private final Set<TabEntry> entries = new ConcurrentSet<>();

    private String header;
    private String footer;

    public void addSlot(int x, int y, String text, int ping, String value, String signature) {
        addSlot(convertXandYToIndex(x, y), text, ping, value, signature);
    }

    public void addSlot(int tabSlot, String text, int ping, String value, String signature) {
        entries.add(new TabEntry().setTabSlot(tabSlot).setTextLine(text).setPing(ping).setValue(value).setSignature(signature));
    }

    public void addSlot(int x, int y, String text, int ping) {
        addSlot(convertXandYToIndex(x, y), text, ping);
    }

    public void addSlot(int tabSlot, String text, int ping) {
        entries.add(new TabEntry().setTabSlot(tabSlot).setTextLine(text).setPing(ping));
    }

    public void addSlot(int x, int y, String text, String value, String signature) {
        addSlot(convertXandYToIndex(x, y), text, value, signature);
    }

    public void addSlot(int tabSlot, String text, String value, String signature) {
        entries.add(new TabEntry().setTabSlot(tabSlot).setTextLine(text).setValue(value).setSignature(signature));
    }

    public void addSlot(int x, int y, String text) {
        addSlot(convertXandYToIndex(x, y), text);
    }

    public void addSlot(int tabSlot, String text) {
        entries.add(new TabEntry().setTabSlot(tabSlot).setTextLine(text));
    }

    /**
     * Convierte un par de coordenadas (x, y) en un índice en un arreglo bidimensional de tamaño 20x20.
     *
     * @param x la coordenada x.
     * @param y la coordenada y.
     * @return el índice correspondiente en el arreglo bidimensional.
     */
    public int convertXandYToIndex(int x, int y) {
        return y + x * 20;
    }
}
