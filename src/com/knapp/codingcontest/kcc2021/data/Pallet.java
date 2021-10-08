/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2021.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.knapp.codingcontest.kcc2021.core.PalletContentFormatter;

public abstract class Pallet implements Serializable {
  private static final long serialVersionUID = 1L;

  // ----------------------------------------------------------------------------

  private final int id;
  private final int truckId;
  private final PalletType type;

  protected final Map<Integer, Layer> layers = new TreeMap<Integer, Layer>();
  protected int currentStackedHeight = 0;
  protected int currentWeight = 0;

  // ----------------------------------------------------------------------------

  protected Pallet(final int id, final int truckId, final PalletType type) {
    this.id = id;
    this.truckId = truckId;
    this.type = type;
  }

  // ----------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public PalletType getType() {
    return type;
  }

  public int getTruckId() {
    return truckId;
  }

  public int getCurrentStackedHeight() {
    return currentStackedHeight;
  }

  public int getCurrentWeight() {
    return currentWeight;
  }

  public Pallet.Layer getLayer(final int layer) {
    return layers.get(Integer.valueOf(layer));
  }

  // ----------------------------------------------------------------------------

  public String formatContent() {
    return PalletContentFormatter.formatContent(this);
  }

  @Override
  public String toString() {
    return "\n  Pallet[id=" + id + ", truckId=" + truckId + ", " + type + ", currentStackedHeight=" + currentStackedHeight
        + ", currentWeight=" + currentWeight + ", " + layers.values() + "]\n\n" + formatContent();
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static class PacketPos implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int x;
    private final int y;
    private final boolean rotated;

    protected PacketPos(final int x, final int y, final boolean rotated) {
      this.x = x;
      this.y = y;
      this.rotated = rotated;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public boolean isRotated() {
      return rotated;
    }

    @Override
    public String toString() {
      return "PacketPos[" + x + "/" + y + "/" + rotated + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + Boolean.hashCode(rotated);
      result = (prime * result) + x;
      result = (prime * result) + y;
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final PacketPos other = (PacketPos) obj;
      if (rotated != other.rotated) {
        return false;
      }
      if (x != other.x) {
        return false;
      }
      if (y != other.y) {
        return false;
      }
      return true;
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public class Layer {
    private final int layer;
    protected final Map<Pallet.PacketPos, Packet> packets = new LinkedHashMap<Pallet.PacketPos, Packet>();

    protected Layer(final int layer) {
      this.layer = layer;
    }

    public int getLayer() {
      return layer;
    }

    @Override
    public String toString() {
      return "\n        Layer#" + getLayer() + "[" + packets + "]";
    }

    public Map<PacketPos, Packet> getPackets() {
      return Collections.unmodifiableMap(packets);
    }
  }

  // ----------------------------------------------------------------------------
}
