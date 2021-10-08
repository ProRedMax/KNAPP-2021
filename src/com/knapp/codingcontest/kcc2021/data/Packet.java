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

public class Packet implements Serializable {
  private static final long serialVersionUID = 1L;

  // ----------------------------------------------------------------------------

  private final int id;
  private final int truckId;
  private final int length;
  private final int width;
  private final int weight;

  // ----------------------------------------------------------------------------

  public Packet(final int id, final int truckId, final int length, final int width, final int weight) {
    this.id = id;
    this.truckId = truckId;
    this.length = length;
    this.width = width;
    this.weight = weight;
  }

  // ----------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public int getTruckId() {
    return truckId;
  }

  public int getLength() {
    return length;
  }

  public int getWidth() {
    return width;
  }

  public int getWeight() {
    return weight;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Packet[id=" + id + ", truckId=" + truckId + ", length=" + length + ", width=" + width + ", weight=" + weight + "]";
  }

  // ----------------------------------------------------------------------------
}
