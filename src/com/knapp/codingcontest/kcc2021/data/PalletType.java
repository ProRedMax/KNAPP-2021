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

public class PalletType implements Serializable, Comparable<PalletType> {
  private static final long serialVersionUID = 1L;

  // ----------------------------------------------------------------------------

  private final String id;
  private final int length;
  private final int width;
  private final int maxHeight;
  private final int maxWeight;

  // ----------------------------------------------------------------------------

  public PalletType(final String id, final int length, final int width, final int maxHeight, final int maxWeight) {
    this.id = id;
    this.length = length;
    this.width = width;
    this.maxHeight = maxHeight;
    this.maxWeight = maxWeight;
  }

  // ----------------------------------------------------------------------------

  public String getId() {
    return id;
  }

  public int getLength() {
    return length;
  }

  public int getWidth() {
    return width;
  }

  public int getMaxHeight() {
    return maxHeight;
  }

  public int getMaxWeight() {
    return maxWeight;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "PalletType[id=" + id + ", length=" + length + ", width=" + width + ", maxHeight=" + maxHeight + ", maxWeight="
        + maxWeight + "]";
  }

  // ----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return id.hashCode();
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
    final PalletType other = (PalletType) obj;
    return id.equals(other.id);
  }

  @Override
  public int compareTo(final PalletType o) {
    return id.compareTo(o.id);
  }

  // ----------------------------------------------------------------------------
}
